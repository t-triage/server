package com.clarolab.functional.test.model;

import com.clarolab.dto.ExecutorStatChartDTO;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.Executor;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.EvolutionStatService;
import com.clarolab.service.ProductGoalService;
import com.clarolab.service.ProductService;
import com.clarolab.util.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EvolutionStatTest extends BaseFunctionalTest {

    @Autowired
    private EvolutionStatService evolutionStatService;

    @Autowired
    private UseCaseDataProvider provider;

    public void clearProvider() {
        provider.clear();
    }

    @Before
    public void populate() {
        int amount = 5;

        clearProvider();
        Executor executor = provider.getExecutor();
        executor.setGoal(provider.getTrendGoal());

        for (int i = 0; i < amount; i++) {
            provider.setEvolutionStat(null);
            provider.setTimestamp(DateUtils.offSetDays(-7 * i));
            provider.getEvolutionStat();
        }
    }

    @Test
    public void testGrowthStats() {
        long from = DateUtils.offSetDays(-14);
        long to = DateUtils.offSetDays(0);

        Executor executor = provider.getExecutor();

        List<ExecutorStatChartDTO> stats = executorService.getGrowthStats(executor.getId(), null, null);
        List<ExecutorStatChartDTO> statsSince = executorService.getGrowthStats(executor.getId(), from, to);

        Assert.assertNotNull(stats);
        Assert.assertNotNull(statsSince);
        Assert.assertTrue(stats.size() > statsSince.size());
    }

    @Test
    public void testPassingStats() {
        long from = DateUtils.offSetDays(-14);
        long to = DateUtils.offSetDays(0);

        Executor executor = provider.getExecutor();

        List<ExecutorStatChartDTO> stats = executorService.getPassingStats(executor.getId(), null, null);
        List<ExecutorStatChartDTO> statsSince = executorService.getPassingStats(executor.getId(), from, to);

        Assert.assertNotNull(stats);
        Assert.assertNotNull(statsSince);
        Assert.assertTrue(stats.size() > statsSince.size());
    }

    @Test
    public void testCommitsStats() {
        long from = DateUtils.offSetDays(-14);
        long to = DateUtils.offSetDays(0);

        Executor executor = provider.getExecutor();

        List<ExecutorStatChartDTO> stats = executorService.getCommitsStats(executor.getId(), null, null);
        List<ExecutorStatChartDTO> statsSince = executorService.getCommitsStats(executor.getId(), from, to);

        Assert.assertNotNull(stats);
        Assert.assertNotNull(statsSince);
        Assert.assertTrue(stats.size() > statsSince.size());
    }

    @Test
    public void testStabilityStats() {
        long from = DateUtils.offSetDays(-14);
        long to = DateUtils.offSetDays(0);

        Executor executor = provider.getExecutor();

        List<ExecutorStatChartDTO> stats = executorService.getStabilityStats(executor.getId(), null, null);
        List<ExecutorStatChartDTO> statsSince = executorService.getStabilityStats(executor.getId(), from, to);

        Assert.assertNotNull(stats);
        Assert.assertNotNull(statsSince);
        Assert.assertTrue(stats.size() > statsSince.size());
    }

    @Test
    public void testTriageDoneStats() {
        long from = DateUtils.offSetDays(-14);
        long to = DateUtils.offSetDays(0);


        Executor executor = provider.getExecutor();

        List<ExecutorStatChartDTO> stats = executorService.getTriageDoneStats(executor.getId(), null, null);
        List<ExecutorStatChartDTO> statsSince = executorService.getTriageDoneStats(executor.getId(), from, to);

        Assert.assertNotNull(stats);
        Assert.assertNotNull(statsSince);
        Assert.assertTrue(stats.size() > statsSince.size());
    }

    @Test
    public void testAllStats() {
        long from = DateUtils.offSetDays(-14);
        long to = DateUtils.offSetDays(0);


        Executor executor = provider.getExecutor();

        List<ExecutorStatChartDTO> growth = executorService.getGrowthStats(executor.getId(), from, to);
        List<ExecutorStatChartDTO> passing = executorService.getPassingStats(executor.getId(), from, to);
        List<ExecutorStatChartDTO> triageDone = executorService.getTriageDoneStats(executor.getId(), from, to);
        List<ExecutorStatChartDTO> stabilty = executorService.getStabilityStats(executor.getId(), from, to);
        List<ExecutorStatChartDTO> commits = executorService.getCommitsStats(executor.getId(), from, to);

        Assert.assertNotNull(growth);
        Assert.assertNotNull(passing);
        Assert.assertNotNull(triageDone);
        Assert.assertNotNull(stabilty);
        Assert.assertNotNull(commits);
    }

}
