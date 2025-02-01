package vn.giaihung.jobhunter.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDTO {
    @NotBlank(message = "Username (Email) can't be empty")
    private String username;

    @NotBlank(message = "Password can't be empty")
    private String password;
}
