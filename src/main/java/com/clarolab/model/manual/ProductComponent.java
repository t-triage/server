/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual;

import com.clarolab.model.Entry;
import com.clarolab.model.Product;
import lombok.*;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_PRODUCT_COMPONENT;

@Entity
@Table(name = TABLE_PRODUCT_COMPONENT, indexes = {
        @Index(name = "IDX_TABLE_PRODUCT_COMPONENT_ENABLED", columnList = "enabled"),
        @Index(name = "IDX_TABLE_PRODUCT_COMPONENT_NAME_ENABLED", columnList = "name,enabled")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductComponent extends Entry {
    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public ProductComponent(Long id, boolean enabled, long updated, long timestamp, String name, String description, Product product) {
        super(id, enabled, updated, timestamp);
        this.name = name;
        this.description = description;
        this.product = product;
    }
}
