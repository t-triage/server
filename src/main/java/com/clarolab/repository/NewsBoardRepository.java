/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.NewsBoard;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsBoardRepository extends BaseRepository<NewsBoard> {

    List<NewsBoard> findAllByEventTimeGreaterThanOrderByIdDesc(long time);

}
