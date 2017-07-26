package org.smartregister.growthmonitoring.application;

import org.smartregister.Context;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.repository.ZScoreRepository;
import org.smartregister.repository.Repository;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.view.activity.DrishtiApplication;

import static org.smartregister.util.Log.logError;

/**
 * Created by koros on 2/3/16.
 */
public class GrowthMonitoringApplication extends DrishtiApplication {

    private static final String TAG = "GrowthMonitoringApplication";
    private WeightRepository weightRepository;
    private ZScoreRepository zScoreRepository;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
    }

    public static synchronized GrowthMonitoringApplication getInstance() {
        return (GrowthMonitoringApplication) mInstance;
    }

    @Override
    public void logoutCurrentUser() {

    }

    public Context context() {
        return context;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }


    public WeightRepository weightRepository() {
        if (weightRepository == null) {
            weightRepository = new WeightRepository(getRepository());
        }
        return weightRepository;
    }


    public ZScoreRepository zScoreRepository() {
        if (zScoreRepository == null) {
            zScoreRepository = new ZScoreRepository(getRepository());
        }

        return zScoreRepository;
    }

}
