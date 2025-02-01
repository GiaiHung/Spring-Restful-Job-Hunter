package vn.giaihung.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.giaihung.jobhunter.domain.Job;
import vn.giaihung.jobhunter.domain.Skill;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    List<Job> findBySkillsIn(List<Skill> skills);
}
