package org.smartregister.growthmonitoring.domain;

import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Jason Rogena - jrogena@ona.io on 31/05/2017.
 */

public class WeightZScore extends ZScore {
    public static double MAX_REPRESENTED_AGE = 60d;

    public WeightZScore() {
    }

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

    public static Double calculate(Gender gender, Date dateOfBirth, Date weighingDate, double weight) {
        try {
            if (dateOfBirth != null && gender != null && weighingDate != null) {
                int ageInMonths = (int) Math.round(getAgeInMonths(dateOfBirth, weighingDate));
                List<WeightZScore> weightZScores =
                        GrowthMonitoringLibrary.getInstance().weightZScoreRepository().findByGender(gender);

                WeightZScore weightZScoreToUse = null;
                for (WeightZScore curWeightZScore : weightZScores) {
                    if (curWeightZScore.getMonth() == ageInMonths) {
                        weightZScoreToUse = curWeightZScore;
                        break;
                    }
                }

                if (weightZScoreToUse != null) {
                    return weightZScoreToUse.getZ(weight);
                }
            }

            return 0.0;
        } catch (Exception e) {
            Timber.e(e.getMessage());
            return null;
        }
    }

    public static double getAgeInMonths(Date dateOfBirth, Date weighingDate) {
        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(dateOfBirth);
        standardiseCalendarDate(dobCalendar);

        Calendar weighingCalendar = Calendar.getInstance();
        weighingCalendar.setTime(weighingDate);
        standardiseCalendarDate(weighingCalendar);

        double result = 0;
        if (dobCalendar.getTimeInMillis() <= weighingCalendar.getTimeInMillis()) {
            result = ((double) (weighingCalendar.getTimeInMillis() - dobCalendar.getTimeInMillis())) / 2629746000L;
        }

        return result;
    }

    private static void standardiseCalendarDate(Calendar calendarDate) {
        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);
    }

    /**
     * This method calculates the expected weight given
     *
     * @param gender
     * @param ageInMonthsDouble
     * @param z
     *
     * @return
     */
    public static Double reverse(Gender gender, double ageInMonthsDouble, Double z) {
        int ageInMonths = (int) Math.round(ageInMonthsDouble);
        List<WeightZScore> weightZScores =
                GrowthMonitoringLibrary.getInstance().weightZScoreRepository().findByGender(gender);

        WeightZScore weightZScoreToUse = null;
        for (WeightZScore curWeightZScore : weightZScores) {
            if (curWeightZScore.getMonth() == ageInMonths) {
                weightZScoreToUse = curWeightZScore;
                break;
            }
        }

        if (weightZScoreToUse != null) {
            return weightZScoreToUse.getX(z);
        }

        return null;
    }

    /**
     * This method calculates Z (The z-score) using the formulae provided here https://www.cdc
     * .gov/growthcharts/percentile_data_files.htm
     *
     * @param x The weight to use
     *
     * @return
     */
    public double getZ(double x) {
        if (getL() != 0) {
            return (Math.pow((x / getM()), getL()) - 1) / (getL() * getS());
        } else {
            return Math.log(x / getM()) / getS();
        }
    }

    /**
     * This method calculates X (weight) given the Z-Score
     *
     * @param z The z-score to use to calculate X
     *
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
