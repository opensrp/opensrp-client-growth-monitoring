package org.smartregister.growthmonitoring.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.smartregister.growthmonitoring.util.AppProperties;

public class AppPropertiesTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getPropertyBooleanTest() {
        AppProperties appProperties = new AppProperties();

        Assert.assertFalse(appProperties.getPropertyBoolean("true"));
    }

    @Test
    public void hasPropertyTest() {
        AppProperties appProperties = new AppProperties();

        Assert.assertFalse(appProperties.hasProperty("monitor.true"));
    }
}
