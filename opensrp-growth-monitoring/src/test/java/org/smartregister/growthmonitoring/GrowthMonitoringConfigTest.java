package org.smartregister.growthmonitoring;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by ndegwamartin on 2019-05-28.
 */
public class GrowthMonitoringConfigTest {

    private static final String CUSTOM_ZSCORE_FILE = "custom_zscore_file.txt";
    private static final String FEMALE_ZSCORE_FILE = "zscores_female.csv";
    private static final String BOYCHILD_ZSCORE_FILE = "zscores_male.csv";

    @Test
    public void testGrowthMonitoringConfigInstantiatesCorrectly() {

        GrowthMonitoringConfig config = new GrowthMonitoringConfig();
        Assert.assertNotNull(config);
        Assert.assertEquals(FEMALE_ZSCORE_FILE, config.getFemaleZScoreFile());
        Assert.assertEquals(BOYCHILD_ZSCORE_FILE, config.getMaleZScoreFile());
        Assert.assertNull(config.getGenderNeutralZScoreFile());

        config.setFemaleZScoreFile(CUSTOM_ZSCORE_FILE);
        Assert.assertEquals(BOYCHILD_ZSCORE_FILE, config.getMaleZScoreFile());
        Assert.assertEquals(CUSTOM_ZSCORE_FILE, config.getFemaleZScoreFile());


        config.setMaleZScoreFile(CUSTOM_ZSCORE_FILE);
        Assert.assertEquals(CUSTOM_ZSCORE_FILE, config.getMaleZScoreFile());
        Assert.assertEquals(CUSTOM_ZSCORE_FILE, config.getFemaleZScoreFile());


        config.setGenderNeutralZScoreFile(CUSTOM_ZSCORE_FILE);
        Assert.assertEquals(CUSTOM_ZSCORE_FILE, config.getGenderNeutralZScoreFile());
    }
}
