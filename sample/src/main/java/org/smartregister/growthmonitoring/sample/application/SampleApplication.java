package org.smartregister.growthmonitoring.sample.application;

import android.content.Intent;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.growthmonitoring.GrowthMonitoringConfig;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.sample.BuildConfig;
import org.smartregister.growthmonitoring.sample.repository.SampleRepository;
import org.smartregister.growthmonitoring.service.intent.WeightForHeightIntentService;
import org.smartregister.growthmonitoring.service.intent.ZScoreRefreshIntentService;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;

import static org.smartregister.util.Log.logError;

/**
 * Created by keyman on 27/07/2017.
 */
public class SampleApplication extends DrishtiApplication {

    public static final ArrayList<String> ALLOWED_LEVELS;
    public static final String DEFAULT_LOCATION_LEVEL = "Health Facility";
    public static final String FACILITY = "Dispensary";

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(DEFAULT_LOCATION_LEVEL);
        ALLOWED_LEVELS.add(FACILITY);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();

        context.updateApplicationContext(getApplicationContext());

        //Initialize Modules
        CoreLibrary.init(context);

        LocationHelper.init(ALLOWED_LEVELS, DEFAULT_LOCATION_LEVEL);

        GrowthMonitoringConfig config = new GrowthMonitoringConfig();
        config.setFemaleWeightZScoreFile("zscores/custom_female_zscore_file.txt");
        config.setWeightForHeightZScoreFile("weight_for_height.csv");
        GrowthMonitoringLibrary
                .init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION, config);

        startWeightForHeightIntentService();
        startZscoreRefreshService();
    }

    @Override
    public void logoutCurrentUser() {

    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new SampleRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            logError("Error on getRepository: " + e);

        }
        return repository;
    }

    public void startZscoreRefreshService() {
        Intent intent = new Intent(this.getApplicationContext(), ZScoreRefreshIntentService.class);
        this.getApplicationContext().startService(intent);
    }

    public void startWeightForHeightIntentService() {
        WeightForHeightIntentService.startParseWFHZScores(this);
    }

    public static synchronized SampleApplication getInstance() {
        return (SampleApplication) mInstance;
    }
}
