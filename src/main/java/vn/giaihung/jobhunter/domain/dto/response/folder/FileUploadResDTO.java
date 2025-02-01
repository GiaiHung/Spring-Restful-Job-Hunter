package vn.giaihung.jobhunter.domain.dto.response.folder;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResDTO {
    private String fileName;
    private Instant uploadedAt;
}
