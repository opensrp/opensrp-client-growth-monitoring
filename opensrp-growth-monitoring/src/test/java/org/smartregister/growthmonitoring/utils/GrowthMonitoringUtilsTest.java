package org.smartregister.growthmonitoring.utils;

import android.os.Build;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.opensrp.api.constants.Gender;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.growthmonitoring.BuildConfig;
import org.smartregister.growthmonitoring.util.GrowthMonitoringConstants;
import org.smartregister.growthmonitoring.util.GrowthMonitoringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.O_MR1)
public class GrowthMonitoringUtilsTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getMinAndMaxRecordingDatesTest() {
        Calendar[] calendar = GrowthMonitoringUtils.getMinAndMaxRecordingDates(new Date(1500208620000L));
        Assert.assertEquals(2, calendar.length);
    }

    @Test
    public void getDumpCsvQueryReturnsCorrectQueryString() {
        Map<String, String> CSV_HEADING_COLUMN_MAP = new HashMap<>();
        CSV_HEADING_COLUMN_MAP.put("sex", GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX);
        CSV_HEADING_COLUMN_MAP.put("height", GrowthMonitoringConstants.ColumnHeaders.HEIGHT);
        CSV_HEADING_COLUMN_MAP.put("l", GrowthMonitoringConstants.ColumnHeaders.COLUMN_L);
        CSV_HEADING_COLUMN_MAP.put("m", GrowthMonitoringConstants.ColumnHeaders.COLUMN_M);
        String queryStringStart = "INSERT INTO `test_table` ( `sex`, `sex`, `height`, `l`, `m`)\n" +
                " VALUES (\"MALE\", \"1\", \"65\", \"-0.3521\", \"7.4327\"),";

        String sqlQueryString = GrowthMonitoringUtils.getDumpCsvQuery(Gender.MALE, RuntimeEnvironment.application.getApplicationContext(), "weight_for_height.csv", "test_table", CSV_HEADING_COLUMN_MAP);
        Assert.assertTrue(sqlQueryString.startsWith(queryStringStart));
    }
}
