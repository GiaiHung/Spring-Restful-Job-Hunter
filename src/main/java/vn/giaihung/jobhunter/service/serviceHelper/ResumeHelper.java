package vn.giaihung.jobhunter.service.serviceHelper;

import org.springframework.stereotype.Service;

import vn.giaihung.jobhunter.domain.Resume;
import vn.giaihung.jobhunter.domain.dto.response.resume.ResumeGetResDTO;

@Service
public class ResumeHelper {
    public ResumeGetResDTO convertResumeToDTO(Resume resume) {
        ResumeGetResDTO resumeGetResDTO = new ResumeGetResDTO();
        resumeGetResDTO.setId(resume.getId());
        resumeGetResDTO.setEmail(resume.getEmail());
        resumeGetResDTO.setUrl(resume.getUrl());
        resumeGetResDTO.setStatus(resume.getStatus());
        resumeGetResDTO.setCreatedAt(resume.getCreatedAt());
        resumeGetResDTO.setCreatedBy(resume.getCreatedBy());
        resumeGetResDTO.setUpdatedAt(resume.getUpdatedAt());
        resumeGetResDTO.setUpdatedBy(resume.getUpdatedBy());
        resumeGetResDTO.setCompanyName(resume.getJob().getCompany().getName());
        resumeGetResDTO
                .setUser(new ResumeGetResDTO.ResumeUserDTO(resume.getUser().getId(), resume.getUser().getName()));
        resumeGetResDTO.setJob(new ResumeGetResDTO.ResumeJobDTO(resume.getJob().getId(), resume.getJob().getName()));

        return resumeGetResDTO;
    }
}
