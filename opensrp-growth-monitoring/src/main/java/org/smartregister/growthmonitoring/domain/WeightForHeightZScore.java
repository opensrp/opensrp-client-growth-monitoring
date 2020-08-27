package org.smartregister.growthmonitoring.domain;

import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;

import java.util.List;

public class WeightForHeightZScore extends WeightZScore {

    public static Float MAX_REPRESENTED_HEIGHT = 120f;
    public static Float MIN_REPRESENTED_HEIGHT = 65f;

    public static double getZScore(String gender, double weight, double height) {
        double zScore = 0.0;
        List<ZScore> zScoreValues = GrowthMonitoringLibrary.getInstance().weightForHeightRepository().findZScoreVariables(gender, height);
        if (zScoreValues.size() > 0) {
            zScore = zScoreValues.get(0).getZ(weight);
        }
        return zScore;
    }
}
