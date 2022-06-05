package org.smartregister.growthmonitoring.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightZScore;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HeightRepository extends BaseRepository {
    public static final String HEIGHT_TABLE_NAME = "heights";
    public static final String ID_COLUMN = "_id";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String EVENT_ID = "event_id";
    public static final String PROGRAM_CLIENT_ID = "program_client_id";// ID to be used to identify entity when base_entity_id is unavailable
    public static final String FORMSUBMISSION_ID = "formSubmissionId";
    public static final String OUT_OF_AREA = "out_of_area";
    public static final String CM = "cm";
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
    public static final String[] HEIGHT_TABLE_COLUMNS = {
            ID_COLUMN, BASE_ENTITY_ID, PROGRAM_CLIENT_ID, CM, DATE, ANMID, LOCATIONID, CHILD_LOCATION_ID, TEAM, TEAM_ID,
            SYNC_STATUS, UPDATED_AT_COLUMN, EVENT_ID, FORMSUBMISSION_ID, Z_SCORE, OUT_OF_AREA, CREATED_AT};
    private static final String TAG = HeightRepository.class.getCanonicalName();
    private static final String HEIGHT_SQL = "CREATE TABLE heights (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "base_entity_id VARCHAR NOT NULL,program_client_id VARCHAR NULL,cm REAL NOT NULL,date DATETIME NOT NULL,anmid " +
            "VARCHAR NULL,location_id VARCHAR NULL,sync_status VARCHAR,updated_at INTEGER NULL,child_location_id VARCHAR,team_id VARCHAR,team VARCHAR,created_at DATETIME NULL " +
            ",z_score REAL NOT NULL DEFAULT " + DEFAULT_Z_SCORE+",out_of_area VARCHAR,formSubmissionId VARCHAR,event_id VARCHAR)";

    private static final String BASE_ENTITY_ID_INDEX = "CREATE INDEX " + HEIGHT_TABLE_NAME + "_" + BASE_ENTITY_ID + "_index ON " + HEIGHT_TABLE_NAME + "(" + BASE_ENTITY_ID + " COLLATE NOCASE);";
    private static final String SYNC_STATUS_INDEX = "CREATE INDEX " + HEIGHT_TABLE_NAME + "_" + SYNC_STATUS + "_index ON " + HEIGHT_TABLE_NAME + "(" + SYNC_STATUS + " COLLATE NOCASE);";
    private static final String UPDATED_AT_INDEX = "CREATE INDEX " + HEIGHT_TABLE_NAME + "_" + UPDATED_AT_COLUMN + "_index ON " + HEIGHT_TABLE_NAME + "(" + UPDATED_AT_COLUMN + ");";

    public HeightRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(HEIGHT_SQL);
        database.execSQL(BASE_ENTITY_ID_INDEX);
        database.execSQL(SYNC_STATUS_INDEX);
        database.execSQL(UPDATED_AT_INDEX);
    }

    public static void migrateCreatedAt(SQLiteDatabase database) {
        try {
            String sql = "UPDATE " + HEIGHT_TABLE_NAME +
                    " SET " + CREATED_AT + " = " +
                    " ( SELECT " + EventClientRepository.event_column.dateCreated.name() +
                    "   FROM " + EventClientRepository.Table.event.name() +
                    "   WHERE " + EventClientRepository.event_column.eventId
                    .name() + " = " + HEIGHT_TABLE_NAME + "." + EVENT_ID +
                    "   OR " + EventClientRepository.event_column.formSubmissionId
                    .name() + " = " + HEIGHT_TABLE_NAME + "." + FORMSUBMISSION_ID +
                    " ) " +
                    " WHERE " + CREATED_AT + " is null ";
            database.execSQL(sql);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * This method sets the height's z-score, before adding it to the database
     *
     * @param dateOfBirth
     * @param gender
     * @param height
     */
    public void add(Date dateOfBirth, Gender gender, Height height) {

        height.setZScore(HeightZScore.calculate(gender, dateOfBirth, height.getDate(), height.getCm()));
        add(height);
    }

    public void add(Height height) {
        try {
            if (height == null) {
                return;
            }

            AllSharedPreferences allSharedPreferences = GrowthMonitoringLibrary.getInstance().context()
                    .allSharedPreferences();
            String providerId = allSharedPreferences.fetchRegisteredANM();
            height.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
            height.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));
            height.setLocationId(allSharedPreferences.fetchDefaultLocalityId(providerId));


            if (StringUtils.isBlank(height.getSyncStatus())) {
                height.setSyncStatus(TYPE_Unsynced);
            }
            if (StringUtils.isBlank(height.getFormSubmissionId())) {
                height.setFormSubmissionId(generateRandomUUIDString());
            }


            if (height.getUpdatedAt() == null) {
                height.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
            }

            SQLiteDatabase database = getRepository().getWritableDatabase();
            if (height.getId() == null) {
                Height sameheight = findUnique(database, height);
                if (sameheight != null) {
                    height.setUpdatedAt(sameheight.getUpdatedAt());
                    height.setId(sameheight.getId());
                    update(database, height);
                } else {
                    if (height.getCreatedAt() == null) {
                        height.setCreatedAt(new Date());
                    }
                    height.setId(database.insert(HEIGHT_TABLE_NAME, null, createValuesFor(height)));
                }
            } else {
                if(height.getSyncStatus()!=null && !height.getSyncStatus().equalsIgnoreCase(TYPE_Synced)){
                    height.setSyncStatus(TYPE_Unsynced);
                }
                update(database, height);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public Height findUnique(SQLiteDatabase db, Height height) {

        if (height == null || (StringUtils.isBlank(height.getFormSubmissionId()) && StringUtils
                .isBlank(height.getEventId()))) {
            return null;
        }

        try {
            SQLiteDatabase database = db;
            if (database == null) {
                database = getRepository().getReadableDatabase();
            }

            String selection = null;
            String[] selectionArgs = null;
            if (StringUtils.isNotBlank(height.getFormSubmissionId()) && StringUtils.isNotBlank(height.getEventId())) {
                selection = FORMSUBMISSION_ID + " = ? " + COLLATE_NOCASE + " OR " + EVENT_ID + " = ? " + COLLATE_NOCASE;
                selectionArgs = new String[] {height.getFormSubmissionId(), height.getEventId()};
            } else if (StringUtils.isNotBlank(height.getEventId())) {
                selection = EVENT_ID + " = ? " + COLLATE_NOCASE;
                selectionArgs = new String[] {height.getEventId()};
            } else if (StringUtils.isNotBlank(height.getFormSubmissionId())) {
                selection = FORMSUBMISSION_ID + " = ? " + COLLATE_NOCASE;
                selectionArgs = new String[] {height.getFormSubmissionId()};
            }

            Cursor cursor = database.query(HEIGHT_TABLE_NAME, HEIGHT_TABLE_COLUMNS, selection, selectionArgs, null, null,
                    ID_COLUMN + " DESC ", null);
            List<Height> heightList = readAllheights(cursor);
            if (!heightList.isEmpty()) {
                return heightList.get(0);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return null;
    }

    public void update(SQLiteDatabase database, Height height) {
        if (height == null || height.getId() == null) {
            return;
        }

        try {
            SQLiteDatabase db;
            if (database == null) {
                db = getRepository().getWritableDatabase();
            } else {
                db = database;
            }

            String idSelection = ID_COLUMN + " = ?";
            db.update(HEIGHT_TABLE_NAME, createValuesFor(height), idSelection, new String[] {height.getId().toString()});
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private ContentValues createValuesFor(Height height) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, height.getId());
        values.put(BASE_ENTITY_ID, height.getBaseEntityId());
        values.put(PROGRAM_CLIENT_ID, height.getProgramClientId());
        values.put(CM, height.getCm());
        values.put(DATE, height.getDate() != null ? height.getDate().getTime() : null);
        values.put(ANMID, height.getAnmId());
        values.put(LOCATIONID, height.getLocationId());
        values.put(CHILD_LOCATION_ID, height.getChildLocationId());
        values.put(TEAM, height.getTeam());
        values.put(TEAM_ID, height.getTeamId());
        values.put(SYNC_STATUS, height.getSyncStatus());
        values.put(UPDATED_AT_COLUMN, height.getUpdatedAt());
        values.put(EVENT_ID, height.getEventId());
        values.put(FORMSUBMISSION_ID, height.getFormSubmissionId());
        values.put(OUT_OF_AREA, height.getOutOfCatchment());
        values.put(Z_SCORE, String.valueOf(height.getZScore() == null ? DEFAULT_Z_SCORE : height.getZScore()));
        values.put(CREATED_AT,
                height.getCreatedAt() != null ? EventClientRepository.dateFormat.format(height.getCreatedAt()) : null);
        return values;
    }

    private List<Height> readAllheights(Cursor cursor) {
        List<Height> heights = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Double zScore = cursor.getDouble(cursor.getColumnIndex(Z_SCORE));
                    if (zScore.equals(DEFAULT_Z_SCORE)) {
                        zScore = null;
                    }


                    Date createdAt = null;
                    String dateCreatedString = cursor.getString(cursor.getColumnIndex(CREATED_AT));
                    if (StringUtils.isNotBlank(dateCreatedString)) {
                        try {
                            createdAt = EventClientRepository.dateFormat.parse(dateCreatedString);
                        } catch (ParseException e) {
                            Log.e(TAG, Log.getStackTraceString(e));
                        }
                    }

                    Height height = new Height(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID)),
                            cursor.getString(cursor.getColumnIndex(PROGRAM_CLIENT_ID)),
                            cursor.getFloat(cursor.getColumnIndex(CM)),
                            new Date(cursor.getLong(cursor.getColumnIndex(DATE))),
                            cursor.getString(cursor.getColumnIndex(ANMID)),
                            cursor.getString(cursor.getColumnIndex(LOCATIONID)),
                            cursor.getString(cursor.getColumnIndex(SYNC_STATUS)),
                            cursor.getLong(cursor.getColumnIndex(UPDATED_AT_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(EVENT_ID)),
                            cursor.getString(cursor.getColumnIndex(FORMSUBMISSION_ID)),
                            zScore,
                            cursor.getInt(cursor.getColumnIndex(OUT_OF_AREA)),
                            createdAt);

                    height.setTeam(cursor.getString(cursor.getColumnIndex(TEAM)));
                    height.setTeamId(cursor.getString(cursor.getColumnIndex(TEAM_ID)));
                    height.setChildLocationId(cursor.getString(cursor.getColumnIndex(CHILD_LOCATION_ID)));

                    heights.add(height);

                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return heights;

    }

    public List<Height> findUnSyncedBeforeTime(int hours) {
        List<Height> heights = new ArrayList<>();
        Cursor cursor = null;
        try {

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, -hours);

            Long time = calendar.getTimeInMillis();

            cursor = getRepository().getReadableDatabase().query(HEIGHT_TABLE_NAME, HEIGHT_TABLE_COLUMNS,
                    UPDATED_AT_COLUMN + " < ? " + COLLATE_NOCASE + " AND " + SYNC_STATUS + " = ? " + COLLATE_NOCASE,
                    new String[] {time.toString(), TYPE_Unsynced}, null, null, null, null);
            heights = readAllheights(cursor);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return heights;
    }

    public Height findUnSyncedByEntityId(String entityId) {
        Height height = null;
        Cursor cursor = null;
        try {

            cursor = getRepository().getReadableDatabase().query(HEIGHT_TABLE_NAME, HEIGHT_TABLE_COLUMNS,
                    BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE + " AND " + SYNC_STATUS + " = ? ",
                    new String[] {entityId, TYPE_Unsynced}, null, null, UPDATED_AT_COLUMN + COLLATE_NOCASE + " DESC", null);
            List<Height> heights = readAllheights(cursor);
            if (!heights.isEmpty()) {
                height = heights.get(0);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return height;
    }
    public List<Height> getMaximum12(String entityId) {
        List<Height> heights = null;
        Cursor cursor = null;
        try {
            cursor = getRepository().getReadableDatabase()
                    .query(HEIGHT_TABLE_NAME, HEIGHT_TABLE_COLUMNS, BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE,
                            new String[] {entityId}, null, null, UPDATED_AT_COLUMN + COLLATE_NOCASE + " DESC", "12");
            heights = readAllheights(cursor);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return heights;
    }

    public List<Height> findByEntityId(String entityId) {
        List<Height> heights = null;
        Cursor cursor = null;
        try {
            cursor = getRepository().getReadableDatabase()
                    .query(HEIGHT_TABLE_NAME, HEIGHT_TABLE_COLUMNS, BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE,
                            new String[] {entityId}, null, null, UPDATED_AT_COLUMN + COLLATE_NOCASE + " DESC", null);
            heights = readAllheights(cursor);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return heights;
    }

    public List<Height> findWithNoZScore() {
        List<Height> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getRepository().getReadableDatabase().query(HEIGHT_TABLE_NAME,
                    HEIGHT_TABLE_COLUMNS, Z_SCORE + " = " + DEFAULT_Z_SCORE, null, null, null, null, null);
            result = readAllheights(cursor);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

    public Height find(Long caseId) {
        Height height = null;
        Cursor cursor = null;
        try {
            cursor = getRepository().getReadableDatabase()
                    .query(HEIGHT_TABLE_NAME, HEIGHT_TABLE_COLUMNS, ID_COLUMN + " = ?", new String[] {caseId.toString()},
                            null, null, null, null);
            List<Height> heights = readAllheights(cursor);
            if (!heights.isEmpty()) {
                height = heights.get(0);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return height;
    }

    public List<Height> findLast5(String entityid) {
        List<Height> heightList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getRepository().getReadableDatabase()
                    .query(HEIGHT_TABLE_NAME, HEIGHT_TABLE_COLUMNS, BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE,
                            new String[] {entityid}, null, null, UPDATED_AT_COLUMN + COLLATE_NOCASE + " DESC", null);
            heightList = readAllheights(cursor);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return heightList;
    }

    public void delete(String id) {
        try {
            getRepository().getWritableDatabase()
                    .delete(HEIGHT_TABLE_NAME, ID_COLUMN + " = ? " + COLLATE_NOCASE + " AND " + SYNC_STATUS + " = ? ",
                            new String[] {id, TYPE_Unsynced});
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public void close(Long caseId) {
        try {
            ContentValues values = new ContentValues();
            values.put(SYNC_STATUS, TYPE_Synced);
            getRepository().getWritableDatabase()
                    .update(HEIGHT_TABLE_NAME, values, ID_COLUMN + " = ?", new String[] {caseId.toString()});
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
}
