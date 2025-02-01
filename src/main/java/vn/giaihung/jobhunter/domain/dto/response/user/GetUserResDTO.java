package vn.giaihung.jobhunter.domain.dto.response.user;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.giaihung.jobhunter.domain.dto.response.company.CompanyResDTO;
import vn.giaihung.jobhunter.domain.dto.response.role.RoleGetRestDTO;
import vn.giaihung.jobhunter.utils.customEnums.GenderEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserResDTO {
    private long id;
    private String email;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;
    private Instant createdAt;
    private CompanyResDTO company;
    private RoleGetRestDTO role;
}
