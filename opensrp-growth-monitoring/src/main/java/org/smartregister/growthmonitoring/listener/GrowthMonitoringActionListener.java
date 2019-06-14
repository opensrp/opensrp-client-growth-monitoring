package org.smartregister.growthmonitoring.listener;

import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.WeightWrapper;

/**
 * Created by keyman on 22/11/2016.
 */
public interface GrowthMonitoringActionListener {

    void onGrowthRecorded(WeightWrapper weightWrapper, HeightWrapper heightWrapper);

}
