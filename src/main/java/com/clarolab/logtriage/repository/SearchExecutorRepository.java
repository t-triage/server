package com.clarolab.logtriage.repository;

import com.clarolab.logtriage.model.SearchExecutor;
import com.clarolab.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchExecutorRepository extends BaseRepository<SearchExecutor> {
    SearchExecutor findTopByName(String name);
}
