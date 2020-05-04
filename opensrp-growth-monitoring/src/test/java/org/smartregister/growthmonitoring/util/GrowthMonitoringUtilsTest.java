package org.smartregister.growthmonitoring.util;

import android.os.Build;

import org.joda.time.LocalDateTime;
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

    @Test
    public void assertStandardiseCalendarDateSetsConsistentTimePortion() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 3);
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 1000);

        GrowthMonitoringUtils.standardiseCalendarDate(calendar);

        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));

    }

    @Test
    public void assertCleanTimestampSetsConsistentTimePortion() {

        LocalDateTime localDateTime = new LocalDateTime().plusHours(3).plusMinutes(5);
        LocalDateTime localDateTime2 = new LocalDateTime().plusHours(7).minusMinutes(2);

        Assert.assertNotEquals(localDateTime.toDate().getTime(), localDateTime2.toDate().getTime());

        long cleanDate1 = GrowthMonitoringUtils.cleanTimestamp(localDateTime.toDate().getTime());
        long cleanDate2 = GrowthMonitoringUtils.cleanTimestamp(localDateTime2.toDate().getTime());

        Assert.assertEquals(cleanDate1, cleanDate2);
    }
}
