package vn.giaihung.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.giaihung.jobhunter.domain.Permission;
import vn.giaihung.jobhunter.domain.Role;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO.Meta;
import vn.giaihung.jobhunter.repository.PermissionRepository;
import vn.giaihung.jobhunter.repository.RoleRepository;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Optional<Role> handleGetRole(long id) {
        return roleRepository.findById(id);
    }

    public PageResultDTO handleGetAllRoles(Pageable pageable) {
        Page<Role> pageRole = roleRepository.findAll(pageable);
        PageResultDTO pageResultDTO = new PageResultDTO();
        PageResultDTO.Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageRole.getTotalPages());
        meta.setTotal(pageRole.getTotalElements());

        pageResultDTO.setMeta(meta);
        pageResultDTO.setResult(pageRole.getContent());

        return pageResultDTO;
    }

    public Role handleCreateRole(Role postRole) throws InvalidIdException {
        // Check by name
        if (roleRepository.existsByName(postRole.getName())) {
            throw new InvalidIdException("Role with name: " + postRole.getName() + " already exists");
        }

        // Check permissions exists
        if (postRole.getPermissions() != null) {
            List<Long> permissionIds = postRole.getPermissions().stream()
                    .map(role -> role.getId())
                    .toList();
            List<Permission> dbPermissions = permissionRepository.findByIdIn(permissionIds);
            postRole.setPermissions(dbPermissions);
        }

        // Create Role
        return roleRepository.save(postRole);
    }

    public Role handleUpdateRole(Role putRole) throws InvalidIdException {
        Optional<Role> roleOptional = handleGetRole(putRole.getId());
        if (!roleOptional.isPresent()) {
            throw new InvalidIdException("Role with id: " + putRole.getId() + " doesn't exist");
        }

        Role dbRole = roleOptional.get();
        if (putRole.getPermissions() != null) {
            List<Long> permissionIds = putRole.getPermissions().stream()
                    .map(p -> p.getId())
                    .toList();

            List<Permission> validPermissions = permissionRepository.findByIdIn(permissionIds);
            putRole.setPermissions(validPermissions);
            dbRole.setPermissions(putRole.getPermissions());
        }

        dbRole.setName(putRole.getName());
        dbRole.setDescription(putRole.getDescription());
        dbRole.setActive(putRole.isActive());

        return roleRepository.save(dbRole);
    }

    public void handleDeleteRole(long id) throws InvalidIdException {
        if (!handleGetRole(id).isPresent()) {
            throw new InvalidIdException("Role with id: " + id + " doesn't exist");
        }
        roleRepository.deleteById(id);
    }
}
