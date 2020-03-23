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
import org.smartregister.growthmonitoring.repository.WeightZScoreRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeightZScoreTests extends BaseUnitTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Spy
    private List<WeightZScore> weightZScores = new ArrayList<>();
    @Mock
    private GrowthMonitoringLibrary growthMonitoringLibrary;
    @Mock
    private WeightZScoreRepository weightZScoreRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void roundOffTest() {
        double doubleValue = WeightZScore.roundOff(45.16);
        Assert.assertEquals("45.2", String.valueOf(doubleValue));
    }

    @Test
    @PrepareForTest(GrowthMonitoringLibrary.class)
    public void calculateTest() {
        Gender gender = Gender.MALE;
        double weight = 20.0;

        weightZScores = createWeightZScore(gender);
        PowerMockito.mockStatic(GrowthMonitoringLibrary.class);
        PowerMockito.when(GrowthMonitoringLibrary.getInstance()).thenReturn(growthMonitoringLibrary);
        PowerMockito.when(growthMonitoringLibrary.weightZScoreRepository()).thenReturn(weightZScoreRepository);
        PowerMockito.when(weightZScoreRepository.findByGender(gender)).thenReturn(weightZScores);

        LocalDate baseLocalDate = new LocalDate("2019-02-20");
        Date birthDate = baseLocalDate.toDate();
        Date weightRecordDate = baseLocalDate.plusMonths(5).toDate();

        Double calculation = WeightZScore.calculate(gender, birthDate, weightRecordDate, weight);
        Assert.assertNotNull(calculation);
        Assert.assertEquals("9.471972731395528", calculation.toString());
    }

    @Test
    @PrepareForTest({GrowthMonitoringLibrary.class})
    public void reverseTest() {
        Gender gender = Gender.MALE;
        double ageInMonthsDouble = 5.0;
        Double z = 2.0;

        weightZScores = createWeightZScore(gender);
        PowerMockito.mockStatic(GrowthMonitoringLibrary.class);
        PowerMockito.when(GrowthMonitoringLibrary.getInstance()).thenReturn(growthMonitoringLibrary);
        PowerMockito.when(growthMonitoringLibrary.weightZScoreRepository()).thenReturn(weightZScoreRepository);
        PowerMockito.when(weightZScoreRepository.findByGender(gender)).thenReturn(weightZScores);

        Double calculation = WeightZScore.reverse(gender, ageInMonthsDouble, z);
        Assert.assertNotNull(calculation);
        Assert.assertEquals("9.3422381173486", calculation.toString());
    }

    private List<WeightZScore> createWeightZScore(Gender gender) {
        List<WeightZScore> weightZScoreList = new ArrayList<>();
        WeightZScore weightZScore10 = new WeightZScore();
        weightZScore10.setGender(gender);
        weightZScore10.setMonth(10);
        weightZScore10.setL(0.082);
        weightZScore10.setM(9.1649);
        weightZScore10.setS(0.10891);
        weightZScore10
                .setSd3Neg(6.6);
        weightZScore10
                .setSd2Neg(7.4);
        weightZScore10
                .setSd2Neg(8.2);
        weightZScore10.setSd0(9.2);
        weightZScore10.setSd1(10.2);
        weightZScore10.setSd2(11.4);
        weightZScore10.setSd3(12.7);
        weightZScoreList.add(weightZScore10);

        WeightZScore weightZScore5 = new WeightZScore();
        weightZScore5.setGender(gender);
        weightZScore5.setMonth(5);
        weightZScore5.setL(0.1395);
        weightZScore5.setM(7.5105);
        weightZScore5.setS(0.1108);
        weightZScore5.setSd3Neg(5.3);
        weightZScore5.setSd2Neg(6.0);
        weightZScore5.setSd1Neg(6.7);
        weightZScore5.setSd0(7.5);
        weightZScore5.setSd1(8.4);
        weightZScore5.setSd2(9.3);
        weightZScore5.setSd3(10.4);
        weightZScoreList.add(weightZScore5);

        return weightZScoreList;
    }
}

