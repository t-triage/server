package com.clarolab.event.analytics;

import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class ManualTestStatService extends BaseService<ManualTestStat> {
    @Autowired
    private ManualTestStatRepository manualTestStatRepository;

    @Override
    public BaseRepository<ManualTestStat> getRepository() { return manualTestStatRepository; }

    public List<ManualTestStat> findByTimestampBetween(long prev, long now) {
        return manualTestStatRepository.findByTimestampBetween(prev, now);
    }

}
