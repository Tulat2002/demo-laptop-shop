package vn.hoidanit.jobhunter.domain.response;

import lombok.*;
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

    private CompanyUser company;

    @Getter
    @Setter
    public static class CompanyUser{
        private long id;
        private String name;
    }

}
