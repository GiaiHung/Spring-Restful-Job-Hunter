package vn.giaihung.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
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

import jakarta.validation.Valid;
import vn.giaihung.jobhunter.domain.Role;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO;
import vn.giaihung.jobhunter.service.RoleService;
import vn.giaihung.jobhunter.utils.annotation.ApiMessage;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles")
    @ApiMessage("Get all roles")
    public ResponseEntity<PageResultDTO> getRoles(Pageable pageable) {
        PageResultDTO pageResultDTO = roleService.handleGetAllRoles(pageable);
        return ResponseEntity.ok(pageResultDTO);
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id) throws InvalidIdException {
        Optional<Role> roleOptional = roleService.handleGetRole(id);
        if (!roleOptional.isPresent()) {
            throw new InvalidIdException("Role with id: " + id + " doesn't exist");
        }
        return ResponseEntity.ok(roleOptional.get());
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role postRole) throws InvalidIdException {
        Role newPost = roleService.handleCreateRole(postRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPost);
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> updateRole(@RequestBody Role putRole) throws InvalidIdException {
        Role roleUpdated = roleService.handleUpdateRole(putRole);
        return ResponseEntity.ok(roleUpdated);
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws InvalidIdException {
        roleService.handleDeleteRole(id);
        return ResponseEntity.ok().body(null);
    }
}
