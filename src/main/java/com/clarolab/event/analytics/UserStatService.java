package com.clarolab.event.analytics;

import com.clarolab.model.CVSLog;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;

import static com.clarolab.util.DateUtils.BaseDateFormat;

@Service
@Log
public class UserStatService extends BaseService<UserStat> {

    @Autowired
    private UserStatRepository userStatRepository;

    @Override
    protected BaseRepository<UserStat> getRepository() { return userStatRepository; }

    public void processUserStat(List<CVSLog> logs) {
        Integer testsUpdated = 1;
        Integer commits = 1;
        Integer linesUpdated = 0;

        if (!logs.isEmpty()) {
            CVSLog prevLog = logs.get(0);
            int i = 0;

            log.log(Level.INFO, String.format("UserStat: Starting to process %d logs", logs.size()));
            for (CVSLog cvsLog: logs) {
                if (cvsLog.getAuthor() == null || cvsLog.getTest() == null)
                    continue;

                if (cvsLog.getAuthor().equals(prevLog.getAuthor())) {
                    linesUpdated += cvsLog.getUpdatedLines();
                    if (!cvsLog.getTest().equals(prevLog.getTest()))
                        testsUpdated++;
                    if (!cvsLog.getCommitHash().equals(prevLog.getCommitHash()))
                        commits++;
                } else {
                    UserStat userStat = UserStat.builder()
                            .actualDate(BaseDateFormat.format(DateUtils.now()))
                            .commits(commits)
                            .testsUpdated(testsUpdated)
                            .linesUpdated(linesUpdated)
                            .user(prevLog.getAuthor())
                            .build();
                    save(userStat);

                    testsUpdated = 1;
                    commits = 1;
                    linesUpdated = cvsLog.getUpdatedLines();
                }

                i++;
                if (i == logs.size()) {
                    UserStat userStat = UserStat.builder()
                            .actualDate(BaseDateFormat.format(DateUtils.now()))
                            .commits(commits)
                            .testsUpdated(testsUpdated)
                            .linesUpdated(linesUpdated)
                            .user(cvsLog.getAuthor())
                            .build();
                    save(userStat);
                }
                prevLog = cvsLog;
            }

            log.log(Level.INFO, String.format("UserStat: Finished processing UserStats"));
        } else {
            log.log(Level.INFO, String.format("UserStat: No data to process"));
        }
    }

}
