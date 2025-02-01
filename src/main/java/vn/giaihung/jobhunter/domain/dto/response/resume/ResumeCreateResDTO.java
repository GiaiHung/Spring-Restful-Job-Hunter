package vn.giaihung.jobhunter.domain.dto.response.resume;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResumeCreateResDTO {
    private long id;
    private String createdBy;
    private Instant createdAt;
}
