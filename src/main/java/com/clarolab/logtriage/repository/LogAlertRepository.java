package com.clarolab.logtriage.repository;

import com.clarolab.logtriage.model.LogAlert;
import com.clarolab.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LogAlertRepository extends BaseRepository<LogAlert> {

    @Query("SELECT MAX(lastCheck) FROM LogAlert")
    Long latestCheckedAlertDate();

    LogAlert findTopByOrderByLastCheckDesc();

}
