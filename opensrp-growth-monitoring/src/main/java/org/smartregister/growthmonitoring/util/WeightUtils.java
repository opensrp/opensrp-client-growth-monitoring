package org.smartregister.growthmonitoring.util;

import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.util.DateUtil;

/**
 * Created by keyman on 3/19/2018.
 */

public class WeightUtils {

    public static boolean lessThanThreeMonths(Weight weight) {
        ////////////////////////check 3 months///////////////////////////////
        return weight == null || weight.getCreatedAt() == null || !DateUtil.checkIfDateThreeMonthsOlder(weight.getCreatedAt());
        ///////////////////////////////////////////////////////////////////////
    }
}
