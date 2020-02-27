package org.smartregister.growthmonitoring.service.intent;

import android.app.IntentService;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.util.GrowthMonitoringConstants;
import org.smartregister.growthmonitoring.util.JsonFormUtils;

import java.util.List;

import timber.log.Timber;

/**
 * Created by keyman on 3/01/2017.
 */
public class HeightIntentService extends IntentService {
    public static final String EVENT_TYPE = "Growth Monitoring";
    public static final String EVENT_TYPE_OUT_OF_CATCHMENT = "Out of Area Service - Growth Monitoring";
    public static final String ENTITY_TYPE = "height";
    private HeightRepository heightRepository;


    public HeightIntentService() {
        super("HeightService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        heightRepository = GrowthMonitoringLibrary.getInstance().heightRepository();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            List<Height> heights =
                    heightRepository.findUnSyncedBeforeTime((int) GrowthMonitoringLibrary.getInstance().getGrowthMonitoringSyncTime());
            if (!heights.isEmpty()) {
                for (Height height : heights) {

                    //Weight
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(GrowthMonitoringConstants.JsonForm.KEY, "height_cm");
                    jsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY, "concept");
                    jsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY_ID,
                            "5090AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    jsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY_PARENT, "");
                    jsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_DATA_TYPE, "decimal");
                    jsonObject.put(GrowthMonitoringConstants.JsonForm.VALUE, height.getCm());

                    //Zscore
                    JSONObject zScoreObject = new JSONObject();
                    zScoreObject.put(GrowthMonitoringConstants.JsonForm.KEY, "Z_Score_Height_Age");
                    zScoreObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY, "");
                    zScoreObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY_ID,
                            "");
                    zScoreObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY_PARENT, "");
                    zScoreObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_DATA_TYPE, "calculation");
                    zScoreObject.put(GrowthMonitoringConstants.JsonForm.VALUE, height.getZScore());

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);
                    jsonArray.put(zScoreObject);

                    JsonFormUtils.createHeightEvent(height, EVENT_TYPE, ENTITY_TYPE, jsonArray);
                    if (height.getBaseEntityId() == null || height.getBaseEntityId().isEmpty()) {
                        JsonFormUtils.createHeightEvent(height, EVENT_TYPE_OUT_OF_CATCHMENT, ENTITY_TYPE, jsonArray);

                    }
                    heightRepository.close(height.getId());
                }
            }
        } catch (Exception e) {
            Timber.e(e, "HeightIntentService --> onHandleIntent");
        }
    }
}
