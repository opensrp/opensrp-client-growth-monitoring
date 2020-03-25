package org.smartregister.growthmonitoring.repository;

import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.domain.HeightZScore;
import org.smartregister.growthmonitoring.util.GrowthMonitoringConstants;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Stores child z-scores obtained from: - http://www.who.int/childgrowth/standards/wfa_boys_0_5_zscores.txt -
 * http://www.who.int/childgrowth/standards/wfa_girls_0_5_zscores.txt
 * <p/>
 * Created by Jason Rogena - jrogena@ona.io on 29/05/2017.
 */

public class HeightZScoreRepository extends BaseRepository {
    public static final String TABLE_NAME = "height_z_scores";
    private static final String TAG = HeightZScoreRepository.class.getName();
    private static final String CREATE_TABLE_QUERY =
            "CREATE TABLE " + TABLE_NAME + " (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + " VARCHAR NOT NULL, " +
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_MONTH + " INTEGER NOT NULL, " +
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_L + " REAL NOT NULL, " +
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_M + " REAL NOT NULL, " +
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_S + " REAL NOT NULL, " +
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD + " REAL NOT NULL, " +
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD3NEG + " REAL NOT NULL, " +
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD2NEG + " REAL NOT NULL, " +
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD1NEG + " REAL NOT NULL, " +
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD0 + " REAL NOT NULL, " +
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD1 + " REAL NOT NULL, " +
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD2 + " REAL NOT NULL, " +
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD3 + " REAL NOT NULL, " + "UNIQUE(" +
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + ", " +
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_MONTH + ") ON CONFLICT REPLACE)";

    private static final String CREATE_INDEX_SEX_QUERY =
            "CREATE INDEX " + TABLE_NAME + "_" + GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + "_index ON " +
                    TABLE_NAME + "(" + GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + " COLLATE NOCASE);";
    private static final String CREATE_INDEX_MONTH_QUERY =
            "CREATE INDEX " + TABLE_NAME + "_" + GrowthMonitoringConstants.ColumnHeaders.COLUMN_MONTH + "_index ON " +
                    TABLE_NAME + "(" + GrowthMonitoringConstants.ColumnHeaders.COLUMN_MONTH + " COLLATE NOCASE);";


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
            getWritableDatabase().execSQL(query);
            return true;
        } catch (Exception e) {
            Timber.e(e, TAG);
        }

        return false;
    }

    public List<HeightZScore> findByGender(Gender gender) {
        List<HeightZScore> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase database = getReadableDatabase();
            cursor = database.query(TABLE_NAME, null,
                    GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + " = ? " + COLLATE_NOCASE,
                    new String[]{gender.name()}, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    result.add(createHeightZScore(gender, cursor));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) cursor.close();
        }

        return result;
    }

    private HeightZScore createHeightZScore(Gender gender, Cursor cursor) {
        HeightZScore heightZScore = new HeightZScore();
        heightZScore.setGender(gender);
        heightZScore.setMonth(cursor.getInt(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_MONTH)));
        heightZScore.setL(cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_L)));
        heightZScore.setM(cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_M)));
        heightZScore.setS(cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_S)));
        heightZScore
                .setSd3Neg(cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD3NEG)));
        heightZScore
                .setSd2Neg(cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD2NEG)));
        heightZScore
                .setSd2Neg(cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD1NEG)));
        heightZScore.setSd0(cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD0)));
        heightZScore.setSd1(cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD1)));
        heightZScore.setSd2(cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD2)));
        heightZScore.setSd3(cursor.getDouble(cursor.getColumnIndex(GrowthMonitoringConstants.ColumnHeaders.COLUMN_SD3)));
        return heightZScore;
    }
}
