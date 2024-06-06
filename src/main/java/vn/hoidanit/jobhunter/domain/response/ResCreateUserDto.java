package vn.hoidanit.jobhunter.domain.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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
    private CompanyUser company;


    @Getter
    @Setter
    public static class CompanyUser{
        private long id;
        private String name;
    }

}
