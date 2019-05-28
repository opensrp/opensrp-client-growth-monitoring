package org.smartregister.growthmonitoring;

/**
 * Created by ndegwamartin on 2019-05-28.
 */
public class GrowthMonitoringConfig {

    private String maleZScoreFile;
    private String femaleZScoreFile;
    private String genderNeutralZScoreFile;

    public String getMaleZScoreFile() {
        if (maleZScoreFile != null) {
            return maleZScoreFile;
        } else {
            return "zscores_male.csv";
        }
    }

    public void setMaleZScoreFile(String maleZScoreFile) {
        this.maleZScoreFile = maleZScoreFile;
    }

    public String getFemaleZScoreFile() {
        if (femaleZScoreFile != null) {
            return femaleZScoreFile;
        } else {
            return "zscores_female.csv";
        }
    }

    public void setFemaleZScoreFile(String femaleZScoreFile) {
        this.femaleZScoreFile = femaleZScoreFile;
    }

    public String getGenderNeutralZScoreFile() {
        return genderNeutralZScoreFile;
    }

    public void setGenderNeutralZScoreFile(String genderNeutralZScoreFile) {
        this.genderNeutralZScoreFile = genderNeutralZScoreFile;
    }
}
