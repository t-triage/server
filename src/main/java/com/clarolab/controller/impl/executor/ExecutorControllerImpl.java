/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl.executor;

import com.clarolab.controller.ExecutorController;
import com.clarolab.controller.impl.BaseControllerImpl;
import com.clarolab.dto.ExecutorDTO;
import com.clarolab.dto.ExecutorStatChartDTO;
import com.clarolab.dto.SlackSpecDTO;
import com.clarolab.model.Executor;
import com.clarolab.model.types.ReportType;
import com.clarolab.service.TTriageService;
import com.clarolab.service.UserService;
import com.clarolab.serviceDTO.ExecutorServiceDTO;
import com.clarolab.serviceDTO.SlackSpecServiceDTO;
import com.clarolab.util.StringUtils;
import com.clarolab.view.KeyValuePair;
import lombok.extern.java.Log;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@CrossOrigin
@RestController
@Log
public class ExecutorControllerImpl extends BaseControllerImpl<ExecutorDTO> implements ExecutorController {

    @Autowired
    private ExecutorServiceDTO executorService;

    @Autowired
    private UserService userService;

    @Autowired
    private SlackSpecServiceDTO slackService;

    @Override
    protected TTriageService<ExecutorDTO> getService() {
        return executorService;
    }

    @Override
    @ApiIgnore
    public ResponseEntity<Long> delete(Long id) {
        return super.delete(id);
    }

    @Override
    public ResponseEntity<Boolean> populate(Long id) {
     /*   if (!LicenceValidator.isValid()) {
            return ResponseEntity.ok(false);
        }*/
        return ResponseEntity.ok(executorService.populate(id));
    }

    @Override
    public ResponseEntity<Page<String>> search(String name) {
        return ResponseEntity.ok(new PageImpl<>(executorService.search(name)));
    }

    @Override
    public ResponseEntity<List<KeyValuePair>> getExecutorNames(Long id) {
         return  ResponseEntity.ok(executorService.getExecutorNames(id));
    }

    @Override
    public ResponseEntity<KeyValuePair> upload(Long executorId, String content, ReportType reportType) {
        Executor executor = executorService.findEntity(executorId);
        String answer = null;
        
        if (executor == null) {
            answer = "Missing executor id";
        }

        if (StringUtils.isEmpty(content)) {
            answer = "Missing xml file with tests";
        }
        if (StringUtils.isEmpty(answer) && !StringUtils.isEmpty(content)) {
            if (content.startsWith("{'note'")) {
                content = content.substring("{'note':'".length(), content.length() - 2);
            }
            answer = executorService.upload(executor, content, reportType);
        }
        KeyValuePair response = KeyValuePair.builder().key("answer").value(answer).build();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<String> importReport(String file) {
        return ResponseEntity.ok(executorService.importReport(file));
    }

    @Override
    public ResponseEntity<String> importCSVReport(String csvContent) {
        JSONObject jsonObj = new JSONObject(csvContent);
        String content = jsonObj.optString("csvContent");
        String separator = jsonObj.optString("separator");
        String format = jsonObj.optString("format");
        return ResponseEntity.ok(executorService.importCSVReport(content, separator, format));
    }

    @Override
    public ResponseEntity<List<ExecutorStatChartDTO>> getGrowthStats(Long executorid, Long from, Long to) {
        return ResponseEntity.ok(executorService.getGrowthStats(executorid, from, to));
    }

    @Override
    public ResponseEntity<List<ExecutorStatChartDTO>> getStabilityStats(Long executorid, Long from, Long to) {
        return ResponseEntity.ok(executorService.getStabilityStats(executorid,  from, to));
    }

    @Override
    public ResponseEntity<List<ExecutorStatChartDTO>> getCommitsStats(Long executorid, Long from, Long to) {
        return ResponseEntity.ok(executorService.getCommitsStats(executorid,  from, to));
    }

    @Override
    public ResponseEntity<List<ExecutorStatChartDTO>> getPassingStats(Long executorid, Long from, Long to) {
        return ResponseEntity.ok(executorService.getPassingStats(executorid,  from, to));
    }

    @Override
    public ResponseEntity<List<ExecutorStatChartDTO>> getTriageDoneStats(Long executorid, Long from, Long to) {
        return ResponseEntity.ok(executorService.getTriageDoneStats(executorid,  from, to));
    }

    public ResponseEntity<List<ExecutorDTO>> executorEnabled() {
        return ResponseEntity.ok(executorService.getExecutorEnabled());
    }

    @Override
    public ResponseEntity<ExecutorDTO> update(ExecutorDTO entity) {
        if (entity.getId() == null) {
            // ok, we are just updating a container spec
            return ResponseEntity.ok(getService().update(entity));
        } else {
            SlackSpecDTO dto = slackService.findExecutor(entity.getId());
            if (dto == null) {
                executorService.update(entity);
            } else {
                entity.setId(dto.getExecutorId());
                return ResponseEntity.ok(getService().update(entity));
            }
        }
        return ResponseEntity.ok(getService().update(entity));
    }
}
