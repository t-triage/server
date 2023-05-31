/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.aaa.user;

import com.clarolab.aaa.exception.OAuth2AuthenticationProcessingException;
import com.clarolab.aaa.model.UserPrincipal;
import com.clarolab.aaa.model.oauth.OAuth2UserInfo;
import com.clarolab.model.ImageModel;
import com.clarolab.model.User;
import com.clarolab.model.types.RoleType;
import com.clarolab.repository.UserRepository;
import com.clarolab.service.ApplicationDomainService;
import com.clarolab.service.ImageModelService;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.clarolab.aaa.AuthenticationProvider.valueOf;
import static com.clarolab.aaa.OAuth2UserInfoFactory.getOAuth2UserInfo;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageModelService imageModelService;

    @Autowired
    private ApplicationDomainService domainService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        if(!domainService.isValidEmail(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("External user domain not allowed");
        }

        Optional<User> userOptional = userRepository.findByUsername(oAuth2UserInfo.getEmail());
        User user;
        if(userOptional.isPresent()) {
            user = userOptional.get();
            if(!valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()).equals(user.getProvider())){
                throw new OAuth2AuthenticationProcessingException(
                        "You have signed up with " + user.getProvider() + " account. " +
                                "Please use your " + user.getProvider() + " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();

        user.setEnabled(true);
        long now = DateUtils.now();
        user.setTimestamp(now);
        user.setUpdated(now);
        user.setLastLoginTime(now);
        user.setProvider(valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setRealname(oAuth2UserInfo.getName());
        user.setUsername(oAuth2UserInfo.getEmail());
        user.setRoleType(getDefaultUserRole());
        user.setAvatar(ImageModel
                .builder()
                .enabled(user.isEnabled())
                .name(user.getUsername())
                .description(user.getUsername())
                .updated(user.getUpdated())
                .timestamp(user.getTimestamp())
                .data(oAuth2UserInfo.getImageUrl())
                .build());
        //We go directly to repository here, since we have particular checks for oauth uses that don't belong to normal users.
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        ImageModel avatar = existingUser.getAvatar();
        if (avatar != null) {
            avatar.setData(oAuth2UserInfo.getImageUrl());
            imageModelService.update(avatar);

        } else {
            avatar = ImageModel
                    .builder()
                    .enabled(existingUser.isEnabled())
                    .name(existingUser.getUsername())
                    .description(existingUser.getUsername())
                    .data(oAuth2UserInfo.getImageUrl())
                    .build();
            imageModelService.save(avatar);
        }
        existingUser.setRealname(oAuth2UserInfo.getName());
        existingUser.setAvatar(avatar);
        existingUser.setLastLoginTime(DateUtils.now());
        //We go directly to repository here, since we have particular checks for oauth uses that don't belong to normal users.
        return userRepository.save(existingUser);
    }

    private RoleType getDefaultUserRole() {
        long count = userRepository.countByEnabledAndProvider(true,null);

        if (count > 0) {
            return RoleType.ROLE_USER;
        } else {
            return RoleType.ROLE_ADMIN;
        }
    }

}
