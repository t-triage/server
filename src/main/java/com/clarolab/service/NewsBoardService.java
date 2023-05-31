/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.model.NewsBoard;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.NewsBoardRepository;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class NewsBoardService extends BaseService<NewsBoard> {

    @Autowired
    private NewsBoardRepository newsBoardRepository;

    @Override
    public BaseRepository<NewsBoard> getRepository() {
        return newsBoardRepository;
    }

    public NewsBoard create(String text, long time, ApplicationEventType type) {
        if (time == 0) {
            time = DateUtils.now();
        }
        NewsBoard news = NewsBoard.builder()
                .message(text)
                .eventTime(time)
                .type(type)
                .build();
        news = save(news);

        return news;
    }

    public List<NewsBoard> latestNews() {
        long yestarday = DateUtils.beginDay(-1);
        return newsBoardRepository.findAllByEventTimeGreaterThanOrderByIdDesc(yestarday);
    }
}
