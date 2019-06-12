package org.smartregister.growthmonitoring.fragment;


import org.junit.Assert;

import org.junit.Test;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.growthmonitoring.domain.GrowthMonitoring;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ndegwamartin on 2019-06-03.
 */
public class GrowthDialogFragmentTest {


    protected static final String DUMMY_BASE_ENTITY_ID = "00ts-ime-hcla-0tib-0eht-ma0i";
    protected static final String TEST_STRING = "teststring";

    @Test
    public void testGrowthDialogFragmentInstantiatesValidInstance() {

        List<GrowthMonitoring> growthMonitoringList = new ArrayList<>();

        GrowthMonitoring growthMonitoring = new GrowthMonitoring();
        growthMonitoring.setDate(new Date());
        growthMonitoring.setAnmId("demo");
        growthMonitoring.setBaseEntityId(DUMMY_BASE_ENTITY_ID);
        growthMonitoring.setChildLocationId(TEST_STRING);
        growthMonitoring.setUnit(3.4f);

        growthMonitoringList.add(growthMonitoring);


        GrowthDialogFragment dialogFragment = GrowthDialogFragment.newInstance(dummydetails(), growthMonitoringList);
        Assert.assertNotNull(dialogFragment);

    }


    public static CommonPersonObjectClient dummydetails() {
        HashMap<String, String> columnMap = new HashMap<>();
        columnMap.put("first_name", "Test");
        columnMap.put("last_name", "Doe");
        columnMap.put("zeir_id", "1");
        columnMap.put("dob", "2018-09-03");
        columnMap.put("gender", "Male");


        CommonPersonObjectClient personDetails = new CommonPersonObjectClient(DUMMY_BASE_ENTITY_ID, columnMap, "Test");
        personDetails.setColumnmaps(columnMap);

        return personDetails;
    }
}
