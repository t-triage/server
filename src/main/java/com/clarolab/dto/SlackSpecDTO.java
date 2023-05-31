/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlackSpecDTO extends BaseDTO {

    private String channel;
    private String dailyChannel;
    private String token;
    private Long productId;
    private Long parentId;
    private Long containerId;
    private Long executorId;
    private boolean sendUserNotification;
    private boolean sendDailyNotification;

}
