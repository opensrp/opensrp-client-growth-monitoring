package org.smartregister.growthmonitoring.fragment;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensrp.api.constants.Gender;
import org.robolectric.Robolectric;
import org.smartregister.growthmonitoring.BaseUnitTest;
import org.smartregister.growthmonitoring.R;
import org.smartregister.view.customcontrols.CustomFontTextView;

public class WeightForHeightMonitoringFragmentTest extends BaseUnitTest {

    private FragmentActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activity = Robolectric.buildActivity(FragmentActivity.class)
                .create()
                .start()
                .resume()
                .get();
    }

    @Test
    public void testThatWeightForHeightViewIsCreated() throws InterruptedException {
        WeightForHeightMonitoringFragment fragment = Mockito.spy(WeightForHeightMonitoringFragment.createInstance(Gender.MALE, "2018-09-12", getWeights(), getHeights()));
        Mockito.doReturn(true).when(fragment).isVisible();
        activity.getSupportFragmentManager().beginTransaction().add(fragment, "Weight-for-Height-Boys").commitNow();
        View view = fragment.getView();
        Assert.assertNotNull(view);
        Assert.assertTrue(view instanceof ConstraintLayout);
        ConstraintLayout constraintLayout = (ConstraintLayout) view;
        Thread.sleep(3000);
        Assert.assertEquals(3, constraintLayout.getChildCount());
        Assert.assertEquals(constraintLayout.getContext().getString(R.string.weight), ((CustomFontTextView) constraintLayout.findViewById(R.id.column_one_metric)).getText());
        Assert.assertEquals(constraintLayout.getContext().getString(R.string.height), ((CustomFontTextView) constraintLayout.findViewById(R.id.metric_label)).getText());
        Assert.assertEquals(constraintLayout.getContext().getString(R.string.z_score), ((CustomFontTextView) constraintLayout.findViewById(R.id.column_three_metric)).getText());
    }
}