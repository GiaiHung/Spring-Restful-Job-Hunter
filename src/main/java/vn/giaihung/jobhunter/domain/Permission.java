package vn.giaihung.jobhunter.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.giaihung.jobhunter.utils.SecurityUtil;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Permission name must not be empty")
    private String name;

    @NotBlank(message = "API path must not be empty")
    private String apiPath;

    @NotBlank(message = "Method must not be empty")
    private String method;

    @NotBlank(message = "Module must not be empty")
    private String module;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Role> roles;

    public Permission(String name, String apiPath, String method, String module) {
        this.name = name;
        this.apiPath = apiPath;
        this.method = method;
        this.module = module;
    }

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
