package org.smartregister.growthmonitoring;

import androidx.annotation.NonNull;

import org.smartregister.Context;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.HeightZScoreRepository;
import org.smartregister.growthmonitoring.repository.WeightForHeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.repository.WeightZScoreRepository;
import org.smartregister.growthmonitoring.util.AppProperties;
import org.smartregister.growthmonitoring.util.GrowthMonitoringUtils;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;

import java.util.concurrent.TimeUnit;

/**
 * Created by koros on 2/3/16.
 */
public class GrowthMonitoringLibrary {

    private static GrowthMonitoringConfig config = new GrowthMonitoringConfig();
    private static GrowthMonitoringLibrary instance;
    private final Repository repository;
    private final Context context;
    private WeightRepository weightRepository;
    private HeightRepository heightRepository;
    private WeightZScoreRepository weightZScoreRepository;
    private HeightZScoreRepository heightZScoreRepository;
    private WeightForHeightRepository weightForHeightRepository;
    private EventClientRepository eventClientRepository;
    private int applicationVersion;
    private int databaseVersion;
    private String applicationVersionName;
    private AppProperties appProperties;

    private long growthMonitoringSyncTime = -1;

    private GrowthMonitoringLibrary(Context context, Repository repository, int applicationVersion, String applicationVersionName, int databaseVersion) {
        this.repository = repository;
        this.context = context;
        this.applicationVersion = applicationVersion;
        this.databaseVersion = databaseVersion;
        this.applicationVersionName = applicationVersionName;
        this.appProperties = GrowthMonitoringUtils.getProperties(this.context.applicationContext());
    }

    /**
     * This init is deprecated, use {@link #init(Context context, Repository repository, int applicationVersion, int databaseVersion, GrowthMonitoringConfig growthMonitoringConfig)} instead which adds application version name.
     */
    @Deprecated
    public static void init(Context context, Repository repository, int applicationVersion, int databaseVersion, GrowthMonitoringConfig growthMonitoringConfig) {

        init(context, repository, applicationVersion, databaseVersion);

        config = growthMonitoringConfig != null ? growthMonitoringConfig : config;

    }

    public static void init(Context context, Repository repository, int applicationVersion, String applicationVersionName, int databaseVersion, GrowthMonitoringConfig growthMonitoringConfig) {

        init(context, repository, applicationVersion, applicationVersionName, databaseVersion);

        config = growthMonitoringConfig != null ? growthMonitoringConfig : config;

    }

    /**
     * This init is deprecated, use {@link #init(Context, Repository, int, String, int)} instead which adds application version name.
     */
    @Deprecated
    public static void init(Context context, Repository repository, int applicationVersion, int databaseVersion) {
        init(context, repository, applicationVersion, null, databaseVersion);
    }

    public static void init(Context context, Repository repository, int applicationVersion, String applicationVersionName, int databaseVersion) {
        if (instance == null) {
            instance = new GrowthMonitoringLibrary(context, repository, applicationVersion, applicationVersionName, databaseVersion);
        }
    }

    public static GrowthMonitoringLibrary getInstance() {
        if (instance == null) {
            throw new IllegalStateException(" Instance does not exist!!! Call " + GrowthMonitoringLibrary.class.getName() +
                    ".init method in the onCreate method of your Application class ");
        }
        return instance;
    }

    public WeightRepository weightRepository() {
        if (weightRepository == null) {
            weightRepository = new WeightRepository();
        }
        return weightRepository;
    }

    public Repository getRepository() {
        return repository;
    }

    public HeightRepository heightRepository() {
        if (heightRepository == null) {
            heightRepository = new HeightRepository();
        }
        return heightRepository;
    }


    public WeightZScoreRepository weightZScoreRepository() {
        if (weightZScoreRepository == null) {
            weightZScoreRepository = new WeightZScoreRepository();
        }

        return weightZScoreRepository;
    }

    public HeightZScoreRepository heightZScoreRepository() {
        if (heightZScoreRepository == null) {
            heightZScoreRepository = new HeightZScoreRepository();
        }

        return heightZScoreRepository;
    }

    public WeightForHeightRepository weightForHeightRepository() {
        if (weightForHeightRepository == null) {
            weightForHeightRepository = new WeightForHeightRepository();
        }
        return weightForHeightRepository;
    }

    public EventClientRepository eventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository();
        }

        return eventClientRepository;
    }

    public Context context() {
        return context;
    }

    public int getApplicationVersion() {
        return applicationVersion;
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public String getApplicationVersionName() {
        return applicationVersionName;
    }

    public GrowthMonitoringConfig getConfig() {
        return config;
    }

    public AppProperties getAppProperties() {
        return appProperties;
    }

    public void setAppProperties(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public long getGrowthMonitoringSyncTime() {
        if (growthMonitoringSyncTime == -1) {
            setGrowthMonitoringSyncTime(BuildConfig.GROWTH_MONITORING_SYNC_TIME);
        }

        return growthMonitoringSyncTime;
    }

    public void setGrowthMonitoringSyncTime(int growthMonitoringSyncTimeInHours) {
        setGrowthMonitoringSyncTime(growthMonitoringSyncTimeInHours, TimeUnit.HOURS);
    }

    public void setGrowthMonitoringSyncTime(int growthMonitoringSyncTime, @NonNull TimeUnit timeUnit) {
        this.growthMonitoringSyncTime = timeUnit.toMinutes(growthMonitoringSyncTime);
    }

    /**
     * Public method to clear the instance/destroy useful for testing
     */
    public static void destroy() {
        instance = null;
    }

}
