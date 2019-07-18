package org.smartregister.growthmonitoring.util;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.repository.EventClientRepository;

import java.util.Date;

import timber.log.Timber;

/**
 * Created by keyman on 26/07/2017.
 */
public class JsonFormUtils extends org.smartregister.util.JsonFormUtils {
    public static void createWeightEvent(Weight weight, String eventType, String entityType, JSONArray fields) {
        try {
            EventClientRepository db = GrowthMonitoringLibrary.getInstance().eventClientRepository();

            Event event =
                    (Event) new Event().withBaseEntityId(weight.getBaseEntityId()).withIdentifiers(weight.getIdentifiers())
                            .withEventDate(weight.getDate()).withEventType(eventType).withLocationId(weight.getLocationId())
                            .withProviderId(weight.getAnmId()).withEntityType(entityType).withFormSubmissionId(
                                    weight.getFormSubmissionId() == null ? generateRandomUUIDString() :
                                            weight.getFormSubmissionId()).withDateCreated(new Date());

            event.setTeam(weight.getTeam());
            event.setTeamId(weight.getTeamId());
            event.setChildLocationId(weight.getChildLocationId());

            event.setClientApplicationVersion(GrowthMonitoringLibrary.getInstance().getApplicationVersion());
            event.setClientDatabaseVersion(GrowthMonitoringLibrary.getInstance().getDatabaseVersion());

            if (fields != null && fields.length() != 0) for (int i = 0; i < fields.length(); i++) {
                JSONObject jsonObject = getJSONObject(fields, i);
                String value = getString(jsonObject, VALUE);
                if (StringUtils.isNotBlank(value)) {
                    addObservation(event, jsonObject);
                }
            }


            if (event != null) {
                JSONObject eventJson = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(event));

                //check if an event already exists and update instead
                if (weight.getEventId() != null) {
                    JSONObject existingEvent = db.getEventsByEventId(weight.getEventId());
                    eventJson = merge(existingEvent, eventJson);
                }

                //merge if event exists
                db.addEvent(event.getBaseEntityId(), eventJson);

            }
        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> createWeightEvent");
        }
    }

    public static void createHeightEvent(Height height, String eventType, String entityType, JSONArray fields) {
        try {
            EventClientRepository db = GrowthMonitoringLibrary.getInstance().eventClientRepository();

            Event event =
                    (Event) new Event().withBaseEntityId(height.getBaseEntityId()).withIdentifiers(height.getIdentifiers())
                            .withEventDate(height.getDate()).withEventType(eventType).withLocationId(height.getLocationId())
                            .withProviderId(height.getAnmId()).withEntityType(entityType).withFormSubmissionId(
                                    height.getFormSubmissionId() == null ? generateRandomUUIDString() :
                                            height.getFormSubmissionId()).withDateCreated(new Date());

            event.setTeam(height.getTeam());
            event.setTeamId(height.getTeamId());
            event.setChildLocationId(height.getChildLocationId());

            event.setClientApplicationVersion(GrowthMonitoringLibrary.getInstance().getApplicationVersion());
            event.setClientDatabaseVersion(GrowthMonitoringLibrary.getInstance().getDatabaseVersion());

            if (fields != null && fields.length() != 0) for (int i = 0; i < fields.length(); i++) {
                JSONObject jsonObject = getJSONObject(fields, i);
                String value = getString(jsonObject, VALUE);
                if (StringUtils.isNotBlank(value)) {
                    addObservation(event, jsonObject);
                }
            }


            if (event != null) {
                JSONObject eventJson = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(event));
                //check if an event already exists and update instead
                if (height.getEventId() != null) {
                    JSONObject existingEvent = db.getEventsByEventId(height.getEventId());
                    eventJson = merge(existingEvent, eventJson);
                }

                //merge if event exists
                db.addEvent(event.getBaseEntityId(), eventJson);

            }
        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> createHeightEvent");
        }
    }

}


