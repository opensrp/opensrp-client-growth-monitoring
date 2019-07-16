package org.smartregister.growthmonitoring.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.growthmonitoring.BaseUnitTest;
import org.smartregister.growthmonitoring.domain.WeightZScore;
import org.smartregister.growthmonitoring.util.GrowthMonitoringUtils;

import java.util.Calendar;
import java.util.Date;

public class GrowthMonitoringUtilsTest extends BaseUnitTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private Calendar calendar;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @PrepareForTest({Calendar.class, WeightZScore.class})
    public void getMinAndMaxRecordingDatesTest() {
        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.mockStatic(WeightZScore.class);
        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

        Calendar[] calendar = GrowthMonitoringUtils.getMinAndMaxRecordingDates(new Date(1500208620000L));
        Assert.assertEquals(2, calendar.length);
    }
}
