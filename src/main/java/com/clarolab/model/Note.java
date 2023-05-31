/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_NOTE;

@Entity
@Table(name = TABLE_NOTE, indexes = {
        @Index(name = "IDX_NOTE_NAME", columnList = "name")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Note extends Entry {

    private String name;

    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER) //could be not assigned
    @JoinColumn(name = "user_id")
    private User author;

    @Builder
    private Note(Long id, boolean enabled, long updated, long timestamp, String name, String description, User author) {
        super(id, enabled, updated, timestamp);
        this.name = name;
        this.description = description;
        this.author = author;
    }
}
