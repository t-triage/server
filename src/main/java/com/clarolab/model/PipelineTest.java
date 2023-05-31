/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import lombok.*;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_PIPELINE_TEST;

@Entity
@Table(name = TABLE_PIPELINE_TEST, indexes = {
		@Index(name = "IDX_PIPELINE_TEST", columnList = "pipeline_id,test_id"),
		@Index(name = "IDX_PIPELINE_LIST", columnList = "pipeline_id")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PipelineTest extends Entry<PipelineTest> {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id")
	private TestCase test;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pipeline_id")
	private Pipeline pipeline;

	@Builder
	public PipelineTest(Long id, boolean enabled, long updated, long timestamp, TestCase test, Pipeline pipeline) {
		super(id, enabled, updated, timestamp);
		this.test = test;
		this.pipeline = pipeline;
	}

}
