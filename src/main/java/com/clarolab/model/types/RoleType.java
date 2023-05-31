/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

public enum RoleType {

    VIEW(1),
    ROLE_USER(2),
    ROLE_ADMIN(3),

    //Use to access vis push only
    ROLE_SERVICE(9);

    private final int roleType;

    RoleType(int roleType) {
        this.roleType = roleType;
    }

    public int getRoleType() {
        return this.roleType;
    }

}
