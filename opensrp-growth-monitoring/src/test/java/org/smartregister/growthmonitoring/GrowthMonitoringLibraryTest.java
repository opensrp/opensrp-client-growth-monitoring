package org.smartregister.growthmonitoring;

import android.os.Build;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.repository.Repository;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class GrowthMonitoringLibraryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        GrowthMonitoringLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), 1, 1);
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(GrowthMonitoringLibrary.class, "instance", null);
    }

    @Test
    public void getGrowthMonitoringSyncTimeShouldReturnDefault15hoursInMinutes() {
        Assert.assertEquals(60 * 15, GrowthMonitoringLibrary.getInstance().getGrowthMonitoringSyncTime());
    }

    @Test
    public void testGrowthMonitoringLibraryClassInitsCorrectly() {
        GrowthMonitoringLibrary.destroy();
        GrowthMonitoringLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), 1, "1.0.0", 1);
        Assert.assertEquals(1, GrowthMonitoringLibrary.getInstance().getApplicationVersion());
        Assert.assertEquals("1.0.0", GrowthMonitoringLibrary.getInstance().getApplicationVersionName());
        Assert.assertEquals(1, GrowthMonitoringLibrary.getInstance().getDatabaseVersion());
    }
}