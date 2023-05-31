/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.unit.test;

import com.clarolab.config.properties.ApplicationConfigurationProperties;
import com.clarolab.model.User;
import com.clarolab.populate.DataProvider;
import com.clarolab.repository.UserRepository;
import com.clarolab.service.PropertyService;
import com.clarolab.service.UserService;
import com.clarolab.service.exception.InvalidDataException;
import com.clarolab.service.exception.NotFoundServiceException;
import com.clarolab.service.exception.ServiceException;
import com.clarolab.unit.BaseUnitTest;
import com.clarolab.util.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;

public class UserServiceUnitTest extends BaseUnitTest {

    User user;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PropertyService propertyService;

    @Mock
    private ApplicationConfigurationProperties applicationConfigurationProperties;

    /*@Rule
    public ExpectedException exception = ExpectedException.none();*/

    @InjectMocks
    UserService USER_SERVICE = new UserService();

    @Before
    public void setup() {
        user = DataProvider.getUserAsAdmin();

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(userRepository.findUserByUsernameIgnoreCase(Mockito.anyString())).thenReturn(user);
       // Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        Mockito.when(userRepository.findAll(Mockito.any(Specification.class), Mockito.any(Sort.class))).thenReturn(Arrays.asList(user));
        Mockito.when(propertyService.valueOf(Mockito.any(), Mockito.anyBoolean())).thenReturn(Boolean.TRUE);
        Mockito.when(applicationConfigurationProperties.isInternalUsersEnabled()).thenReturn(Boolean.TRUE);

        mockitoRule.silent();
    }

    @Test(expected = NotFoundServiceException.class)
    public void createNullUser() throws ServiceException {
        Assert.assertEquals(user, USER_SERVICE.save(null));
    }

    @Test(expected = InvalidDataException.class)
    public void createInvalidUsernameUser() throws ServiceException {
        Assert.assertEquals(user, USER_SERVICE.save(user));
        user.setUsername("NONES");
        Assert.assertEquals(user, USER_SERVICE.update(user));
    }

    @Ignore
    public void createUser() throws ServiceException {
        Assert.assertEquals(user, USER_SERVICE.save(user));
    }

    @Test
    public void findByUsernameUser() throws ServiceException {
        Assert.assertEquals(user, USER_SERVICE.findByUsername(user.getUsername()));
    }

    @Test()
    public void updateUser() throws ServiceException {
        long updated = DateUtils.now();
        user.setUpdated(updated);
        Assert.assertEquals(user, USER_SERVICE.update(user));
    }

    @Test
    public void listAllUsers() {
        Assert.assertEquals(Arrays.asList(user), USER_SERVICE.findAll());
    }
}
