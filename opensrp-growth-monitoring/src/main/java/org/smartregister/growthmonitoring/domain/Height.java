package org.smartregister.growthmonitoring.domain;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by keyman on 3/1/17.
 */
public class Height {
    private static final String ZEIR_ID = "ZEIR_ID";
    private Long id;
    private String baseEntityId;
    private String eventId;
    private String formSubmissionId;
    private String programClientId;
    private Float cm;
    private Date date;
    private String anmId;
    private String locationId;
    private String childLocationId;
    private String team;
    private String teamId;
    private String syncStatus;
    private Integer outOfCatchment;
    private Long updatedAt;
    private Double zScore;
    private Date createdAt;

    public Height() {
    }

    public Height(Long id, String baseEntityId, Float cm, Date date, String anmId, String locationId, String syncStatus,
                  Long updatedAt, String eventId, String formSubmissionId, Integer outOfCatchment) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.programClientId = null;
        this.cm = cm;
        this.date = date;
        this.anmId = anmId;
        this.locationId = locationId;
        this.syncStatus = syncStatus;
        this.updatedAt = updatedAt;
        this.eventId = eventId;
        this.formSubmissionId = formSubmissionId;
        this.outOfCatchment = outOfCatchment;
    }

    public Height(Long id, String baseEntityId, String programClientId, Float cm, Date date, String anmId, String locationId,
                  String syncStatus, Long updatedAt, String eventId, String formSubmissionId, Double zScore,
                  Integer outOfCatchment, Date createdAt) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.programClientId = programClientId;
        this.cm = cm;
        this.date = date;
        this.anmId = anmId;
        this.locationId = locationId;
        this.syncStatus = syncStatus;
        this.updatedAt = updatedAt;
        this.eventId = eventId;
        this.formSubmissionId = formSubmissionId;
        this.outOfCatchment = outOfCatchment;
        this.zScore = zScore;
        this.createdAt = createdAt;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getProgramClientId() {
        return programClientId;
    }

    public void setProgramClientId(String programClientId) {
        this.programClientId = programClientId;
    }

    public HashMap<String, String> getIdentifiers() {
        HashMap<String, String> identifiers = new HashMap<>();
        identifiers.put(ZEIR_ID, programClientId);
        return identifiers;
    }

    public Float getCm() {
        return cm;
    }

    public void setCm(Float cm) {
        this.cm = cm;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAnmId() {
        return anmId;
    }

    public void setAnmId(String anmId) {
        this.anmId = anmId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getFormSubmissionId() {
        return formSubmissionId;
    }

    public void setFormSubmissionId(String formSubmissionId) {
        this.formSubmissionId = formSubmissionId;
    }

    public Integer getOutOfCatchment() {
        return outOfCatchment;
    }

    public void setOutOfCatchment(Integer outOfCatchment) {
        this.outOfCatchment = outOfCatchment;
    }

    public Double getZScore() {
        return zScore;
    }

    public void setZScore(Double zScore) {
        this.zScore = zScore;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getChildLocationId() {
        return childLocationId;
    }

    public void setChildLocationId(String childLocationId) {
        this.childLocationId = childLocationId;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }
}
