package vn.giaihung.jobhunter.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.giaihung.jobhunter.domain.User;
import vn.giaihung.jobhunter.domain.dto.request.ReqLoginDTO;
import vn.giaihung.jobhunter.domain.dto.response.ResponseLoginDTO;
import vn.giaihung.jobhunter.domain.dto.response.ResponseLoginDTO.ResponseUserLogin;
import vn.giaihung.jobhunter.domain.dto.response.ResponseLoginDTO.UserGetAccount;
import vn.giaihung.jobhunter.domain.dto.response.user.CreateUserResDTO;
import vn.giaihung.jobhunter.service.UserService;
import vn.giaihung.jobhunter.utils.SecurityUtil;
import vn.giaihung.jobhunter.utils.annotation.ApiMessage;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${giaihung.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<CreateUserResDTO> register(@Valid @RequestBody User postUser) throws InvalidIdException {
        boolean isEmailExists = userService.validateUserExists(postUser.getEmail());
        if (isEmailExists) {
            throw new InvalidIdException("Email " + postUser.getEmail() + "already exists, please use another email.");
        }
        CreateUserResDTO newUser = userService.handleCreateUser(postUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResponseLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) throws Exception {
        String email = loginDTO.getUsername();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email, loginDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        ResponseLoginDTO resLogin = new ResponseLoginDTO();
        ResponseUserLogin resUserLogin = new ResponseLoginDTO.ResponseUserLogin();
        User currentUser = userService.getUserByEmail(email);

        if (currentUser == null) {
            throw new Exception("User with email: " + email + " doesn't exist");
        }

        resUserLogin.setId(currentUser.getId());
        resUserLogin.setEmail(currentUser.getEmail());
        resUserLogin.setName(currentUser.getName());
        resUserLogin.setRole(currentUser.getRole());
        resLogin.setUser(resUserLogin);

        // Create token
        String accessToken = securityUtil.createAccessToken(authentication.getName(), resLogin);
        resLogin.setAccessToken(accessToken);

        // Set thông tin người dùng đăng nhập vào context (có thể sử dụng sau này)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create refresh token
        String refreshToken = securityUtil.createRefreshToken(email, resLogin);
        userService.updateRefreshToken(email, refreshToken);
        ResponseCookie resCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                .body(resLogin);
    }

    @GetMapping("/auth/account")
    @ApiMessage("Fetch account")
    public ResponseEntity<UserGetAccount> getAccount() throws Exception {
        Optional<String> emailOptional = SecurityUtil.getCurrentUserLogin();
        String email = emailOptional.isPresent() ? emailOptional.get() : "";
        User currentUser = userService.getUserByEmail(email);

        if (currentUser == null) {
            throw new Exception("User with email: " + email + " doesn't exist");
        }

        UserGetAccount userGetAccount = new UserGetAccount();
        ResponseUserLogin resUserLogin = new ResponseUserLogin();
        resUserLogin.setId(currentUser.getId());
        resUserLogin.setEmail(currentUser.getEmail());
        resUserLogin.setName(currentUser.getName());
        resUserLogin.setRole(currentUser.getRole());
        userGetAccount.setUser(resUserLogin);
        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<ResponseLoginDTO> refreshAccessToken(
            @CookieValue(name = "refresh_token", defaultValue = "no-cookie") String refreshToken) throws Exception {
        if (refreshToken.equals("no-cookie")) {
            throw new InvalidIdException("You don't have refresh token in your cookie");
        }

        Jwt decodedToken = securityUtil.validateRefreshToken(refreshToken);
        String email = decodedToken.getSubject();
        User authenticatedUser = userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if (authenticatedUser == null) {
            throw new InvalidIdException("Refresh token is not valid");
        }

        ResponseLoginDTO resLogin = new ResponseLoginDTO();
        ResponseUserLogin resUserLogin = new ResponseLoginDTO.ResponseUserLogin();

        resUserLogin.setId(authenticatedUser.getId());
        resUserLogin.setEmail(authenticatedUser.getEmail());
        resUserLogin.setName(authenticatedUser.getName());
        resUserLogin.setRole(authenticatedUser.getRole());
        resLogin.setUser(resUserLogin);

        // Create token
        String accessToken = securityUtil.createAccessToken(email, resLogin);

        resLogin.setAccessToken(accessToken);

        String newRefreshToken = securityUtil.createRefreshToken(email, resLogin);
        userService.updateRefreshToken(email, newRefreshToken);
        ResponseCookie resCookie = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                .body(resLogin);
    }

    @SuppressWarnings("null")
    @PostMapping("/auth/logout")
    @ApiMessage("Logged out user")
    public ResponseEntity<Void> logout() throws InvalidIdException {
        ResponseCookie deleteCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        Optional<String> emailOptional = SecurityUtil.getCurrentUserLogin();
        String email = emailOptional.isPresent() ? emailOptional.get() : "";
        User currentUser = userService.getUserByEmail(email);

        if (currentUser == null) {
            throw new InvalidIdException("Can't logged out due to some authentication errors");
        }

        currentUser.setRefreshToken(null);
        userService.handleSaveUser(currentUser);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }
}
