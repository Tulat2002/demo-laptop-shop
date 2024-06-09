package vn.hoidanit.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class ResUpdateResumeDto {

    private Instant updatedAt;
    private String updatedBy;

}
