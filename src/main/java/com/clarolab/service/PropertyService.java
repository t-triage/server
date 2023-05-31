/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;


import com.clarolab.aaa.util.SecurityLog;
import com.clarolab.model.Entry;
import com.clarolab.model.Property;
import com.clarolab.populate.DataProvider;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.PropertyRepository;
import com.clarolab.service.exception.InvalidDataException;
import com.clarolab.service.exception.ServiceException;
import com.clarolab.util.StringUtils;
import lombok.extern.java.Log;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.clarolab.util.StringUtils.isEmpty;
import static com.clarolab.util.StringUtils.parseDataError;

@Service
@Log
public class PropertyService extends BaseService<Property> {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private SecurityLog securityLog;
    
    private Map<String, Property> cache = new HashMap<>();

    @Override
    public BaseRepository<Property> getRepository() {
        return propertyRepository;
    }

    public Property findByName(String name) {
        Property cacheValue = findCache(name);
        if (cacheValue != null) {
            return cacheValue;
        }
        return propertyRepository.findPropertiesByName(name);
    }

    public List<Property> findAllByName(String name) {
        return propertyRepository.findAllByNameContains(name);
    }

    @Override
    public Page<Property> findAll(String[] criteria, @NotNull Pageable pageable) {
        String[] hidden = {"hidden:false"};
        criteria = StringUtils.isEmpty(criteria) ? hidden : (String[]) ArrayUtils.add(criteria, hidden[0]);
        return super.findAll(criteria, pageable);
    }

    @Override
    public Property save(Property entry) throws ServiceException {
        if (isEmpty(entry.getName()))
            //throw new InvalidDataException(parseDataError("Invalid Property Name", "Unnamed"));   commented because performance profile didn't start
            return null;

        if (isEmpty(entry.getValue()) && isEmpty(entry.getLongValue()))
            throw new InvalidDataException(parseDataError("Invalid Property Value", "Empty"));

        Property byName = findByName(entry.getName());
        if(byName!=null) {
            byName.setValue(entry.getValue());
            byName.setLongValue(entry.getLongValue());
            byName.setUseLongValue(entry.isUseLongValue());
            byName.setDescription(entry.getDescription());
            byName.setHidden(false);
            return update(byName);
        }
        byName = super.save(entry);
        updateCache(byName.getName(), byName);
        return byName;
    }

    @Override
    public Property update(Property property) throws ServiceException {

        if (isEmpty(property.getName()))
            throw new InvalidDataException(parseDataError("Invalid Property Name", property.getName()));

        if (isEmpty(property.getValue()) && isEmpty(property.getLongValue()))
            throw new InvalidDataException(parseDataError("Invalid Property Value", property.getValue()));
        updateCache(property.getName(), property);

        return super.update(property);
    }

    public String valueOf(final String name, final String defValue) {
        Optional<Property> property = Optional.ofNullable(findByName(name));
        return property.isPresent() ?
                (property.get().isUseLongValue() ?
                        property.get().getLongValue() : property.get().getValue())
                :
                defValue;
    }

    public Integer valueOf(final String name, final Integer defValue) {
        Integer integer = defValue;
        Optional<Property> property = Optional.ofNullable(findByName(name));
        try {
            integer = property.map(prop -> Integer.valueOf(prop.getValue())).orElse(defValue);
        } catch (Exception e) {
            log.severe("Unable to convert property to Integer: " + name + " Using defaultValue: " + defValue);
        }
        return integer;
    }

    public Double valueOf(final String name, final Double defValue) {
        Double aDouble = defValue;
        Optional<Property> property = Optional.ofNullable(findByName(name));
        try {
            aDouble = property.map(prop -> Double.valueOf(prop.getValue())).orElse(defValue);
        } catch (Exception e) {
            log.severe("Unable to convert property to Double: " + name + " Using defaultValue: " + defValue);
        }

        return aDouble;
    }

    public Boolean valueOf(final String name, final Boolean defValue) {
        Boolean aBoolean = defValue;
        Optional<Property> property = Optional.ofNullable(findByName(name));
        try {
            aBoolean = property.map(prop -> Boolean.valueOf(prop.getValue())).orElse(defValue);
        } catch (Exception e) {
            log.severe("Unable to convert property to Boolean: " + name + " Using defaultValue: " + defValue);
        }
        return aBoolean;
    }

    public Long valueOf(final String name, final Long defValue) {
        Long aLong = defValue;
        Optional<Property> property = Optional.ofNullable(findByName(name));
        try {

            aLong = property.map(prop -> Long.valueOf(prop.getValue())).orElse(defValue);
        } catch (Exception e) {
            log.severe("Unable to convert property to Long: " + name + " Using defaultValue: " + defValue);
        }
        return aLong;
    }

    public Property setValue(String name, String newValue) throws ServiceException {
        Optional<Property> property = Optional.ofNullable(findByName(name));
        if (property.isPresent()) {
            if (!property.get().getValue().equals(newValue)) {
                property.get().setValue(newValue);
                return update(property.get());
            }
            return property.get();
        } else {
            Property newProperty = DataProvider.getProperty();
            newProperty.setValue(newValue);
            newProperty.setName(name);
            newProperty.setDescription("Lazy property " + name);
            return save(newProperty);
        }
    }

    public void valueChanged(String name, String oldValue, String newValue) {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        if (StringUtils.isEmpty(oldValue)) {
            oldValue = "";
        }
        if (StringUtils.isEmpty(newValue)) {
            newValue = "";
        }
        if (oldValue.equals(newValue)) {
            return; // no changes
        }
        securityLog.info(String.format("Property %s has changed from %s to %s.", name, oldValue, newValue));
    }
    
    public void loadCache(String[] propertyNames) {
        for (String name : propertyNames) {
            String propertyName = name.toLowerCase();
            if (findCache(propertyName) == null) {
                cache.put(propertyName, findByName(name));
            }
        }
    }
    
    private Property findCache(String name) {
        boolean enabled = true;
        String propertyName = name.toLowerCase();
        
        if (enabled) {
            return cache.get(propertyName);
        } else {
            return null;
        }
        
    }

    private void updateCache(String name, Entry entity) {
        String propertyName = name.toLowerCase();

        cache.put(propertyName, (Property) entity);
    }

    public void warmUp() {
        String[] properties = {"DAYS_TO_EXPIRE_TRIAGE", "MAX_TESTCASES_TO_PROCESS", "MAX_TESTCASES_PER_DAY", "PREVIOUS_DAYS_VALID_INFO", 
                "SAVED_TIME_MIN", "SAVED_TIME_PERIOD", "CONSECUTIVE_PASS_COUNT", "PREVIOUS_FAIL_COUNT", "MAX_EVENTS_TO_PROCESS", 
                "OLD_EVENTS_TO_DELETE_DAYS", "SLACK_ENABLED", "INTERNAL_LOGIN_ENABLED", "SERVICEL_LOGIN_ENABLED", 
                "JWT_AUTH_TOKEN_EXPIRATION_MS", "AUTOTRIAGE_SAME_ERROR_TEST", "GDPR_ENABLED", "STATS_FAIL_EXCEPTION_TOP_LIMIT", 
                "AGENT_PRODUCTIVITY_SERVICE_ENABLED", "STACK_TRACE_EXCEPTIONS_TO_PROCESS", "RULE_ENGINE_ON", "GOOGLE_ANALYTICS_UA", 
                "TERMS_AND_CONDITION_ACCEPTED_TIME", "FEATURE_MANUAL_TEST_ENABLED", "URL"};
        loadCache(properties);
    }
}
