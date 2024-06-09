package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResCreateResumeDto;
import vn.hoidanit.jobhunter.domain.response.ResFetchResumeDto;
import vn.hoidanit.jobhunter.domain.response.ResUpdateResumeDto;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDto;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDto> createResume(@Valid @RequestBody Resume resume) throws IdInvalidException {
        //check id exits
        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(resume);
        if (!isIdExist) {
            throw new IdInvalidException("User id/job khong ton tai");
        }
        //create new resume
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.createResume(resume));
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Fetch a resume with id")
    public ResponseEntity<ResFetchResumeDto> getResumeById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.fetchById(id);
        if (resumeOptional.isEmpty()){
            throw new IdInvalidException("Resume id not found");
        }
        return ResponseEntity.ok().body(this.resumeService.getResume(resumeOptional.get()));
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch All Resume With Paginate")
    public ResponseEntity<ResultPaginationDto> getAllResumes(
            @Filter Specification<Resume> specification, Pageable pageable)
    {
        return ResponseEntity.ok().body(this.resumeService.fetchAllResume(specification, pageable));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDto> updateResume(@RequestBody Resume resume) throws IdInvalidException {
        //check id exist
        Optional<Resume> resumeOptional = this.resumeService.fetchById(resume.getId());
        if (resumeOptional.isEmpty()){
            throw new IdInvalidException("Resume voi id " + resume.getId() + " khong ton tai");
        }
        Resume reqResume = resumeOptional.get();
        reqResume.setStatus(resume.getStatus());

        return ResponseEntity.ok().body(this.resumeService.updateResume(reqResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.fetchById(id);
        if (resumeOptional.isEmpty()){
            throw new IdInvalidException("Resume voi " + id + " khong ton tai");
        }
        this.resumeService.deleteResume(id);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDto> fetchResumeByUser(Pageable pageable) {

        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }

}
