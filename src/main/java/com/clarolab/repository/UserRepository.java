/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.aaa.AuthenticationProvider;
import com.clarolab.model.User;
import com.clarolab.model.types.RoleType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User> {

    User findUserByUsernameIgnoreCase(String username);

    User findUserByRealnameIgnoreCase(String realname);

    @Query("SELECT u FROM User u WHERE (LOWER(realname) like ?1 OR LOWER(username) like ?2) AND enabled = true ORDER BY realname")
    List<User> search(String realname, String username);

    List<User> findAllByRealnameIgnoreCaseLikeOrUsernameLikeAndEnabled(String realname, String username, boolean enabled);
    
    List<User> findAllBySlackIdIsNullAndEnabled(boolean enabled);

    @Query("SELECT u FROM User u WHERE u.roleType = ?1 AND u.enabled = true")
    List<User> findAllAdmin(RoleType roleType);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    long countByEnabledAndProvider(boolean enabled, AuthenticationProvider provider);

}
