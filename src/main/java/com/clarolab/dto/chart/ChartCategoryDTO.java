package com.clarolab.dto.chart;

import lombok.Data;

@Data
public class ChartCategoryDTO {
    private String category;
    private long value;

    public ChartCategoryDTO(String category, long value) {
        this.category = category;
        this.value = value;
    }
}
