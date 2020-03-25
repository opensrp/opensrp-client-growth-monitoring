package org.smartregister.growthmonitoring.domain;

import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;

import timber.log.Timber;

public class WeightForHeightZscore extends ZScore {

    public static Double calculate(String gender, double weight, double height) {
        try {
            ZScore zScore = GrowthMonitoringLibrary.getInstance().weightForHeightRepository().findZScoreVariables(gender, height).get(0);
            if (zScore != null) {
                return zScore.getZ(weight);
            }
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
        return 0.0;
    }

}
