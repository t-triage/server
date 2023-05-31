/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto.push;

import com.clarolab.util.StringUtils;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ArtifactDTO {

    private String fileName;
    private String content;
    private String fileType;
    private String url;

    public String toJsonString() {
        StringBuffer representation = new StringBuffer();
        representation.append("{fileName: \"");
        representation.append(fileName);
        representation.append("\", fileType: \"");
        representation.append(fileType);
        representation.append("\", url: \"");
        representation.append(url);
        representation.append("\", content: \"");
        if (!StringUtils.isEmpty(content)) {
            representation.append(content.replaceAll("\"", "'"));
        }

        representation.append("\"}");
        return representation.toString();
    }
}
