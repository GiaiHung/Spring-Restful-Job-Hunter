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

import jakarta.validation.Valid;
import vn.giaihung.jobhunter.domain.Company;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO;
import vn.giaihung.jobhunter.service.CompanyService;
import vn.giaihung.jobhunter.utils.annotation.ApiMessage;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company postCompany) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                companyService.handleCreateCompany(postCompany));
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch all companies")
    public ResponseEntity<PageResultDTO> getCompanies(
            @Filter Specification<Company> companySpecs,
            Pageable pageable) {
        PageResultDTO companiesPage = companyService.getCompanies(companySpecs, pageable);
        return ResponseEntity.ok(companiesPage);
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompany(@PathVariable("id") long companyId) throws InvalidIdException {
        Company company = companyService.getCompany(companyId);
        if (company == null) {
            throw new InvalidIdException("Company with id: " + companyId + " doesn't exist");
        }
        return ResponseEntity.ok().body(company);
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company putCompany) {
        Company currentCompany = companyService.handleUpdateCompany(putCompany);
        if (currentCompany != null) {
            return ResponseEntity.ok(currentCompany);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) throws Exception {
        Company company = companyService.getCompany(id);
        if (company == null) {
            throw new InvalidIdException("Skill with id: " + id + " doesn't exist");
        }
        companyService.handleDeleteCompany(id);

        return ResponseEntity.ok(null);
    }
}
