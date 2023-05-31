package com.clarolab.jira.model;


import com.clarolab.model.Entry;
import com.clarolab.model.Product;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_JIRA_CONFIGURATION;


@Entity
@Table(name = TABLE_JIRA_CONFIGURATION, indexes = {

})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JiraConfig extends Entry<JiraConfig>  {
    private String jiraUrl;

    private String projectKey;

    private String reporterEmail;

    @Type(type = "org.hibernate.type.TextType")
    private String refreshToken;

    @Type(type = "org.hibernate.type.TextType")
    private String finalToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String jiraVersion;

    private String initialStateId;
    private String resolvedStateId;
    private String closedStateId;
    private String reopenStateId;
    private String clientID;
    private String clientSecret;
    private String cloudId;
    private String issueType;
    private String defaultFieldsValues;

    @Builder
    private JiraConfig(Long id, boolean enabled, long updated, long timestamp, String jiraUrl, String jiraVersion, String projectKey, String reporterEmail, String refreshToken, String finalToken, Product product, String initialStateId, String resolvedStateId, String closedStateId, String reopenStateId, String clientID, String clientSecret,String cloudId, String issueType, String defaultFieldsValues){
        super(id, enabled, updated, timestamp);
        this.jiraUrl = jiraUrl;
        this.jiraVersion = jiraVersion;
        this.projectKey = projectKey;
        this.reporterEmail = reporterEmail;
        this.refreshToken = refreshToken;
        this.finalToken = finalToken;
        this.product = product;
        this.initialStateId = initialStateId;
        this.resolvedStateId = resolvedStateId;
        this.closedStateId = closedStateId;
        this.reopenStateId = reopenStateId;
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.cloudId = cloudId;
        this.issueType = issueType;
        this.defaultFieldsValues = defaultFieldsValues;
    }


}
