package vn.giaihung.jobhunter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.giaihung.jobhunter.domain.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    @SuppressWarnings({ "null", "unchecked" })
    public Company save(Company company);

    @SuppressWarnings("null")
    public Page<Company> findAll(Pageable pageable);

    public Company findById(long id);
}
