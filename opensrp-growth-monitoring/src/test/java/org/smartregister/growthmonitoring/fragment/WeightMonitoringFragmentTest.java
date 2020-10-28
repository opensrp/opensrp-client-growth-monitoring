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

/**
 * Created by ndegwamartin on 2020-04-15.
 */
public class WeightMonitoringFragmentTest extends BaseUnitTest {

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
    public void testThatWeightForAgeViewIsCreated() {
        WeightMonitoringFragment fragment = Mockito.spy(WeightMonitoringFragment.createInstance("2018-09-12", Gender.MALE, getWeights()));
        activity.getSupportFragmentManager().beginTransaction().add(fragment, "Weight-for-Age-Boys").commitNow();
        View view = fragment.getView();
        Assert.assertNotNull(view);
        Assert.assertTrue(view instanceof ConstraintLayout);
        ConstraintLayout constraintLayout = (ConstraintLayout) view;
        Assert.assertEquals(constraintLayout.getChildCount(), 2);
        Assert.assertEquals(((CustomFontTextView)constraintLayout.findViewById(R.id.column_one_metric)).getText(), constraintLayout.getContext().getString(R.string.age));
        Assert.assertEquals(((CustomFontTextView)constraintLayout.findViewById(R.id.metric_label)).getText(), constraintLayout.getContext().getString(R.string.weight));
        Assert.assertEquals(((CustomFontTextView)constraintLayout.findViewById(R.id.column_three_metric)).getText(), constraintLayout.getContext().getString(R.string.z_score));
    }

}