package com.clarolab.unit.test;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataProviderElement {

    private String attribute1;
    private String attribute2;

    @Override
    public String toString(){
        return "DataProviderElement(" + attribute1 + ", " + attribute2 + ")";
    }
}
