/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.UserPreference;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.UserPreferencesRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class UserPreferenceService extends BaseService<UserPreference> {

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Override
    public BaseRepository<UserPreference> getRepository() {
        return userPreferencesRepository;
    }
}
