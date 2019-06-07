package org.smartregister.growthmonitoring.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.util.GrowthMonitoringConstants;
import org.smartregister.growthmonitoring.util.JsonFormUtils;

import java.util.List;

/**
 * Created by keyman on 3/01/2017.
 */
public class WeightIntentService extends IntentService {
    private static final String TAG = WeightIntentService.class.getCanonicalName();
    public static final String EVENT_TYPE = "Growth Monitoring";
    public static final String EVENT_TYPE_OUT_OF_CATCHMENT = "Out of Area Service - Growth Monitoring";
    public static final String ENTITY_TYPE = "weight";
    private WeightRepository weightRepository;


    public WeightIntentService() {
        super("WeightService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            List<Weight> weights = weightRepository.findUnSyncedBeforeTime(GrowthMonitoringConstants.WEIGHT_SYNC_TIME);
            if (!weights.isEmpty()) {
                for (Weight weight : weights) {

                    //Weight
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(GrowthMonitoringConstants.JsonForm.KEY, "Weight_Kgs");
                    jsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY, "concept");
                    jsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY_ID, "5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    jsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY_PARENT, "");
                    jsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_DATA_TYPE, "decimal");
                    jsonObject.put(GrowthMonitoringConstants.JsonForm.VALUE, weight.getKg());

                    //Zscore
                    JSONObject zScoreObject = new JSONObject();
                    zScoreObject.put(GrowthMonitoringConstants.JsonForm.KEY, "Z_Score_Weight_Age");
                    zScoreObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY, "concept");
                    zScoreObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY_ID, "162584AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    zScoreObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY_PARENT, "");
                    zScoreObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_DATA_TYPE, "calculation");
                    zScoreObject.put(GrowthMonitoringConstants.JsonForm.VALUE, weight.getZScore());

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);
                    jsonArray.put(zScoreObject);

                    JsonFormUtils.createWeightEvent(getApplicationContext(), weight, EVENT_TYPE, ENTITY_TYPE, jsonArray);
                    if (weight.getBaseEntityId() == null || weight.getBaseEntityId().isEmpty()) {
                        JsonFormUtils.createWeightEvent(getApplicationContext(), weight, EVENT_TYPE_OUT_OF_CATCHMENT, ENTITY_TYPE, jsonArray);

                    }
                    weightRepository.close(weight.getId());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        weightRepository = GrowthMonitoringLibrary.getInstance().weightRepository();

        return super.onStartCommand(intent, flags, startId);
    }
}
