package com.clarolab.dto.chart;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChartSerieDTO {
    private String name;
    private List<ChartCategoryDTO> data = new ArrayList<>();

}
