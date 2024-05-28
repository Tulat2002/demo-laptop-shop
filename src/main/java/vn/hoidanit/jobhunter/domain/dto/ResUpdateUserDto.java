package vn.hoidanit.jobhunter.domain.dto;

import lombok.Data;
import vn.hoidanit.jobhunter.util.enums.GenderEnum;

import java.time.Instant;

@Data
public class ResUpdateUserDto {

    private long id;
    private String name;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant updatedAt;

}
