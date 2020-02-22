package org.smartregister.growthmonitoring.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightZScore;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class WeightRepository extends GrowthRepository {
    public static final String WEIGHT_TABLE_NAME = "weights";
    public static final String ID_COLUMN = "_id";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String EVENT_ID = "event_id";
    public static final String PROGRAM_CLIENT_ID = "program_client_id";
    // ID to be used to identify entity when base_entity_id is unavailable
    public static final String FORMSUBMISSION_ID = "formSubmissionId";
    public static final String OUT_OF_AREA = "out_of_area";
    public static final String KG = "kg";
    public static final String DATE = "date";
    public static final String ANMID = "anmid";
    public static final String LOCATIONID = "location_id";
    public static final String CHILD_LOCATION_ID = "child_location_id";
    public static final String SYNC_STATUS = "sync_status";
    public static final String UPDATED_AT_COLUMN = "updated_at";
    public static final String Z_SCORE = "z_score";
    public static final double DEFAULT_Z_SCORE = 999999d;
    public static final String CREATED_AT = "created_at";
    public static final String TEAM_ID = "team_id";
    public static final String TEAM = "team";
    public static final String[] WEIGHT_TABLE_COLUMNS =
            {ID_COLUMN, BASE_ENTITY_ID, PROGRAM_CLIENT_ID, KG, DATE, ANMID, LOCATIONID, CHILD_LOCATION_ID, TEAM, TEAM_ID,
                    SYNC_STATUS, UPDATED_AT_COLUMN, EVENT_ID, FORMSUBMISSION_ID, Z_SCORE, OUT_OF_AREA, CREATED_AT};
    public static final String UPDATE_TABLE_ADD_EVENT_ID_COL =
            "ALTER TABLE " + WEIGHT_TABLE_NAME + " ADD COLUMN " + EVENT_ID + " VARCHAR;";
    public static final String EVENT_ID_INDEX =
            "CREATE INDEX " + WEIGHT_TABLE_NAME + "_" + EVENT_ID + "_index ON " + WEIGHT_TABLE_NAME + "(" + EVENT_ID +
                    " COLLATE NOCASE);";
    public static final String UPDATE_TABLE_ADD_FORMSUBMISSION_ID_COL =
            "ALTER TABLE " + WEIGHT_TABLE_NAME + " ADD COLUMN " + FORMSUBMISSION_ID + " VARCHAR;";
    public static final String FORMSUBMISSION_INDEX =
            "CREATE INDEX " + WEIGHT_TABLE_NAME + "_" + FORMSUBMISSION_ID + "_index ON " + WEIGHT_TABLE_NAME + "(" +
                    FORMSUBMISSION_ID + " COLLATE NOCASE);";
    public static final String UPDATE_TABLE_ADD_OUT_OF_AREA_COL =
            "ALTER TABLE " + WEIGHT_TABLE_NAME + " ADD COLUMN " + OUT_OF_AREA + " VARCHAR;";
    public static final String UPDATE_TABLE_ADD_OUT_OF_AREA_COL_INDEX =
            "CREATE INDEX " + WEIGHT_TABLE_NAME + "_" + OUT_OF_AREA + "_index ON " + WEIGHT_TABLE_NAME + "(" + OUT_OF_AREA +
                    " COLLATE NOCASE);";
    public static final String ALTER_ADD_Z_SCORE_COLUMN =
            "ALTER TABLE " + WEIGHT_TABLE_NAME + " ADD COLUMN " + Z_SCORE + " REAL NOT NULL DEFAULT " + DEFAULT_Z_SCORE;
    public static final String ALTER_ADD_CREATED_AT_COLUMN =
            "ALTER TABLE " + WEIGHT_TABLE_NAME + " ADD COLUMN " + CREATED_AT + " DATETIME NULL ";
    public static final String UPDATE_TABLE_ADD_TEAM_COL =
            "ALTER TABLE " + WEIGHT_TABLE_NAME + " ADD COLUMN " + TEAM + " VARCHAR;";
    public static final String UPDATE_TABLE_ADD_TEAM_ID_COL =
            "ALTER TABLE " + WEIGHT_TABLE_NAME + " ADD COLUMN " + TEAM_ID + " VARCHAR;";
    public static final String UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL =
            "ALTER TABLE " + WEIGHT_TABLE_NAME + " ADD COLUMN " + CHILD_LOCATION_ID + " VARCHAR;";
    private static final String WEIGHT_SQL = "CREATE TABLE weights (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "base_entity_id VARCHAR NOT NULL,program_client_id VARCHAR NULL,kg REAL NOT NULL,date DATETIME NOT NULL,anmid " +
            "VARCHAR NULL,location_id VARCHAR NULL,sync_status VARCHAR,updated_at INTEGER NULL)";
    private static final String BASE_ENTITY_ID_INDEX =
            "CREATE INDEX " + WEIGHT_TABLE_NAME + "_" + BASE_ENTITY_ID + "_index ON " + WEIGHT_TABLE_NAME + "(" +
                    BASE_ENTITY_ID + " COLLATE NOCASE);";
    private static final String SYNC_STATUS_INDEX =
            "CREATE INDEX " + WEIGHT_TABLE_NAME + "_" + SYNC_STATUS + "_index ON " + WEIGHT_TABLE_NAME + "(" + SYNC_STATUS +
                    " COLLATE NOCASE);";
    private static final String UPDATED_AT_INDEX =
            "CREATE INDEX " + WEIGHT_TABLE_NAME + "_" + UPDATED_AT_COLUMN + "_index ON " + WEIGHT_TABLE_NAME + "(" +
                    UPDATED_AT_COLUMN + ");";

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(WEIGHT_SQL);
        database.execSQL(BASE_ENTITY_ID_INDEX);
        database.execSQL(SYNC_STATUS_INDEX);
        database.execSQL(UPDATED_AT_INDEX);
    }

    public static void migrateCreatedAt(SQLiteDatabase database) {
        try {
            String sql = "UPDATE " + WEIGHT_TABLE_NAME + " SET " + CREATED_AT + " = " + " ( SELECT " +
                    EventClientRepository.event_column.dateCreated.name() + " FROM " +
                    EventClientRepository.Table.event.name() + " WHERE " +
                    EventClientRepository.event_column.eventId.name() + " = " + WEIGHT_TABLE_NAME + "." + EVENT_ID +
                    " OR " + EventClientRepository.event_column.formSubmissionId.name() + " = " + WEIGHT_TABLE_NAME + "." +
                    FORMSUBMISSION_ID + " ) " + " WHERE " + CREATED_AT + " is null ";
            database.execSQL(sql);
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
    }

    /**
     * This method sets the weight's z-score, before adding it to the database
     *
     * @param dateOfBirth
     * @param gender
     * @param weight
     */
    public void add(Date dateOfBirth, Gender gender, Weight weight) {
        weight.setZScore(WeightZScore.calculate(gender, dateOfBirth, weight.getDate(), weight.getKg()));
        add(weight);
    }

    public void add(Weight weight) {
        try {
            if (weight == null) {
                return;
            }

            AllSharedPreferences allSharedPreferences = GrowthMonitoringLibrary.getInstance().context().allSharedPreferences();
            String providerId = allSharedPreferences.fetchRegisteredANM();
            weight.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
            weight.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));
            weight.setLocationId(allSharedPreferences.fetchDefaultLocalityId(providerId));
            weight.setChildLocationId(getChildLocationId(weight.getLocationId(), allSharedPreferences));

            if (StringUtils.isBlank(weight.getSyncStatus())) {
                weight.setSyncStatus(TYPE_Unsynced);
            }
            if (StringUtils.isBlank(weight.getFormSubmissionId())) {
                weight.setFormSubmissionId(generateRandomUUIDString());
            }


            if (weight.getUpdatedAt() == null) {
                weight.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
            }

            SQLiteDatabase database = getWritableDatabase();
            if (weight.getId() == null) {
                Weight sameWeight = findUnique(database, weight);
                if (sameWeight != null) {
                    weight.setUpdatedAt(sameWeight.getUpdatedAt());
                    weight.setId(sameWeight.getId());
                    update(database, weight);
                } else {
                    if (weight.getCreatedAt() == null) {
                        weight.setCreatedAt(new Date());
                    }
                    weight.setId(database.insert(WEIGHT_TABLE_NAME, null, createValuesFor(weight)));
                }
            } else {
                weight.setSyncStatus(TYPE_Unsynced);
                update(database, weight);
            }
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
    }

    public Weight findUnique(SQLiteDatabase db, Weight weight) {

        if (weight == null ||
                (StringUtils.isBlank(weight.getFormSubmissionId()) && StringUtils.isBlank(weight.getEventId()))) {
            return null;
        }

        try {
            SQLiteDatabase database = db;
            if (database == null) {
                database = getReadableDatabase();
            }

            String selection = null;
            String[] selectionArgs = null;
            if (StringUtils.isNotBlank(weight.getFormSubmissionId()) && StringUtils.isNotBlank(weight.getEventId())) {
                selection = FORMSUBMISSION_ID + " = ? " + COLLATE_NOCASE + " OR " + EVENT_ID + " = ? " + COLLATE_NOCASE;
                selectionArgs = new String[]{weight.getFormSubmissionId(), weight.getEventId()};
            } else if (StringUtils.isNotBlank(weight.getEventId())) {
                selection = EVENT_ID + " = ? " + COLLATE_NOCASE;
                selectionArgs = new String[]{weight.getEventId()};
            } else if (StringUtils.isNotBlank(weight.getFormSubmissionId())) {
                selection = FORMSUBMISSION_ID + " = ? " + COLLATE_NOCASE;
                selectionArgs = new String[]{weight.getFormSubmissionId()};
            }
            Cursor cursor = database.query(WEIGHT_TABLE_NAME, WEIGHT_TABLE_COLUMNS, selection, selectionArgs, null, null,
                    ID_COLUMN + " DESC ", null);
            List<Weight> weightList = readAllWeights(cursor);
            if (!weightList.isEmpty()) {
                return weightList.get(0);
            }
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }

        return null;
    }

    public Weight findUniqueByDate(SQLiteDatabase db, String baseEntityId, Date encounterDate) {

        if (StringUtils.isBlank(baseEntityId) || encounterDate == null) {
            return null;
        }

        try {

            SQLiteDatabase database = db;
            if (database == null) {
                database = getReadableDatabase();
            }

            String selection = BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE + " AND strftime('%d-%m-%Y', datetime(" + DATE + "/1000, 'unixepoch')) = strftime('%d-%m-%Y', datetime(?/1000, 'unixepoch')) " + COLLATE_NOCASE;
            String[] selectionArgs = new String[]{baseEntityId, String.valueOf(encounterDate.getTime())};

            Cursor cursor = database.query(WEIGHT_TABLE_NAME, WEIGHT_TABLE_COLUMNS, selection, selectionArgs, null, null, ID_COLUMN + " DESC ", null);
            List<Weight> weightList = readAllWeights(cursor);
            if (!weightList.isEmpty()) {
                return weightList.get(0);
            }

        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }

        return null;
    }

    public void update(SQLiteDatabase database, Weight weight) {
        if (weight == null || weight.getId() == null) {
            return;
        }

        try {
            SQLiteDatabase db;
            if (database == null) {
                db = getWritableDatabase();
            } else {
                db = database;
            }

            String idSelection = ID_COLUMN + " = ?";
            db.update(WEIGHT_TABLE_NAME, createValuesFor(weight), idSelection, new String[]{weight.getId().toString()});
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
    }

    private ContentValues createValuesFor(Weight weight) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, weight.getId());
        values.put(BASE_ENTITY_ID, weight.getBaseEntityId());
        values.put(PROGRAM_CLIENT_ID, weight.getProgramClientId());
        values.put(KG, weight.getKg());
        values.put(DATE, weight.getDate() != null ? weight.getDate().getTime() : null);
        values.put(ANMID, weight.getAnmId());
        values.put(LOCATIONID, weight.getLocationId());
        values.put(CHILD_LOCATION_ID, weight.getChildLocationId());
        values.put(TEAM, weight.getTeam());
        values.put(TEAM_ID, weight.getTeamId());
        values.put(SYNC_STATUS, weight.getSyncStatus());
        values.put(UPDATED_AT_COLUMN, weight.getUpdatedAt());
        values.put(EVENT_ID, weight.getEventId());
        values.put(FORMSUBMISSION_ID, weight.getFormSubmissionId());
        values.put(OUT_OF_AREA, weight.getOutOfCatchment());
        values.put(Z_SCORE, weight.getZScore() == null ? DEFAULT_Z_SCORE : weight.getZScore());
        values.put(CREATED_AT,
                weight.getCreatedAt() != null ? EventClientRepository.dateFormat.format(weight.getCreatedAt()) : null);
        return values;
    }

    private List<Weight> readAllWeights(Cursor cursor) {
        List<Weight> weights = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Double zScore = cursor.getDouble(cursor.getColumnIndex(Z_SCORE));
                    if (zScore.equals(new Double(DEFAULT_Z_SCORE))) {
                        zScore = null;
                    }

                    Date createdAt = null;
                    String dateCreatedString = cursor.getString(cursor.getColumnIndex(CREATED_AT));
                    if (StringUtils.isNotBlank(dateCreatedString)) {
                        try {
                            createdAt = EventClientRepository.dateFormat.parse(dateCreatedString);
                        } catch (ParseException e) {
                            Timber.e(Log.getStackTraceString(e));
                        }
                    }

                    getWeight(cursor, weights, zScore, createdAt);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return weights;

    }

    private void getWeight(Cursor cursor, List<Weight> weights, Double zScore, Date createdAt) {
        Weight weight = new Weight();
        weight.setId(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)));
        weight.setBaseEntityId(cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID)));
        weight.setProgramClientId(cursor.getString(cursor.getColumnIndex(PROGRAM_CLIENT_ID)));
        weight.setKg(cursor.getFloat(cursor.getColumnIndex(KG)));
        weight.setDate(new Date(cursor.getLong(cursor.getColumnIndex(DATE))));
        weight.setAnmId(cursor.getString(cursor.getColumnIndex(ANMID)));
        weight.setLocationId(cursor.getString(cursor.getColumnIndex(LOCATIONID)));
        weight.setSyncStatus(cursor.getString(cursor.getColumnIndex(SYNC_STATUS)));
        weight.setUpdatedAt(cursor.getLong(cursor.getColumnIndex(UPDATED_AT_COLUMN)));
        weight.setEventId(cursor.getString(cursor.getColumnIndex(EVENT_ID)));
        weight.setFormSubmissionId(cursor.getString(cursor.getColumnIndex(FORMSUBMISSION_ID)));
        weight.setZScore(zScore);
        weight.setOutOfCatchment(cursor.getInt(cursor.getColumnIndex(OUT_OF_AREA)));
        weight.setCreatedAt(createdAt);
        weight.setTeam(cursor.getString(cursor.getColumnIndex(TEAM)));
        weight.setTeamId(cursor.getString(cursor.getColumnIndex(TEAM_ID)));
        weight.setChildLocationId(cursor.getString(cursor.getColumnIndex(CHILD_LOCATION_ID)));
        weights.add(weight);
    }

    public List<Weight> findUnSyncedBeforeTime(int minutes) {
        List<Weight> weights = new ArrayList<>();
        Cursor cursor = null;
        try {

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -minutes);

            long time = calendar.getTimeInMillis();

            cursor = getReadableDatabase().query(WEIGHT_TABLE_NAME, WEIGHT_TABLE_COLUMNS,
                    UPDATED_AT_COLUMN + " < ? " + COLLATE_NOCASE + " AND " + SYNC_STATUS + " = ? " + COLLATE_NOCASE,
                    new String[]{Long.toString(time), TYPE_Unsynced}, null, null, null, null);
            weights = readAllWeights(cursor);
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return weights;
    }

    public Weight findUnSyncedByEntityId(String entityId) {
        Weight weight = null;
        Cursor cursor = null;
        try {

            cursor = getReadableDatabase().query(WEIGHT_TABLE_NAME, WEIGHT_TABLE_COLUMNS,
                    BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE + " AND " + SYNC_STATUS + " = ? ",
                    new String[]{entityId, TYPE_Unsynced}, null, null, UPDATED_AT_COLUMN + COLLATE_NOCASE + " DESC", null);
            List<Weight> weights = readAllWeights(cursor);
            if (!weights.isEmpty()) {
                weight = weights.get(0);
            }
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return weight;
    }

    public List<Weight> findByEntityId(String entityId) {
        List<Weight> weights = null;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase()
                    .query(WEIGHT_TABLE_NAME, WEIGHT_TABLE_COLUMNS, BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE,
                            new String[]{entityId}, null, null, null, null);
            weights = readAllWeights(cursor);
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return weights;
    }

    public List<Weight> findWithNoZScore() {
        List<Weight> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase()
                    .query(WEIGHT_TABLE_NAME, WEIGHT_TABLE_COLUMNS, Z_SCORE + " = " + DEFAULT_Z_SCORE, null, null, null,
                            null, null);
            result = readAllWeights(cursor);
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

    public Weight find(Long caseId) {
        Weight weight = null;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase()
                    .query(WEIGHT_TABLE_NAME, WEIGHT_TABLE_COLUMNS, ID_COLUMN + " = ?", new String[]{caseId.toString()},
                            null, null, null, null);
            List<Weight> weights = readAllWeights(cursor);
            if (!weights.isEmpty()) {
                weight = weights.get(0);
            }
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return weight;
    }

    public List<Weight> findLast5(String entityid) {
        List<Weight> weightList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase()
                    .query(WEIGHT_TABLE_NAME, WEIGHT_TABLE_COLUMNS, BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE,
                            new String[]{entityid}, null, null, UPDATED_AT_COLUMN + COLLATE_NOCASE + " DESC", null);
            weightList = readAllWeights(cursor);
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return weightList;
    }

    public void delete(String id) {
        try {
            getWritableDatabase()
                    .delete(WEIGHT_TABLE_NAME, ID_COLUMN + " = ? " + COLLATE_NOCASE + " AND " + SYNC_STATUS + " = ? ",
                            new String[]{id, TYPE_Unsynced});
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
    }

    public void close(Long caseId) {
        try {
            ContentValues values = new ContentValues();
            values.put(SYNC_STATUS, TYPE_Synced);
            getWritableDatabase()
                    .update(WEIGHT_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId.toString()});
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
    }
}
