package org.smartregister.growthmonitoring.repository;

import android.support.annotation.NonNull;
import android.util.Log;

import org.jetbrains.annotations.Nullable;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 2019-11-21.
 */
public abstract class GrowthRepository extends BaseRepository {

    @Nullable
    public static String getChildLocationId(@NonNull String defaultLocationId, @NonNull AllSharedPreferences allSharedPreferences) {
        try {
            String currentLocality = allSharedPreferences.fetchCurrentLocality();

            if (currentLocality != null) {
                String currentLocalityId = LocationHelper.getInstance().getOpenMrsLocationId(currentLocality);
                if (currentLocalityId != null && !defaultLocationId.equals(currentLocalityId)) {
                    return currentLocalityId;
                }
            }

            return null;
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
            return null;
        }
    }
}
