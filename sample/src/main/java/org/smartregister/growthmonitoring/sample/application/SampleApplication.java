package org.smartregister.growthmonitoring.sample.application;

import android.content.Intent;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.sample.BuildConfig;
import org.smartregister.growthmonitoring.sample.repository.SampleRepository;
import org.smartregister.growthmonitoring.service.intent.WeightForHeightIntentService;
import org.smartregister.growthmonitoring.service.intent.ZScoreRefreshIntentService;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import static org.smartregister.util.Log.logError;

/**
 * Created by keyman on 27/07/2017.
 */
public class SampleApplication extends DrishtiApplication {


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();

        context.updateApplicationContext(getApplicationContext());

        //Initialize Modules
        CoreLibrary.init(context);
        GrowthMonitoringLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        startWeightForHeightIntentService();

        startZscoreRefreshService();

    }

    public static synchronized SampleApplication getInstance() {
        return (SampleApplication) mInstance;
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
    public void startWeightForHeightIntentService() {
        WeightForHeightIntentService.startParseWFHZScores(this);
    }

    @Override
    public void logoutCurrentUser() {

    }
    public void startZscoreRefreshService() {
        Intent intent = new Intent(this.getApplicationContext(), ZScoreRefreshIntentService.class);
        this.getApplicationContext().startService(intent);
    }
}
