/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.aaa.AuthenticationProvider;
import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.dto.UserDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.ImageModel;
import com.clarolab.model.User;
import com.clarolab.model.types.RoleType;
import com.clarolab.service.PropertyService;
import com.clarolab.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;
import static com.clarolab.util.Constants.*;
import static com.clarolab.util.StringUtils.isEmpty;

@Component
public class UserMapper implements Mapper<User, UserDTO> {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Autowired
    private PropertyService propertyService;

    @Override
    public UserDTO convertToDTO(User user) {
        /* El user en esta capa NO deberia ser null. Si llega null es porque hay algo mal
        /*if (user == null) {
            return null;
        }*/
        UserDTO userDTO = new UserDTO();

        setEntryFields(user, userDTO);

        userDTO.setUsername(user.getUsername()); // GDPR to disable
        userDTO.setAvatar(user.getAvatar() == null ? null : user.getAvatar().getData());
        userDTO.setRealname(user.getRealname());
        userDTO.setAgreedTermsConditions(getTermAndConditionValue(user));
        userDTO.setInternal(user.isInternal());
        userDTO.setDisplayName(user.getDisplayName());
        if (user.getProvider() != null) {
            changeProviderToDto(userDTO, user);
        } else {
            userDTO.setProvider(PROVIDER_INTERNAL); //Set null values from the Provider field to internal
        }
        if (user.getRoleType() != null)
            userDTO.setRoleType(user.getRoleType().name());

        return userDTO;
    }

    //Method to convert the Provider Entity Data to DTO
    private void changeProviderToDto(UserDTO userDTO, User user) {
        switch (user.getProvider().name()) {
            case "internal":
                userDTO.setProvider(PROVIDER_INTERNAL);
                break;
            case "google":
                userDTO.setProvider(PROVIDER_GOOGLE);
                break;
            case "onelogin":
                userDTO.setProvider(PROVIDER_ONELOGIN);
                break;
            case "okta":
                userDTO.setProvider(PROVIDER_OKTA);
                break;
        }
    }

    public UserDTO convertToDTOTiny(User user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setEnabled(user.isEnabled());
        userDTO.setDisplayName(user.getDisplayName());

        return userDTO;
    }

    //in a near future we will move this to an external service
    private boolean getTermAndConditionValue(User user) {
        Long value = propertyService.valueOf(TERMS_AND_CONDITION_ACCEPTED_TIME, DEFAULT_TERMS_AND_CONDITION_ACCEPTED_TIME);
        return user.getTimestampTermsConditions() >= value && user.isAgreedTermsConditions();
    }

    @Override
    public User convertToEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = null;
        User loggedUser = authContextHelper.getCurrentUser();

        if (!canUpdate(loggedUser, user, dto)) {
            return null;
        }

        // Get the role type
        if (dto.getId() == null || dto.getId() < 1) {
            user = User.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .agreedTermsConditions(dto.isAgreedTermsConditions())
                    .timestampTermsConditions(dto.getTimestampTermsConditions())
                    .avatar(ImageModel
                            .builder()
                            .enabled(dto.getEnabled())
                            .timestamp(dto.getTimestamp())
                            .updated(dto.getUpdated())
                            .description("Image from: " + dto.getUsername())
                            .name("Image from: " + dto.getUsername())
                            .data(dto.getAvatar())
                            .build())
                    .realname(dto.getRealname())
                    .username(dto.getUsername())
                    .build();
            changeProviderToEntity(user, dto.getProvider());
            userService.updatePassword(user, dto.getPassword());
            changeRoleToEntity(loggedUser, user, dto);
        } else {
            user = userService.find(dto.getId());
            user.setUpdated(dto.getUpdated());
            user.setEnabled(dto.getEnabled());
            user.setAgreedTermsConditions(dto.isAgreedTermsConditions());
            user.setTimestampTermsConditions(dto.getTimestampTermsConditions());
            userService.updatePassword(user, dto.getPassword());
            if (user.getProvider() == null) {   //If provider != null means that the user is external, so... I shouldn't change these values
                user.setUsername(dto.getUsername());
                user.setRealname(dto.getRealname());
            } else {
                changeProviderToEntity(user, dto.getProvider());
            }
            changeRoleToEntity(loggedUser, user, dto);
        }
        return user;
    }

    //Method to convert the Provider DTO Data to Entity
    private void changeProviderToEntity(User entity, String dto) {
        switch (dto) {
            case PROVIDER_GOOGLE:
                entity.setProvider(AuthenticationProvider.google);
                break;
            case PROVIDER_ONELOGIN:
                entity.setProvider(AuthenticationProvider.onelogin);
                break;
            case PROVIDER_OKTA:
                entity.setProvider(AuthenticationProvider.okta);
                break;
            default:
                entity.setProvider(AuthenticationProvider.internal);
                break;
        }
    }

    private void changeRoleToEntity(User loggedUser, User entity, UserDTO dto) {
        if (StringUtils.isEmpty(dto.getRoleType())) {
            return;
        }

        if (loggedUser.isAdmin()) {
            entity.setRoleType(RoleType.valueOf(dto.getRoleType()));
        }

        if (entity.getRoleType() == null) {
            entity.setRoleType(RoleType.ROLE_USER);
        }
    }

    private boolean canUpdate(User loggedUser, User entity, UserDTO dto) {
        if (loggedUser == null) {
            return false;
        }
        if (loggedUser.isAdmin()) {
            return true;
        }

        if (!isEmpty(dto.getUsername()) && loggedUser.getUsername().equals(dto.getUsername())) {
            return true;
        }

        if (entity == null || isEmpty(entity.getUsername())) {
            return false;
        }

        return loggedUser.getUsername().equals(entity.getUsername());

    }

}
