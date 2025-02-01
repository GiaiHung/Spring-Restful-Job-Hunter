package vn.giaihung.jobhunter.controller;

import java.util.List;
import java.util.Optional;

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
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;
import vn.giaihung.jobhunter.domain.Company;
import vn.giaihung.jobhunter.domain.Job;
import vn.giaihung.jobhunter.domain.Resume;
import vn.giaihung.jobhunter.domain.User;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO;
import vn.giaihung.jobhunter.domain.dto.response.resume.ResumeCreateResDTO;
import vn.giaihung.jobhunter.domain.dto.response.resume.ResumeGetResDTO;
import vn.giaihung.jobhunter.repository.ResumeRepository;
import vn.giaihung.jobhunter.service.ResumeService;
import vn.giaihung.jobhunter.service.UserService;
import vn.giaihung.jobhunter.utils.SecurityUtil;
import vn.giaihung.jobhunter.utils.annotation.ApiMessage;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final ResumeRepository resumeRepository;
    private final UserService userService;

    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(
            ResumeService resumeService,
            ResumeRepository resumeRepository,
            UserService userService,
            FilterBuilder filterBuilder,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.resumeRepository = resumeRepository;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @SuppressWarnings("null")
    @GetMapping("/resumes")
    @ApiMessage("Get all resumes")
    public ResponseEntity<PageResultDTO> getAllResumes(@Filter Specification<Resume> spec, Pageable pageable) {
        List<Long> jobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = userService.getUserByEmail(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    jobIds = companyJobs
                            .stream()
                            .map(job -> job.getId())
                            .toList();
                }
            }
        }

        Specification<Resume> userResumeSpec = filterSpecificationConverter.convert(
                filterBuilder.field("job").in(filterBuilder.input(jobIds)).get());
        Specification<Resume> finalSpec = userResumeSpec.and(spec);

        PageResultDTO dtos = resumeService.handleGetAllResumes(finalSpec, pageable);
        return ResponseEntity.ok().body(dtos);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get single resume")
    public ResponseEntity<ResumeGetResDTO> getResume(@PathVariable("id") long resumeId)
            throws InvalidIdException {
        ResumeGetResDTO dto = resumeService.handleGetResume(resumeId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get resume by user")
    public ResponseEntity<PageResultDTO> getResumesByUser(Pageable pageable) {
        PageResultDTO pageResultDTO = resumeService.handleGetResumesByUser(pageable);
        return ResponseEntity.ok().body(pageResultDTO);
    }

    @PostMapping("/resumes")
    @ApiMessage("Create resume")
    public ResponseEntity<ResumeCreateResDTO> createResume(@Valid @RequestBody Resume postResume)
            throws InvalidIdException {
        ResumeCreateResDTO dto = resumeService.handleCreateResume(postResume);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/resumes")
    @ApiMessage("Update resume status")
    public ResponseEntity<Resume> updateResume(@RequestBody Resume putResume) throws InvalidIdException {
        Optional<Resume> resumeOptional = resumeRepository.findById(putResume.getId());
        if (!resumeOptional.isPresent()) {
            throw new InvalidIdException("Resume with id: " + putResume.getId() + " not found");
        }

        Resume updatedResume = resumeOptional.get();
        updatedResume.setStatus(putResume.getStatus());
        resumeRepository.save(updatedResume);

        return ResponseEntity.ok(updatedResume);
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long resumeId) throws InvalidIdException {
        if (!resumeRepository.existsById(resumeId)) {
            throw new InvalidIdException("Resume with id: " + resumeId + " doesn't exist");
        }
        resumeRepository.deleteById(resumeId);

        return ResponseEntity.ok(null);
    }
}
