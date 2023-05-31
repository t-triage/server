package com.clarolab.dto;

import com.clarolab.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateStatsDTO{

    private Long failExecutionDate;

    private Long countOfFailExecution;


}
