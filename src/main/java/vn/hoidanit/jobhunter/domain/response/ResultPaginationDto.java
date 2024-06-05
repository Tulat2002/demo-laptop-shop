package vn.hoidanit.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDto {

    private Meta meta;

    private Object result;

    @Setter
    @Getter
    public static class Meta {

        private int page;

        private int pageSize;

        private int pages;

        private long total;

    }


}
