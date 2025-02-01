package vn.giaihung.jobhunter.domain;

import java.time.Instant;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.giaihung.jobhunter.domain.listener.CompanyPersistListener;

@Entity
@EntityListeners(CompanyPersistListener.class)
@Table(name = "companies")
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Company name can't be empty")
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private String address;

    private String logo;

    // Let frontend auto format
    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Job> jobs;

    // @PrePersist
    // public void handleCreatedAt() {
    // Optional<String> currentUserLogin = SecurityUtil.getCurrentUserLogin();
    // String username = currentUserLogin.isPresent() ? currentUserLogin.get() :
    // "anonymous";
    // this.createdBy = username;
    // this.createdAt = Instant.now();
    // }
}
