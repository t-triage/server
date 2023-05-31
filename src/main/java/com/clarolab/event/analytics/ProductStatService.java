package com.clarolab.event.analytics;

import com.clarolab.model.Product;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.ProductRepository;
import com.clarolab.service.BaseService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log
public class ProductStatService extends BaseService<ProductStat> {

    @Autowired
    private ProductStatRepository productStatRepository;

    @Autowired
    private ProductRepository productRepository;


    @Override
    protected BaseRepository<ProductStat> getRepository() {
        return productStatRepository;
    }

    public Long getFailsByProduct(Product product, Long prev, Long now) {
        Object[] ob = productStatRepository.getFailsByProduct(product, prev, now);
        Object[] data = (Object[]) ob[0];
        if (data[0] == null || data[1] == null) {
            return Long.valueOf(0);
        }
        return (Long)data[0] - (Long)data[1];
    }

    public Map<Product,List<ProductStat>> getAllProductStat(){

        List<ProductStat> allStats = productStatRepository.findAllByEnabled(true);

        Map<Product, List<ProductStat>> answer = new HashMap<>();
        List<ProductStat> stats;

        for (Product product : productRepository.findAll()) {
            stats = new ArrayList<>();
            for (ProductStat stat : allStats) {
                if (product == stat.getProduct()) {
                    stats.add(stat);
                }
            }
            answer.put(product, stats);
        }

        return answer;
    }

    public EvolutionProductStat getAgileIndex(List<ProductStat> stats) {
        if (stats.isEmpty()) {
            return null;
        }
        EvolutionProductStat answer = new EvolutionProductStat();
        ProductStat first = stats.get(0);

        answer.setProduct(first.getProduct());
        answer.setTotalTests((int) first.getTotalTests());
        answer.setPassRate((int) first.getPass());

        if (stats.size() == 1) {
            answer.setPassRate(first);
            answer.setTotalTest(first);
        } else {
            answer.setPassRate(stats);
            answer.setTotalTest(stats);
        }

        return answer;
    }
}
