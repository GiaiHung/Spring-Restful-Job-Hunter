package vn.giaihung.jobhunter.domain.dto.response.job;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import vn.giaihung.jobhunter.domain.dto.response.company.CompanyResDTO;
import vn.giaihung.jobhunter.utils.customEnums.LevelEnum;

@Getter
@Setter
public class JobUpdateResDTO {
    private long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private CompanyResDTO company;
    private LevelEnum level;
    private Instant startDate;
    private Instant endDate;
    private boolean isActive;
    private List<String> skills;
    private Instant updatedAt;
    private String updatedBy;
}
