package org.smartregister.growthmonitoring.repository;

import org.smartregister.repository.Repository;

import java.util.UUID;

/**
 * Created by keyman on 09/03/2017.
 */
public class BaseRepository {
    public static String TYPE_Unsynced = "Unsynced";
    public static String TYPE_Synced = "Synced";
    public static String COLLATE_NOCASE = " COLLATE NOCASE ";

    private Repository repository;

    public BaseRepository(Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

    protected String generateRandomUUIDString() {
        return UUID.randomUUID().toString();
    }
}
