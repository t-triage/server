/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper.csv;

import com.clarolab.dto.UserDTO;
import com.clarolab.export.mapper.UserListMapper;
import org.springframework.stereotype.Component;
import org.supercsv.io.ICsvBeanWriter;

import java.util.List;
import java.util.Map;

@Component
public class CVSUserListMapper implements CVSMapper, UserListMapper {

    @Override
    public void createCVSDocument(ICsvBeanWriter csvWriter, Map<String, Object> model) throws Exception {
        List<UserDTO> content = (List<UserDTO>) model.get("content");

        csvWriter.writeHeader(headerNames);

        for (UserDTO user : content) {
            csvWriter.write(user, getHeaders(headerNames));
        }
        csvWriter.close();
    }
}
