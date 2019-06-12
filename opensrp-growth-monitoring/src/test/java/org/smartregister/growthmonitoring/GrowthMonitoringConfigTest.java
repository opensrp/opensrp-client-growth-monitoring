package org.smartregister.growthmonitoring;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by ndegwamartin on 2019-05-28.
 */
public class GrowthMonitoringConfigTest {

    private static final String CUSTOM_ZSCORE_FILE = "custom_zscore_file.txt";
    private static final String FEMALE_ZSCORE_FILE = "weight_z_scores_female.csv";
    private static final String BOYCHILD_ZSCORE_FILE = "weight_z_scores_male.csv";

    @Test
    public void testGrowthMonitoringConfigInstantiatesCorrectly() {

        GrowthMonitoringConfig config = new GrowthMonitoringConfig();
        Assert.assertNotNull(config);
        Assert.assertEquals(FEMALE_ZSCORE_FILE, config.getFemaleWeightZScoreFile());
        Assert.assertEquals(BOYCHILD_ZSCORE_FILE, config.getMaleWeightZScoreFile());
        Assert.assertNull(config.getGenderNeutralZScoreFile());

        config.setFemaleWeightZScoreFile(CUSTOM_ZSCORE_FILE);
        Assert.assertEquals(BOYCHILD_ZSCORE_FILE, config.getMaleWeightZScoreFile());
        Assert.assertEquals(CUSTOM_ZSCORE_FILE, config.getFemaleWeightZScoreFile());


        config.setMaleWeightZScoreFile(CUSTOM_ZSCORE_FILE);
        Assert.assertEquals(CUSTOM_ZSCORE_FILE, config.getMaleWeightZScoreFile());
        Assert.assertEquals(CUSTOM_ZSCORE_FILE, config.getFemaleWeightZScoreFile());


        config.setGenderNeutralZScoreFile(CUSTOM_ZSCORE_FILE);
        Assert.assertEquals(CUSTOM_ZSCORE_FILE, config.getGenderNeutralZScoreFile());
    }
}
