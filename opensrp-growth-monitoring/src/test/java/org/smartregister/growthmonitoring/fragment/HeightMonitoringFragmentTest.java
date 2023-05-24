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
import org.smartregister.growthmonitoring.domain.HeightZScore;
import org.smartregister.growthmonitoring.repository.HeightZScoreRepository;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;

/**
 * Created by ndegwamartin on 2020-04-15.
 */
public class HeightMonitoringFragmentTest extends BaseUnitTest {

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
    public void testThatHeightForAgeViewIsCreated() throws InterruptedException {
        ArrayList<HeightZScore> heightZScores = new ArrayList<>();
        heightZScores.add(getHeightZScore(56));
        heightZScores.add(getHeightZScore(44));
//
        HeightZScoreRepository heightZScoreRepository = Mockito.spy(GrowthMonitoringLibrary.getInstance().heightZScoreRepository());
        Mockito.doReturn(heightZScores).when(heightZScoreRepository).findByGender(Mockito.any());
        ReflectionHelpers.setField(GrowthMonitoringLibrary.getInstance(), "heightZScoreRepository", heightZScoreRepository);

        HeightMonitoringFragment fragment = Mockito.spy(HeightMonitoringFragment.createInstance("2018-09-12", Gender.FEMALE, getHeights()));
        Mockito.doReturn(true).when(fragment).isVisible();
        activity.getSupportFragmentManager().beginTransaction().add(fragment, "Height-for-Age-Boys").commitNow();
        View view = fragment.getView();
        Assert.assertNotNull(view);
        Assert.assertTrue(view instanceof ConstraintLayout);
        ConstraintLayout constraintLayout = (ConstraintLayout) view;
        Thread.sleep(3000);
        Assert.assertEquals(3, constraintLayout.getChildCount());
        Assert.assertEquals(constraintLayout.getContext().getString(R.string.age), ((CustomFontTextView)constraintLayout.findViewById(R.id.column_one_metric)).getText());
        Assert.assertEquals(constraintLayout.getContext().getString(R.string.height), ((CustomFontTextView)constraintLayout.findViewById(R.id.metric_label)).getText());
        Assert.assertEquals(constraintLayout.getContext().getString(R.string.z_score), ((CustomFontTextView)constraintLayout.findViewById(R.id.column_three_metric)).getText());
    }

    private HeightZScore getHeightZScore(int age) {
        HeightZScore heightZScore = new HeightZScore();
        heightZScore.setGender(Gender.MALE);
        heightZScore.setMonth(age);
        return heightZScore;
    }

    @After
    public void destroy(){
        ReflectionHelpers.setField(GrowthMonitoringLibrary.getInstance(), "heightZScoreRepository", null);
    }
}