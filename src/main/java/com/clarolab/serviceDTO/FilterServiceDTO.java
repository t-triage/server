package com.clarolab.serviceDTO;

import com.clarolab.dto.FilterDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FilterServiceDTO {

    public boolean contains(String key, List<FilterDTO> filters){
        return filters
                .stream()
                .anyMatch(filterDTO -> filterDTO.getName().equalsIgnoreCase(key));
    }

    public Optional<FilterDTO> get(String key, List<FilterDTO> filters){
        return filters
                .stream()
                .filter(filterDTO -> filterDTO.getName().equalsIgnoreCase(key))
                .findFirst();
    }

    public Object valueOf(String key, List<FilterDTO> filters) {
        Optional<FilterDTO> filterDTO = get(key, filters);
        return filterDTO.orElse(null);
    }

    private boolean valueOf(boolean value){
        return value;
    }

}
