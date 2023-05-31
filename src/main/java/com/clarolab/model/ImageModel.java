/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import static com.clarolab.util.Constants.TABLE_IMAGE;

@Entity
@Table(name = TABLE_IMAGE, indexes = {
        @Index(name = "IDX_IMAGE_NAME", columnList = "name")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageModel extends Entry<ImageModel>{

    private String name;

    private String type;

    @Type(type = "org.hibernate.type.TextType")
    private String data;

    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Builder
    private ImageModel(Long id, boolean enabled, long updated, long timestamp, String name, String type, String data, String description) {
        super(id, enabled, updated, timestamp);
        this.name = name;
        this.type = type;
        this.data = data;
        this.description = description;
    }
}
