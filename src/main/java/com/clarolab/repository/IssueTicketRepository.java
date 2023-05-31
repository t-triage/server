/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.IssueTicket;
import com.clarolab.model.Product;
import com.clarolab.model.TestCase;
import com.clarolab.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueTicketRepository extends BaseRepository<IssueTicket> {

    List<IssueTicket> findAllByAndAssignee(User user);

    IssueTicket getByTestCase(TestCase testCase);

    IssueTicket getFirstByTestCase(TestCase testCase);

    @Query("SELECT i FROM IssueTicket i WHERE i.product=?1 AND i.enabled = true AND (i.issueType = 0 OR i.issueType = 5)")
    List<IssueTicket> findAllByProduct(Product product);

    @Query("SELECT i FROM IssueTicket i WHERE i.product=?1 AND i.enabled = true AND (i.issueType = 0 OR i.issueType = 5) AND i.timestamp > ?2 AND i.timestamp < ?3")
    List<IssueTicket> findAllByProduct(Product product, Long startDate,  Long endDate);    
}
