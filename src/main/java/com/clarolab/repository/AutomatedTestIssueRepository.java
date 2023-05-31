/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.*;
import com.clarolab.model.types.IssueType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutomatedTestIssueRepository extends BaseRepository<AutomatedTestIssue> {

    AutomatedTestIssue getFirstByTestCase(TestCase testCase);

    AutomatedTestIssue findByTestCaseAndEnabled(TestCase testCase, boolean enabled);

    // Issues with type FIXED never should be shown
    @Query("SELECT a FROM AutomatedTestIssue a WHERE a.issueType <> 3 AND a.issueType <> 8")
    List<AutomatedTestIssue> findAllButFixed(@Nullable Specification spec, @Nullable Sort sort);// Issues with type FIXED never should be shown
    
    List<AutomatedTestIssue> findByTestTriage(TestTriage testTriage);

    @Query("SELECT count(a) FROM AutomatedTestIssue a WHERE a.issueType in (0, 5) AND enabled=true")
    Long countAllButFixed();

    @Query("SELECT count(a) FROM AutomatedTestIssue a WHERE a.issueType in (0, 5) AND testTriage.container = ?1 AND enabled=true")
    Long countAllButFixed(Container container);

    Long countAllByIssueTypeInAndTestTriageContainerAndEnabledAndTimestampGreaterThanEqual(List<IssueType> types, Container container, boolean enabled, long date);

    @Query("SELECT count (a) FROM AutomatedTestIssue a WHERE a.triager = ?1 AND (a.issueType = 0 OR a.issueType = 5)")
    Long countAllButFixed(User user);

    @Query("SELECT count (a) FROM AutomatedTestIssue a WHERE a.triager = ?1 AND (a.issueType = 0 OR a.issueType = 5) AND a.timestamp > ?2 AND a.timestamp < ?3")
    Long countAllButFixedAndTimestamp(User user, Long prev, Long now);

    @Query("SELECT count (a) FROM AutomatedTestIssue a WHERE a.triager = ?1 AND (a.issueType = 3)") //6 if I want to include passing ones
    Long countAllFixed(User user);

    @Query("SELECT a.triager.realname, count(a.id) FROM AutomatedTestIssue a, TestCase tc WHERE tc.product = ?1 AND a.testCase = tc.id AND (a.issueType = 0 OR a.issueType = 5) GROUP BY a.triager.realname")
    List<Object[]> findAutomationIssuesGroupedByUser(Product product);

    @Query("SELECT a.triager.realname, count(a.id) FROM AutomatedTestIssue a, TestCase tc WHERE tc.product = ?1 AND a.testCase = tc.id AND (a.issueType = 3) GROUP BY a.triager.realname")
    List<Object[]> findAutomationFixedGroupedByUser(Product product);

    @Query("SELECT a.triager.realname, count(a.id) FROM AutomatedTestIssue a, TestCase tc WHERE a.testCase = tc.id AND (a.issueType = 0 OR a.issueType = 5) GROUP BY a.triager.realname")
    List<Object[]> findAutomationIssuesGroupedByUser();

    @Query("SELECT a.triager.realname, count(a.id) FROM AutomatedTestIssue a, TestCase tc WHERE a.testCase = tc.id AND (a.issueType = 3) GROUP BY a.triager.realname")
    List<Object[]> findAutomationFixedGroupedByUser();

    @Query("SELECT a FROM AutomatedTestIssue a WHERE a.product=?1 AND a.enabled = true AND (a.issueType = 0 OR a.issueType = 5)")
    List<AutomatedTestIssue> findAllByProduct(Product product);

    @Query("SELECT a FROM AutomatedTestIssue a WHERE a.product=?1 AND a.enabled = true AND (a.issueType = 0 OR a.issueType = 5) AND a.timestamp > ?2 AND a.timestamp < ?3")
    List<AutomatedTestIssue> findAllByProductAndTimestamp(Product product, Long prev, Long now);

    @Query("SELECT a FROM AutomatedTestIssue a WHERE a.triager=?1 AND a.enabled = true AND (a.issueType = 0 OR a.issueType = 5) AND a.timestamp > ?2 AND a.timestamp < ?3 ORDER BY a.timestamp")
    List<AutomatedTestIssue> findAllByUserAndTimestamp(User user, Long prev,  Long now);

    @Query("SELECT distinct a.triager FROM AutomatedTestIssue a")
    List<User> findAllUsers();

}
