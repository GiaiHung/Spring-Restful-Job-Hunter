package vn.giaihung.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import vn.giaihung.jobhunter.domain.Job;
import vn.giaihung.jobhunter.domain.Resume;
import vn.giaihung.jobhunter.domain.User;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO.Meta;
import vn.giaihung.jobhunter.domain.dto.response.resume.ResumeCreateResDTO;
import vn.giaihung.jobhunter.domain.dto.response.resume.ResumeGetResDTO;
import vn.giaihung.jobhunter.repository.JobRepository;
import vn.giaihung.jobhunter.repository.ResumeRepository;
import vn.giaihung.jobhunter.repository.UserRepository;
import vn.giaihung.jobhunter.service.serviceHelper.ResumeHelper;
import vn.giaihung.jobhunter.utils.SecurityUtil;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ResumeHelper resumeHelper;

    // Turkraft query builder
    @Autowired
    FilterBuilder fb;
    @Autowired
    private FilterParser filterParser;
    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    public ResumeService(ResumeRepository resumeRepository, JobRepository jobRepository,
            UserRepository userRepository, ResumeHelper resumeHelper) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.resumeHelper = resumeHelper;
    }

    public PageResultDTO handleGetAllResumes(@Filter Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageResume = resumeRepository.findAll(spec, pageable);
        PageResultDTO pageResultDTO = new PageResultDTO();
        PageResultDTO.Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageResume.getTotalPages());
        meta.setTotal(pageResume.getTotalElements());

        pageResultDTO.setMeta(meta);
        List<ResumeGetResDTO> dtos = pageResume.getContent().stream()
                .map(resume -> resumeHelper.convertResumeToDTO(resume))
                .toList();
        pageResultDTO.setResult(dtos);

        return pageResultDTO;
    }

    public ResumeGetResDTO handleGetResume(long resumeId) throws InvalidIdException {
        Optional<Resume> resumeOptional = resumeRepository.findById(resumeId);
        if (!resumeOptional.isPresent()) {
            throw new InvalidIdException("Resume with id: " + resumeId + " not found");
        }
        Resume resume = resumeOptional.get();
        ResumeGetResDTO resumeGetResDTO = resumeHelper.convertResumeToDTO(resume);

        return resumeGetResDTO;
    }

    public PageResultDTO handleGetResumesByUser(Pageable pageable) {
        Optional<String> emailUserLoggedInOptional = SecurityUtil.getCurrentUserLogin();
        String emailUserLoggedIn = emailUserLoggedInOptional.isPresent() ? emailUserLoggedInOptional.get() : "";
        FilterNode node = filterParser.parse("email='" + emailUserLoggedIn + "'");
        FilterSpecification<Resume> specification = filterSpecificationConverter.convert(node);
        Page<Resume> pageResume = resumeRepository.findAll(specification, pageable);

        PageResultDTO rs = new PageResultDTO();
        PageResultDTO.Meta mt = new PageResultDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResumeGetResDTO> listResume = pageResume.getContent()
                .stream()
                .map(item -> {
                    try {
                        return handleGetResume(item.getId());
                    } catch (InvalidIdException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .collect(Collectors.toList());

        rs.setResult(listResume);

        return rs;
    }

    public ResumeCreateResDTO handleCreateResume(Resume postResume) throws InvalidIdException {
        // Check job exists
        Optional<Job> resumeJob = jobRepository.findById(postResume.getJob().getId());
        if (!resumeJob.isPresent()) {
            throw new InvalidIdException(
                    "Can't create resume due to job with id: " + postResume.getJob().getId() + " doesn't exist");
        }

        // Check user exists
        User resumeUser = userRepository.findById(postResume.getUser().getId());
        if (resumeUser == null) {
            throw new InvalidIdException(
                    "Can't create resume due to user with id: " + postResume.getUser().getId() + " doesn't exist");
        }

        Resume newResume = resumeRepository.save(postResume);

        ResumeCreateResDTO resumeCreateResDTO = new ResumeCreateResDTO();
        resumeCreateResDTO.setId(newResume.getId());
        resumeCreateResDTO.setCreatedBy(newResume.getCreatedBy());
        resumeCreateResDTO.setCreatedAt(newResume.getCreatedAt());

        return resumeCreateResDTO;
    }
}
