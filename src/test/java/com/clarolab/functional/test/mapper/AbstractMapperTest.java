/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper;

import com.clarolab.dto.BaseDTO;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.Entry;
import com.clarolab.populate.UseCaseDataProvider;
import lombok.extern.java.Log;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Level;

@Log
public abstract class AbstractMapperTest<E extends Entry, D extends BaseDTO> extends BaseFunctionalTest {

    @Autowired
    protected UseCaseDataProvider provider;

    private final Class<E> entityType;
    private final Class<D> dtoType;

    protected AbstractMapperTest(Class<E> entityType, Class<D> dtoType) {
        this.entityType = entityType;
        this.dtoType = dtoType;
    }

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @SuppressWarnings("unchecked")
    public E getEntity() {
        try {
            return (E) Arrays.stream(provider.getClass().getMethods())
                    .filter(method -> method.getReturnType().equals(entityType) && method.getParameterCount() == 0)
                    .findFirst()
                    .orElseThrow(NoSuchMethodException::new)
                    .invoke(provider);
        } catch (IllegalAccessException e) {
            Assert.fail("Cannot access factory class.");
        } catch (InvocationTargetException e) {
            Assert.fail("Cannot Invoke builder method.");
        } catch (NoSuchMethodException e) {
            Assert.fail("No such entity type implemented.");
        }
        return null;
    }

    public D getDTO() {
        try {
            return TestDTOFactory.create(dtoType);
        } catch (Exception e) {
            log.log(Level.SEVERE, String.format("Cannot access DTO factory class %S", dtoType.toString()), e);
            Assert.fail("Cannot access DTO factory class.");
        }
        return null;
    }

    public void assertConversion(E entry, D dto) {
        //ID not present / zero / negative -> entry ID should be null -> this means it will be inserted.
        if (dto.getId() == null || dto.getId() < 1) {
            Assert.assertNull(entry.getId());
        } else {
            Assert.assertEquals(entry.getId(), dto.getId());
        }
    }

}
