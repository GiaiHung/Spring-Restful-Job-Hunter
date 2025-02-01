package vn.giaihung.jobhunter.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.giaihung.jobhunter.domain.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    @SuppressWarnings("null")
    Page<Permission> findAll(Pageable pageable);

    boolean existsByModuleAndApiPathAndMethod(String module, String apiPath, String method);

    boolean existsById(long id);

    List<Permission> findByIdIn(List<Long> ids);
}
