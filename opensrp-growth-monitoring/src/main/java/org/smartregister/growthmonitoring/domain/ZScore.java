package org.smartregister.growthmonitoring.domain;

import android.util.Log;

import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Jason Rogena - jrogena@ona.io on 31/05/2017.
 */

public class ZScore {
    public static double MAX_REPRESENTED_AGE = 60d;
    private Gender gender;
    protected int month;
    private double l;
    private double m;
    private double s;
    private double sd3Neg;
    private double sd2Neg;
    private double sd1Neg;
    private double sd0;
    private double sd1;
    private double sd2;
    private double sd3;

    public ZScore() {

    }

    public ZScore(Gender gender, int month, double l, double m, double s, double sd3Neg,
                  double sd2Neg, double sd1Neg, double sd0, double sd1, double sd2, double sd3) {
        this.gender = gender;
        this.month = month;
        this.l = l;
        this.m = m;
        this.s = s;
        this.sd3Neg = sd3Neg;
        this.sd2Neg = sd2Neg;
        this.sd1Neg = sd1Neg;
        this.sd0 = sd0;
        this.sd1 = sd1;
        this.sd2 = sd2;
        this.sd3 = sd3;
    }

    //    public static int getZScoreColor(final double zScore) {
//        double absScore = Math.abs(zScore);
//        if (absScore < 2.0) {
//            return R.color.z_score_0;
//        } else if (absScore >= 2.0 && absScore < 3.0) {
//            return R.color.z_score_2;
//        } else {
//            return R.color.z_score_3;
//        }
//    }
    public static int getZScoreColor(final double absScore) {
        //double absScore = Math.abs(zScore);

        if (absScore <= -3.0) {
            Log.v("ZSCORE", "zscore:" + absScore + ":color:red");
            return R.color.red;
        } else if (absScore <= -2.0 && absScore > -3.0) {
            Log.v("ZSCORE", "zscore:" + absScore + ":color:dark_yellow");
            return R.color.dark_yellow;
        } else if (absScore <= -1.0 && absScore > -2.0) {
            Log.v("ZSCORE", "zscore:" + absScore + ":color:yellow");
            return R.color.yellow;
        } else if (absScore <= 2) {
            Log.v("ZSCORE", "zscore:" + absScore + ":color:green");
            return R.color.green;
        } else {
            Log.v("ZSCORE", "zscore:" + absScore + ":color:black");
            return android.R.color.holo_purple;
        }
    }

    public static int getWeightColor(final double kg, Date dob) {
        double absScore = kg;
        if (absScore >= 12.5) {
            return R.color.z_score_0;
        } else if (absScore >= 11.5) {
            return R.color.mam;
        } else {
            return R.color.sam;
        }
    }

    public static int getMuacColor(final double cm) {
        double absScore = Math.abs(cm);
        if (absScore >= 12.5) {
            return R.color.z_score_0;
        } else if (absScore >= 11.5) {
            return R.color.mam;
        } else {
            return R.color.sam;
        }
    }

    public static String getMuacText(final double cm) {
        double absScore = Math.abs(cm);
        if (absScore >= 12.5) {
            return "NORMAL";
        } else if (absScore >= 11.5) {
            return "MAM";
        } else {
            return "SAM";
        }
    }

    public static String getZScoreText(final double absScore) {
        //double absScore = Math.abs(zScore);

        if (absScore <= -3.0) {
            Log.v("ZSCORE", "zscore:" + absScore + ":color:red");
            return "SAM";
        } else if (absScore <= -2.0 && absScore > -3.0) {
            Log.v("ZSCORE", "zscore:" + absScore + ":color:dark_yellow");
            return "DARK YELLOW";
        } else if (absScore <= -1.0 && absScore > -2.0) {
            Log.v("ZSCORE", "zscore:" + absScore + ":color:yellow");
            return "MAM";
        } else if (absScore <= 2) {
            Log.v("ZSCORE", "zscore:" + absScore + ":color:green");
            return "NORMAL";
        } else {
            Log.v("ZSCORE", "zscore:" + absScore + ":color:black");
            return "OVER WEIGHT";
        }
    }

    public static int getZscoreColorByText(String text) {
        if (text.contains("SAM")) {
            return R.color.sam;
        }else if (text.contains("OVER WEIGHT")) {
            return android.R.color.holo_purple;
        }
        else if (text.contains("DARK YELLOW")) {
            return R.color.dark_yellow;
        } else if (text.contains("MAM")) {
            return R.color.yellow;
        } else {
            return R.color.z_score_0;
        }
    }

    public static int getPEMStatusColor(String status) {
        switch (status) {
            case "SAM":
                return R.color.sam;
            case "OVER WEIGHT":
                return android.R.color.holo_purple;
            case "MAM":
                return R.color.mam;
            case "NORMAL":
                return R.color.z_score_0;
            default:
                return R.color.z_score_3;
        }
    }

    public static String getPEMStatus(double weightKg, double heightCM) {
        double bmi = (weightKg * 1000) / (heightCM * heightCM);
        if (bmi < 16) {
            return "SAM";
        } else if (bmi >= 16 && bmi <= 16.9) {
            return "MAM";
        } else if (bmi >= 17 && bmi <= 18.4) {
            return "MILD";
        } else if (bmi >= 18.5 && bmi <= 20) {
            return "MAR";
        } else {
            return "NORMAL";
        }

    }

    public static double roundOff(double value) {
        int scale = (int) Math.pow(10, 1);
        return (double) Math.round(value * scale) / scale;
    }

    /**
     * This method calculates Z (The z-score) using the formulae provided here https://www.cdc.gov/growthcharts/percentile_data_files.htm
     *
     * @param x The weight to use
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
     * This method calculates X (weight) given the Z-Score
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

    public static double calculate(Gender gender, Date dateOfBirth, Date weighingDate, double weight) {
        if (dateOfBirth != null && gender != null && weighingDate != null) {
            int ageInMonths = (int) Math.round(getAgeInMonths(dateOfBirth, weighingDate));
            List<ZScore> zScores = GrowthMonitoringLibrary.getInstance().zScoreRepository().findByGender(gender);

            ZScore zScoreToUse = null;
            for (ZScore curZScore : zScores) {
                if (curZScore.month == ageInMonths) {
                    zScoreToUse = curZScore;
                    break;
                }
            }

            if (zScoreToUse != null) {
                return zScoreToUse.getZ(weight);
            }
        }

        return 0;
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
        List<ZScore> zScores = GrowthMonitoringLibrary.getInstance().zScoreRepository().findByGender(gender);

        ZScore zScoreToUse = null;
        for (ZScore curZScore : zScores) {
            if (curZScore.month == ageInMonths) {
                zScoreToUse = curZScore;
                break;
            }
        }

        if (zScoreToUse != null) {
            return zScoreToUse.getX(z);
        }

        return null;
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
            result = ((double) (weighingCalendar.getTimeInMillis() - dobCalendar.getTimeInMillis())) / 2629746000l;
        }

        return result;
    }

    private static void standardiseCalendarDate(Calendar calendarDate) {
        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getL() {
        return l;
    }

    public void setL(double l) {
        this.l = l;
    }

    public double getM() {
        return m;
    }

    public void setM(double m) {
        this.m = m;
    }

    public double getS() {
        return s;
    }

    public void setS(double s) {
        this.s = s;
    }

    public double getSd3Neg() {
        return sd3Neg;
    }

    public void setSd3Neg(double sd3Neg) {
        this.sd3Neg = sd3Neg;
    }

    public double getSd2Neg() {
        return sd2Neg;
    }

    public void setSd2Neg(double sd2Neg) {
        this.sd2Neg = sd2Neg;
    }

    public double getSd1Neg() {
        return sd1Neg;
    }

    public void setSd1Neg(double sd1Neg) {
        this.sd1Neg = sd1Neg;
    }

    public double getSd0() {
        return sd0;
    }

    public void setSd0(double sd0) {
        this.sd0 = sd0;
    }

    public double getSd1() {
        return sd1;
    }

    public void setSd1(double sd1) {
        this.sd1 = sd1;
    }

    public double getSd2() {
        return sd2;
    }

    public void setSd2(double sd2) {
        this.sd2 = sd2;
    }

    public double getSd3() {
        return sd3;
    }

    public void setSd3(double sd3) {
        this.sd3 = sd3;
    }
}
