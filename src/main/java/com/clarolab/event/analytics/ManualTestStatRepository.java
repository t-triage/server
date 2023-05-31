package com.clarolab.event.analytics;

import com.clarolab.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManualTestStatRepository extends BaseRepository<ManualTestStat> {
    List<ManualTestStat> findByTimestampBetween(long prev, long now);
}
