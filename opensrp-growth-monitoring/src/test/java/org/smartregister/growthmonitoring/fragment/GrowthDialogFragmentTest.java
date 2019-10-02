package org.smartregister.growthmonitoring.fragment;


import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.util.AppProperties;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ndegwamartin on 2019-06-03.
 */
public class GrowthDialogFragmentTest {
    protected static final String DUMMY_BASE_ENTITY_ID = "00ts-ime-hcla-0tib-0eht-ma0i";
    protected static final String TEST_STRING = "teststring";
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private GrowthMonitoringLibrary growthMonitoringLibrary;

    @Mock
    private AppProperties appProperties;

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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @PrepareForTest({GrowthMonitoringLibrary.class})
    public void testGrowthDialogFragmentInstantiatesValidInstance() {
        List<Weight> weightArrayList = getWeights();
        List<Height> heightArrayList = getHeights();

        PowerMockito.mockStatic(GrowthMonitoringLibrary.class);
        PowerMockito.when(GrowthMonitoringLibrary.getInstance()).thenReturn(growthMonitoringLibrary);
        PowerMockito.when(growthMonitoringLibrary.getAppProperties()).thenReturn(appProperties);
        PowerMockito.when(appProperties.hasProperty("monitor.height")).thenReturn(true);
        PowerMockito.when(appProperties.getPropertyBoolean("monitor.height")).thenReturn(true);

        GrowthDialogFragment dialogFragment = GrowthDialogFragment.newInstance(dummydetails(), weightArrayList, heightArrayList);
        Assert.assertNotNull(dialogFragment);

    }

    @NotNull
    private List<Height> getHeights() {
        List<Height> heightArrayList = new ArrayList<>();

        Height height;
//recorded April
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 3);
        height = new Height();
        height.setDate(cal.getTime());
        height.setAnmId("demo");
        height.setBaseEntityId(DUMMY_BASE_ENTITY_ID);
        height.setChildLocationId(TEST_STRING);
        height.setCm(30.4f);
        heightArrayList.add(height);

//recorded March
        height = new Height();
        cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 2);
        height.setDate(cal.getTime());
        height.setAnmId("demo");
        height.setBaseEntityId(DUMMY_BASE_ENTITY_ID);
        height.setChildLocationId(TEST_STRING);
        height.setCm(10.0f);
        heightArrayList.add(height);

//recorded August
        height = new Height();
        cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 9);
        height.setDate(cal.getTime());
        height.setAnmId("demo");
        height.setBaseEntityId(DUMMY_BASE_ENTITY_ID);
        height.setChildLocationId(TEST_STRING);
        height.setCm(50.2f);
        heightArrayList.add(height);

//recorded May
        height = new Height();
        cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 6);
        height.setDate(cal.getTime());
        height.setAnmId("demo");
        height.setBaseEntityId(DUMMY_BASE_ENTITY_ID);
        height.setChildLocationId(TEST_STRING);
        height.setCm(40.3f);
        heightArrayList.add(height);


        return heightArrayList;
    }

    @NotNull
    private List<Weight> getWeights() {
        List<Weight> weightArrayList = new ArrayList<>();

        Weight weight = new Weight();
        weight.setDate(new Date());
        weight.setAnmId("demo");
        weight.setBaseEntityId(DUMMY_BASE_ENTITY_ID);
        weight.setChildLocationId(TEST_STRING);
        weight.setKg(3.4f);

        weightArrayList.add(weight);
        return weightArrayList;
    }

    @PrepareForTest({GrowthMonitoringLibrary.class})
    @Test
    public void testSortHeightsOrdersItemsCorrectly() {

        PowerMockito.mockStatic(GrowthMonitoringLibrary.class);
        PowerMockito.when(GrowthMonitoringLibrary.getInstance()).thenReturn(growthMonitoringLibrary);
        PowerMockito.when(growthMonitoringLibrary.getAppProperties()).thenReturn(appProperties);
        PowerMockito.when(appProperties.hasProperty(AppProperties.KEY.MONITOR_GROWTH)).thenReturn(true);
        PowerMockito.when(appProperties.getPropertyBoolean(AppProperties.KEY.MONITOR_GROWTH)).thenReturn(true);

        GrowthDialogFragment growthDialogFragment = GrowthDialogFragment.newInstance(dummydetails(), getWeights(), getHeights());
        List<Height> unsortedHeights = growthDialogFragment.getHeights();

        Assert.assertNotNull(unsortedHeights);

        //Verify prior order
        Assert.assertEquals(Float.valueOf("30.4"), unsortedHeights.get(0).getCm());
        Assert.assertEquals(Float.valueOf("10.0"), unsortedHeights.get(1).getCm());
        Assert.assertEquals(Float.valueOf("50.2"), unsortedHeights.get(2).getCm());
        Assert.assertEquals(Float.valueOf("40.3"), unsortedHeights.get(3).getCm());

        growthDialogFragment.sortHeights();

        List<Height> sortedHeights = growthDialogFragment.getHeights();
        Assert.assertNotNull(unsortedHeights);

        //Verify sorted order
        Assert.assertEquals(Float.valueOf("50.2"), sortedHeights.get(0).getCm());
        Assert.assertEquals(Float.valueOf("40.3"), sortedHeights.get(1).getCm());
        Assert.assertEquals(Float.valueOf("30.4"), sortedHeights.get(2).getCm());
        Assert.assertEquals(Float.valueOf("10.0"), sortedHeights.get(3).getCm());
    }
}
