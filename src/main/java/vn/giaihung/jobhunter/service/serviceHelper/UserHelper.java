package vn.giaihung.jobhunter.service.serviceHelper;

import org.springframework.stereotype.Service;

import vn.giaihung.jobhunter.domain.User;
import vn.giaihung.jobhunter.domain.dto.response.company.CompanyResDTO;
import vn.giaihung.jobhunter.domain.dto.response.role.RoleGetRestDTO;
import vn.giaihung.jobhunter.domain.dto.response.user.CreateUserResDTO;
import vn.giaihung.jobhunter.domain.dto.response.user.GetUserResDTO;
import vn.giaihung.jobhunter.domain.dto.response.user.UpdateUserResDTO;

@Service
public class UserHelper {
    public CreateUserResDTO convertToCreateUserResDTO(User user) {
        CreateUserResDTO res = new CreateUserResDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        if (user.getCompany() != null) {
            res.setCompany(new CompanyResDTO(user.getCompany().getId(), user.getCompany().getName()));
        }
        return res;
    }

    public UpdateUserResDTO convertToResUpdateUserDTO(User user) {
        UpdateUserResDTO res = new UpdateUserResDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        if (user.getCompany() != null) {
            res.setCompany(new CompanyResDTO(user.getCompany().getId(), user.getCompany().getName()));
        }
        return res;
    }

    public GetUserResDTO convertToGetResUserDTO(User user) {
        GetUserResDTO res = new GetUserResDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        if (user.getCompany() != null) {
            res.setCompany(new CompanyResDTO(user.getCompany().getId(), user.getCompany().getName()));
        }
        if (user.getRole() != null) {
            res.setRole(new RoleGetRestDTO(user.getRole().getId(), user.getRole().getName()));
        }
        return res;
    }
}
