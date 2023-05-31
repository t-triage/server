package com.clarolab.logtriage.mapper;

import com.clarolab.logtriage.dto.SearchExecutorDTO;
import com.clarolab.logtriage.model.SearchExecutor;
import com.clarolab.logtriage.service.LogConnectorService;
import com.clarolab.logtriage.service.SearchExecutorService;
import com.clarolab.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.clarolab.mapper.MapperHelper.getIDList;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Service
public class SearchExecutorMapper implements Mapper<SearchExecutor, SearchExecutorDTO> {

    @Autowired
    private SearchExecutorService service;

    @Autowired
    private LogConnectorService connectorService;

    @Override
    public SearchExecutorDTO convertToDTO(SearchExecutor searchExecutor) {
        SearchExecutorDTO dto = new SearchExecutorDTO();

        setEntryFields(searchExecutor, dto);

        dto.setName(searchExecutor.getName());
        dto.setSearch(searchExecutor.getSearch());
        dto.setPattern(searchExecutor.getPattern());
        dto.setPackageNames(searchExecutor.getPackageNames());
        dto.setLogConnector(searchExecutor.getLogConnector() != null ? searchExecutor.getLogConnector().getId() : null);
        dto.setLogAlerts(getIDList(searchExecutor.getAlerts()));

        return dto;
    }

    @Override
    public SearchExecutor convertToEntity(SearchExecutorDTO dto) {
        SearchExecutor searchExecutor;

        if (dto.getId() == null || dto.getId() < 1) {
            searchExecutor = SearchExecutor.builder()
                    .name(dto.getName())
                    .search(dto.getSearch())
                    .pattern(dto.getPattern())
                    .packageNames(dto.getPackageNames())
                    .logConnector(connectorService.find(dto.getLogConnector()))
                    .build();
        } else {
            searchExecutor = service.find(dto.getId());
            searchExecutor.setName(dto.getName());
            searchExecutor.setSearch(dto.getSearch());
            searchExecutor.setPattern(dto.getPattern());
            searchExecutor.setPackageNames(dto.getPackageNames());
            searchExecutor.setLogConnector(connectorService.find(dto.getLogConnector()));
        }

        return searchExecutor;
    }
}
