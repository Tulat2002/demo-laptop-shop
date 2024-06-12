package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDto;
import vn.hoidanit.jobhunter.repository.SkillRepository;

import java.util.Optional;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill createSkill(Skill skill){
        return this.skillRepository.save(skill);
    }

    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill fetchSkillById(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if (skillOptional.isPresent())
            return skillOptional.get();
        return null;
    }

    public Skill updateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public ResultPaginationDto fetchAllSkills(Specification<Skill> specification, Pageable pageable) {
        Page<Skill> skillPage = this.skillRepository.findAll(specification, pageable);
        ResultPaginationDto rs = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() +1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(skillPage.getTotalPages());
        meta.setTotal(skillPage.getTotalElements());
        rs.setMeta(meta);
        rs.setResult(skillPage.getContent());
        return rs;
    }

    public void deleteSkill(long id) {
        //delete skill in jobs inside job_skill table
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        //delete Subscriber
        currentSkill.getSubscribers().forEach(subs -> subs.getSkills().remove(currentSkill));

        //delete skill
        this.skillRepository.delete(currentSkill);
    }

}
