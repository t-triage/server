package com.clarolab.logtriage.service;

import com.clarolab.logtriage.model.SearchExecutor;
import com.clarolab.logtriage.repository.SearchExecutorRepository;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class SearchExecutorService extends BaseService<SearchExecutor> {

    @Autowired
    private SearchExecutorRepository repository;

    @Override
    protected BaseRepository<SearchExecutor> getRepository() {
        return this.repository;
    }

    public SearchExecutor find(String name) {
        return this.repository.findTopByName(name);
    }

}
