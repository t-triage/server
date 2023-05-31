package com.clarolab.repository;

import com.clarolab.model.CVSRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CVSRepositoryRepository extends BaseRepository<CVSRepository> {

    @Query("SELECT url, id FROM CVSRepository")
    List<Object[]> findAllNames();



}
