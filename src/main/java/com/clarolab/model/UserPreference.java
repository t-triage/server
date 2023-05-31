/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import lombok.*;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_USER_PREFERENCE;

@Entity
@Table(name = TABLE_USER_PREFERENCE)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference extends Entry {

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private long rowPerPage;
    private long currentPageNUmber;
    private long currentContainer;

    @Builder
    private UserPreference(Long id, boolean enabled, long updated, long timestamp, User user, long rowPerPage, long currentPageNUmber, long currentContainer) {
        super(id, enabled, updated, timestamp);
        this.user = user;
        this.rowPerPage = rowPerPage;
        this.currentPageNUmber = currentPageNUmber;
        this.currentContainer = currentContainer;
    }
}
