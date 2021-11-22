package org.smartregister.growthmonitoring.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.util.GMConstants;
import org.smartregister.growthmonitoring.util.JsonFormUtils;

import java.util.List;

/**
 * Created by keyman on 3/01/2017.
 */
public class HeightIntentService extends IntentService {
    private static final String TAG = HeightIntentService.class.getCanonicalName();
    public static final String EVENT_TYPE = "Height Monitoring";
    public static final String EVENT_TYPE_OUT_OF_CATCHMENT = "Out of Area Service - Height Monitoring";
    public static final String ENTITY_TYPE = "Height";
    private HeightRepository heightRepository;


    public HeightIntentService() {
        super("HeightService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            List<Height> weights = heightRepository.findUnSyncedBeforeTime(GMConstants.WEIGHT_SYNC_TIME);
            if (!weights.isEmpty()) {
                for (Height weight : weights) {

                    //Weight
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(GMConstants.JsonForm.KEY, "Height_cm");
                    jsonObject.put(GMConstants.JsonForm.OPENMRS_ENTITY, "concept");
                    jsonObject.put(GMConstants.JsonForm.OPENMRS_ENTITY_ID, "5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    jsonObject.put(GMConstants.JsonForm.OPENMRS_ENTITY_PARENT, "");
                    jsonObject.put(GMConstants.JsonForm.OPENMRS_DATA_TYPE, "decimal");
                    jsonObject.put(GMConstants.JsonForm.VALUE, weight.getCm());

                    //Zscore
                    JSONObject zScoreObject = new JSONObject();
                    zScoreObject.put(GMConstants.JsonForm.KEY, "Z_Score_Height_Age");
                    zScoreObject.put(GMConstants.JsonForm.OPENMRS_ENTITY, "concept");
                    zScoreObject.put(GMConstants.JsonForm.OPENMRS_ENTITY_ID, "162584AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    zScoreObject.put(GMConstants.JsonForm.OPENMRS_ENTITY_PARENT, "");
                    zScoreObject.put(GMConstants.JsonForm.OPENMRS_DATA_TYPE, "calculation");
                    zScoreObject.put(GMConstants.JsonForm.VALUE, weight.getZScore());
                    //level
                    String level = ZScore.getZScoreText(weight.getZScore());
                    JSONObject levelObject = new JSONObject();
                    levelObject.put(GMConstants.JsonForm.KEY, "height_level");
                    levelObject.put(GMConstants.JsonForm.OPENMRS_ENTITY, "concept");
                    levelObject.put(GMConstants.JsonForm.OPENMRS_ENTITY_ID, "height_level");
                    levelObject.put(GMConstants.JsonForm.OPENMRS_ENTITY_PARENT, "");
                    levelObject.put(GMConstants.JsonForm.OPENMRS_DATA_TYPE, "calculation");
                    levelObject.put(GMConstants.JsonForm.VALUE, level);

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);
                    jsonArray.put(zScoreObject);

                    JsonFormUtils.createHeightEvent(getApplicationContext(), weight, EVENT_TYPE, ENTITY_TYPE, jsonArray);
                    if (weight.getBaseEntityId() == null || weight.getBaseEntityId().isEmpty()) {
                        JsonFormUtils.createHeightEvent(getApplicationContext(), weight, EVENT_TYPE_OUT_OF_CATCHMENT, ENTITY_TYPE, jsonArray);

                    }
                    heightRepository.close(weight.getId());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        heightRepository = GrowthMonitoringLibrary.getInstance().getHeightRepository();
        return super.onStartCommand(intent, flags, startId);
    }
}
