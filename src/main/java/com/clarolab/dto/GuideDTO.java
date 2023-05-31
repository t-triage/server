package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuideDTO extends BaseDTO {
    private int elementType;

    private String pageUrl;
    private String pageIdentifier;
    private String pageCondition;

    private String title;
    private String text;
    private String icon;
    private String image;
    private String video;
    private String html;
}
