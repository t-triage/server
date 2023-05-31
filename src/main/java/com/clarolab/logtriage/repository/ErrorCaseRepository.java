package com.clarolab.logtriage.repository;

import com.clarolab.logtriage.model.ErrorCase;
import com.clarolab.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorCaseRepository extends BaseRepository<ErrorCase> {

    ErrorCase findByLevelAndPathAndMessage(String level, String path, String message);

    List<ErrorCase> findAllByLevelAndPath(String level, String path);

}
