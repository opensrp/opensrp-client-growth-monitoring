package org.smartregister.growthmonitoring;

import org.smartregister.Context;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.HeightZScoreRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.repository.WeightZScoreRepository;
import org.smartregister.growthmonitoring.util.AppProperties;
import org.smartregister.growthmonitoring.util.GrowthMonitoringUtils;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;

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
    private EventClientRepository eventClientRepository;
    private int applicationVersion;
    private int databaseVersion;
    private AppProperties appProperties;

    private GrowthMonitoringLibrary(Context context, Repository repository, int applicationVersion, int databaseVersion) {
        this.repository = repository;
        this.context = context;
        this.applicationVersion = applicationVersion;
        this.databaseVersion = databaseVersion;
        this.appProperties = GrowthMonitoringUtils.getProperties(this.context.applicationContext());
    }

    public static void init(Context context, Repository repository, int applicationVersion, int databaseVersion,
                            GrowthMonitoringConfig growthMonitoringConfig) {

        init(context, repository, applicationVersion, databaseVersion);

        config = growthMonitoringConfig != null ? growthMonitoringConfig : config;

    }

    public static void init(Context context, Repository repository, int applicationVersion, int databaseVersion) {
        if (instance == null) {
            instance = new GrowthMonitoringLibrary(context, repository, applicationVersion, databaseVersion);
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
            weightRepository = new WeightRepository(getRepository());
        }
        return weightRepository;
    }

    public Repository getRepository() {
        return repository;
    }

    public HeightRepository heightRepository() {
        if (heightRepository == null) {
            heightRepository = new HeightRepository(getRepository());
        }
        return heightRepository;
    }


    public WeightZScoreRepository weightZScoreRepository() {
        if (weightZScoreRepository == null) {
            weightZScoreRepository = new WeightZScoreRepository(getRepository());
        }

        return weightZScoreRepository;
    }

    public HeightZScoreRepository heightZScoreRepository() {
        if (heightZScoreRepository == null) {
            heightZScoreRepository = new HeightZScoreRepository(getRepository());
        }

        return heightZScoreRepository;
    }

    public EventClientRepository eventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository(getRepository());
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

    public GrowthMonitoringConfig getConfig() {
        return config;
    }

    public AppProperties getAppProperties() {
        return appProperties;
    }

    public void setAppProperties(AppProperties appProperties) {
        this.appProperties = appProperties;
    }
}
