package com.clarolab.repository;

import com.clarolab.dto.LogCommitsPerDayDTO;
import com.clarolab.dto.LogCommitsPerPersonAndPerDayDTO;
import com.clarolab.dto.LogCommitsPerPersonDTO;
import com.clarolab.model.CVSLog;
import com.clarolab.model.CVSRepository;
import com.clarolab.model.TestCase;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LogRepository extends BaseRepository<CVSLog> {

    List<CVSLog> findByCommitHashAndEnabledTrue(String commitHash);

    CVSLog findTopByCommitHashAndTest(String commitHash, TestCase test);

    // Commits by author
    @Query("SELECT new com.clarolab.dto.LogCommitsPerPersonDTO(c.author.id, c.author.realname, count(c)) FROM CVSLog c WHERE c.commitDate > ?1 AND c.commitDate < ?2 AND c.enabled = true GROUP BY c.author.id, c.author.realname ORDER BY count(c) DESC")
    List<LogCommitsPerPersonDTO> countCommitsByAuthor(long prev, long now);

    @Query("SELECT new com.clarolab.dto.LogCommitsPerDayDTO(c.commitDate, count(c), c.commitDay) FROM CVSLog c WHERE c.commitDate > ?1 AND c.commitDate < ?2 AND c.enabled = true GROUP BY c.commitDay, c.commitDate")
    List<LogCommitsPerDayDTO> countCommitsByDay(long prev, long now);

    @Query("SELECT new com.clarolab.dto.LogCommitsPerPersonAndPerDayDTO(c.author.id, c.author.realname, c.commitDate, count(c), c.commitDay) FROM CVSLog c WHERE c.commitDate > ?1 AND c.commitDate < ?2 AND c.enabled = true GROUP BY c.commitDay, c.commitDate, c.author.id, c.author.realname")
    List<LogCommitsPerPersonAndPerDayDTO> countCommitsByAuthorAndByDay(long prev, long now);

    long deleteByTimestampLessThan(long timestamp);

    List<CVSLog> findByCvsRepository(CVSRepository cvsRepository);

    @Query("SELECT log FROM CVSLog log WHERE log.commitDate > ?1 AND log.commitDate < ?2 ORDER BY log.author.id, log.test.id, log.commitHash, log.commitDate")
    List<CVSLog> findAllBetweenGroupByAuthor(long prev, long now);
}
