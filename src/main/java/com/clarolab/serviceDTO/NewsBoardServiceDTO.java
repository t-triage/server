/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.NewsBoardDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.NewsBoardMapper;
import com.clarolab.model.NewsBoard;
import com.clarolab.service.NewsBoardService;
import com.clarolab.service.TTriageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Log
public class NewsBoardServiceDTO implements BaseServiceDTO<NewsBoard, NewsBoardDTO, NewsBoardMapper> {

    @Autowired
    private NewsBoardService service;

    @Autowired
    private NewsBoardMapper mapper;


    @Override
    public TTriageService<NewsBoard> getService() {
        return service;
    }

    @Override
    public Mapper<NewsBoard, NewsBoardDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<NewsBoard, NewsBoardDTO, NewsBoardMapper> getServiceDTO() {
        return this;
    }

    public List<NewsBoardDTO> latestNews() {
        List<NewsBoard> list = service.latestNews();

        if (list.size() > 20) {
            list = list.subList(0, 20);
        }

        List<NewsBoardDTO> answer = new ArrayList<>(list.size());
        for (NewsBoard news : list) {
            answer.add(getMapper().convertToDTO(news));
        }

        return answer;
    }
}
