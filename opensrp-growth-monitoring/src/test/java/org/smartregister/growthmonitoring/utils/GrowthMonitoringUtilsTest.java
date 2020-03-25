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
        CSV_HEADING_COLUMN_MAP.put("Month", GrowthMonitoringConstants.ColumnHeaders.COLUMN_MONTH);
        CSV_HEADING_COLUMN_MAP.put("L", GrowthMonitoringConstants.ColumnHeaders.COLUMN_L);
        CSV_HEADING_COLUMN_MAP.put("M", GrowthMonitoringConstants.ColumnHeaders.COLUMN_M);
        CSV_HEADING_COLUMN_MAP.put("S", GrowthMonitoringConstants.ColumnHeaders.COLUMN_S);
        String queryStringStart = "INSERT INTO `test_table` ( `sex`, `month`, `l`, `m`, `s`)\n" +
                " VALUES (\"MALE\", \"0\", \"0.3487\", \"3.3464\", \"0.14602\"),";

        String sqlQueryString = GrowthMonitoringUtils.getDumpCsvQuery(Gender.MALE, RuntimeEnvironment.application.getApplicationContext(), "weight_z_scores_male.csv", "test_table", CSV_HEADING_COLUMN_MAP);
        Assert.assertTrue(sqlQueryString.startsWith(queryStringStart));
    }
}
