package vn.giaihung.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import vn.giaihung.jobhunter.domain.User;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO;
import vn.giaihung.jobhunter.domain.dto.response.user.CreateUserResDTO;
import vn.giaihung.jobhunter.domain.dto.response.user.GetUserResDTO;
import vn.giaihung.jobhunter.domain.dto.response.user.UpdateUserResDTO;
import vn.giaihung.jobhunter.service.UserService;
import vn.giaihung.jobhunter.utils.annotation.ApiMessage;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@RestController
@RequestMapping("/api/v1")
// For swagger display info - optional
@Tag(name = "User", description = "User management APIs")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Get single user")
    @Operation(summary = "Get single user", description = "Get User object by using user id")
    public ResponseEntity<GetUserResDTO> getUser(@PathVariable("id") long userId) throws InvalidIdException {
        GetUserResDTO user = userService.handleGetUser(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<PageResultDTO> getAllUsers(
            @Filter Specification<User> userSpecs,
            Pageable pageable) {
        PageResultDTO allUsers = userService.getAllUsers(userSpecs, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(allUsers);
    }

    @PostMapping("/users")
    @ApiMessage("Create user")
    public ResponseEntity<CreateUserResDTO> createUser(@Valid @RequestBody User postUser) throws InvalidIdException {
        boolean isEmailExists = userService.validateUserExists(postUser.getEmail());
        if (isEmailExists) {
            throw new InvalidIdException("Email " + postUser.getEmail() + "already exists, please use another email.");
        }
        CreateUserResDTO newUser = userService.handleCreateUser(postUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/users")
    @ApiMessage("Update user")
    public ResponseEntity<UpdateUserResDTO> updateUser(@RequestBody User updateUser) throws InvalidIdException {
        UpdateUserResDTO updatedUser = userService.handleUpdateUser(updateUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long userId) throws InvalidIdException {
        userService.handleDeleteUser(userId);
        return ResponseEntity.ok().body(null);
    }
}
