package org.smartregister.growthmonitoring;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ndegwamartin on 2019-05-28.
 */
public class GrowthMonitoringConfigTest {

    private static final String CUSTOM_ZSCORE_FILE = "custom_zscore_file.txt";
    private static final String FEMALE_WEIGHT_ZSCORE_FILE = "weight_z_scores_female.csv";
    private static final String BOYCHILD_WEIGHT_ZSCORE_FILE = "weight_z_scores_male.csv";
    private static final String FEMALE_HEIGHT_ZSCORE_FILE = "height_z_scores_female.csv";
    private static final String BOYCHILD_HEIGHT_ZSCORE_FILE = "height_z_scores_male.csv";
    private static final String WEIGHT_FOR_HEIGHT_Z_SCORE_FILE = "weight_for_height.csv";

    @Test
    public void testGrowthMonitoringConfigInstantiatesCorrectly() {

        GrowthMonitoringConfig config = new GrowthMonitoringConfig();
        Assert.assertNotNull(config);
        Assert.assertEquals(FEMALE_WEIGHT_ZSCORE_FILE, config.getFemaleWeightZScoreFile());
        Assert.assertEquals(BOYCHILD_WEIGHT_ZSCORE_FILE, config.getMaleWeightZScoreFile());

        Assert.assertEquals(FEMALE_HEIGHT_ZSCORE_FILE, config.getFemaleHeightZScoreFile());
        Assert.assertEquals(BOYCHILD_HEIGHT_ZSCORE_FILE, config.getMaleHeightZScoreFile());
        Assert.assertNull(config.getGenderNeutralZScoreFile());

        config.setFemaleWeightZScoreFile(CUSTOM_ZSCORE_FILE);
        Assert.assertEquals(BOYCHILD_WEIGHT_ZSCORE_FILE, config.getMaleWeightZScoreFile());
        Assert.assertEquals(CUSTOM_ZSCORE_FILE, config.getFemaleWeightZScoreFile());


        config.setMaleWeightZScoreFile(CUSTOM_ZSCORE_FILE);
        Assert.assertEquals(CUSTOM_ZSCORE_FILE, config.getMaleWeightZScoreFile());
        Assert.assertEquals(CUSTOM_ZSCORE_FILE, config.getFemaleWeightZScoreFile());

        config.setFemaleHeightZScoreFile(CUSTOM_ZSCORE_FILE);
        Assert.assertEquals(CUSTOM_ZSCORE_FILE, config.getFemaleHeightZScoreFile());

        config.setMaleHeightZScoreFile(CUSTOM_ZSCORE_FILE);
        Assert.assertEquals(CUSTOM_ZSCORE_FILE, config.getMaleHeightZScoreFile());

        config.setGenderNeutralZScoreFile(CUSTOM_ZSCORE_FILE);
        Assert.assertEquals(CUSTOM_ZSCORE_FILE, config.getGenderNeutralZScoreFile());
    }

    @Test
    public void weightForHeightConfigInstantiatesCorrectly() {
        GrowthMonitoringConfig growthMonitoringConfig = new GrowthMonitoringConfig();
        growthMonitoringConfig.setWeightForHeightZScoreFile(WEIGHT_FOR_HEIGHT_Z_SCORE_FILE);

        Assert.assertEquals(WEIGHT_FOR_HEIGHT_Z_SCORE_FILE, growthMonitoringConfig.getWeightForHeightZScoreFile());
    }
}
