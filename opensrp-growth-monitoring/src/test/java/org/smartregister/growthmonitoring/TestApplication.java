package org.smartregister.growthmonitoring;

import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import timber.log.Timber;

import static org.mockito.Mockito.mock;

public class TestApplication extends DrishtiApplication {

    @Override
    public void onCreate() {
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        SyncConfiguration syncConfiguration = Mockito.mock(SyncConfiguration.class);
        Mockito.doReturn(SyncFilter.LOCATION).when(syncConfiguration).getEncryptionParam();
        CoreLibrary.init(context, syncConfiguration);
        GrowthMonitoringLibrary.init(context, repository, 0, "1.0.0", 0);
        setTheme(R.style.Theme_AppCompat);
    }

    @Override
    public void logoutCurrentUser() {
        Timber.v("Logout");
    }

    @Override
    public Repository getRepository() {
        repository = mock(Repository.class);
        return repository;
    }

    @Override
    public void onTerminate() {
        Robolectric.flushBackgroundThreadScheduler();
    }
}