package com.userms.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "分頁響應格式")
public class PageResult<T> {

    @Schema(description = "當前頁數據內容")
    private List<T> content;

    @Schema(description = "當前頁碼（從0開始）", example = "0")
    private int page;

    @Schema(description = "每頁大小", example = "20")
    private int size;

    @Schema(description = "總記錄數", example = "100")
    private long totalElements;

    @Schema(description = "總頁數", example = "5")
    private int totalPages;

    @Schema(description = "是否為第一頁", example = "true")
    private boolean first;

    @Schema(description = "是否為最後一頁", example = "false")
    private boolean last;

    @Schema(description = "是否為空", example = "false")
    private boolean empty;

    private PageResult(List<T> content, int page, int size, long totalElements, int totalPages, boolean first, boolean last, boolean empty) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
        this.empty = empty;
    }

    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }

    public static <T> PageResult<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean first = page == 0;
        boolean last = page >= totalPages - 1;
        boolean empty = content == null || content.isEmpty();

        return new PageResult<>(content, page, size, totalElements, totalPages, first, last, empty);
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(List.of(), 0, 0, 0, 0, true, true, true);
    }
}