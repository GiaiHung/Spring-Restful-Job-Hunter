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
import vn.giaihung.jobhunter.domain.Permission;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO;
import vn.giaihung.jobhunter.service.PermissionService;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/permissions")
    public ResponseEntity<PageResultDTO> getAllPermissions(Pageable pageable) {
        PageResultDTO pageResultDTO = permissionService.handleGetAllPermissions(pageable);
        return ResponseEntity.ok().body(pageResultDTO);
    }

    @GetMapping("/permissions/{id}")
    public ResponseEntity<Permission> getPermission(@PathVariable("id") long permissionId) throws InvalidIdException {
        Optional<Permission> permissionOptional = permissionService.handleGetPermission(permissionId);
        if (!permissionOptional.isPresent()) {
            throw new InvalidIdException("Permission with id: " + permissionId + " doesn't exist");
        }
        return ResponseEntity.ok().body(permissionOptional.get());
    }

    @PostMapping("/permissions")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission postPermission)
            throws InvalidIdException {
        Permission permission = permissionService.handleCreatePermission(postPermission);
        return ResponseEntity.status(HttpStatus.CREATED).body(permission);
    }

    @PutMapping("/permissions")
    public ResponseEntity<Permission> updatePermission(@RequestBody Permission putPermission)
            throws InvalidIdException {
        Permission updatedPermission = permissionService.handleUpdatePermission(putPermission);
        return ResponseEntity.ok().body(updatedPermission);
    }

    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws InvalidIdException {
        permissionService.handleDeletePermission(id);
        return ResponseEntity.ok().body(null);
    }
}
