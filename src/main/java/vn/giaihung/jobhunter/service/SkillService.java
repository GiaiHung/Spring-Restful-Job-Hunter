package vn.giaihung.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.giaihung.jobhunter.domain.Skill;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO.Meta;
import vn.giaihung.jobhunter.repository.SkillRepository;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public PageResultDTO handleGetSkills(Pageable pageable) {
        Page<Skill> pageSkill = skillRepository.findAll(pageable);
        PageResultDTO pageResultDTO = new PageResultDTO();
        PageResultDTO.Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());

        pageResultDTO.setMeta(meta);
        pageResultDTO.setResult(pageSkill.getContent());

        return pageResultDTO;
    }

    public Optional<Skill> handleGetSkill(long skillId) {
        return skillRepository.findById(skillId);
    }

    public Skill handleGetSkillByName(String name) {
        return skillRepository.findByName(name);
    }

    public void handleSaveSkill(Skill skill) {
        skillRepository.save(skill);
    }

    public void handleDeleteSkill(long skillId) throws InvalidIdException {
        Optional<Skill> skillOptional = handleGetSkill(skillId);
        if (!skillOptional.isPresent()) {
            throw new InvalidIdException("Skill with id: " + skillId + " doesn't exist");
        }
        Skill currentSkill = skillOptional.get();

        // Delete data in job_skill table
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        // Delete data in subscriber_skill table
        currentSkill.getSubscribers().forEach(subscriber -> subscriber.getSkills().remove(currentSkill));

        skillRepository.deleteById(skillId);
    }

    public boolean checkSkillByName(String name) {
        return skillRepository.existsByName(name);
    }
}
