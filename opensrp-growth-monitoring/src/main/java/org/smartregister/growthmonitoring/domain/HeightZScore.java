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

public class HeightZScore {
    public static double MAX_REPRESENTED_AGE = 60d;
    private final Gender gender;
    private final int month;
    private final double l;
    private final double m;
    private final double s;
    private final double sd;
    private final double sd3Neg;
    private final double sd2Neg;
    private final double sd1Neg;
    private final double sd0;
    private final double sd1;
    private final double sd2;
    private final double sd3;

    public HeightZScore(Gender gender, int month, double l, double m, double s, double sd, double sd3Neg,
                        double sd2Neg, double sd1Neg, double sd0, double sd1, double sd2, double sd3) {
        this.gender = gender;
        this.month = month;
        this.l = l;
        this.m = m;
        this.s = s;
        this.sd = sd;
        this.sd3Neg = sd3Neg;
        this.sd2Neg = sd2Neg;
        this.sd1Neg = sd1Neg;
        this.sd0 = sd0;
        this.sd1 = sd1;
        this.sd2 = sd2;
        this.sd3 = sd3;
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

    public static Double calculate(Gender gender, Date dateOfBirth, Date heightDate, double weight) {
        try {
            if (dateOfBirth != null && gender != null && heightDate != null) {
                int ageInMonths = (int) Math.round(getAgeInMonths(dateOfBirth, heightDate));
                List<HeightZScore> heightZScores = GrowthMonitoringLibrary.getInstance().heightZScoreRepository()
                        .findByGender(gender);

                HeightZScore heightZScoreToUse = null;
                for (HeightZScore curHeightZScore : heightZScores) {
                    if (curHeightZScore.month == ageInMonths) {
                        heightZScoreToUse = curHeightZScore;
                        break;
                    }
                }

                if (heightZScoreToUse != null) {
                    return new Double(heightZScoreToUse.getZ(weight));
                }
            }

            return 0.0;
        } catch (Exception e) {
            Timber.e(e.getMessage());
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
            result = ((double) (heightCalendar.getTimeInMillis() - dobCalendar.getTimeInMillis())) / 2629746000l;
        }

        return result;
    }

    /**
     * This method calculates Z (The z-score) using the formulae provided here https://www.cdc.gov/growthcharts/percentile_data_files.htm
     *
     * @param x The height to use
     * @return
     */
    public double getZ(double x) {
        if (l != 0) {
            return (Math.pow((x / m), l) - 1) / (l * s);
        } else {
            return Math.log(x / m) / s;
        }
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
        List<HeightZScore> heightZScores = GrowthMonitoringLibrary.getInstance().heightZScoreRepository()
                .findByGender(gender);

        HeightZScore heightZScoreToUse = null;
        for (HeightZScore curHeightZScore : heightZScores) {
            if (curHeightZScore.month == ageInMonths) {
                heightZScoreToUse = curHeightZScore;
                break;
            }
        }

        if (heightZScoreToUse != null) {
            return new Double(heightZScoreToUse.getX(z));
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
        if (l != 0) {
            return m * Math.pow(Math.E, Math.log((z * l * s) + 1) / l);
        } else {
            return m * Math.pow(Math.E, z * s);
        }
    }

    public Gender getGender() {
        return gender;
    }

    public double getSd3Neg() {
        return sd3Neg;
    }

    public double getSd2Neg() {
        return sd2Neg;
    }

    public double getSd1Neg() {
        return sd1Neg;
    }

    public double getSd0() {
        return sd0;
    }

    public double getSd1() {
        return sd1;
    }

    public double getSd3() {
        return sd3;
    }

    public double getSd2() {
        return sd2;
    }
}
