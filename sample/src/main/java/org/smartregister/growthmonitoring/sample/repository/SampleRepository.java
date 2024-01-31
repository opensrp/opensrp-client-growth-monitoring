package org.smartregister.growthmonitoring.sample.repository;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.domain.db.Column;
import org.smartregister.growthmonitoring.repository.WeightForHeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.repository.ZScoreRepository;
import org.smartregister.growthmonitoring.sample.BuildConfig;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;

/**
 * Created by keyman on 28/07/2017.
 */
public class SampleRepository extends Repository {


    private static final String TAG = SampleRepository.class.getCanonicalName();
    protected SQLiteDatabase readableDatabase;
    protected SQLiteDatabase writableDatabase;
    private Context context;
    private String password = "Sample_PASS";

    public SampleRepository(Context context, org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(), null, openSRPContext.sharedRepositoriesArray());
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        EventClientRepository.createTable(database, EventClientRepository.Table.client, EventClientRepository.client_column.values());
        EventClientRepository.createTable(database, EventClientRepository.Table.address, EventClientRepository.address_column.values());
        EventClientRepository.createTable(database, EventClientRepository.Table.event, EventClientRepository.event_column.values());
        EventClientRepository.createTable(database, EventClientRepository.Table.obs, EventClientRepository.obs_column.values());

        WeightRepository.createTable(database);
        database.execSQL(WeightRepository.EVENT_ID_INDEX);
        database.execSQL(WeightRepository.FORMSUBMISSION_INDEX);
        database.execSQL(WeightRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL_INDEX);

        EventClientRepository.createTable(database, EventClientRepository.Table.path_reports, EventClientRepository.report_column.values());
        WeightForHeightRepository.createTable(database);

        onUpgrade(database, 1, BuildConfig.DATABASE_VERSION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SampleRepository.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    upgradeToVersion2(db);
                    break;
                case 3:
                    upgradeToVersion3(db);
                    break;
                case 4:
                    upgradeToVersion4(db);
                    break;
                default:
                    break;
            }
            upgradeTo++;
        }
    }

    private void upgradeToVersion2(SQLiteDatabase db) {
        try {
            ZScoreRepository.createTable(db);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion2 " + e.getMessage());
        }
    }

    private void upgradeToVersion3(SQLiteDatabase db) {
        try {
            Column[] columns = {EventClientRepository.event_column.formSubmissionId};
            EventClientRepository.createIndex(db, EventClientRepository.Table.event, columns);

            WeightRepository.migrateCreatedAt(db);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion3 " + e.getMessage());
        }

    }

    private void upgradeToVersion4(SQLiteDatabase db) {
        try {
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion4 " + Log.getStackTraceString(e));
        }
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return getReadableDatabase(password);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return getWritableDatabase(password);
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase(String password) {
        try {
            if (readableDatabase == null || !readableDatabase.isOpen()) {
                if (readableDatabase != null) {
                    readableDatabase.close();
                }
                readableDatabase = super.getReadableDatabase(password);
            }
            return readableDatabase;
        } catch (Exception e) {
            Log.e(TAG, "Database Error. " + e.getMessage());
            return null;
        }

    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase(String password) {
        if (writableDatabase == null || !writableDatabase.isOpen()) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            writableDatabase = super.getWritableDatabase(password);
        }
        return writableDatabase;
    }

    @Override
    public synchronized void close() {
        if (readableDatabase != null) {
            readableDatabase.close();
        }

        if (writableDatabase != null) {
            writableDatabase.close();
        }
        super.close();
    }


}
