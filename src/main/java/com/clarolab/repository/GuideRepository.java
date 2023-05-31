/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.onboard.Guide;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuideRepository extends BaseRepository<Guide> {

    List<Guide> findAllByPageUrlAndEnabled(String page, boolean enabled);

}
