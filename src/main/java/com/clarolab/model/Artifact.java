/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.model.types.ArtifactType;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_ARTIFACT;

@Entity
@Table(name = TABLE_ARTIFACT, indexes = {
        @Index(name = "IDX_ARTIFACT_ID", columnList = "build_id")
})

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Artifact extends Entry{

    private String name;

    @Type(type = "org.hibernate.type.TextType")
    private String url;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private ArtifactType artifactType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "build_id", nullable = false)
    private Build build;

    @Builder
    private Artifact(Long id, boolean enabled, long updated, long timestamp, String name, String url, ArtifactType artifactType, Build newBuild) {
        super(id, enabled, updated, timestamp);
        this.name = name;
        this.url = url;
        this.artifactType = artifactType;
        this.build = newBuild;
    }

    public static boolean isImageFile(String pathFile){
        String IMAGE_FILE_PATTERN = "[^\\s]+\\.(?i)(jpg|png|gif|bmp)$";
                                                        //This case is a harcoded validation for LIA allure
        return pathFile.matches(IMAGE_FILE_PATTERN) || pathFile.endsWith("attachment");
    }

    public static boolean isLogFile(String pathFile){
        return pathFile.endsWith("log");
    }
}
