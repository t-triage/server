/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.model.types.ReportType;
import com.clarolab.util.LogicalCondition;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.TABLE_EXECUTOR;

@Entity
@Table(name = TABLE_EXECUTOR, indexes = {
		@Index(name = "IDX_EXECUTOR_ENABLED", columnList = "enabled"),
		@Index(name = "IDX_EXECUTOR_PROCESS_LIST", columnList = "container_id, enabled"),
		@Index(name = "IDX_EXECUTOR_SEARCH", columnList = "container_id, name, enabled"),
		@Index(name = "IDX_EXECUTOR_PUSH", columnList = "container_id, name")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Executor extends Entry<Executor> {
	private static final String renameJobPrefix = "-";

	private String name;

	@Type(type = "org.hibernate.type.TextType")
	private String description;

	//Internal information that should not be exposed and used only in backend
	@Column(name = "hidden_data")
	@Nullable
	private String hiddenData;

	@Type(type = "org.hibernate.type.TextType")
	private String url;

	//In case of Multijob, Flow, etc who does it run to this job?
	private String callers;

	private long maxTestExecuted;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "container_id")
	private Container container;

	@OneToMany(mappedBy = "executor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("number DESC")
	private List<Build> builds;

	@Enumerated
	@Column(columnDefinition = "smallint")
	private ReportType reportType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "goal_id")
	private TrendGoal goal;

	@Builder
	private Executor(Long id, boolean enabled, long updated, long timestamp, long maxTestExecuted, String name, String description, String hiddenData, String url, String callers, Container container, List<Build> builds, ReportType reportType, TrendGoal goal) {
		super(id, enabled, updated, timestamp);
		this.name = name;
		this.description = description;
		this.hiddenData = hiddenData;
		this.url = url;
		this.container = container;
		this.builds = builds;
		this.maxTestExecuted = maxTestExecuted;
		this.callers = callers;
		this.reportType = reportType;
		this.goal = goal;

		initBuilds();
	}

	public List<Build> getLastBuilds() {
		return getBuilds().stream()
				.filter(build -> LogicalCondition.AND(build.isEnabled(), LogicalCondition.NOT(build.isProcessed())))
				.sorted()
				.collect(Collectors.toList());
	}

	public Build getLastExecutedBuild(){
		Build lastBuild = null;
		for (Build build : getBuilds()) {
			if (lastBuild == null) {
				lastBuild = build;
			} else {
				if (lastBuild.compareTo(build) < 0) {
					lastBuild = build;
				}
			}
		}
		return 	lastBuild;
	}

    public Build getFirstExecutedBuild() {
		return Iterables.getFirst(getBuilds(), null);
    }

	public void add(List<Build> builds) {
		initBuilds();
		builds.forEach(build -> this.add(build));
	}

	public void add(Build build) {
		if (build == null) {
			return;
		}
		initBuilds();
		build.setExecutor(this);
		if (!builds.contains(build)) {
			this.getBuilds().add(build);
		}
	}

	//This is used to compare Executor elements
	//See JenkinsJobService.getNewJobs#61
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Executor other = (Executor) o;
		//if (!super.equals(o)) return false;
		if (!StringUtils.isEmpty(getHiddenData()) && !StringUtils.isEmpty(other.getHiddenData())) {
			return getHiddenData().equalsIgnoreCase(other.getHiddenData());
		}
		return name.equals(((Executor) o).name);
	}

	public List<TestExecution> getTestCases() {
		try{
			return getLastExecutedBuild().getTestCases();
		}catch (NullPointerException e){
			return Lists.newArrayList();
		}
	}

	public String getContainerName(){
		return container.getName();
	}

	public String getConnectorName(){
		return getContainer().getConnectorName();
	}

	public String getProductName() {
		return container.getProductName();
	}

	public Product getProduct() {
		if (getContainer() == null)
			return null;
		return getContainer().getProduct();
	}

	public boolean isHierarchicalyEnabled(){
        return isEnabled() && container.isHierarchicalyEnabled();
    }

    private void initBuilds(){
		if(this.getBuilds() == null)
			this.setBuilds(Lists.newArrayList());
	}

	public void setContainer(Container newContainer) {
		container = newContainer;

		getBuilds().stream().forEach(build -> build.setContainer(newContainer));
	}

	public List<String> getCallers(){
		return Lists.newArrayList(callers.split(",")).stream().map(String::trim).collect(Collectors.toList());
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + name.hashCode();
		return result;
	}

	public void disable() {
		setEnabled(false);
		String randomPrefix = StringUtils.randomString(4);
		if (!getName().startsWith(renameJobPrefix)) {
			setName(getDeletedName(getName()));
			setUrl(renameJobPrefix + randomPrefix + getUrl());
		}
	}

	public void enable() {
		setEnabled(true);
		if (getName().startsWith(renameJobPrefix)) {
			setName(getName().substring(renameJobPrefix.length()));
			setUrl(getUrl().substring(renameJobPrefix.length() + 4));
		}
	}

	public boolean hasReportType() {
		return reportType != null && !ReportType.UNKNOWN.equals(reportType);
	}

	public ReportType getInheritReportType() {
		if (getReportType() == null) {
			return getContainer().getReportType();
		}
		return reportType;
	}

	public static String getDeletedName(String aName) {
		return renameJobPrefix + aName;
	}
	
}
