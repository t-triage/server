/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.aaa.AuthenticationProvider;
import com.clarolab.model.types.RoleType;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_USER;


@Entity
@Table(name = TABLE_USER, indexes = {
        @Index(name = "IDX_USER_USERNAME", columnList = "username", unique = true),
        @Index(name = "IDX_USER_REALNAME", columnList = "realname, enabled"),
        @Index(name = "IDX_USER_PROVIDER", columnList = "provider, enabled")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends Entry {

    protected String username;
    protected String realname;
    protected String slackId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private ImageModel avatar;

    private String password;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "preference_id")
    private UserPreference userPreference;

    @Column(columnDefinition = "smallint")
    private RoleType roleType;

    @Enumerated(EnumType.STRING)
    private AuthenticationProvider provider;
    private String providerId;
    private boolean agreedTermsConditions;
    private long timestampTermsConditions;

    private long lastLoginTime;

    @Builder
    private User(Long id, boolean enabled, Long updated, Long timestamp, String username, String realname, ImageModel avatar, String password, boolean agreedTermsConditions, RoleType roleType, long lastLoginTime, long timestampTermsConditions) {
        super(id, enabled, updated, timestamp);
        this.username = username;
        this.realname = realname;
        this.avatar = avatar;
        this.password = password;
        this.agreedTermsConditions = agreedTermsConditions;
        this.roleType = roleType;
        this.lastLoginTime = lastLoginTime;
        this.timestampTermsConditions = timestampTermsConditions;
    }

    public boolean isAdmin() {
        return getRoleType().equals(RoleType.ROLE_ADMIN);
    }

    public boolean isInternal() {
        if (provider == null) {
            return true;
        }
        return AuthenticationProvider.internal.equals(getProvider());
    }

    public String getDisplayName() {
        if (StringUtils.isEmpty(getRealname())) {
            if (getUsername().contains("@")) {
                return getUsername().replaceAll("@(\\w)*(.(\\w)*)*", com.clarolab.util.StringUtils.getEmpty());
            } else {
                return getUsername();
            }
        } else {
            return getRealname();
        }
    }

}
