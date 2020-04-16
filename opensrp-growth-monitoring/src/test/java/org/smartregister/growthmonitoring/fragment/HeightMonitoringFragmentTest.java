package org.smartregister.growthmonitoring.fragment;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensrp.api.constants.Gender;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.Context;
import org.smartregister.growthmonitoring.BaseUnitTest;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.util.AppProperties;
import org.smartregister.repository.Repository;
import org.smartregister.view.customcontrols.CustomFontTextView;

/**
 * Created by ndegwamartin on 2020-04-15.
 */
@PrepareForTest({GrowthMonitoringLibrary.class})
public class HeightMonitoringFragmentTest extends BaseUnitTest {
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

    @Mock
    private CustomFontTextView customFontTextView;

    @Mock
    private ImageButton scrollButton;

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
    public void assertOnCreateViewInvokesSetFilterTouchesWhenObscuredForDialogViewSetWithTrueParam() {

        Mockito.doReturn(layoutInflater).when(activity).getLayoutInflater();
        Mockito.doReturn(dialogView).when(layoutInflater).inflate(R.layout.growth_monitoring_fragment, viewGroup, false);
        Mockito.doReturn(customFontTextView).when(dialogView).findViewById(R.id.metric_label);
        Mockito.doReturn(scrollButton).when(dialogView).findViewById(R.id.scroll_button);

        HeightMonitoringFragment fragment = Mockito.spy(HeightMonitoringFragment.createInstance("2018-09-12", Gender.FEMALE, getHeights()));

        Mockito.doReturn(window).when(dialog).getWindow();
        Mockito.doReturn(activity).when(fragment).getActivity();
        Mockito.doNothing().when(window).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        View view = fragment.onCreateView(layoutInflater, viewGroup, null);

        Assert.assertNotNull(view);

        Mockito.verify(view).setFilterTouchesWhenObscured(true);

    }
}