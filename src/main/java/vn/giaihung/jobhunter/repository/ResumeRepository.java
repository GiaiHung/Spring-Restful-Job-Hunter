package vn.giaihung.jobhunter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.giaihung.jobhunter.domain.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long>, JpaSpecificationExecutor<Resume> {
    @SuppressWarnings("null")
    public Page<Resume> findAll(Pageable pageable);

    @SuppressWarnings({ "null", "rawtypes" })
    public Page<Resume> findAll(Specification specification, Pageable pageable);

    boolean existsById(long id);
}
