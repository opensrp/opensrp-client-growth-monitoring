package org.smartregister.growthmonitoring.util;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.util.HeightUtils;

import java.util.Date;

public class HeightUtilsTest {

    @Test
    public void lessThanThreeMonthsTest() {
        Height spyHeight = Mockito.spy(Height.class);
        spyHeight.setCreatedAt(new Date());

        Assert.assertTrue(HeightUtils.lessThanThreeMonths(spyHeight));
    }

    @Test
    public void moreThanThreeMonthsTest() {
        Height spyHeight = Mockito.spy(Height.class);
        spyHeight.setCreatedAt(new Date(1550707200000L));

        Assert.assertFalse(HeightUtils.lessThanThreeMonths(spyHeight));
    }
}