/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.aaa.util.SecurityLog;
import com.clarolab.config.properties.ApplicationConfigurationProperties;
import com.clarolab.model.User;
import com.clarolab.model.types.RoleType;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.UserPreferencesRepository;
import com.clarolab.repository.UserRepository;
import com.clarolab.service.exception.ConfigurationError;
import com.clarolab.service.exception.InvalidDataException;
import com.clarolab.service.exception.ServiceException;
import com.clarolab.startup.LicenceValidator;
import com.clarolab.util.LogicalCondition;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.clarolab.util.Constants.*;
import static com.clarolab.util.StringUtils.*;

@Service
@Log
public class UserService extends BaseService<User> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private SecurityLog securityLog;

    @Autowired
    private ApplicationConfigurationProperties applicationConfigurationProperties;

    @Autowired
    private LicenceValidator licenceValidator;

    @Autowired
    private NotificationService notificationService;


   /* @Autowired
    private PasswordEncoder passwordEncoder;*/

    @Override
    public BaseRepository<User> getRepository() {
        return userRepository;
    }


    public User findByUsername(String username) {
        User user = null;
        if (isValidEmailAddress(username))
            user = userRepository.findUserByUsernameIgnoreCase(username.trim());
        else
            throw new InvalidDataException(parseDataError("Invalid Username", username));
        return user;
    }

    public User findByRealname(String realname) {
        User user = null;
        if (org.eclipse.jgit.util.StringUtils.isEmptyOrNull(realname))
            throw new InvalidDataException(parseDataError("Invalid Realname", realname));

        user = userRepository.findUserByRealnameIgnoreCase(realname);
        return user;
    }

    @Override
    public User save(User entry) throws ServiceException {
        long countEnabled = this.countEnabled();

        if (!isInternalUserEnabled())
            throw new ConfigurationError("Unable to create users when INTERNAL_LOGIN_ENABLED is false");

        if (countEnabled != 0) {
            if (!licenceValidator.validateUserCreation()) {
                log.info("License not valid...");
                throw new ConfigurationError("Unable to create user, you have reached the limit of " + DEFAULT_FREE_LICENSE_MAX_USERS + " users");
            }
        }

        return super.save(entry);
    }

    public User update(User user) throws ServiceException {
        if (LogicalCondition.NOT(isValidEmailAddress(user.getUsername())))
            throw new InvalidDataException(parseDataError("Invalid Username", user.getUsername()));

        if (LogicalCondition.OR(isEmpty(user.getRealname()), LogicalCondition.NOT(isValidLength(user.getRealname(), DEFAULT_MINIMUN_REAL_NAME_LENGTH))))
            throw new InvalidDataException(parseDataError("Invalid Realname", user.getRealname()));

        if (user.getAvatar() == null)
            throw new InvalidDataException(parseDataError("Invalid Avatar", user.getAvatar()));

        if (user.getRoleType() == null)
            throw new InvalidDataException(parseDataError("Invalid RoleTypes", user.getUsername()));

        return super.update(user);
    }

    public List<User> search(String name) {
        if (name == null || name.length() < MIN_SEARCH_LENGHT_)
            return Lists.newArrayList();

        name = StringUtils.prepareStringForSearch(name);
        return userRepository.search(name, name);

    }

    public String getEncryptedPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    public String getEncryptedUsername(String plainUsername) {
        return passwordEncoder.encode(plainUsername);
    }

    public void updatePassword(User user, String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            return;
        }

        user.setPassword(getEncryptedPassword(plainPassword));
        securityLog.info(String.format("Password changed for user: %s", user.getUsername()));
    }

    public boolean isInternalUserEnabled() {
        if (!isInternalUserEnabledByConfigurationProperty()) {
            return false;
        }
        return propertyService.valueOf(INTERNAL_LOGIN_ENABLED, DEFAULT_INTERNAL_LOGIN_ENABLED);
    }

    private boolean isInternalUserEnabledByConfigurationProperty() {
        return applicationConfigurationProperties.isInternalUsersEnabled();
    }

    public boolean canLogin(User user) {
        if (!user.isEnabled()) {
            return false;
        }

        if (user.isInternal()) {
            return isInternalUserEnabled();
        }

        return true;
    }

    public Optional<User> getAnyAdminUser() {
        return userRepository.findAll()
                .stream()
                .filter(User::isAdmin).findAny();
    }

    public List<User> getAllAdminUser() {
        return userRepository.findAllAdmin(RoleType.ROLE_ADMIN);
    }

    public List<User> findUsersWithoutSlack() {
        return userRepository.findAllBySlackIdIsNullAndEnabled(true);
    }


}
