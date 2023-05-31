/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.slack;

import com.clarolab.model.Container;
import com.clarolab.model.Entry;
import com.clarolab.model.Executor;
import com.clarolab.model.Product;
import com.clarolab.util.StringUtils;
import lombok.*;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_SLACK_SPEC;

@Entity
@Table(name = TABLE_SLACK_SPEC, indexes = {})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SlackSpec extends Entry {

    private String token;
    private String channel;
    private String dailyChannel;
    private boolean sendUserNotification;
    private boolean sendDailyNotification;

    @ManyToOne(fetch = FetchType.LAZY) //could be not assigned if container exists
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER) //could be not assigned if product exists
    @JoinColumn(name = "container_id")
    private Container container;

    @ManyToOne(fetch = FetchType.LAZY) //could be not assigned if product exists
    @JoinColumn(name = "executor_id")
    private Executor executor;

    @ManyToOne(fetch = FetchType.LAZY) //could be not assigned if product exists
    @JoinColumn(name = "parent_id")
    private SlackSpec parent;

    @Builder
    public SlackSpec(Long id, boolean enabled, long updated, long timestamp, String token, String channel, Product product, Container container, boolean sendUserNotification, boolean sendDailyNotification, String dailyChannel, SlackSpec parent, Executor executor) {
        super(id, enabled, updated, timestamp);
        this.token = token;
        this.channel = channel;
        this.product = product;
        this.container = container;
        this.sendUserNotification = sendUserNotification;
        this.sendDailyNotification = sendDailyNotification;
        this.dailyChannel = dailyChannel;
        this.parent = parent;
        this.executor = executor;

        if (this.container != null && this.product == null) {
            this.product = container.getProduct();
        }
    }
    
    public String getFinalDailyChannel() {
        String answer = null;
        if (StringUtils.isEmpty(getDailyChannel())) {
            if (getParent() != null) {
                answer = getParent().getFinalDailyChannel();
            } else {
                answer = null;
            }
        } else {
            return getDailyChannel();
        }
        
        if (StringUtils.isEmpty(answer)) {
            return getFinalChannel();
        }
        
        return answer;
    }

    public String getFinalChannel() {
        if (StringUtils.isEmpty(getChannel())) {
            if (getParent() != null) {
                return getParent().getFinalChannel();
            } else {
                return null;
            }
        } else {
            return getChannel();
        }
    }

    public String getFinalToken() {
        if (StringUtils.isEmpty(getToken())) {
            if (getParent() != null) {
                return getParent().getFinalToken();
            } else {
                return null;
            }
        } else {
            return getToken();
        }
    }

    public boolean isHierarchicalyEnabled(){
        return isEnabled() && !StringUtils.isEmpty(token) && !StringUtils.isEmpty(channel) && (container == null || container.isHierarchicalyEnabled()) && (product == null || product.isEnabled());
    }
    
    public String getContainerName() {
        if (getContainer() != null) {
            return getContainer().getName();
        }

        if (getExecutor() != null) {
            return getExecutor().getContainer().getName();
        }
        
        if (getProduct() != null) {
            return getProduct().getName();
        }
        
        return "";
    }
}
