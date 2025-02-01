package vn.giaihung.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.giaihung.jobhunter.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    public User findById(long id);

    public User findByEmail(String email);

    @SuppressWarnings({ "null", "unchecked" })
    public User save(User user);

    public boolean existsByEmail(String email);

    public User findByRefreshTokenAndEmail(String refreshToken, String email);
}
