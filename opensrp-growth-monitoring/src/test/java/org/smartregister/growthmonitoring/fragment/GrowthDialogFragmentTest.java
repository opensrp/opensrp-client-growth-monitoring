package org.smartregister.growthmonitoring.fragment;

import android.app.Dialog;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

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
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.util.AppProperties;
import org.smartregister.repository.Repository;

import java.util.List;

/**
 * Created by ndegwamartin on 2019-06-03.
 */
@PrepareForTest({GrowthMonitoringLibrary.class})
public class GrowthDialogFragmentTest extends BaseUnitTest {

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
        GrowthMonitoringLibrary.init(context, repository, 0, "1.0.0", 0);

        PowerMockito.mockStatic(GrowthMonitoringLibrary.class);
        PowerMockito.when(GrowthMonitoringLibrary.getInstance()).thenReturn(growthMonitoringLibrary);
        PowerMockito.when(growthMonitoringLibrary.getAppProperties()).thenReturn(appProperties);
        PowerMockito.when(appProperties.hasProperty(AppProperties.KEY.MONITOR_GROWTH)).thenReturn(true);
        PowerMockito.when(appProperties.getPropertyBoolean(AppProperties.KEY.MONITOR_GROWTH)).thenReturn(true);
    }


    @Test
    public void testGrowthDialogFragmentInstantiatesValidInstance() {
        List<Weight> weightArrayList = getWeights();
        List<Height> heightArrayList = getHeights();

        GrowthDialogFragment dialogFragment = GrowthDialogFragment.newInstance(dummydetails(), weightArrayList, heightArrayList);
        Assert.assertNotNull(dialogFragment);

    }

    @Test
    public void testSortHeightsOrdersItemsCorrectly() {

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

    @Test
    public void testSortWeightsOrdersItemsCorrectly() {

        GrowthDialogFragment growthDialogFragment = GrowthDialogFragment.newInstance(dummydetails(), getWeights(), getHeights());
        Assert.assertNotNull(growthDialogFragment);


        List<Weight> unsortedWeights = growthDialogFragment.getWeights();

        Assert.assertNotNull(unsortedWeights);

        //Verify prior order
        Assert.assertEquals(Float.valueOf("3.4"), unsortedWeights.get(0).getKg());
        Assert.assertEquals(Float.valueOf("5.2"), unsortedWeights.get(1).getKg());

        growthDialogFragment.sortWeights();

        List<Weight> sortedWeights = growthDialogFragment.getWeights();
        Assert.assertNotNull(unsortedWeights);

        //Verify sorted order
        Assert.assertEquals(Float.valueOf("5.2"), sortedWeights.get(0).getKg());
        Assert.assertEquals(Float.valueOf("3.4"), sortedWeights.get(1).getKg());
    }


    @Test
    public void assertSetUpViewInvokesSetFilterTouchesWhenObscuredForDialogViewSetWithTrueParam() {

        Mockito.doReturn(layoutInflater).when(activity).getLayoutInflater();
        Mockito.doReturn(dialogView).when(layoutInflater).inflate(R.layout.growth_dialog_view, viewGroup, false);

        GrowthDialogFragment fragment = Mockito.spy(GrowthDialogFragment.newInstance(dummydetails(), getWeights(), getHeights()));

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
