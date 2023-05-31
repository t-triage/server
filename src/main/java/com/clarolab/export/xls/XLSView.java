/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.xls;

import com.clarolab.export.mapper.xls.UndefinedXLSMapper;
import com.clarolab.export.mapper.xls.XLSMapper;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class XLSView extends AbstractXlsView {

    private Map<String, XLSMapper> mapperMap;

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.addHeader("Content-Disposition", "attachment; filename=\"report_" + LocalDate.now() + ".xls\"");

        String type = (String) model.get("type");
        XLSMapper mapper = mapperMap.getOrDefault(type, new UndefinedXLSMapper());
        mapper.createXLSDocument(model, workbook);
    }

    @Autowired
    public void setMapperMap(List<XLSMapper> mapperMap) {
        this.mapperMap = mapperMap.stream().collect(Collectors.toMap(XLSMapper::mapperType, Function.identity()));
    }

}
