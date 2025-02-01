package vn.giaihung.jobhunter.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.giaihung.jobhunter.domain.Role;

@Getter
@Setter
public class ResponseLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;
    private ResponseUserLogin user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseUserLogin {
        private long id;
        private String email;
        private String name;
        private Role role;
    }

    // Frontend needs user dto wrapped inside user field
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserGetAccount {
        private ResponseUserLogin user;
    }

    // We don't store role in session
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInsideToken {
        private long id;
        private String email;
        private String name;
    }
}
