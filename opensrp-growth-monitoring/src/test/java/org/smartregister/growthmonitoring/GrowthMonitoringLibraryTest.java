package org.smartregister.growthmonitoring;

import org.junit.After;
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

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {27})
public class GrowthMonitoringLibraryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        GrowthMonitoringLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), 1, 1);
    }

    @After
    public void tearDown() throws Exception {
        ReflectionHelpers.setStaticField(GrowthMonitoringLibrary.class, "instance", null);
    }

    @Test
    public void getGrowthMonitoringSyncTimeShouldReturnDefault15hoursInMinutes() {
        assertEquals(60 * 15, GrowthMonitoringLibrary.getInstance().getGrowthMonitoringSyncTime());
    }
}