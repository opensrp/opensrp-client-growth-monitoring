package org.smartregister.growthmonitoring.repository;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.growthmonitoring.BaseUnitTest;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.repository.EventClientRepository;

import java.util.Calendar;


/**
 * Created by ndegwamartin on 2019-11-28.
 */
public class WeightRepositoryTest extends BaseUnitTest {

    @Mock
    private SQLiteDatabase sqliteDatabase;

    @Mock
    private Cursor cursor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecSQLMethodIsInvokedCorrectNumberOfTimesOnCreateTable() {

        WeightRepository.createTable(sqliteDatabase);
        Mockito.verify(sqliteDatabase, Mockito.times(4)).execSQL(ArgumentMatchers.anyString());

    }

    @Test
    public void testExecSQLMethodIsInvokedCorrectlyOnMigrateCreatedAt() {

        String expectedSQL = "UPDATE " + WeightRepository.WEIGHT_TABLE_NAME + " SET " + WeightRepository.CREATED_AT + " = " + " ( SELECT " +
                EventClientRepository.event_column.dateCreated.name() + " FROM " +
                EventClientRepository.Table.event.name() + " WHERE " +
                EventClientRepository.event_column.eventId.name() + " = " + WeightRepository.WEIGHT_TABLE_NAME + "." + WeightRepository.EVENT_ID +
                " OR " + EventClientRepository.event_column.formSubmissionId.name() + " = " + WeightRepository.WEIGHT_TABLE_NAME + "." +
                WeightRepository.FORMSUBMISSION_ID + " ) " + " WHERE " + WeightRepository.CREATED_AT + " is null ";

        WeightRepository.migrateCreatedAt(sqliteDatabase);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).execSQL(expectedSQL);
    }

    @Test
    public void testFindUniqueByDateReturnsNullIfParametersAreNull() {

        WeightRepository weightRepository = new WeightRepository();

        Weight weight = weightRepository.findUniqueByDate(sqliteDatabase, null, null);
        Assert.assertNull(weight);


        weight = weightRepository.findUniqueByDate(sqliteDatabase, TEST_BASE_ENTITY_ID, null);
        Assert.assertNull(weight);

        weight = weightRepository.findUniqueByDate(sqliteDatabase, null, Calendar.getInstance().getTime());
        Assert.assertNull(weight);


    }

    @Test
    public void testFindUniqueByDateInvokesQueryMethodCorrectly() {

        WeightRepository weightRepository = new WeightRepository();

        Mockito.doReturn(cursor).when(sqliteDatabase).query(ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class), ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class), (String) ArgumentMatchers.isNull(), (String) ArgumentMatchers.isNull(), ArgumentMatchers.anyString(), (String) ArgumentMatchers.isNull());

        weightRepository.findUniqueByDate(sqliteDatabase, TEST_BASE_ENTITY_ID, Calendar.getInstance().getTime());

        Mockito.verify(sqliteDatabase, Mockito.times(1)).query(ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class), ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class), (String) ArgumentMatchers.isNull(), (String) ArgumentMatchers.isNull(), ArgumentMatchers.anyString(), (String) ArgumentMatchers.isNull());


    }

}
