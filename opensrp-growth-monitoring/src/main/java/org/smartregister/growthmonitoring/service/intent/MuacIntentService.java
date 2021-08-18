package org.smartregister.growthmonitoring.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.MUAC;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.MUACRepository;
import org.smartregister.growthmonitoring.util.GMConstants;
import org.smartregister.growthmonitoring.util.JsonFormUtils;

import java.util.List;

/**
 * Created by keyman on 3/01/2017.
 */
public class MuacIntentService extends IntentService {
    private static final String TAG = MuacIntentService.class.getCanonicalName();
    public static final String EVENT_TYPE = "MUAC Monitoring";
    public static final String EVENT_TYPE_OUT_OF_CATCHMENT = "Out of Area Service - MUAC Monitoring";
    public static final String ENTITY_TYPE = "MUAC";
    private MUACRepository muacRepository;


    public MuacIntentService() {
        super("MUACService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            List<MUAC> weights = muacRepository.findUnSyncedBeforeTime(GMConstants.WEIGHT_SYNC_TIME);
            if (!weights.isEmpty()) {
                for (MUAC weight : weights) {

                    //Weight
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(GMConstants.JsonForm.KEY, "MUAC_cm");
                    jsonObject.put(GMConstants.JsonForm.OPENMRS_ENTITY, "concept");
                    jsonObject.put(GMConstants.JsonForm.OPENMRS_ENTITY_ID, "5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    jsonObject.put(GMConstants.JsonForm.OPENMRS_ENTITY_PARENT, "");
                    jsonObject.put(GMConstants.JsonForm.OPENMRS_DATA_TYPE, "decimal");
                    jsonObject.put(GMConstants.JsonForm.VALUE, weight.getCm());

                    //Zscore
                    JSONObject zScoreObject = new JSONObject();
                    zScoreObject.put(GMConstants.JsonForm.KEY, "muac_level");
                    zScoreObject.put(GMConstants.JsonForm.OPENMRS_ENTITY, "concept");
                    zScoreObject.put(GMConstants.JsonForm.OPENMRS_ENTITY_ID, "162584AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    zScoreObject.put(GMConstants.JsonForm.OPENMRS_ENTITY_PARENT, "");
                    zScoreObject.put(GMConstants.JsonForm.OPENMRS_DATA_TYPE, "calculation");
                    zScoreObject.put(GMConstants.JsonForm.VALUE, ZScore.getMuacText( weight.getCm()));

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);
                    jsonArray.put(zScoreObject);

                    JsonFormUtils.createMuacEvent(getApplicationContext(), weight, EVENT_TYPE, ENTITY_TYPE, jsonArray);
                    if (weight.getBaseEntityId() == null || weight.getBaseEntityId().isEmpty()) {
                        JsonFormUtils.createMuacEvent(getApplicationContext(), weight, EVENT_TYPE_OUT_OF_CATCHMENT, ENTITY_TYPE, jsonArray);

                    }
                    muacRepository.close(weight.getId());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        muacRepository = GrowthMonitoringLibrary.getInstance().getMuacRepository();
        return super.onStartCommand(intent, flags, startId);
    }
}
