package com.clarolab.dto.db;

import com.clarolab.dto.BaseDTO;
import com.clarolab.model.TestCase;
import com.clarolab.model.TestExecution;
import com.clarolab.model.User;
import com.clarolab.util.StringUtils;
import lombok.Data;

@Data
public class TestTriagePassedDTO extends BaseDTO {
    private String displayName;
    private String groupName;
    private String triagerDisplayName;

    public TestTriagePassedDTO(long id, long timestamp, TestExecution testExecution, User triager) {
        this.setId(id);
        this.setTimestamp(timestamp);
        this.displayName = testExecution.getDisplayName();
        this.groupName =  getGroupName(testExecution);
        this.triagerDisplayName = triager.getDisplayName();
    }

    public String getGroupName(TestExecution testExecution) {
        String suiteName = StringUtils.classTail(testExecution.getSuiteName());
        String groupName = getGroupName(testExecution.getTestCase());

        if (StringUtils.isEmpty(groupName) || groupName.equalsIgnoreCase(suiteName)) {
            return suiteName;
        } else {
            if (StringUtils.isEmpty(suiteName)) {
                return groupName;
            } else {
                return String.format("%s: %s", suiteName, groupName);
            }
        }
    }

    public String getGroupName(TestCase testCase) {
        return StringUtils.classTail(testCase.getLocationPath());
    }
}
