package org.smartregister.growthmonitoring.fragment;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
    public void testThatWeightForHeightViewIsCreated() {
        WeightForHeightMonitoringFragment fragment = WeightForHeightMonitoringFragment.createInstance(Gender.MALE, "2018-09-12", getWeights(), getHeights());
        activity.getSupportFragmentManager().beginTransaction().add(fragment, "Weight-for-Height-Boys").commitNow();
        View view = fragment.getView();
        Assert.assertNotNull(view);
        Assert.assertTrue(view instanceof ConstraintLayout);
        ConstraintLayout constraintLayout = (ConstraintLayout) view;
        Assert.assertEquals(constraintLayout.getChildCount(), 2);
        Assert.assertEquals(((CustomFontTextView)constraintLayout.findViewById(R.id.column_one_metric)).getText(), constraintLayout.getContext().getString(R.string.weight));
        Assert.assertEquals(((CustomFontTextView)constraintLayout.findViewById(R.id.metric_label)).getText(), constraintLayout.getContext().getString(R.string.height));
        Assert.assertEquals(((CustomFontTextView)constraintLayout.findViewById(R.id.column_three_metric)).getText(), constraintLayout.getContext().getString(R.string.z_score));
    }
}