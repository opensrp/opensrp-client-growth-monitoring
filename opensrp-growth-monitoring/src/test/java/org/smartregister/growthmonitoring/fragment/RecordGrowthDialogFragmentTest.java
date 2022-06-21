package org.smartregister.growthmonitoring.fragment;

import android.app.Dialog;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.fragment.app.FragmentActivity;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.growthmonitoring.BaseUnitTest;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.listener.GrowthMonitoringActionListener;
import org.smartregister.growthmonitoring.util.AppProperties;
import org.smartregister.repository.Repository;

/**
 * Created by ndegwamartin on 2020-04-15.
 */

@PrepareForTest({GrowthMonitoringLibrary.class})
public class RecordGrowthDialogFragmentTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private GrowthMonitoringLibrary growthMonitoringLibrary;

    @Mock
    private AppProperties appProperties;

    @Mock
    private Context context;

    @Mock
    private Repository repository;

    @Mock
    private LayoutInflater layoutInflater;

    @Mock
    private ViewGroup viewGroup;

    private FragmentActivity activity;

    @Mock
    private ViewGroup dialogView;

    @Mock
    private Dialog dialog;

    @Mock
    private Window window;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        GrowthMonitoringLibrary.init(context, repository, 0, 0);

        activity = Mockito.spy(Robolectric.buildActivity(FragmentActivity.class).create().get());

        PowerMockito.mockStatic(GrowthMonitoringLibrary.class);
        PowerMockito.when(GrowthMonitoringLibrary.getInstance()).thenReturn(growthMonitoringLibrary);
        PowerMockito.when(growthMonitoringLibrary.getAppProperties()).thenReturn(appProperties);
        PowerMockito.when(appProperties.hasProperty(AppProperties.KEY.MONITOR_GROWTH)).thenReturn(true);
        PowerMockito.when(appProperties.getPropertyBoolean(AppProperties.KEY.MONITOR_GROWTH)).thenReturn(true);
    }

    @After
    public void tearDown() {
        activity.finish();
    }

    @Test
    public void assertSetUpViewInvokesSetFilterTouchesWhenObscuredForDialogViewSetWithTrueParam() {

        Mockito.doReturn(layoutInflater).when(activity).getLayoutInflater();
        Mockito.doReturn(dialogView).when(layoutInflater).inflate(R.layout.record_growth_dialog_view, viewGroup, false);

        RecordGrowthDialogFragment fragment = Mockito.spy(RecordGrowthDialogFragment.newInstance(new LocalDate().minusYears(2).toDate(), new WeightWrapper(), new HeightWrapper()));

        Mockito.doReturn(dialog).when(fragment).getDialog();
        Mockito.doReturn(window).when(dialog).getWindow();
        Mockito.doReturn(activity).when(fragment).getActivity();
        Mockito.doNothing().when(window).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        View view = ReflectionHelpers.callInstanceMethod(fragment, "setUpViews",
                ReflectionHelpers.ClassParameter.from(LayoutInflater.class, layoutInflater),
                ReflectionHelpers.ClassParameter.from(ViewGroup.class, viewGroup));


        Assert.assertNotNull(view);

        Mockito.verify(view).setFilterTouchesWhenObscured(true);

    }

    @Test
    public void testSaveGrowthRecord() {
        RecordGrowthDialogFragment fragment = Mockito.spy(RecordGrowthDialogFragment.newInstance(new LocalDate().minusYears(2).toDate(), new WeightWrapper(), new HeightWrapper()));
        ReflectionHelpers.setStaticField(RecordGrowthDialogFragment.class, "monitorGrowth", true);
        Mockito.doReturn(activity).when(fragment).getActivity();


        EditText weight = Mockito.mock(EditText.class);
        Editable editable = Mockito.mock(Editable.class);
        Mockito.doReturn(editable).when(weight).getText();
        ReflectionHelpers.setField(fragment, "editWeight", weight);
        ReflectionHelpers.setField(fragment, "editHeight", weight);

        DatePicker datePicker = Mockito.mock(DatePicker.class);
        Mockito.doReturn(1).when(datePicker).getDayOfMonth();
        Mockito.doReturn(1).when(datePicker).getMonth();
        Mockito.doReturn(1).when(datePicker).getYear();
        ReflectionHelpers.setField(fragment, "earlierDatePicker", datePicker);

        WeightWrapper weightWrapper = Mockito.mock(WeightWrapper.class);
        ReflectionHelpers.setField(fragment, "weightWrapper", weightWrapper);
        Mockito.doNothing().when(weightWrapper).setUpdatedWeightDate(Mockito.<DateTime>any(), Mockito.anyBoolean());

        HeightWrapper heightWrapper = Mockito.mock(HeightWrapper.class);
        ReflectionHelpers.setField(fragment, "heightWrapper", heightWrapper);
        Mockito.doNothing().when(heightWrapper).setUpdatedHeightDate(Mockito.<DateTime>any(), Mockito.anyBoolean());

        Mockito.doNothing().when(fragment).dismiss();

        GrowthMonitoringActionListener growthMonitoringActionListener = Mockito.mock(GrowthMonitoringActionListener.class);
        ReflectionHelpers.setField(fragment, "GrowthMonitoringActionListener", growthMonitoringActionListener);
        Mockito.doNothing().when(growthMonitoringActionListener).onGrowthRecorded(Mockito.<WeightWrapper>any(), Mockito.<HeightWrapper>any());

        Mockito.doReturn("0").when(editable).toString();
        ReflectionHelpers.callInstanceMethod(RecordGrowthDialogFragment.class, fragment, "saveGrowthRecord", ReflectionHelpers.ClassParameter.from(boolean.class, true));

        Mockito.verify(fragment, Mockito.never()).dismiss();

        Mockito.doReturn("2").when(editable).toString();
        ReflectionHelpers.callInstanceMethod(RecordGrowthDialogFragment.class, fragment, "saveGrowthRecord", ReflectionHelpers.ClassParameter.from(boolean.class, true));

        Mockito.verify(fragment).dismiss();
    }
}