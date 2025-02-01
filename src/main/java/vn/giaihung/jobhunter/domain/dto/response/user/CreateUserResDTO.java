package vn.giaihung.jobhunter.domain.dto.response.user;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.giaihung.jobhunter.domain.dto.response.company.CompanyResDTO;
import vn.giaihung.jobhunter.utils.customEnums.GenderEnum;

@Getter
@Setter
public class CreateUserResDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant createdAt;
    private CompanyResDTO company;
}
