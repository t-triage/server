/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import lombok.*;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_PIN;

@Entity
@Table(name = TABLE_PIN)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestPin extends Entry {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    private long createDate;

    private String reason;

    @Builder
    private TestPin(Long id, boolean enabled, long updated, long timestamp, User author, long createDate, String reason) {
        super(id, enabled, updated, timestamp);
        this.author = author;
        this.createDate = createDate;
        this.reason = reason;
    }
}
