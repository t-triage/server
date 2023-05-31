package com.clarolab.connectors.impl.utils.report.allure.json.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Data
@Builder
public class AllureReportSummary {

    private String AllureReport;
    private AllureReportSummaryStatistic statistic;
    private AllureTime time;

    public int getTotal(){
        return statistic.getTotal();
    }

    public int getPassed(){
        return statistic.getPassed();
    }

    public int getFailed(){
        return statistic.getFailed();
    }

    public int getBroken(){
        return statistic.getBroken();
    }

    public int getSkipped(){
        return statistic.getSkipped();
    }

    public long getDuration(){
        return time.getDuration();
    }

    public long getExecutionDate(){
        return time.getStart();
    }

}
