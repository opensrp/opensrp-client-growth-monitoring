package org.smartregister.growthmonitoring.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.repository.WeightForHeightRepository;
import org.smartregister.growthmonitoring.util.GrowthMonitoringConstants;
import org.smartregister.util.Utils;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class WeightForHeightIntentService extends IntentService {

    private static final String ACTION_PARSE_WFH_CSV = "org.smartregister.growthmonitoring.service.intent.action.parse";
    private static final Map<String, String> CSV_HEADING_COLUMN_MAP;

    static {
        CSV_HEADING_COLUMN_MAP = new HashMap<>();
        CSV_HEADING_COLUMN_MAP.put("sex", GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX);
        CSV_HEADING_COLUMN_MAP.put("height", GrowthMonitoringConstants.ColumnHeaders.HEIGHT);
        CSV_HEADING_COLUMN_MAP.put("l", GrowthMonitoringConstants.ColumnHeaders.COLUMN_L);
        CSV_HEADING_COLUMN_MAP.put("m", GrowthMonitoringConstants.ColumnHeaders.COLUMN_M);
        CSV_HEADING_COLUMN_MAP.put("s", GrowthMonitoringConstants.ColumnHeaders.COLUMN_S);
    }

    public WeightForHeightIntentService() {
        super("WeightForHeightIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startParseWFHZScores(Context context) {
        Intent intent = new Intent(context, WeightForHeightIntentService.class);
        intent.setAction(ACTION_PARSE_WFH_CSV);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PARSE_WFH_CSV.equals(action)) {
                parseWFHCSV(GrowthMonitoringLibrary.getInstance().getConfig().getWeightForHeightZScoreFile());
            }
        }
    }

    /**
     * Handle parsing the WFH CSV chart in the provided background thread
     */
    private void parseWFHCSV(String fileName) {
        StringBuilder queryString;
        try {
            if (StringUtils.isNotBlank(fileName)) {
                boolean isTableEmpty = GrowthMonitoringLibrary.getInstance().weightForHeightRepository().isTableEmpty(WeightForHeightRepository.TABLE_NAME);
                if (isTableEmpty) {
                    CSVParser csvParser = CSVParser.parse(Utils.readAssetContents(this, fileName), CSVFormat.newFormat('\t'));
                    HashMap<Integer, Boolean> columnStatus = new HashMap<>();

                    queryString = new StringBuilder("INSERT INTO `" + WeightForHeightRepository.TABLE_NAME + "` ( `");
                    for (CSVRecord record : csvParser) {
                        if (csvParser.getCurrentLineNumber() == 2) {// The second line
                            queryString.append(")\n VALUES (\"");
                        } else if (csvParser.getCurrentLineNumber() > 2) {
                            queryString.append("),\n (\"");
                        }

                        for (int columnIndex = 0; columnIndex < record.size(); columnIndex++) {
                            String curColumn = record.get(columnIndex);
                            if (csvParser.getCurrentLineNumber() == 1) {
                                if (CSV_HEADING_COLUMN_MAP.containsKey(curColumn)) {
                                    columnStatus.put(columnIndex, true);
                                    if (columnIndex > 0) {
                                        queryString.append(", `");
                                    }
                                    queryString.append(CSV_HEADING_COLUMN_MAP.get(curColumn)).append("`");
                                } else {
                                    columnStatus.put(columnIndex, false);
                                }
                            } else {
                                if (columnStatus.get(columnIndex)) {
                                    if (columnIndex > 0) {
                                        queryString.append(", \"");
                                    }
                                    queryString.append(curColumn).append("\"");
                                }
                            }
                        }
                    }
                    queryString.append(");");
                    boolean result = GrowthMonitoringLibrary.getInstance().weightForHeightRepository().runRawQuery(queryString.toString());
                    Timber.d("WeightForHeightIntentService --> parseWFHCSV --> Result :: %s", result);
                }
            }
        } catch (Exception ex) {
            Timber.e(ex, "WeightForHeightIntentService --> parseWFHCSV");
        }
    }
}
