package org.smartregister.growthmonitoring.repository;

import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.util.GrowthMonitoringConstants;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Stores Weight-For-Height z-score computation chart values
 * for male and female
 */
public class WeightForHeightRepository extends BaseRepository {
    public static final String TABLE_NAME = "weight_for_height_z_values";
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + " VARCHAR NOT NULL," +
            GrowthMonitoringConstants.ColumnHeaders.HEIGHT + " REAL NOT NULL, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_L + " REAL NOT NULL, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_M + " REAL NOT NULL, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_S + " REAL NOT NULL, " + "UNIQUE(" +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + ", " +
            GrowthMonitoringConstants.ColumnHeaders.HEIGHT + ") ON CONFLICT REPLACE)";
    private static final String CREATE_INDEX_SEX_QUERY = "CREATE INDEX " + TABLE_NAME + "_" + GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + "_index ON " +
            TABLE_NAME + "(" + GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + " COLLATE NOCASE);";
    private static final String CREATE_INDEX_HEIGHT_QUERY = "CREATE INDEX " + TABLE_NAME + "_" + GrowthMonitoringConstants.ColumnHeaders.HEIGHT + "_index ON " +
            TABLE_NAME + "(" + GrowthMonitoringConstants.ColumnHeaders.HEIGHT + " COLLATE NOCASE);";

    public static final double NO_HEIGHT_DEFAULT = 0.0;

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY);
        database.execSQL(CREATE_INDEX_SEX_QUERY);
        database.execSQL(CREATE_INDEX_HEIGHT_QUERY);
    }

    /**
     * Get values for z-score computation
     *
     * @param gender Gender value by which to filter
     * @param height Gender value by which to filter
     * @return A list of z-score variables for use in the computation
     */
    public List<ZScore> findZScoreVariables(String gender, double height) {
        Cursor cursor = null;
        List<ZScore> zScoreVariables = new ArrayList<>();
        try {
            SQLiteDatabase database = getReadableDatabase();
            String selection;
            String[] selectionArgs;

            if (height != NO_HEIGHT_DEFAULT) {
                selection = GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + " = ? AND " + GrowthMonitoringConstants.ColumnHeaders.HEIGHT + " = ? ";
                selectionArgs = new String[]{gender, String.valueOf(height)};
            } else {
                selection = GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + " = ? ";
                selectionArgs = new String[]{gender};
            }

            cursor = database.query(TABLE_NAME, null,
                    selection + COLLATE_NOCASE, selectionArgs, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    zScoreVariables.add(getZScoreValuesFromCursor(cursor));
                    cursor.moveToNext();
                }
            }
        } catch (Exception ex) {
            Timber.e(ex);
        } finally {
            if (cursor != null) cursor.close();
        }
        return zScoreVariables;
    }

    public boolean isTableEmpty(String tableName) {
        Cursor cursor = null;
        String query = "SELECT count(1) WHERE NOT EXISTS (SELECT * FROM " + tableName + ");";
        int result = 1;
        try {
            SQLiteDatabase database = getReadableDatabase();
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    result = cursor.getInt(cursor.getColumnIndex("count(1)"));
                    cursor.moveToNext();
                }
            }
        } catch (Exception ex) {
            Timber.e(ex);
        } finally {
            if (cursor != null) cursor.close();
        }
        return result == 1;
    }

    public boolean runRawQuery(String query) {
        try {
            getWritableDatabase().execSQL(query);
            return true;
        } catch (Exception e) {
            Timber.e(e);
        }

        return false;
    }

    private ZScore getZScoreValuesFromCursor(Cursor cursor) {
        ZScore zScoreValues = new ZScore();
        int gender = Integer.parseInt(cursor.getString(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX)));
        zScoreValues.setGender(gender == 1 ? Gender.MALE : Gender.FEMALE);
        zScoreValues.setL(cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_L)));
        zScoreValues.setM(cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_M)));
        zScoreValues.setS(cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_S)));

        return zScoreValues;
    }
}
