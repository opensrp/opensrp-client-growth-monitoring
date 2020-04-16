package org.smartregister.growthmonitoring.fragment;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import org.joda.time.LocalDate;
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
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.growthmonitoring.BaseUnitTest;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.util.AppProperties;
import org.smartregister.repository.Repository;

/**
 * Created by ndegwamartin on 2020-04-15.
 */
@PrepareForTest({GrowthMonitoringLibrary.class})
public class EditGrowthDialogFragmentTest extends BaseUnitTest {

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

    @Mock
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

        PowerMockito.mockStatic(GrowthMonitoringLibrary.class);
        PowerMockito.when(GrowthMonitoringLibrary.getInstance()).thenReturn(growthMonitoringLibrary);
        PowerMockito.when(growthMonitoringLibrary.getAppProperties()).thenReturn(appProperties);
        PowerMockito.when(appProperties.hasProperty(AppProperties.KEY.MONITOR_GROWTH)).thenReturn(true);
        PowerMockito.when(appProperties.getPropertyBoolean(AppProperties.KEY.MONITOR_GROWTH)).thenReturn(true);
    }

    @Test
    public void assertSetUpViewInvokesSetFilterTouchesWhenObscuredForDialogViewSetWithTrueParam() {

        Mockito.doReturn(layoutInflater).when(activity).getLayoutInflater();
        Mockito.doReturn(dialogView).when(layoutInflater).inflate(R.layout.edit_growth_dialog_view, viewGroup, false);

        EditGrowthDialogFragment fragment = Mockito.spy(EditGrowthDialogFragment.newInstance(new LocalDate().minusYears(2).toDate(), new WeightWrapper(), new HeightWrapper()));

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

}
