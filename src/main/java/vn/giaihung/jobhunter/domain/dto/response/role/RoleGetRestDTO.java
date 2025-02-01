package vn.giaihung.jobhunter.domain.dto.response.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleGetRestDTO {
    private long id;
    private String name;
}
