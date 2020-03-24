package org.smartregister.growthmonitoring.domain;

import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.util.GrowthMonitoringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Jason Rogena - jrogena@ona.io on 31/05/2017.
 */

public class HeightZScore extends ZScore {
    public static double MAX_REPRESENTED_AGE = 60d;

    public static int getZScoreColor(final double zScore) {
        double absScore = Math.abs(zScore);
        if (absScore < 2.0) {
            return R.color.z_score_0;
        } else if (absScore >= 2.0 && absScore < 3.0) {
            return R.color.z_score_2;
        } else {
            return R.color.z_score_3;
        }
    }

    public static double roundOff(double value) {
        int scale = (int) Math.pow(10, 1);
        return (double) Math.round(value * scale) / scale;
    }

    public static Double calculate(Gender gender, Date dateOfBirth, Date heightDate, double weight) {
        try {
            if (dateOfBirth != null && gender != null && heightDate != null) {
                int ageInMonths = (int) Math.round(getAgeInMonths(dateOfBirth, heightDate));
                List<HeightZScore> heightZScores =
                        GrowthMonitoringLibrary.getInstance().heightZScoreRepository().findByGender(gender);

                HeightZScore heightZScoreToUse = null;
                for (HeightZScore curHeightZScore : heightZScores) {
                    if (curHeightZScore.getMonth() == ageInMonths) {
                        heightZScoreToUse = curHeightZScore;
                        break;
                    }
                }

                if (heightZScoreToUse != null) {
                    return heightZScoreToUse.getZ(weight);
                }
            }

            return 0.0;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    public static double getAgeInMonths(Date dateOfBirth, Date heightDate) {
        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(dateOfBirth);
        GrowthMonitoringUtils.standardiseCalendarDate(dobCalendar);

        Calendar heightCalendar = Calendar.getInstance();
        heightCalendar.setTime(heightDate);
        GrowthMonitoringUtils.standardiseCalendarDate(heightCalendar);

        double result = 0;
        if (dobCalendar.getTimeInMillis() <= heightCalendar.getTimeInMillis()) {
            result = ((double) (heightCalendar.getTimeInMillis() - dobCalendar.getTimeInMillis())) / 2629746000L;
        }

        return result;
    }

    /**
     * This method calculates the expected weight given
     *
     * @param gender
     * @param ageInMonthsDouble
     * @param z
     * @return
     */
    public static Double reverse(Gender gender, double ageInMonthsDouble, Double z) {
        int ageInMonths = (int) Math.round(ageInMonthsDouble);
        List<HeightZScore> heightZScores =
                GrowthMonitoringLibrary.getInstance().heightZScoreRepository().findByGender(gender);

        HeightZScore heightZScoreToUse = null;
        for (HeightZScore curHeightZScore : heightZScores) {
            if (curHeightZScore.getMonth() == ageInMonths) {
                heightZScoreToUse = curHeightZScore;
                break;
            }
        }

        if (heightZScoreToUse != null) {
            return heightZScoreToUse.getX(z);
        }

        return null;
    }

    /**
     * This method calculates X (height) given the Z-Score
     *
     * @param z The z-score to use to calculate X
     * @return
     */
    public double getX(double z) {
        if (getL() != 0) {
            return getM() * Math.pow(Math.E, Math.log((z * getL() * getS()) + 1) / getL());
        } else {
            return getM() * Math.pow(Math.E, z * getS());
        }
    }
}
