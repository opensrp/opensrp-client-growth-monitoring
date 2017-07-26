package org.smartregister.growthmonitoring.util;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.application.GrowthMonitoringApplication;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.repository.Repository;

import java.util.Date;

/**
 * Created by keyman on 26/07/2017.
 */
public class JsonFormUtils extends org.smartregister.util.JsonFormUtils {
    public static void createWeightEvent(Context context, Weight weight, String eventType, String entityType, JSONArray fields) {
        try {
            //FIXME split_growth_Monitoring: Add database
            Repository db = (Repository) GrowthMonitoringApplication.getInstance().getRepository();

            Event event = (Event) new Event()
                    .withBaseEntityId(weight.getBaseEntityId())
                    .withIdentifiers(weight.getIdentifiers())
                    .withEventDate(weight.getDate())
                    .withEventType(eventType)
                    .withLocationId(weight.getLocationId())
                    .withProviderId(weight.getAnmId())
                    .withEntityType(entityType)
                    .withFormSubmissionId(weight.getFormSubmissionId() == null ? generateRandomUUIDString() : weight.getFormSubmissionId())
                    .withDateCreated(new Date());


            if (fields != null && fields.length() != 0)
                for (int i = 0; i < fields.length(); i++) {
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
                    //FIXME split_growth_Monitoring: Add database
                  /* JSONObject existingEvent = db.getEventsByEventId(weight.getEventId());
                   eventJson = merge(existingEvent, eventJson); */
                }

                //merge if event exists
                //FIXME split_growth_Monitoring: Add database
                //db.addEvent(event.getBaseEntityId(), eventJson);

            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

}


