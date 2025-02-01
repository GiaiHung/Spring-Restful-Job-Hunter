package vn.giaihung.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.giaihung.jobhunter.domain.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    public Skill findByName(String name);

    public boolean existsByName(String name);

    public List<Skill> findByIdIn(List<Long> id);
}
