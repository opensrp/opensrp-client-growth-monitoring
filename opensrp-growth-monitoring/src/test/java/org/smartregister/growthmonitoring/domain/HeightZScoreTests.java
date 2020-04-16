package org.smartregister.growthmonitoring.domain;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.opensrp.api.constants.Gender;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.growthmonitoring.BaseUnitTest;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.repository.HeightZScoreRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HeightZScoreTests extends BaseUnitTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Spy
    private List<HeightZScore> heightZScores = new ArrayList<>();
    @Mock
    private GrowthMonitoringLibrary growthMonitoringLibrary;
    @Mock
    private HeightZScoreRepository heightZScoreRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void roundOffTest() {
        double doubleValue = HeightZScore.roundOff(45.16);
        Assert.assertEquals("45.2", String.valueOf(doubleValue));
    }

    @Test
    @PrepareForTest(GrowthMonitoringLibrary.class)
    public void calculateTest() {
        Gender gender = Gender.MALE;
        double height = 20.0;

        heightZScores = createHeightZScore(gender);
        PowerMockito.mockStatic(GrowthMonitoringLibrary.class);
        PowerMockito.when(GrowthMonitoringLibrary.getInstance()).thenReturn(growthMonitoringLibrary);
        PowerMockito.when(growthMonitoringLibrary.heightZScoreRepository()).thenReturn(heightZScoreRepository);
        PowerMockito.when(heightZScoreRepository.findByGender(gender)).thenReturn(heightZScores);


        LocalDate baseLocalDate = new LocalDate("2019-02-20");
        Date birthDate = baseLocalDate.toDate();
        Date heightRecordDate = baseLocalDate.plusMonths(5).toDate();

        Double calculation = HeightZScore.calculate(gender, birthDate, heightRecordDate, height);
        Assert.assertNotNull(calculation);
        Assert.assertEquals("-21.73913348224829", calculation.toString());
    }

    @Test
    @PrepareForTest({GrowthMonitoringLibrary.class})
    public void reverseTest() {
        Gender gender = Gender.MALE;
        double ageInMonthsDouble = 5.0;
        Double z = 2.0;

        heightZScores = createHeightZScore(gender);
        PowerMockito.mockStatic(GrowthMonitoringLibrary.class);
        PowerMockito.when(GrowthMonitoringLibrary.getInstance()).thenReturn(growthMonitoringLibrary);
        PowerMockito.when(growthMonitoringLibrary.heightZScoreRepository()).thenReturn(heightZScoreRepository);
        PowerMockito.when(heightZScoreRepository.findByGender(gender)).thenReturn(heightZScores);

        Double calculation = HeightZScore.reverse(gender, ageInMonthsDouble, z);
        Assert.assertNotNull(calculation);
        Assert.assertEquals("70.125638608", calculation.toString());
    }

    private List<HeightZScore> createHeightZScore(Gender gender) {
        List<HeightZScore> heightZScoreList = new ArrayList<>();
        HeightZScore heightZScore10 = new HeightZScore();
        heightZScore10.setGender(gender);
        heightZScore10.setMonth(10);
        heightZScore10.setL(1.0);
        heightZScore10.setM(71.9687);
        heightZScore10.setS(0.03117);
        heightZScore10
                .setSd3Neg(65.2);
        heightZScore10
                .setSd2Neg(67.5);
        heightZScore10
                .setSd2Neg(69.7);
        heightZScore10.setSd0(72.0);
        heightZScore10.setSd1(74.2);
        heightZScore10.setSd2(76.5);
        heightZScore10.setSd3(78.7);
        heightZScoreList.add(heightZScore10);

        HeightZScore heightZScore5 = new HeightZScore();
        heightZScore5.setGender(gender);
        heightZScore5.setMonth(5);
        heightZScore5.setL(1.0);
        heightZScore5.setM(65.9026);
        heightZScore5.setS(0.03204);
        heightZScore5.setSd3Neg(59.6);
        heightZScore5.setSd2Neg(61.7);
        heightZScore5.setSd2Neg(63.8);
        heightZScore5.setSd0(65.9);
        heightZScore5.setSd1(68.0);
        heightZScore5.setSd2(70.1);
        heightZScore5.setSd3(72.2);
        heightZScoreList.add(heightZScore5);

        return heightZScoreList;
    }
}

