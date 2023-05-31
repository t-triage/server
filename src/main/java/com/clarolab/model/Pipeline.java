/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_PIPELINE;

@Entity
@Table(name = TABLE_PIPELINE, indexes = {
		@Index(name = "IDX_PIPELINE_NAME_ENABLED", columnList = "name,enabled")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Pipeline extends Entry<Pipeline> {
	private String name;

	@Type(type = "org.hibernate.type.TextType")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	private long maxTestExecuted;

	@Builder
	private Pipeline(Long id, boolean enabled, long updated, long timestamp, String name, String description, long maxTestExecuted, Product product) {
		super(id, enabled, updated, timestamp);
		this.name = name;
		this.description = description;
		this.maxTestExecuted = maxTestExecuted;
		this.product = product;
	}
}
