package com.clarolab.dto;

import com.clarolab.model.manual.types.ExecutionStatusType;
import lombok.Data;

@Data
public class ManualTestPlanStatDTO extends BaseDTO {

    private String name;
    private UserDTO assigneeDTO;
    private Long fromDate;
    private Long toDate;
    private ExecutionStatusType status;
    private long value;
    private int pending;
    private int pass;
    private int fail;
    private int blocked;
    private int no;
    private int inProgress;
    private int undefined;

    public ManualTestPlanStatDTO(long id, String name, long fromDate, long toDate, ExecutionStatusType status, long total) {
        this.setId(id);
        this.name = name;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.status = status;
        this.value = total;
    }

    public void assignValueTo(ManualTestPlanStatDTO stat) {
        if (ExecutionStatusType.PENDING.equals(getStatus())) {
            stat.setPending(getStatusValue());
        }
        else if (ExecutionStatusType.PASS.equals(getStatus())) {
            stat.setPass(getStatusValue());
        }
        else if (ExecutionStatusType.FAIL.equals(getStatus())) {
            stat.setFail(getStatusValue());
        }
        else if (ExecutionStatusType.BLOCKED.equals(getStatus())) {
            stat.setBlocked(getStatusValue());
        }
        else if (ExecutionStatusType.NO.equals(getStatus())) {
            stat.setNo(getStatusValue());
        }
        else if (ExecutionStatusType.IN_PROGRESS.equals(getStatus())) {
            stat.setInProgress(getStatusValue());
        }
        else if (ExecutionStatusType.UNDEFINED.equals(getStatus())) {
            stat.setUndefined(getStatusValue());
        }
    }


    public int getStatusValue() {
        return (int) value;
    }
}
