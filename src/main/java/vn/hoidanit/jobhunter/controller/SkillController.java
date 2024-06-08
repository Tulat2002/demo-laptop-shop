package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDto;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;


@RestController
@RequestMapping("/api/v1")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create a Skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        //check name
        if (skill.getName() != null && this.skillService.isNameExist(skill.getName())){
            throw new IdInvalidException("Skill name = " + skill.getName() + " da ton tai");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.createSkill(skill));
    }

    @PutMapping("/skills")
    @ApiMessage("Update a Skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        //check id
        Skill currentSkill = this.skillService.fetchSkillById(skill.getId());
        if (currentSkill == null){
            throw new IdInvalidException("Skill id = " + skill.getId() + " not found");
        }
        //check name
        if (skill.getName() != null && this.skillService.isNameExist(skill.getName())){
            throw new IdInvalidException("Skill name = " + skill.getName() + " da ton tai");
        }
        currentSkill.setName(skill.getName());
        return ResponseEntity.ok().body(this.skillService.updateSkill(currentSkill));
    }

    @GetMapping("/skills")
    @ApiMessage("Fetch All Skill")
    public ResponseEntity<ResultPaginationDto> getAllSkills(
            @Filter Specification<Skill> specification, Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.fetchAllSkills(specification, pageable));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a Skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvalidException {
        //check id
        Skill currentSkill = this.skillService.fetchSkillById(id);
        if (currentSkill == null){
            throw new IdInvalidException("Skill id = " + id + " not found");
        }
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok().body(null);
    }

}
