package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO extends BaseDTO {

    private Long user;
    private String subject;
    private String description;
    private String actualDate;
    private String timeAgo;
    private boolean seen;
    private int priority;

}
