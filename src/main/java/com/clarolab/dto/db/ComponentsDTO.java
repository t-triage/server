package com.clarolab.dto.db;

import com.clarolab.dto.BaseDTO;
import com.clarolab.model.manual.ProductComponent;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ComponentsDTO extends BaseDTO {
    private ProductComponent component1;
    private ProductComponent component2;
    private ProductComponent component3;

    public ComponentsDTO(ProductComponent component1, ProductComponent component2, ProductComponent component3) {
        this.component1 = component1;
        this.component2 = component2;
        this.component3 = component3;
    }

    public Set<ProductComponent> getComponents() {
        Set<ProductComponent> answer = new HashSet<>();
        if (component1 != null) {
            answer.add(component1);
        }
        if (component2 != null) {
            answer.add(component2);
        }
        if (component3 != null) {
            answer.add(component3);
        }

        return answer;
    }
}
