package vn.hoidanit.jobhunter.domain.response;

import lombok.Data;
import vn.hoidanit.jobhunter.util.enums.GenderEnum;

import java.time.Instant;

@Data
public class ResCreateUserDto {

    private long id;
    private String name;
    private String email;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant createdAt;

}
