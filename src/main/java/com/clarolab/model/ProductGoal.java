package com.clarolab.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

import static com.clarolab.util.Constants.TABLE_PRODUCT_GOAL;

@Entity
@Table(name = TABLE_PRODUCT_GOAL, indexes = {

})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductGoal extends Entry{
    private Long requiredTestCase;
    private Long expectedTestCase;
    private Long requiredPassRate;
    private Long expectedPassRate;


    @Builder
    private ProductGoal(Long id, boolean enabled, long updated, long timestamp, Long expectedGrowth,
                      Long requiredTestCase, Long expectedTestCase, Long requiredPassRate, Long expectedPassRate) {

        super(id, enabled, updated, timestamp);
        this.expectedTestCase = expectedTestCase;
        this.requiredTestCase = requiredTestCase;

        this.expectedPassRate = expectedPassRate;
        this.requiredPassRate = requiredPassRate;

    }
}
