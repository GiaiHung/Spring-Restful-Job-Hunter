package vn.giaihung.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.giaihung.jobhunter.domain.Permission;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO.Meta;
import vn.giaihung.jobhunter.repository.PermissionRepository;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission handleSavePermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    public PageResultDTO handleGetAllPermissions(Pageable pageable) {
        Page<Permission> pagePermission = permissionRepository.findAll(pageable);
        PageResultDTO pageResultDTO = new PageResultDTO();
        PageResultDTO.Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pagePermission.getTotalPages());
        meta.setTotal(pagePermission.getTotalElements());

        pageResultDTO.setMeta(meta);
        pageResultDTO.setResult(pagePermission.getContent());

        return pageResultDTO;
    }

    public Optional<Permission> handleGetPermission(long permissionId) {
        return permissionRepository.findById(permissionId);
    }

    public Permission handleCreatePermission(Permission permission) throws InvalidIdException {
        // Validate by API Path, method and module
        if (permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod())) {
            throw new InvalidIdException("Permission already exists");
        }

        // Create permission
        return permissionRepository.save(permission);
    }

    public boolean checkPermissionExists(Permission permission) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod());
    }

    public Permission handleUpdatePermission(Permission putPermission) throws InvalidIdException {
        Optional<Permission> permissionOptional = handleGetPermission(putPermission.getId());
        // Check exists
        if (!permissionOptional.isPresent()) {
            throw new InvalidIdException("Permission with id: " + putPermission.getId() + " doesn't exist");
        }

        // Check after update duplicates with any existing permission
        Permission currentPermission = permissionOptional.get();
        currentPermission.setName(putPermission.getName());
        currentPermission.setApiPath(putPermission.getApiPath());
        currentPermission.setMethod(putPermission.getMethod());
        currentPermission.setModule(putPermission.getModule());

        // In case only update the name will not work
        // if (checkPermissionExists(currentPermission)) {
        // throw new InvalidIdException("Permission already exists");
        // }

        // Update
        return handleSavePermission(currentPermission);
    }

    public void handleDeletePermission(long id) throws InvalidIdException {
        Optional<Permission> permissionOptional = handleGetPermission(id);
        // Check permissions exists
        if (!permissionOptional.isPresent()) {
            throw new InvalidIdException("Permission with id: " + id + " doesn't exist");
        }

        // Delete roles if exist
        Permission permission = permissionOptional.get();
        permission.getRoles().forEach(role -> role.getPermissions().remove(permission));

        // Delete permission
        permissionRepository.deleteById(permission.getId());
    }
}
