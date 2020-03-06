package org.smartregister.growthmonitoring.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.util.Utils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class WeightForHeightIntentService extends IntentService {

    private static final String ACTION_PARSE_WFH_CSV = "org.smartregister.growthmonitoring.service.intent.action.parse";

    public WeightForHeightIntentService() {
        super("WeightForHeightIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFoo(Context context) {
        Intent intent = new Intent(context, WeightForHeightIntentService.class);
        intent.setAction(ACTION_PARSE_WFH_CSV);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PARSE_WFH_CSV.equals(action)) {
                parseWFHCSV();
            }
        }
    }

    /**
     * Handle parsing the WFH CSV chart in the provided background thread
     */
    private void parseWFHCSV() {

        try {
            String fileName = null;
            if (StringUtils.isNotBlank(fileName)) {
                CSVParser csvParser = CSVParser.parse(Utils.readAssetContents(this, fileName), CSVFormat.newFormat('\t'));
                for (CSVRecord record : csvParser) {

                }
            }
        } catch (Exception ex) {

        }
    }
}
