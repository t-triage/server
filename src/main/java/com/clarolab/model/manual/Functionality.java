package com.clarolab.model.manual;

import com.clarolab.model.Entry;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

import static com.clarolab.util.Constants.TABLE_MANUAL_TEST_FUNCTIONALITY;

@Entity
@Table(name = TABLE_MANUAL_TEST_FUNCTIONALITY, indexes = {

})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Functionality extends Entry<ManualTestCase> {
    private String name;
    private String risk;

    private String story;
    private String externalId;


    @Builder
    private Functionality(Long id, boolean enabled, long updated, long timestamp, String name,
                          String risk, String story, String externalId) {

            super(id, enabled, updated, timestamp);
            this.name = name;
            this.risk = risk;

            this.story = story;
            this.externalId = externalId;
    }

}
