package com.clarolab.functional.test;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.Property;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.PopulateSystemProperties;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.PropertyService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.MAX_TESTCASES_TO_PROCESS;

public class PropertyFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private PopulateSystemProperties populateSystemProperties;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void cacheHit() {
        String prefix = "cacheHit";
        provider.setName(prefix);
        provider.getProperty();
        
        Property property = propertyService.findByName(provider.getProperty().getName());

        Assert.assertNotNull("New property is not found", property);
    }
    
    @Test
    public void propertyLoad() {
        populateSystemProperties.initProperties();
        
        Property property = propertyService.findByName(MAX_TESTCASES_TO_PROCESS);

        Assert.assertNotNull("Pre populated property is not found", property);
    }

    @Test
    public void newProperty() {
        String prefix = "newProperty";
        provider.setName(prefix);
        provider.getProperty();

        Property property = propertyService.findByName(provider.getProperty().getName());

        Assert.assertNotNull("New property is not found", property);
    }

    @Test
    public void cacheMiss() {
        String prefix = "cacheMiss";

        Property property = propertyService.findByName(prefix);
        Assert.assertNull("Property not yet created shouldn't exist", property);
        
        Property newProperty = DataProvider.getProperty();
        newProperty.setName(prefix);
        newProperty.setValue(prefix);
        newProperty = propertyService.save(newProperty);

        property = propertyService.findByName(prefix);
        Assert.assertNotNull("New property is not found", property);
        Assert.assertEquals("Same property has different name", newProperty.getName(), property.getName());
        Assert.assertEquals("Same property has different value", newProperty.getValue(), property.getValue());
    }

}
