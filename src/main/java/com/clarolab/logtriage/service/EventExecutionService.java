package com.clarolab.logtriage.service;

import com.clarolab.logtriage.model.EventExecution;
import com.clarolab.logtriage.repository.EventExecutionRepository;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventExecutionService extends BaseService<EventExecution> {

    @Autowired
    private ErrorCaseService errorCaseService;

    @Autowired
    private EventExecutionRepository repository;

    @Override
    protected BaseRepository<EventExecution> getRepository() {
        return this.repository;
    }

    public Boolean exists(EventExecution event) {
        return this.repository.exists(event.getContent());
    }

    public List<EventExecution> getEventsFromErrorCase(Long errorCase) {
        return repository.findAllByErrorCase(errorCase);
    }

    public List<EventExecution> saveAll(List<EventExecution> events) {
        return repository.saveAll(events);
    }

}
