package vn.giaihung.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.giaihung.jobhunter.domain.Skill;
import vn.giaihung.jobhunter.domain.dto.response.PageResultDTO;
import vn.giaihung.jobhunter.service.SkillService;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/skills")
    public ResponseEntity<PageResultDTO> getSkills(Pageable pageable) {
        PageResultDTO skillsPage = skillService.handleGetSkills(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(skillsPage);
    }

    @PostMapping("/skills")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws InvalidIdException {
        if (skillService.checkSkillByName(null)) {
            throw new InvalidIdException("Skill with name: '" + skill.getName() + "' already exists");
        }
        Skill newSkill = new Skill();
        newSkill.setName(skill.getName());
        skillService.handleSaveSkill(newSkill);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSkill);
    }

    @PutMapping("/skills")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill updateSkill) throws InvalidIdException {
        Optional<Skill> skillOptional = skillService.handleGetSkill(updateSkill.getId());
        if (!skillOptional.isPresent()) {
            throw new InvalidIdException("Skill with id '" + updateSkill.getId() + "' doesn't exist");
        }

        Skill currentSkill = skillOptional.get();
        currentSkill.setName(updateSkill.getName());
        skillService.handleSaveSkill(currentSkill);

        return ResponseEntity.status(HttpStatus.OK).body(currentSkill);
    }

    @DeleteMapping("/skills/{skillId}")
    public ResponseEntity<Skill> deleteSkill(@PathVariable("skillId") long skillId) throws InvalidIdException {
        skillService.handleDeleteSkill(skillId);

        return ResponseEntity.ok(null);
    }
}
