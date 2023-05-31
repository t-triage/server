/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto;

import com.clarolab.model.Container;
import com.clarolab.model.User;
import lombok.Data;

@Data
public class ContainerItemDTO {
    private long pendingBuilds;
    private User user;
    private UserDTO userDTO;
    private Container container;

    public ContainerItemDTO(Container id, long pendingBuilds, User user) {
        this.container = id;
        this.pendingBuilds = pendingBuilds;
        this.user = user;
    }

    public Long getConainerId() {
        return getContainer().getId();
    }
}
