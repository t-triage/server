/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl.report;


import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.controller.ExportController;
import com.clarolab.serviceDTO.ExecutorServiceDTO;
import com.clarolab.serviceDTO.ProductServiceDTO;
import com.clarolab.serviceDTO.UserServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static com.clarolab.util.Constants.*;

@Controller
public class ExportControllerImpl implements ExportController {


    @Autowired
    private UserServiceDTO userService;

    @Autowired
    private ExecutorServiceDTO executorService;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Autowired
    private ProductServiceDTO productService;

    @Override
    @GetMapping(value = USER)
    public String downloadUserList(Model model) {
        model.addAttribute("content", userService.findAll());
        model.addAttribute("type", EXPORT_USERLIST);
        return "";
    }

    @Override
    @GetMapping(value = EXECUTOR)
    public String downloadExecutorList(Model model) {
        model.addAttribute("content", executorService.getExecutorViews(null, true));
        model.addAttribute("type", EXPORT_EXECUTORLIST);
        return "";
    }

    @Override
    @GetMapping(value = USER_FULL_REPORT)
    public String downloadUserReport(Model model) {
        model.addAttribute("content", authContextHelper.getCurrentUser());
        model.addAttribute("type", EXPORT_USERREPORT);
        model.addAttribute("rotated", false);
        return "";
    }

    @Override
    @GetMapping(value = PRODUCT_FULL_REPORT)
    public String downloadProductReport(Long productId, Model model) {
        model.addAttribute("content", productService.find(productId));
        model.addAttribute("type", EXPORT_PRODUCTREPORT);
        model.addAttribute("rotated", false);
        return "";
    }
}
