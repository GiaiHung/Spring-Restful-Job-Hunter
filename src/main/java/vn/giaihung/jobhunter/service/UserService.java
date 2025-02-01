package vn.giaihung.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.giaihung.jobhunter.domain.Company;
import vn.giaihung.jobhunter.domain.Role;
import vn.giaihung.jobhunter.domain.User;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO.Meta;
import vn.giaihung.jobhunter.domain.dto.response.user.CreateUserResDTO;
import vn.giaihung.jobhunter.domain.dto.response.user.GetUserResDTO;
import vn.giaihung.jobhunter.domain.dto.response.user.UpdateUserResDTO;
import vn.giaihung.jobhunter.repository.UserRepository;
import vn.giaihung.jobhunter.service.serviceHelper.UserHelper;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserHelper userHelper;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserHelper userHelper,
            CompanyService companyService,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userHelper = userHelper;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public GetUserResDTO handleGetUser(long userId) throws InvalidIdException {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new InvalidIdException("User with id: " + userId + " doesn't exist");
        }
        GetUserResDTO getUserResDTO = userHelper.convertToGetResUserDTO(user);
        return getUserResDTO;
    }

    public User getUserById(long id) {
        return userRepository.findById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

    public PageResultDTO getAllUsers(Specification<User> userSpecs, Pageable pageable) {
        Page<User> pageUser = userRepository.findAll(userSpecs, pageable);
        PageResultDTO pageResultDTO = new PageResultDTO();

        PageResultDTO.Meta Meta = new Meta();
        Meta.setPage(pageable.getPageNumber() + 1);
        Meta.setPageSize(pageable.getPageSize());
        Meta.setPages(pageUser.getTotalPages());
        Meta.setTotal(pageUser.getTotalElements());

        pageResultDTO.setMeta(Meta);

        List<GetUserResDTO> listUser = pageUser.getContent()
                .stream()
                .map(user -> userHelper.convertToGetResUserDTO(user))
                .toList();
        pageResultDTO.setResult(listUser);
        return pageResultDTO;
    }

    public void handleSaveUser(User user) {
        userRepository.save(user);
    }

    public CreateUserResDTO handleCreateUser(User postUser) {
        String hashedPassword = passwordEncoder.encode(postUser.getPassword());
        postUser.setPassword(hashedPassword);
        if (postUser.getCompany() != null) {
            Company userCompany = companyService.getCompany(postUser.getCompany().getId());
            if (userCompany != null) {
                postUser.setCompany(userCompany);
            }
        }
        if (postUser.getRole() != null) {
            Optional<Role> userRoleOptional = roleService.handleGetRole(postUser.getRole().getId());
            postUser.setRole(userRoleOptional.isPresent() ? userRoleOptional.get() : null);
        }
        userRepository.save(postUser);
        return userHelper.convertToCreateUserResDTO(postUser);
    }

    public UpdateUserResDTO handleUpdateUser(User user) throws InvalidIdException {
        User currentUser = getUserById(user.getId());
        if (currentUser != null) {
            currentUser.setAddress(user.getAddress());
            currentUser.setGender(user.getGender());
            currentUser.setAge(user.getAge());
            currentUser.setName(user.getName());
            if (user.getCompany() != null) {
                Company userCompany = companyService.getCompany(user.getCompany().getId());
                if (userCompany != null) {
                    currentUser.setCompany(userCompany);
                }
            }
            if (user.getRole() != null) {
                Optional<Role> userRoleOptional = roleService.handleGetRole(user.getRole().getId());
                if (userRoleOptional.isPresent()) {
                    currentUser.setRole(userRoleOptional.get());
                }
            }
            userRepository.save(currentUser);
        } else {
            throw new InvalidIdException("User with id: " + user.getId() + " doesn't exist!");
        }
        return userHelper.convertToResUpdateUserDTO(currentUser);
    }

    public void handleDeleteUser(long userId) throws InvalidIdException {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new InvalidIdException("User with id: " + userId + " doesn't exist!");
        }
        userRepository.deleteById(userId);
    }

    public boolean validateUserExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void updateRefreshToken(String email, String refreshToken) throws Exception {
        User currentUser = userRepository.findByEmail(email);

        if (currentUser == null) {
            throw new Exception("User with email: " + email + " doesn't exist!");
        }

        currentUser.setRefreshToken(refreshToken);
        userRepository.save(currentUser);
    }

}
