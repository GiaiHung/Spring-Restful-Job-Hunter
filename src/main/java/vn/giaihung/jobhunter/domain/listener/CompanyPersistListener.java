package vn.giaihung.jobhunter.domain.listener;

import java.time.Instant;
import java.util.Optional;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import vn.giaihung.jobhunter.domain.Company;
import vn.giaihung.jobhunter.utils.SecurityUtil;

public class CompanyPersistListener {
    @PrePersist
    public void prePersist(Company company) {
        Optional<String> currentUserLogin = SecurityUtil.getCurrentUserLogin();
        String username = currentUserLogin.isPresent() ? currentUserLogin.get() : "anonymous";
        company.setCreatedBy(username);
        company.setCreatedAt(Instant.now());
    }

    @PreUpdate
    public void postUpdate(Company company) {
        Optional<String> currentUserLogin = SecurityUtil.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            String username = currentUserLogin.get();
            company.setUpdatedAt(Instant.now());
            company.setUpdatedBy(username);
        }
    }
}
