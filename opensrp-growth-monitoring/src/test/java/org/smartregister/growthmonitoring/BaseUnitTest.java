package org.smartregister.growthmonitoring;

import android.os.Build;

import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.O_MR1)
@PowerMockIgnore({"android.*",
        "androidx.*",
        "com.android.internal.policy.*",
        "com.sun.org.*",
        "javax.xml.*",
        "org.apache.log4j.*",
        "org.mockito.*",
        "org.powermock.*",
        "org.robolectric.*",
        "org.springframework.context.*",
        "org.w3c.dom.*",
        "org.xml.sax.*",
        "org.xmlpull*",
        "org.mockito.*",
        "org.xmlpull.v1.*"})
public abstract class BaseUnitTest {
    protected static final String TEST_BASE_ENTITY_ID = "test-base-entity-id";
    protected static final String TEST_STRING = "test-string-param";

    public static CommonPersonObjectClient dummydetails() {
        HashMap<String, String> columnMap = new HashMap<>();
        columnMap.put("first_name", "Test");
        columnMap.put("last_name", "Doe");
        columnMap.put("zeir_id", "1");
        columnMap.put("dob", "2018-09-03");
        columnMap.put("gender", "Male");


        CommonPersonObjectClient personDetails = new CommonPersonObjectClient(TEST_BASE_ENTITY_ID, columnMap, "Test");
        personDetails.setColumnmaps(columnMap);

        return personDetails;
    }


    @NotNull
    protected List<Height> getHeights() {
        List<Height> heightArrayList = new ArrayList<>();

        Height height;
//recorded April
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 3);
        height = new Height();
        height.setDate(cal.getTime());
        height.setAnmId("demo");
        height.setBaseEntityId(TEST_BASE_ENTITY_ID);
        height.setChildLocationId(TEST_STRING);
        height.setCm(30.4f);
        heightArrayList.add(height);

//recorded March
        height = new Height();
        cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 2);
        height.setDate(cal.getTime());
        height.setAnmId("demo");
        height.setBaseEntityId(TEST_BASE_ENTITY_ID);
        height.setChildLocationId(TEST_STRING);
        height.setCm(10.0f);
        heightArrayList.add(height);

//recorded August
        height = new Height();
        cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 9);
        height.setDate(cal.getTime());
        height.setAnmId("demo");
        height.setBaseEntityId(TEST_BASE_ENTITY_ID);
        height.setChildLocationId(TEST_STRING);
        height.setCm(50.2f);
        heightArrayList.add(height);

//recorded May
        height = new Height();
        cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 6);
        height.setDate(cal.getTime());
        height.setAnmId("demo");
        height.setBaseEntityId(TEST_BASE_ENTITY_ID);
        height.setChildLocationId(TEST_STRING);
        height.setCm(40.3f);
        heightArrayList.add(height);


        return heightArrayList;
    }

    @NotNull
    protected List<Weight> getWeights() {
        List<Weight> weightArrayList = new ArrayList<>();

        Weight weight = new Weight();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 3);
        weight.setDate(cal.getTime());
        weight.setAnmId("demo");
        weight.setBaseEntityId(TEST_BASE_ENTITY_ID);
        weight.setChildLocationId(TEST_STRING);
        weight.setKg(3.4f);

        weightArrayList.add(weight);

        weight = new Weight();
        cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 9);
        weight.setDate(cal.getTime());
        weight.setAnmId("demo");
        weight.setBaseEntityId(TEST_BASE_ENTITY_ID);
        weight.setChildLocationId(TEST_STRING);
        weight.setKg(5.2f);

        weightArrayList.add(weight);
        return weightArrayList;
    }
}
