package vn.giaihung.jobhunter.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import vn.giaihung.jobhunter.utils.SecurityUtil;
import vn.giaihung.jobhunter.utils.customEnums.GenderEnum;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "User name can't be empty")
    private String name;

    @NotBlank(message = "Email can't be empty")
    private String email;

    @Size(min = 6, message = "Password can't be less than 6 characters")
    private String password;
    private int age;
    private String address;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Resume> resumes;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @PrePersist
    public void handleBeforeCreate() {
        Optional<String> currentUserLogin = SecurityUtil.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            String username = currentUserLogin.get();
            setCreatedAt(Instant.now());
            setCreatedBy(username);
        }
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        Optional<String> currentUserLogin = SecurityUtil.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            String username = currentUserLogin.get();
            setUpdatedAt(Instant.now());
            setUpdatedBy(username);
        }
    }
}
