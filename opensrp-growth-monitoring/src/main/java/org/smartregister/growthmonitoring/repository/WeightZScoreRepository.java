package org.smartregister.growthmonitoring.repository;

import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.domain.WeightZScore;
import org.smartregister.growthmonitoring.util.GrowthMonitoringConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Stores child z-scores obtained from:
 * - http://www.who.int/childgrowth/standards/wfa_boys_0_5_zscores.txt
 * - http://www.who.int/childgrowth/standards/wfa_girls_0_5_zscores.txt
 * <p/>
 * Created by Jason Rogena - jrogena@ona.io on 29/05/2017.
 */

public class WeightZScoreRepository extends BaseRepository {
    public static final String TABLE_NAME = "weight_z_scores";
    private static final String TAG = WeightZScoreRepository.class.getName();
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME +
            " (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + " VARCHAR NOT NULL, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_MONTH + " INTEGER NOT NULL, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_L + " REAL NOT NULL, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_M + " REAL NOT NULL, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_S + " REAL NOT NULL, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD3NEG + " REAL NOT NULL, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD2NEG + " REAL NOT NULL, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD1NEG + " REAL NOT NULL, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD0 + " REAL NOT NULL, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD1 + " REAL NOT NULL, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD2 + " REAL NOT NULL, " +
            GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD3 + " REAL NOT NULL, " +
            "UNIQUE(" + GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + ", " + GrowthMonitoringConstants.ColumnHeaders.COLUMN_MONTH + ") ON CONFLICT REPLACE)";

    private static final String CREATE_INDEX_SEX_QUERY =
            "CREATE INDEX " + TABLE_NAME + "_" + GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + "_index ON " + TABLE_NAME + "(" + GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + " COLLATE " + "NOCASE);";
    private static final String CREATE_INDEX_MONTH_QUERY = "CREATE INDEX " + TABLE_NAME + "_" + GrowthMonitoringConstants.ColumnHeaders.COLUMN_MONTH + "_index ON " + TABLE_NAME + "(" + GrowthMonitoringConstants.ColumnHeaders.COLUMN_MONTH + " COLLATE NOCASE);";

    public WeightZScoreRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY);
        database.execSQL(CREATE_INDEX_SEX_QUERY);
        database.execSQL(CREATE_INDEX_MONTH_QUERY);
    }

    /**
     * @param query
     * @return
     */
    public boolean runRawQuery(String query) {
        try {
            getRepository().getWritableDatabase().execSQL(query);
            return true;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return false;
    }

    public List<WeightZScore> findByGender(Gender gender) {
        List<WeightZScore> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase database = getRepository().getReadableDatabase();
            cursor = database.query(TABLE_NAME,
                    null,
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + " = ? " + COLLATE_NOCASE,
                    new String[] {gender.name()}, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    result.add(new WeightZScore(gender,
                            cursor.getInt(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_MONTH)),
                            cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_L)),
                            cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_M)),
                            cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_S)),
                            cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD3NEG)),
                            cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD2NEG)),
                            cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD1NEG)),
                            cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD0)),
                            cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD1)),
                            cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD2)),
                            cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD3))));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) cursor.close();
        }

        return result;
    }
}