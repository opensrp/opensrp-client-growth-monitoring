package org.smartregister.growthmonitoring;

import org.robolectric.Robolectric;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
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
        CoreLibrary.init(context);
        GrowthMonitoringLibrary.init(context, repository, 0, 0);
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