package org.smartregister.growthmonitoring.repository;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.BaseUnitTest;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.util.GrowthMonitoringConstants;

import java.util.List;

import static org.smartregister.repository.BaseRepository.COLLATE_NOCASE;

public class WeightForHeightRepositoryTest extends BaseUnitTest {

    @Mock
    private SQLiteDatabase sqliteDatabase;

    private WeightForHeightRepository weightForHeightRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        weightForHeightRepository = Mockito.spy(new WeightForHeightRepository());
    }

    @Test
    public void execSQLIsInvokedCorrectlyWhenCreatingWFHTable() {
        WeightForHeightRepository.createTable(sqliteDatabase);
        Mockito.verify(sqliteDatabase, Mockito.times(3)).execSQL(ArgumentMatchers.anyString());
    }

    @Test
    public void getWFHZScoreValuesInvokesDatabaseQueryCorrectly() {
        Mockito.doReturn(sqliteDatabase).when(weightForHeightRepository).getReadableDatabase();
        weightForHeightRepository.findZScoreVariables("1", 60);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).query(
                ArgumentMatchers.eq(WeightForHeightRepository.TABLE_NAME),
                ArgumentMatchers.isNull(String[].class),
                ArgumentMatchers.eq(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + " = ? AND " + GrowthMonitoringConstants.ColumnHeaders.HEIGHT + " = ? "),
                ArgumentMatchers.eq(new String[]{"1", "60.0"}),
                ArgumentMatchers.isNull(String.class), ArgumentMatchers.isNull(String.class), ArgumentMatchers.isNull(String.class), ArgumentMatchers.isNull(String.class));
    }

    @Test
    public void getWFHZScoreValuesReturnsCorrectZValues() {
        Mockito.doReturn(sqliteDatabase).when(weightForHeightRepository).getReadableDatabase();
        String[] columns = new String[]{
                GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX,
                GrowthMonitoringConstants.ColumnHeaders.HEIGHT,
                GrowthMonitoringConstants.ColumnHeaders.COLUMN_L,
                GrowthMonitoringConstants.ColumnHeaders.COLUMN_M,
                GrowthMonitoringConstants.ColumnHeaders.COLUMN_S
        };
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        String gender = "1", height = "60.0", l = "-0.3521", m = "2.441", s = "0.09182";
        matrixCursor.addRow(new Object[]{gender, height, l, m, s});

        Mockito.doReturn(matrixCursor).when(sqliteDatabase).query(
                ArgumentMatchers.eq(WeightForHeightRepository.TABLE_NAME),
                ArgumentMatchers.isNull(String[].class),
                ArgumentMatchers.eq(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + " = ? AND " + GrowthMonitoringConstants.ColumnHeaders.HEIGHT + " = ? "),
                ArgumentMatchers.eq(new String[]{"1", "60.0"}),
                ArgumentMatchers.isNull(String.class), ArgumentMatchers.isNull(String.class), ArgumentMatchers.isNull(String.class), ArgumentMatchers.isNull(String.class)
        );

        List<ZScore> zScoreValues = weightForHeightRepository.findZScoreVariables("1", 60);
        Assert.assertEquals(Gender.MALE, zScoreValues.get(0).getGender());
        Assert.assertEquals(Double.parseDouble(l), zScoreValues.get(0).getL(), 0.0);
    }
}
