package com.clarolab.model.onboard;

import com.clarolab.model.Entry;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_GUIDE;

@Entity
@Table(name = TABLE_GUIDE, indexes = {
        @Index(name = "IDX_GUIDE_PAGE_ENABLED", columnList = "pageUrl, enabled")
})

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Guide extends Entry {
    @Enumerated
    @Column(columnDefinition = "smallint")
    private GuideType elementType;

    private String pageUrl;
    private String pageIdentifier;
    private String pageCondition;

    private String title;
    @Type(type = "org.hibernate.type.TextType")
    private String text;
    private String icon;
    private String image;
    private String video;
    @Type(type = "org.hibernate.type.TextType")
    private String html;

    @Builder
    private Guide(Long id, boolean enabled, long updated, long timestamp, GuideType elementType, String pageUrl, String pageIdentifier, String pageCondition, String title, String text, String icon, String image, String video, String html) {
        super(id, enabled, updated, timestamp);
        this.elementType = elementType;
        this.pageUrl = pageUrl;
        this.pageIdentifier = pageIdentifier;
        this.pageCondition = pageCondition;
        this.title = title;
        this.text = text;
        this.icon = icon;
        this.image = image;
        this.video = video;
        this.html = html;
    }
}
