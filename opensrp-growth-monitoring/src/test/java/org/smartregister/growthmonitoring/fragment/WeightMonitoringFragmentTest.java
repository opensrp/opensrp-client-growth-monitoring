package org.smartregister.growthmonitoring.fragment;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import android.view.View;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensrp.api.constants.Gender;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.growthmonitoring.BaseUnitTest;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.domain.WeightZScore;
import org.smartregister.growthmonitoring.repository.WeightZScoreRepository;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
    public void testThatWeightForAgeViewIsCreated() throws InterruptedException {
        ArrayList<WeightZScore> weightZScores = new ArrayList<>();
        weightZScores.add(getWeightZScore(56));
        weightZScores.add(getWeightZScore(44));

        WeightZScoreRepository  weightZScoreRepository = Mockito.spy(GrowthMonitoringLibrary.getInstance().weightZScoreRepository());
        Mockito.doReturn(weightZScores).when(weightZScoreRepository).findByGender(Mockito.any());
        ReflectionHelpers.setField(GrowthMonitoringLibrary.getInstance(), "weightZScoreRepository", weightZScoreRepository);

        WeightMonitoringFragment fragment = Mockito.spy(WeightMonitoringFragment.createInstance("2018-09-12", Gender.MALE, getWeights()));
        Mockito.doReturn(true).when(fragment).isVisible();
        activity.getSupportFragmentManager().beginTransaction().add(fragment, "Weight-for-Age-Boys").commitNow();
        View view = fragment.getView();
        Assert.assertNotNull(view);
        Assert.assertTrue(view instanceof ConstraintLayout);
        ConstraintLayout constraintLayout = (ConstraintLayout) view;
        TimeUnit.SECONDS.toMillis(3);
        Assert.assertEquals(3, constraintLayout.getChildCount());
        Assert.assertEquals(constraintLayout.getContext().getString(R.string.age), ((CustomFontTextView) constraintLayout.findViewById(R.id.column_one_metric)).getText());
        Assert.assertEquals(constraintLayout.getContext().getString(R.string.weight), ((CustomFontTextView) constraintLayout.findViewById(R.id.metric_label)).getText());
        Assert.assertEquals(constraintLayout.getContext().getString(R.string.z_score), ((CustomFontTextView) constraintLayout.findViewById(R.id.column_three_metric)).getText());
    }

    private WeightZScore getWeightZScore(int age) {
        WeightZScore weightZScore = new WeightZScore();
        weightZScore.setGender(Gender.MALE);
        weightZScore.setMonth(age);
        return weightZScore;
    }

    @After
    public void destroy(){
        ReflectionHelpers.setField(GrowthMonitoringLibrary.getInstance(), "weightZScoreRepository", null);
    }

}