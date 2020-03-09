package org.smartregister.growthmonitoring;

import org.smartregister.growthmonitoring.util.GrowthMonitoringConstants;

/**
 * Created by ndegwamartin on 2019-05-28.
 */
public class GrowthMonitoringConfig {

    private String maleWeightZScoreFile;
    private String maleHeightZScoreFile;
    private String femaleWeightZScoreFile;
    private String femaleHeightZScoreFile;
    private String genderNeutralZScoreFile;
    private String weightForHeightZScoreFile;
    private String childTable;

    public GrowthMonitoringConfig() {
        setChildTable(GrowthMonitoringConstants.CHILD_TABLE_NAME);
    }

    public String getMaleWeightZScoreFile() {
        if (maleWeightZScoreFile != null) {
            return maleWeightZScoreFile;
        } else {
            return "weight_z_scores_male.csv";
        }
    }

    public void setMaleWeightZScoreFile(String maleWeightZScoreFile) {
        this.maleWeightZScoreFile = maleWeightZScoreFile;
    }

    public String getMaleHeightZScoreFile() {
        if (maleHeightZScoreFile != null) {
            return maleHeightZScoreFile;
        } else {
            return "height_z_scores_male.csv";
        }
    }

    public void setMaleHeightZScoreFile(String maleHeightZScoreFile) {
        this.maleHeightZScoreFile = maleHeightZScoreFile;
    }

    public String getFemaleWeightZScoreFile() {
        if (femaleWeightZScoreFile != null) {
            return femaleWeightZScoreFile;
        } else {
            return "weight_z_scores_female.csv";
        }
    }

    public void setFemaleWeightZScoreFile(String femaleWeightZScoreFile) {
        this.femaleWeightZScoreFile = femaleWeightZScoreFile;
    }

    public String getFemaleHeightZScoreFile() {
        if (femaleHeightZScoreFile != null) {
            return femaleHeightZScoreFile;
        } else {
            return "height_z_scores_female.csv";
        }
    }

    public void setWeightForHeightZScoreFile(String weightForHeightZScoreFile) {
        this.weightForHeightZScoreFile = weightForHeightZScoreFile;
    }

    public String getWeightForHeightZScoreFile() {
        return this.weightForHeightZScoreFile;
    }

    public void setFemaleHeightZScoreFile(String femaleHeightZScoreFile) {
        this.femaleHeightZScoreFile = femaleHeightZScoreFile;
    }

    public String getGenderNeutralZScoreFile() {
        return genderNeutralZScoreFile;
    }

    public void setGenderNeutralZScoreFile(String genderNeutralZScoreFile) {
        this.genderNeutralZScoreFile = genderNeutralZScoreFile;
    }

    public String getChildTable() {
        return childTable;
    }

    public void setChildTable(String childTable) {
        this.childTable = childTable;
    }
}
