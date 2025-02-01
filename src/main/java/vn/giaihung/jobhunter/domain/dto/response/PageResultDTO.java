package vn.giaihung.jobhunter.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageResultDTO {
    private Meta Meta;
    private Object result;

    @Getter
    @Setter
    public static class Meta {
        private int page;
        private int pageSize;
        private int pages;
        private long total;
    }
}
