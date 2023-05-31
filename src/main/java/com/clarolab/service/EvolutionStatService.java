package com.clarolab.service;

import com.clarolab.event.analytics.EvolutionStat;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.EvolutionStatRepository;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class EvolutionStatService extends BaseService<EvolutionStat> {

    @Autowired
    private EvolutionStatRepository evolutionStatRepository;

    @Override
    protected BaseRepository<EvolutionStat> getRepository() {
        return evolutionStatRepository;
    }

    public List<EvolutionStat> findAllEvolutionStatByExecutorSince(Long executorid, Long from, Long to) {
        if (from == null)
            from = DateUtils.offSetDays(-30);
        if (to == null)
            to = DateUtils.offSetDays(0);
        return evolutionStatRepository.findAllByExecutorSince(executorid, from, to);
    }

}
