package com.clarolab.event.analytics;

import com.clarolab.model.Entry;
import com.clarolab.model.Executor;
import com.clarolab.model.Product;
import lombok.*;

import javax.persistence.*;
import java.util.IntSummaryStatistics;
import java.util.List;

import static com.clarolab.util.Constants.TABLE_STAT_EVOLUTION;

@Entity
@Table(name = TABLE_STAT_EVOLUTION)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvolutionStat extends Entry {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor_id")
    Executor executor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    long totalTests = 0;
    int growth = 0;
    int triageDone = 0;
    int passing = 0;
    int stability = 0;
    int commits = 0;
    int stabilityIndex = 0;
    long executionDate = 0;

    @Builder
    public EvolutionStat(Long id, boolean enabled, long updated, long timestamp, Executor executor, Product product, long totalTests, int growth, int triageDone, int passing, int stability, int commits, int stabilityIndex, long executionDate) {
        super(id, enabled, updated, timestamp);
        this.executor = executor;
        this.product = product;
        this.totalTests = totalTests;
        this.growth = growth;
        this.triageDone = triageDone;
        this.passing = passing;
        this.stability = stability;
        this.commits = commits;
        this.stabilityIndex = stabilityIndex;
        this.executionDate = executionDate;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setTotalTests(long totalTests) {
        this.totalTests = totalTests;
    }

    public void setGrowth(int growth) {
        this.growth = growth;
    }

    public void setTriageDone(int triageDone) {
        this.triageDone = triageDone;
    }

    public void setPassing(int passing) {
        this.passing = passing;
    }

    public void setStability(int stability) {
        this.stability = stability;
    }

    public void setCommits(int commits){
        this.commits = commits;
    }

    public void setStabilityIndex(int stabilityIndex) {
        this.stabilityIndex = stabilityIndex;
    }

    public void setExecutionDate(long aTime) {
        this.executionDate = aTime;
    }

    public void setExecutor(Executor newExecutor) {
        executor = newExecutor;
        if (newExecutor.getProduct() != null)
            setProduct(newExecutor.getProduct());
    }

    //Ya no se utiliza y se usa en la misma funcion que la llamaba
    public void initGrowth(double testAmount) {
        int value = 0;
        if (testAmount < 0) {
            value = -1;
        } else if (testAmount == 0) {
            value = 0;
        } else if (testAmount < 2) {
            value = 1;
        } else if (testAmount < 6) {
            value = 2;
        } else if (testAmount < 10) {
            value = 3;
        } else if (testAmount < 15) {
            value = 4;
        } else {
            value = 5;
        }
        setGrowth(value);
    }

    public void initTriageDone(double testTriages, double testFails) {
        Executor executor = this.getExecutor();
        Long requiredTriageDone = executor.getGoal().getRequiredTriageDone();
        Long expectedTriageDone = executor.getGoal().getExpectedTriageDone();

        if(requiredTriageDone != null && expectedTriageDone != null) {
            int value = -2;
            if (testFails == 0) {
                value = 1;
            } else if (testTriages == 0) {
                value = 1;
            }
            if (value > -2) {
                setTriageDone(value);
                return;
            }

            //Double testAmount = testTriages - testFails) * 100;
            Double testAmount = testFails;
            if (testAmount <= (double) expectedTriageDone) {
                value = 1;
            } else if (testAmount > (double) expectedTriageDone && testAmount <= (double) requiredTriageDone) {
                value = 0;
            } else if (testAmount > (double) requiredTriageDone) {
                value = -1;
            }
            setTriageDone(value);
        }
    }

    public void initPassing(double testAmount) {
        Executor executor = this.getExecutor();
        Long requiredPassing = executor.getGoal().getRequiredPassing();
        Long expectedPassing = executor.getGoal().getExpectedPassing();

        if(requiredPassing != null && expectedPassing != null) {
            int value = 0;

            if (testAmount < requiredPassing) {
                value = -1;
            } else if (testAmount >= (double) requiredPassing && testAmount < (double) expectedPassing) {
                value = 0;
            } else if (testAmount >= (double) expectedPassing) {
                value = 1;
            }

            setPassing(value);
        }
    }

    public void initStability(double testAmount) {
        Executor executor = this.getExecutor();
        Long requiredStability = executor.getGoal().getRequiredStability();
        Long expectedStability = executor.getGoal().getExpectedStability();

        if(requiredStability != null && expectedStability != null) {
            int value = 0;
            if (testAmount < requiredStability) {
                value = -1;
            } else if (testAmount >= (double) requiredStability && testAmount < (double) expectedStability) {
                value = 0;
            } else if (testAmount >= (double) expectedStability) {
                value = 1;
            }
            setStability(value);
        }
    }

    public void initCommits(double testAmount){
        Executor executor = this.getExecutor();
        Long requiredCommits = executor.getGoal().getRequiredCommits();
        Long expectedCommits = executor.getGoal().getExpectedCommits();

        if(requiredCommits != null && expectedCommits != null) {
            int value = 0;
            if (testAmount < requiredCommits) {
                value = -1;
            } else if (testAmount >= (double) requiredCommits && testAmount < (double) expectedCommits) {
                value = 0;
            } else if (testAmount >= (double) expectedCommits) {
                value = 1;
            }
            setCommits(value);
        }
    }

    public void initStabilityIndex() {
        long total = getStability() * 2 + getGrowth() + getTriageDone() * 3 + getPassing();
        setStabilityIndex((int) (total / 5));
    }


    public void setGrowth(List<ExecutorStat> stats) {
        List<ExecutorStat> recent = stats.subList(0, (int) (stats.size() / 2));
        List<ExecutorStat> old = stats.subList((int) (stats.size() / 2), stats.size());


        IntSummaryStatistics recentStats = recent.stream()
                .mapToInt(value -> value.getTotalTests() == null ? 0 : value.getTotalTests().intValue())
                .summaryStatistics();
        IntSummaryStatistics oldStats = old.stream()
                .mapToInt(value -> value.getTotalTests() == null ? 0 : value.getTotalTests().intValue())
                .summaryStatistics();

        Executor executor = this.getExecutor();
        Long required = executor.getGoal().getRequiredGrowth();
        Long expected = executor.getGoal().getExpectedGrowth();

        if(required != null && expected != null) {
            int value = 0;

            if (recentStats.getAverage() < required) {
                value = -1;
            } else if (recentStats.getAverage() >= required && recentStats.getAverage() < expected) {
                value = 0;
            } else if (recentStats.getAverage() >= expected) {
                value = 1;
            }
            setGrowth(value);
        }
        //initGrowth(recentStats.getAverage() - oldStats.getAverage());
    }

    public void setTriageDone(List<ExecutorStat> stats) {
        List<ExecutorStat> recent = stats.subList(0, (int) (stats.size() / 2));
        List<ExecutorStat> old = stats.subList((int) (stats.size() / 2), stats.size());

        IntSummaryStatistics recentTriages = recent.stream()
                .mapToInt(value -> (int) value.getToTriage())
                .summaryStatistics();
        IntSummaryStatistics recentFailures = recent.stream()
                .mapToInt(value -> (int) value.getTotalFails())
                .summaryStatistics();
        IntSummaryStatistics oldTriages = old.stream()
                .mapToInt(value -> (int) value.getToTriage())
                .summaryStatistics();
        IntSummaryStatistics oldFailures= old.stream()
                .mapToInt(value -> (int) value.getTotalFails())
                .summaryStatistics();


        initTriageDone(recentTriages.getAverage() - oldTriages.getAverage(), recentFailures.getAverage() - oldFailures.getAverage());
    }

    public void setPassing(List<ExecutorStat> stats) {
        List<ExecutorStat> recent = stats.subList(0, (int) (stats.size() / 2));
        List<ExecutorStat> old = stats.subList((int) (stats.size() / 2), stats.size());

        IntSummaryStatistics recentStats = recent.stream()
                .mapToInt(value -> (int) value.getTotalPass())
                .summaryStatistics();
        IntSummaryStatistics oldStats = old.stream()
                .mapToInt(value -> (int) value.getTotalPass())
                .summaryStatistics();

        Double diff = recentStats.getAverage() - oldStats.getAverage();
        Double percent = (diff * 100)/oldStats.getAverage();

        initPassing(percent);
    }

    public void setStability(List<ExecutorStat> stats) {
        List<ExecutorStat> recent = stats.subList(0, (int) (stats.size() / 2));
        List<ExecutorStat> old = stats.subList((int) (stats.size() / 2), stats.size());

        IntSummaryStatistics recentStats = recent.stream()
                .mapToInt(value -> (int) value.getNewFails())
                .summaryStatistics();
        IntSummaryStatistics oldStats = old.stream()
                .mapToInt(value -> (int) value.getNewFails())
                .summaryStatistics();

        //Creo que acá lo haría los viejos menos los nuevos, para ver una diferencia positiva si es que hay menos nuevos fallos
        //A diferencia de como estaba antes

        Double diff = oldStats.getAverage() - recentStats.getAverage();
        Double percent = (diff * 100)/oldStats.getAverage();

        initStability(percent);
    }


    public void setGrowth(ExecutorStat first) {
        initGrowth(first.getTotalTests().intValue());
    }

    public void setTriageDone(ExecutorStat first) {
        initTriageDone(first.getToTriage(), first.getTotalFails());
    }

    public void setPassing(ExecutorStat first) {
        initPassing(first.getTotalPass());
    }

    public void setStability(ExecutorStat first) {
        initStability(first.getNewFails());
    }




}
