package com.clarolab.event.process;

import com.clarolab.model.ApplicationEvent;
import com.clarolab.model.CVSRepository;
import com.clarolab.service.CVSRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ReadCVSRepositoryEventHandler extends AbstractEventHandler implements EventHandler {

    @Autowired
    private CVSRepositoryService cvsRepositoryService;

    @Override
    public ApplicationEventType[] handleTypes() {
        ApplicationEventType[] handleTypes = {ApplicationEventType.TIME_NEW_DAY};
        return handleTypes;
    }

    @Override
    public boolean process(ApplicationEvent event) {

        read();

        return true;
    }

    @Transactional
    public boolean read() {

        List<CVSRepository> repositories = cvsRepositoryService.findAll();

            for (CVSRepository cvsRepository : repositories){
                cvsRepositoryService.read(cvsRepository);
            }

        return true;
    }

    @Override
    public Integer getPriority() {
        return 5;
    }
}
