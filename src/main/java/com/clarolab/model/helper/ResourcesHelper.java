/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.helper;

import com.clarolab.service.exception.OperationUnaceptableException;
import com.clarolab.service.exception.ServiceException;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.StringWriter;

@Log
public final class ResourcesHelper {


    public static String getDefaulTermAndCondition(String filename) throws ServiceException {
        String text = "";
        try {
            InputStream inputStream = ResourcesHelper.class.getClassLoader().getResourceAsStream(filename);
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer, "UTF-8");
            text = writer.toString();
        } catch (Exception ex) {
            throw new OperationUnaceptableException("Terms and Conditions file not found...");
        }
        return text;
    }

}
