package com.clarolab.event.analytics;

import com.clarolab.model.Product;
import com.clarolab.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductStatRepository extends BaseRepository<ProductStat> {

    @Query("SELECT SUM(ps.fails), SUM(ps.autoTriaged) FROM ProductStat ps WHERE ps.product = ?1 AND timestamp > ?2 AND timestamp < ?3")
    Object[] getFailsByProduct(Product product, Long prev, Long now);

    List<ProductStat> findAllByEnabled(boolean enabled);
}
