package vn.giaihung.jobhunter.domain.dto.response.resume;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.giaihung.jobhunter.utils.customEnums.ResumeStatusEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResumeGetResDTO {
    private long id;
    private String email;
    private String url;
    @Enumerated(EnumType.STRING)
    private ResumeStatusEnum status;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private String companyName;
    private ResumeUserDTO user;
    private ResumeJobDTO job;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ResumeUserDTO {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ResumeJobDTO {
        private long id;
        private String name;
    }

}
