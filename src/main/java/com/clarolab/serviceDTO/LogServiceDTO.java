package com.clarolab.serviceDTO;

import com.clarolab.dto.LogCommitsPerDayDTO;
import com.clarolab.dto.LogCommitsPerPersonAndPerDayDTO;
import com.clarolab.dto.LogCommitsPerPersonDTO;
import com.clarolab.dto.LogDTO;
import com.clarolab.dto.chart.ChartCategoryDTO;
import com.clarolab.dto.chart.ChartSerieDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.LogMapper;
import com.clarolab.model.CVSLog;
import com.clarolab.service.LogService;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LogServiceDTO implements BaseServiceDTO<CVSLog, LogDTO, LogMapper> {

    @Autowired
    private LogService service;

    @Autowired
    private LogMapper mapper;

    @Override
    public TTriageService<CVSLog> getService() {
        return service;
    }

    @Override
    public Mapper<CVSLog, LogDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<CVSLog, LogDTO, LogMapper> getServiceDTO() {
        return this;
    }

    public List<LogCommitsPerPersonDTO> getCommitsPerPerson() {
        return service.getCommitsPerPerson();
    }

    public List<LogCommitsPerDayDTO> getCommitsPerDay() {

        return service.getCommitsPerDay();
    }

    public List<ChartSerieDTO> getCommitsPerPersonAndPerDay() {

        List<LogCommitsPerPersonAndPerDayDTO> list = service.getCommitsPerPersonAndPerDay();
        List<ChartSerieDTO> answer = new ArrayList<>();

        for (LogCommitsPerPersonAndPerDayDTO l : list) {
            ChartSerieDTO serie = new ChartSerieDTO();
            serie.setName(l.getAuthorName());
            serie.getData().add(new ChartCategoryDTO(l.getCommitDay(), l.getCommitCount()));

            if (answer.isEmpty()) {
                answer.add(serie);
            } else {
                // Check on list whether it is already an author with that name
                boolean contains = answer.stream().anyMatch(o -> o.getName().equals(l.getAuthorName()));
                if (contains) {
                    // If it exists get that object
                    ChartSerieDTO present = answer.stream().filter(o -> o.getName().equals(l.getAuthorName())).findFirst().get();
                    if (present.getName().equals(l.getAuthorName())) {
                        present.getData().add(serie.getData().get(0));
                    }
                } else {
                    answer.add(serie);
                }
            }
        }
        return answer;
    }

}
