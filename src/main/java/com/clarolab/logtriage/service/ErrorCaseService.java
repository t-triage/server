package com.clarolab.logtriage.service;

import com.clarolab.logtriage.model.ErrorCase;
import com.clarolab.logtriage.repository.ErrorCaseRepository;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import com.clarolab.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ErrorCaseService extends BaseService<ErrorCase> {

    private static final double MESSAGE_SIMILARITY_THRESHOLD = 0.8;
    private static final double STACKTRACE_SIMILARITY_THRESHOLD = 0.7;

    @Autowired
    private ErrorCaseRepository repository;

    @Override
    protected BaseRepository<ErrorCase> getRepository() {
        return this.repository;
    }

    public ErrorCase findError(String level, String path, String message) {
        return this.repository.findByLevelAndPathAndMessage(level, path, message);
    }

    public ErrorCase findErrorBySimilarity(String level, String path, String message, String stackTrace) {
        List<ErrorCase> errors = this.repository.findAllByLevelAndPath(level, path);
        ErrorCase errorCase = null;

        double minMessageSimilarity = 0.0;
        for (ErrorCase error : errors) {
            double messageSimilarity = StringUtils.getSimilarity(error.getMessage(), message, MESSAGE_SIMILARITY_THRESHOLD, true);

            if (messageSimilarity >= MESSAGE_SIMILARITY_THRESHOLD && messageSimilarity > minMessageSimilarity) {
                if (messageSimilarity == 1.0) {
                    errorCase = error;
                    break;
                } else {
                    double stackTraceSimilarity = StringUtils.getSimilarity(error.getStackTrace(), stackTrace, STACKTRACE_SIMILARITY_THRESHOLD);

                    if (stackTraceSimilarity >= STACKTRACE_SIMILARITY_THRESHOLD) {
                        errorCase = error;
                        minMessageSimilarity = messageSimilarity;
                    }
                }
            }
        }

        return errorCase;
    }

}
