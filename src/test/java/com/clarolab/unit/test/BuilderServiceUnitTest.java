/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.unit.test;

import com.clarolab.model.Build;
import com.clarolab.populate.DataProvider;
import com.clarolab.repository.BuildRepository;
import com.clarolab.service.BuildService;
import com.clarolab.service.exception.ServiceException;
import com.clarolab.unit.BaseUnitTest;
import com.clarolab.util.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;

public class BuilderServiceUnitTest extends BaseUnitTest {

    Build build;

    @Mock
    private BuildRepository buildRepository;

    /*@Rule
    public ExpectedException exception = ExpectedException.none();*/

    @InjectMocks
    BuildService BUILD_SERVICE = new BuildService();

    @Before
    public void setup() {
        build = DataProvider.getBuild();

        Mockito.when(buildRepository.save(Mockito.any(Build.class))).thenReturn(build);
        //Mockito.when(buildRepository.findAll()).thenReturn(Arrays.asList(build));
        Mockito.when(buildRepository.findAll(Mockito.any(Specification.class), Mockito.any(Sort.class))).thenReturn(Arrays.asList(build));
        mockitoRule.silent();
    }

    @Test(expected = Exception.class)
    public void createNullBuild() throws ServiceException {
        Assert.assertEquals(build, BUILD_SERVICE.save(null));
    }

    @Test
    public void createBuild() throws ServiceException {
        Assert.assertEquals(build, BUILD_SERVICE.save(build));
    }

    @Test()
    public void updateBuild() throws ServiceException {
        long updated = DateUtils.now();
        build.setUpdated(updated);
        Assert.assertEquals(build, BUILD_SERVICE.update(build));
    }

    @Test
    public void listAllBuilds() {
        Assert.assertEquals(Arrays.asList(build), BUILD_SERVICE.findAll());
    }
}
