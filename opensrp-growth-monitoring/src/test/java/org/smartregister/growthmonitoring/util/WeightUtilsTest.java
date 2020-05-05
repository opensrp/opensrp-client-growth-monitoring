package org.smartregister.growthmonitoring.util;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.util.WeightUtils;

import java.util.Date;

public class WeightUtilsTest {

    @Test
    public void lessThanThreeMonthsTest() {
        Weight spyWeight = Mockito.spy(Weight.class);
        spyWeight.setCreatedAt(new Date());

        Assert.assertTrue(WeightUtils.lessThanThreeMonths(spyWeight));
    }

    @Test
    public void moreThanThreeMonthsTest() {
        Weight spyWeight = Mockito.spy(Weight.class);
        spyWeight.setCreatedAt(new Date(1550707200000L));

        Assert.assertFalse(WeightUtils.lessThanThreeMonths(spyWeight));
    }
}