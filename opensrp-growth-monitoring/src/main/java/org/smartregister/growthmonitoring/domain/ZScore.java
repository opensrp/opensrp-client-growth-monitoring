package org.smartregister.growthmonitoring.domain;

import org.opensrp.api.constants.Gender;

/**
 * Created by wizard on 08/07/19.
 */
public class ZScore {
    private Gender gender;
    private int month;
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

    /**
     * This method calculates Z (The z-score) using the formulae provided here https://www.cdc
     * .gov/growthcharts/percentile_data_files.htm
     *
     * @param x The height/weight to use
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
}
