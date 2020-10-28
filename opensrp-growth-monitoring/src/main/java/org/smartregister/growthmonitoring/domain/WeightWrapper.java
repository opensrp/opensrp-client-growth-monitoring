package org.smartregister.growthmonitoring.domain;

import org.joda.time.DateTime;
import org.smartregister.domain.Photo;

import java.io.Serializable;

/**
 * Created by keyman on 16/11/2016.
 */
public class WeightWrapper implements Serializable {
    private String id;
    private Long dbKey;
    private String gender;
    private Photo photo;
    private String patientName;
    private String patientNumber;
    private String patientAge;
    private String pmtctStatus;
    private Float weight;
    private DateTime updatedWeightDate;
    private boolean today;
    private String dob;
    private Integer outOfCatchment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDbKey() {
        return dbKey;
    }

    public void setDbKey(Long dbKey) {
        this.dbKey = dbKey;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public DateTime getUpdatedWeightDate() {
        return updatedWeightDate;
    }

    public void setUpdatedWeightDate(DateTime updatedWeightDate, boolean today) {
        this.today = today;
        this.updatedWeightDate = updatedWeightDate;
    }

    public boolean isToday() {
        return today;
    }

    public String getUpdatedWeightDateAsString() {
        return updatedWeightDate != null ? updatedWeightDate.toString("yyyy-MM-dd") : "";
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public String getPmtctStatus() {
        return pmtctStatus;
    }

    public void setPmtctStatus(String pmtctStatus) {
        this.pmtctStatus = pmtctStatus;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public Integer getOutOfCatchment() {
        return outOfCatchment;
    }

    public void setOutOfCatchment(Integer outOfCatchment) {
        this.outOfCatchment = outOfCatchment;
    }
}
