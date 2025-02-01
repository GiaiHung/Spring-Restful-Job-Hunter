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
import vn.giaihung.jobhunter.domain.Job;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO;
import vn.giaihung.jobhunter.domain.dto.response.job.JobCreateResDTO;
import vn.giaihung.jobhunter.domain.dto.response.job.JobUpdateResDTO;
import vn.giaihung.jobhunter.service.JobService;
import vn.giaihung.jobhunter.utils.annotation.ApiMessage;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getAllJobs(@PathVariable("id") long jobId) throws InvalidIdException {
        return ResponseEntity.status(HttpStatus.OK).body(jobService.handleGetJob(jobId));
    }

    @GetMapping("/jobs")
    public ResponseEntity<PageResultDTO> getAllJobs(@Filter Specification<Job> specification, Pageable pageable) {
        PageResultDTO pageResultDTO = jobService.handleGetAllJobs(specification, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(pageResultDTO);
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<JobCreateResDTO> createJob(@Valid @RequestBody Job job) {
        JobCreateResDTO dto = jobService.handleCreateJob(job);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<JobUpdateResDTO> updateJob(@Valid @RequestBody Job job) throws InvalidIdException {
        JobUpdateResDTO dto = jobService.handleUpdateJob(job);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") long jobId) throws InvalidIdException {
        jobService.handleDeleteJob(jobId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
