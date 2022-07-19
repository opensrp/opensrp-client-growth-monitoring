package org.smartregister.growthmonitoring.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.growthmonitoring.BaseUnitTest;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.service.intent.HeightIntentService;
import org.smartregister.growthmonitoring.service.intent.WeightIntentService;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;

public class JsonFormUtilsTest extends BaseUnitTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private EventClientRepository eventClientRepository;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private Context openSRPContext;

    @Before
    public void setUp() {
        GrowthMonitoringLibrary.destroy();//Clear any static instance
        GrowthMonitoringLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), 2, "2.0.1", 3);
    }

    @After
    public void tearDown() {
        GrowthMonitoringLibrary.destroy();
    }

    @Test
    public void testCreateWeightGeneratesEventToRepositoryEventCorrectly() throws JSONException {

        Mockito.doReturn(eventClientRepository).when(openSRPContext).getEventClientRepository();
        Mockito.doReturn(allSharedPreferences).when(openSRPContext).allSharedPreferences();
        Mockito.doReturn(AllConstants.DATA_CAPTURE_STRATEGY.NORMAL).when(allSharedPreferences).fetchCurrentDataStrategy();

        //Weight
        JSONObject weightJsonObject = new JSONObject();
        weightJsonObject.put(GrowthMonitoringConstants.JsonForm.KEY, "Weight_Kgs");
        weightJsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY, "concept");
        weightJsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY_ID, "5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        weightJsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY_PARENT, "");
        weightJsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_DATA_TYPE, "decimal");
        weightJsonObject.put(GrowthMonitoringConstants.JsonForm.VALUE, getWeights().get(0).getKg());

        JSONArray array = new JSONArray();
        array.put(weightJsonObject);

        JsonFormUtils.createWeightEvent(getWeights().get(0), WeightIntentService.EVENT_TYPE, WeightIntentService.ENTITY_TYPE, array, openSRPContext);

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<JSONObject> jsonObjectArgumentCaptor = ArgumentCaptor.forClass(JSONObject.class);

        Mockito.verify(eventClientRepository).addEvent(stringArgumentCaptor.capture(), jsonObjectArgumentCaptor.capture());

        Assert.assertNotNull(stringArgumentCaptor.getValue());
        Assert.assertEquals(getWeights().get(0).getBaseEntityId(), stringArgumentCaptor.getValue());

        JSONObject jsonObject = jsonObjectArgumentCaptor.getValue();
        Assert.assertNotNull(jsonObject);

        Event event = org.smartregister.util.JsonFormUtils.gson.fromJson(jsonObject.toString(), Event.class);
        Assert.assertNotNull(event);
        Assert.assertEquals(TEST_BASE_ENTITY_ID, event.getBaseEntityId());
        Assert.assertEquals(TEST_STRING, event.getChildLocationId());
        Assert.assertEquals(TEST_STRING, event.getLocationId());
        Assert.assertEquals(WeightIntentService.EVENT_TYPE, event.getEventType());
        Assert.assertEquals("demo", event.getProviderId());
        Assert.assertEquals(WeightIntentService.ENTITY_TYPE, event.getEntityType());
        Assert.assertEquals(2, event.getClientApplicationVersion().intValue());
        Assert.assertEquals("2.0.1", event.getClientApplicationVersionName());
        Assert.assertEquals(3, event.getClientDatabaseVersion().intValue());
        Assert.assertEquals(AllConstants.DATA_STRATEGY, event.getObs().get(0).getFormSubmissionField());
        Assert.assertEquals(AllConstants.DATA_CAPTURE_STRATEGY.NORMAL, event.getObs().get(0).getValue());
        Assert.assertEquals("Weight_Kgs", event.getObs().get(1).getFormSubmissionField());
        Assert.assertEquals("3.4", event.getObs().get(1).getValue());
        Assert.assertEquals(1, event.getDetails().size());
        Assert.assertTrue(event.getDetails().containsKey(AllConstants.DATA_STRATEGY));
        Assert.assertEquals(AllConstants.DATA_CAPTURE_STRATEGY.NORMAL, event.getDetails().get(AllConstants.DATA_STRATEGY));

    }

    @Test
    public void testCreateHeightGeneratesEventToRepositoryEventCorrectly() throws JSONException {

        Mockito.doReturn(eventClientRepository).when(openSRPContext).getEventClientRepository();
        Mockito.doReturn(allSharedPreferences).when(openSRPContext).allSharedPreferences();
        Mockito.doReturn(AllConstants.DATA_CAPTURE_STRATEGY.ADVANCED).when(allSharedPreferences).fetchCurrentDataStrategy();

        //Height
        JSONObject heightJsonObject = new JSONObject();
        heightJsonObject.put(GrowthMonitoringConstants.JsonForm.KEY, "height_cm");
        heightJsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY, "concept");
        heightJsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY_ID,
                "5090AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        heightJsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_ENTITY_PARENT, "");
        heightJsonObject.put(GrowthMonitoringConstants.JsonForm.OPENMRS_DATA_TYPE, "decimal");
        heightJsonObject.put(GrowthMonitoringConstants.JsonForm.VALUE, getHeights().get(0).getCm());

        JSONArray array = new JSONArray();
        array.put(heightJsonObject);

        JsonFormUtils.createHeightEvent(getHeights().get(0), HeightIntentService.EVENT_TYPE, HeightIntentService.ENTITY_TYPE, array, openSRPContext);

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<JSONObject> jsonObjectArgumentCaptor = ArgumentCaptor.forClass(JSONObject.class);

        Mockito.verify(eventClientRepository).addEvent(stringArgumentCaptor.capture(), jsonObjectArgumentCaptor.capture());

        Assert.assertNotNull(stringArgumentCaptor.getValue());
        Assert.assertEquals(getHeights().get(0).getBaseEntityId(), stringArgumentCaptor.getValue());

        JSONObject jsonObject = jsonObjectArgumentCaptor.getValue();
        Assert.assertNotNull(jsonObject);

        Event event = org.smartregister.util.JsonFormUtils.gson.fromJson(jsonObject.toString(), Event.class);
        Assert.assertNotNull(event);
        Assert.assertEquals(TEST_BASE_ENTITY_ID, event.getBaseEntityId());
        Assert.assertEquals(TEST_STRING, event.getChildLocationId());
        Assert.assertEquals(TEST_STRING, event.getLocationId());
        Assert.assertEquals(HeightIntentService.EVENT_TYPE, event.getEventType());
        Assert.assertEquals("demo", event.getProviderId());
        Assert.assertEquals(HeightIntentService.ENTITY_TYPE, event.getEntityType());
        Assert.assertEquals(2, event.getClientApplicationVersion().intValue());
        Assert.assertEquals("2.0.1", event.getClientApplicationVersionName());
        Assert.assertEquals(3, event.getClientDatabaseVersion().intValue());
        Assert.assertEquals(AllConstants.DATA_STRATEGY, event.getObs().get(0).getFormSubmissionField());
        Assert.assertEquals(AllConstants.DATA_CAPTURE_STRATEGY.ADVANCED, event.getObs().get(0).getValue());
        Assert.assertEquals("height_cm", event.getObs().get(1).getFormSubmissionField());
        Assert.assertEquals("30.4", event.getObs().get(1).getValue());
        Assert.assertEquals(1, event.getDetails().size());
        Assert.assertTrue(event.getDetails().containsKey(AllConstants.DATA_STRATEGY));
        Assert.assertEquals(AllConstants.DATA_CAPTURE_STRATEGY.ADVANCED, event.getDetails().get(AllConstants.DATA_STRATEGY));

    }

}