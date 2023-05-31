package com.clarolab.dto.db;

import com.clarolab.dto.BaseDTO;
import com.clarolab.model.helper.tag.TagHelper;
import com.clarolab.model.types.ApplicationFailType;
import com.clarolab.model.types.StateType;
import com.clarolab.model.types.TestFailType;
import com.clarolab.util.StringUtils;
import lombok.Data;

@Data
public class TestTriageHistoryDTO  extends BaseDTO {
    private StateType currentState;
    private int stateNumber;
    private ApplicationFailType applicationFailType;
    private TestFailType testFailType;
    private boolean triaged;
    private boolean expired;
    private int buildNumber;
    private boolean autoTriaged;
    private String tags;

    public TestTriageHistoryDTO(long id, StateType currentState, ApplicationFailType applicationFailType, TestFailType testFailType, boolean triaged, boolean expired, String tags, int buildNumber, long timestamp) {
        this.setId(id);
        this.setTimestamp(timestamp);
        this.currentState = currentState;
        this.applicationFailType = applicationFailType;
        this.testFailType = testFailType;
        this.triaged = triaged;
        this.expired = expired;
        this.buildNumber = buildNumber;
        this.tags = tags;
    }

    public void initialize() {
        populateAutoTriage();
        populateState();
    }

    private void populateState() {
        switch (currentState) {
            case PASS:
                stateNumber = 1;
                break;
            case NEWPASS:
                stateNumber = 2;
                break;
            case PERMANENT:
                stateNumber = 3;
                break;
            case FAIL:
                stateNumber = 4;
                break;
            case NEWFAIL:
                stateNumber = 5;
                break;
            default:
                stateNumber = 6;
                break;
        }
    }

    private void populateAutoTriage() {
        if (StringUtils.isEmpty(tags)) {
            autoTriaged = false;
        } else {
            autoTriaged = tags.contains(TagHelper.AUTO_TRIAGED);
        }
        tags = null;
    }
}
