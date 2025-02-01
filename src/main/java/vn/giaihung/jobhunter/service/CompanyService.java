package vn.giaihung.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.giaihung.jobhunter.domain.Company;
import vn.giaihung.jobhunter.domain.User;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO.Meta;
import vn.giaihung.jobhunter.repository.CompanyRepository;
import vn.giaihung.jobhunter.repository.UserRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleSaveCompany(Company company) {
        return companyRepository.save(company);
    }

    public Company handleCreateCompany(Company postCompany) {
        Company company = new Company();
        company.setName(postCompany.getName());
        company.setDescription(postCompany.getDescription());
        company.setAddress(postCompany.getAddress());
        company.setLogo(postCompany.getLogo());
        return companyRepository.save(company);
    }

    public Company handleUpdateCompany(Company putCompany) {
        Company currentCompany = getCompany(putCompany.getId());
        if (currentCompany != null) {
            currentCompany.setName(putCompany.getName());
            currentCompany.setDescription(putCompany.getDescription());
            currentCompany.setAddress(putCompany.getAddress());
            currentCompany.setLogo(putCompany.getLogo());
            currentCompany = handleSaveCompany(currentCompany);
        }
        return currentCompany;
    }

    public PageResultDTO getCompanies(Specification<Company> companySpecs, Pageable pageable) {
        Page<Company> pageCompany = companyRepository.findAll(companySpecs, pageable);
        PageResultDTO pageResultDTO = new PageResultDTO();
        PageResultDTO.Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());

        pageResultDTO.setMeta(meta);
        pageResultDTO.setResult(pageCompany.getContent());

        return pageResultDTO;
    }

    public Company getCompany(long id) {
        return companyRepository.findById(id);
    }

    public void handleDeleteCompany(long id) throws Exception {
        Company deletedCompany = companyRepository.findById(id);
        if (deletedCompany == null) {
            throw new Exception("Company with id " + id + " is not found");
        }

        List<User> users = deletedCompany.getUsers();
        for (User user : users) {
            userRepository.deleteById(user.getId());
        }
        companyRepository.deleteById(id);
    }
}
