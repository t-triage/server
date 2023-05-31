package com.clarolab.event.analytics;

import com.clarolab.model.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.IntSummaryStatistics;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvolutionProductStat {
    Product product;
    int totalTests = 0;
    int passRate = 0;

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setTotalTests(int totalTests) {
        this.totalTests = totalTests;
    }

    public void setPassRate(int passRate) {
        this.passRate = passRate;
    }

    public void initTotalTest(double totalTest) {
        Product product = this.getProduct();
        Long requiredTestCase = product.getGoal().getRequiredTestCase();
        Long expectedTestCase = product.getGoal().getExpectedTestCase();
        int value = 0;

        if (requiredTestCase != null && expectedTestCase != null){
            if (totalTest < requiredTestCase) {
                value = -1;
            } else if (totalTest >= (double) requiredTestCase && totalTest < (double) expectedTestCase) {
                value = 0;
            } else if (totalTest >= (double) expectedTestCase) {
                value = 1;
            }

            setTotalTests(value);
        }
    }

    public void initPassRate(double pass) {
        Product product = this.getProduct();
        Long requiredPassRate = product.getGoal().getRequiredPassRate();
        Long expectedPassRate = product.getGoal().getExpectedPassRate();

        int value = 0;

        if (requiredPassRate != null && expectedPassRate != null){

            if (pass < requiredPassRate) {
                value = -1;
            } else if (pass >= (double) requiredPassRate && pass < (double) expectedPassRate) {
                value = 0;
            } else if (pass >= (double) expectedPassRate) {
                value = 1;
            }

            setPassRate(value);
        }
    }

    public void setTotalTest(List<ProductStat> stats) {
        List<ProductStat> recent = stats.subList(0, (int) (stats.size() / 2));
        List<ProductStat> old = stats.subList((int) (stats.size() / 2), stats.size());

        IntSummaryStatistics recentStats = recent.stream()
                .mapToInt(value -> (int) value.getTotalTests())
                .summaryStatistics();
        IntSummaryStatistics oldStats = old.stream()
                .mapToInt(value -> (int) value.getTotalTests())
                .summaryStatistics();

        Double diff = recentStats.getAverage() - oldStats.getAverage();

        initTotalTest(diff);
    }

    public void setPassRate(List<ProductStat> stats) {
        List<ProductStat> recent = stats.subList(0, (int) (stats.size() / 2));
        List<ProductStat> old = stats.subList((int) (stats.size() / 2), stats.size());

        IntSummaryStatistics recentStats = recent.stream()
                .mapToInt(value -> (int) value.getPass())
                .summaryStatistics();
        IntSummaryStatistics oldStats = old.stream()
                .mapToInt(value -> (int) value.getPass())
                .summaryStatistics();

        Double diff = recentStats.getAverage() - oldStats.getAverage();

        initPassRate(diff);
    }

    public void setTotalTest(ProductStat first) {
        initTotalTest((int) first.getTotalTests());
    }


    public void setPassRate(ProductStat first) {
        initPassRate(first.getPass());
    }


  


}
