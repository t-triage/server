/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_DEADLINE;

@Entity
@Table(name = TABLE_DEADLINE, indexes = {
        @Index(name = "IDX_DEADLINE_NAME", columnList = "name")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Deadline extends Entry {

    private String name;

    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "note_id")
    private Note note;

    private long deadlineDate;

    @Builder
    private Deadline(Long id, boolean enabled, long updated, long timestamp, String name, String description, Product product, Note note, long deadlineDate) {
        super(id, enabled, updated, timestamp);
        this.name = name;
        this.description = description;
        this.product = product;
        this.note = note;
        this.deadlineDate = deadlineDate;
    }
}
