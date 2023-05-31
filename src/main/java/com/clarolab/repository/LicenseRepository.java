package com.clarolab.repository;

import com.clarolab.startup.License;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LicenseRepository extends BaseRepository<License> {

    License findTopByEnabledIsTrue();

    @Transactional
    @Modifying
    @Query("UPDATE License SET enabled = false WHERE enabled = true")
    int disableExpiredLicense();

}
