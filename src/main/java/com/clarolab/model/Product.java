/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

import static com.clarolab.util.Constants.TABLE_PRODUCT;

@Entity
@Table(name = TABLE_PRODUCT, indexes = {
        @Index(name = "IDX_PRODUCT_NAME", columnList = "name")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends Entry {

    private String name;

    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Type(type = "org.hibernate.type.TextType")
    private String packageNames;

    @Type(type = "org.hibernate.type.TextType")
    private String logPattern;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private ImageModel logo;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Deadline> deadlines;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Container> containers;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CVSRepository> repositories;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private ProductGoal goal;

    private boolean hasMultipleEnvironment = false;

    @Builder
    public Product(Long id, boolean enabled, long updated, long timestamp, String name, String description, String packageNames, String logPattern, List<CVSRepository> repositories, ImageModel logo, List<Deadline> deadlines, List<Container> containers, Note note, boolean hasMultipleEnvironment, ProductGoal goal) {
        super(id, enabled, updated, timestamp);
        this.name = name;
        this.description = description;
        this.packageNames = packageNames;
        this.logPattern = logPattern;
        this.repositories = repositories;
        this.logo = logo;
        this.deadlines = deadlines;
        this.containers = containers;
        this.note = note;
        this.hasMultipleEnvironment = hasMultipleEnvironment;
        this.goal = goal;
    }

    public void add(Container container){
        getContainers().add(container);
        container.setProduct(this);
    }
}
