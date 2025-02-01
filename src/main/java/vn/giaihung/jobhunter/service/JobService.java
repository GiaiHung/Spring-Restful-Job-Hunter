package vn.giaihung.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.giaihung.jobhunter.domain.Company;
import vn.giaihung.jobhunter.domain.Job;
import vn.giaihung.jobhunter.domain.Skill;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO.Meta;
import vn.giaihung.jobhunter.domain.dto.response.company.CompanyResDTO;
import vn.giaihung.jobhunter.domain.dto.response.job.JobCreateResDTO;
import vn.giaihung.jobhunter.domain.dto.response.job.JobUpdateResDTO;
import vn.giaihung.jobhunter.repository.CompanyRepository;
import vn.giaihung.jobhunter.repository.JobRepository;
import vn.giaihung.jobhunter.repository.SkillRepository;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public Job handleGetJob(long jobId) throws InvalidIdException {
        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if (!jobOptional.isPresent()) {
            throw new InvalidIdException("Job with id: " + jobId + " doesn't exist");
        }
        return jobOptional.get();
    }

    public PageResultDTO handleGetAllJobs(Specification<Job> specification, Pageable pageable) {
        PageResultDTO pageResultDTO = new PageResultDTO();
        Meta meta = new Meta();

        Page<Job> jobPage = jobRepository.findAll(specification, pageable);
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(jobPage.getTotalPages());
        meta.setTotal(jobPage.getTotalElements());

        pageResultDTO.setResult(jobPage.getContent());
        pageResultDTO.setMeta(meta);

        return pageResultDTO;
    }

    public JobCreateResDTO handleCreateJob(Job job) {
        // Check skills of job sent from browser exists in db
        if (job.getSkills() != null) {
            List<Long> skillIds = job.getSkills()
                    .stream()
                    .map(jobMapping -> jobMapping.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = skillRepository.findByIdIn(skillIds);
            job.setSkills(dbSkills);
        }
        Job newJob = jobRepository.save(job);
        JobCreateResDTO dto = new JobCreateResDTO();
        dto.setId(newJob.getId());
        dto.setName(newJob.getName());
        dto.setSalary(newJob.getSalary());
        dto.setQuantity(newJob.getQuantity());
        dto.setLocation(newJob.getLocation());
        dto.setLevel(newJob.getLevel());
        dto.setStartDate(newJob.getStartDate());
        dto.setEndDate(newJob.getEndDate());
        dto.setActive(newJob.isActive());
        dto.setCreatedAt(newJob.getCreatedAt());
        dto.setCreatedBy(newJob.getCreatedBy());

        if (newJob.getSkills() != null) {
            List<String> skills = newJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;
    }

    public JobUpdateResDTO handleUpdateJob(Job job) throws InvalidIdException {
        Optional<Job> jobOptional = jobRepository.findById(job.getId());
        if (!jobOptional.isPresent()) {
            throw new InvalidIdException("Job with id: " + job.getId() + " doesn't exist");
        }

        Job currentJob = jobOptional.get();

        // Update skills
        if (job.getSkills() != null) {
            List<Long> skillIds = currentJob.getSkills().stream()
                    .map(s -> s.getId())
                    .toList();
            List<Skill> dbSkills = skillRepository.findByIdIn(skillIds);
            currentJob.setSkills(dbSkills);
        }

        // Update companies
        if (job.getCompany() != null) {
            Company company = companyRepository.findById(job.getCompany().getId());
            if (company != null) {
                job.setCompany(company);
            }
        }

        // Other fields
        currentJob.setName(job.getName());
        currentJob.setSalary(job.getSalary());
        currentJob.setQuantity(job.getQuantity());
        currentJob.setLocation(job.getLocation());
        currentJob.setLevel(job.getLevel());
        currentJob.setStartDate(job.getStartDate());
        currentJob.setEndDate(job.getEndDate());
        currentJob.setActive(job.isActive());

        jobRepository.save(currentJob);

        JobUpdateResDTO dto = new JobUpdateResDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skillNames = currentJob.getSkills().stream()
                    .map(j -> j.getName())
                    .toList();

            dto.setSkills(skillNames);
        }

        if (currentJob.getCompany() != null) {
            dto.setCompany(new CompanyResDTO(
                    currentJob.getCompany().getId(),
                    currentJob.getCompany().getName()));
        }

        return dto;
    }

    public void handleDeleteJob(long jobId) throws InvalidIdException {
        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if (!jobOptional.isPresent()) {
            throw new InvalidIdException("Job with id: " + jobId + " doesn't exist");
        }
        Job currentJob = jobOptional.get();

        jobRepository.deleteById(currentJob.getId());
    }
}
