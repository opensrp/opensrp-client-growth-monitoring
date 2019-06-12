package org.smartregister.growthmonitoring.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.DateTime;
import org.opensrp.api.constants.Gender;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightZScore;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightZScore;
import org.smartregister.growthmonitoring.repository.HeightZScoreRepository;
import org.smartregister.growthmonitoring.repository.WeightZScoreRepository;
import org.smartregister.growthmonitoring.util.GrowthMonitoringConstants;
import org.smartregister.util.FileUtilities;
import org.smartregister.util.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static org.smartregister.growthmonitoring.util.GrowthMonitoringConstants.ColumnHeaders;

/**
 * Created by Jason Rogena - jrogena@ona.io on 30/05/2017.
 */

public class ZScoreRefreshIntentService extends IntentService {
    private static final String TAG = ZScoreRefreshIntentService.class.getName();
    private static final Map<String, String> CSV_HEADING_SQL_COLUMN_MAP;
    private static final Map<String, String> HEIGHT_CSV_HEADING_SQL_COLUMN_MAP;

    static {
        CSV_HEADING_SQL_COLUMN_MAP = new HashMap<>();
        CSV_HEADING_SQL_COLUMN_MAP.put("Month", ColumnHeaders.COLUMN_MONTH);
        CSV_HEADING_SQL_COLUMN_MAP.put("L", ColumnHeaders.COLUMN_L);
        CSV_HEADING_SQL_COLUMN_MAP.put("M", ColumnHeaders.COLUMN_M);
        CSV_HEADING_SQL_COLUMN_MAP.put("S", ColumnHeaders.COLUMN_S);
        CSV_HEADING_SQL_COLUMN_MAP.put("SD3neg", ColumnHeaders.COLUMN_SD3NEG);
        CSV_HEADING_SQL_COLUMN_MAP.put("SD2neg", ColumnHeaders.COLUMN_SD2NEG);
        CSV_HEADING_SQL_COLUMN_MAP.put("SD1neg", ColumnHeaders.COLUMN_SD1NEG);
        CSV_HEADING_SQL_COLUMN_MAP.put("SD0", ColumnHeaders.COLUMN_SD0);
        CSV_HEADING_SQL_COLUMN_MAP.put("SD1", ColumnHeaders.COLUMN_SD1);
        CSV_HEADING_SQL_COLUMN_MAP.put("SD2", ColumnHeaders.COLUMN_SD2);
        CSV_HEADING_SQL_COLUMN_MAP.put("SD3", ColumnHeaders.COLUMN_SD3);

        HEIGHT_CSV_HEADING_SQL_COLUMN_MAP = CSV_HEADING_SQL_COLUMN_MAP;
        HEIGHT_CSV_HEADING_SQL_COLUMN_MAP.put("SD", ColumnHeaders.COLUMN_SD);
    }

    public ZScoreRefreshIntentService() {
        super(TAG);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ZScoreRefreshIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Dump CSV to file
        dumpWeightCsv(Gender.MALE, false);
        dumpWeightCsv(Gender.FEMALE, false);

        dumpHeightCsv(Gender.MALE, false);
        dumpHeightCsv(Gender.FEMALE, false);

        calculateChildWeightZScores();
        calculateChildHeightZScores();

        //FIXME split-growth-monitoring:Calling hia2Service after calculating zscore
        //Intent hia2Intent = new Intent(GrowthMonitoringLibrary.getInstance(), HIA2IntentService.class);
        //startService(hia2Intent);
    }

    /**
     * This method dumps the WeightZScore CSV corresponding to the provided gender into the z_score table
     *
     * @param gender
     * @param force
     */
    private void dumpWeightCsv(Gender gender, boolean force) {
        try {
            List<WeightZScore> existingScores = GrowthMonitoringLibrary.getInstance().weightZScoreRepository()
                    .findByGender(gender);
            if (force
                    || existingScores.size() == 0) {
                String filename = null;
                if (gender.equals(Gender.FEMALE)) {
                    filename = GrowthMonitoringLibrary.getInstance().getConfig().getFemaleWeightZScoreFile();
                } else if (gender.equals(Gender.MALE)) {
                    filename = GrowthMonitoringLibrary.getInstance().getConfig().getMaleWeightZScoreFile();
                }

                if (filename != null) {
                    CSVParser csvParser = CSVParser.parse(Utils.readAssetContents(this, filename),
                            CSVFormat.newFormat('\t'));

                    HashMap<Integer, Boolean> columnStatus = new HashMap<>();
                    String query = "INSERT INTO `" + WeightZScoreRepository.TABLE_NAME + "` ( `" + ColumnHeaders.COLUMN_SEX + "`";
                    for (CSVRecord record : csvParser) {
                        if (csvParser.getCurrentLineNumber() == 2) {// The second line
                            query = query + ")\n VALUES (\"" + gender.name() + "\"";
                        } else if (csvParser.getCurrentLineNumber() > 2) {
                            query = query + "),\n (\"" + gender.name() + "\"";
                        }

                        for (int columnIndex = 0; columnIndex < record.size(); columnIndex++) {
                            String curColumn = record.get(columnIndex);
                            if (csvParser.getCurrentLineNumber() == 1) {
                                if (CSV_HEADING_SQL_COLUMN_MAP.containsKey(curColumn)) {
                                    columnStatus.put(columnIndex, true);
                                    query = query + ", `" + CSV_HEADING_SQL_COLUMN_MAP.get(curColumn) + "`";
                                } else {
                                    columnStatus.put(columnIndex, false);
                                }
                            } else {
                                if (columnStatus.get(columnIndex)) {
                                    query = query + ", \"" + curColumn + "\"";
                                }
                            }
                        }
                    }
                    query = query + ");";

                    boolean result = GrowthMonitoringLibrary.getInstance().weightZScoreRepository().runRawQuery(query);
                    Log.d(TAG, "Result is " + result);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * This method dumps the HeightZScore CSV corresponding to the provided gender into the z_score table
     *
     * @param gender
     * @param force
     */
    private void dumpHeightCsv(Gender gender, boolean force) {
        try {
            List<HeightZScore> existingScores = GrowthMonitoringLibrary.getInstance().heightZScoreRepository()
                    .findByGender(gender);
            if (force
                    || existingScores.size() == 0) {
                String filename = null;
                if (gender.equals(Gender.FEMALE)) {
                    filename = GrowthMonitoringLibrary.getInstance().getConfig().getFemaleHeightZScoreFile();
                } else if (gender.equals(Gender.MALE)) {
                    filename = GrowthMonitoringLibrary.getInstance().getConfig().getMaleHeightZScoreFile();
                }

                if (filename != null) {
                    CSVParser csvParser = CSVParser.parse(Utils.readAssetContents(this, filename),
                            CSVFormat.newFormat('\t'));

                    HashMap<Integer, Boolean> columnStatus = new HashMap<>();
                    String query = "INSERT INTO `" + HeightZScoreRepository.TABLE_NAME + "` ( `" + ColumnHeaders.COLUMN_SEX + "`";
                    for (CSVRecord record : csvParser) {
                        if (csvParser.getCurrentLineNumber() == 2) {// The second line
                            query = query + ")\n VALUES (\"" + gender.name() + "\"";
                        } else if (csvParser.getCurrentLineNumber() > 2) {
                            query = query + "),\n (\"" + gender.name() + "\"";
                        }

                        for (int columnIndex = 0; columnIndex < record.size(); columnIndex++) {
                            String curColumn = record.get(columnIndex);
                            if (csvParser.getCurrentLineNumber() == 1) {
                                if (HEIGHT_CSV_HEADING_SQL_COLUMN_MAP.containsKey(curColumn)) {
                                    columnStatus.put(columnIndex, true);
                                    query = query + ", `" + HEIGHT_CSV_HEADING_SQL_COLUMN_MAP.get(curColumn) + "`";
                                } else {
                                    columnStatus.put(columnIndex, false);
                                }
                            } else {
                                if (columnStatus.get(columnIndex)) {
                                    query = query + ", \"" + curColumn + "\"";
                                }
                            }
                        }
                    }
                    query = query + ");";

                    boolean result = GrowthMonitoringLibrary.getInstance().heightZScoreRepository().runRawQuery(query);
                    Log.d(TAG, "Result is " + result);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * This method retrieves all weight records that don't have ZScores and tries to calculate their
     * corresponding ZScores
     */
    private void calculateChildWeightZScores() {
        try {
            HashMap<String, CommonPersonObjectClient> children = new HashMap<>();
            List<Weight> weightsWithoutZScores = GrowthMonitoringLibrary.getInstance().weightRepository().findWithNoZScore();
            for (Weight curWeight : weightsWithoutZScores) {
                if (!TextUtils.isEmpty(curWeight.getBaseEntityId())) {
                    if (!children.containsKey(curWeight.getBaseEntityId())) {
                        CommonPersonObjectClient childDetails = getChildDetails(curWeight.getBaseEntityId());
                        children.put(curWeight.getBaseEntityId(), childDetails);
                    }

                    CommonPersonObjectClient curChild = children.get(curWeight.getBaseEntityId());

                    if (curChild != null) {
                        Gender gender = Gender.UNKNOWN;
                        String genderString = Utils.getValue(curChild.getColumnmaps(), "gender", false);
                        if (genderString != null && genderString.equalsIgnoreCase("female")) {
                            gender = Gender.FEMALE;
                        } else if (genderString != null && genderString.equalsIgnoreCase("male")) {
                            gender = Gender.MALE;
                        }

                        Date dob = null;
                        String dobString = Utils.getValue(curChild.getColumnmaps(), "dob", false);
                        if (!TextUtils.isEmpty(dobString)) {
                            DateTime dateTime = new DateTime(dobString);
                            dob = dateTime.toDate();
                        }

                        if (gender != Gender.UNKNOWN && dob != null) {
                            GrowthMonitoringLibrary.getInstance().weightRepository().add(dob, gender, curWeight);
                        } else {
                            Log.w(TAG, "Could not get the date of birth or gender for child with base entity id " + curWeight
                                    .getBaseEntityId());
                        }
                    } else {
                        Log.w(TAG, "Could not get the details for child with base entity id " + curWeight.getBaseEntityId());
                    }
                } else {
                    Log.w(TAG, "Current weight with id " + curWeight.getId() + " has no base entity id");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * This method retrieves all height records that don't have ZScores and tries to calculate their
     * corresponding ZScores
     */
    private void calculateChildHeightZScores() {
        try {
            HashMap<String, CommonPersonObjectClient> children = new HashMap<>();
            List<Height> heightsWithoutZScores = GrowthMonitoringLibrary.getInstance().heightRepository().findWithNoZScore();
            for (Height curHeight : heightsWithoutZScores) {
                if (!TextUtils.isEmpty(curHeight.getBaseEntityId())) {
                    if (!children.containsKey(curHeight.getBaseEntityId())) {
                        CommonPersonObjectClient childDetails = getChildDetails(curHeight.getBaseEntityId());
                        children.put(curHeight.getBaseEntityId(), childDetails);
                    }

                    CommonPersonObjectClient curChild = children.get(curHeight.getBaseEntityId());

                    if (curChild != null) {
                        Gender gender = Gender.UNKNOWN;
                        String genderString = Utils.getValue(curChild.getColumnmaps(), "gender", false);
                        if (genderString != null && genderString.equalsIgnoreCase("female")) {
                            gender = Gender.FEMALE;
                        } else if (genderString != null && genderString.equalsIgnoreCase("male")) {
                            gender = Gender.MALE;
                        }

                        Date dob = null;
                        String dobString = Utils.getValue(curChild.getColumnmaps(), "dob", false);
                        if (!TextUtils.isEmpty(dobString)) {
                            DateTime dateTime = new DateTime(dobString);
                            dob = dateTime.toDate();
                        }

                        if (gender != Gender.UNKNOWN && dob != null) {
                            GrowthMonitoringLibrary.getInstance().heightRepository().add(dob, gender, curHeight);
                        } else {
                            Log.w(TAG, "Could not get the date of birth or gender for child with base entity id " + curHeight
                                    .getBaseEntityId());
                        }
                    } else {
                        Log.w(TAG, "Could not get the details for child with base entity id " + curHeight.getBaseEntityId());
                    }
                } else {
                    Log.w(TAG, "Current weight with id " + curHeight.getId() + " has no base entity id");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private CommonPersonObjectClient getChildDetails(String baseEntityId) {
        CommonPersonObject rawDetails = GrowthMonitoringLibrary.getInstance().context()
                .commonrepository(GrowthMonitoringConstants.CHILD_TABLE_NAME).findByBaseEntityId(baseEntityId);
        if (rawDetails != null) {
            // Get extra child details
            CommonPersonObjectClient childDetails = Utils.convert(rawDetails);
            childDetails.getColumnmaps().putAll(GrowthMonitoringLibrary.getInstance().context()
                    .detailsRepository().getAllDetailsForClient(baseEntityId));

            return childDetails;
        }

        return null;
    }

    private void fetchCSV(Gender gender) {
        String urlString = null;
        if (gender.equals(Gender.FEMALE)) {
            urlString = GrowthMonitoringConstants.ZSCORE_FEMALE_URL;
        } else if (gender.equals(Gender.MALE)) {
            urlString = GrowthMonitoringConstants.ZSCORE_MALE_URL;
        }

        try {
            URL url;

            url = new URL(urlString);
            URLConnection urlConnection = null;

            int responseCode = 0;
            if (url.getProtocol().equalsIgnoreCase("https")) {
                urlConnection = url.openConnection();

                // Sets the user agent for this request.
                urlConnection.setRequestProperty("User-Agent",
                        FileUtilities.getUserAgent(GrowthMonitoringLibrary.getInstance().context().applicationContext()));

                // Gets a response code from the RSS server
                responseCode = ((HttpsURLConnection) urlConnection).getResponseCode();

            } else if (url.getProtocol().equalsIgnoreCase("http")) {
                urlConnection = url.openConnection();

                // Sets the user agent for this request.
                urlConnection.setRequestProperty("User-Agent",
                        FileUtilities.getUserAgent(GrowthMonitoringLibrary.getInstance().context().applicationContext()));

                // Gets a response code from the RSS server
                responseCode = ((HttpsURLConnection) urlConnection).getResponseCode();
            }

            switch (responseCode) {
                // If the response is OK
                case HttpURLConnection.HTTP_OK:
                    // Gets the last modified data for the URL
                    processResponse(urlConnection, gender);
                    break;
                default:
                    Log.e(TAG, "Response code " + responseCode + " returned for Z-Score fetch from " + urlString);
                    break;
            }


        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    private void processResponse(URLConnection urlConnection, Gender gender) {
        // TODO: write file to asset folder
        //String response = readInputStreamToString(urlConnection);
    }

    /**
     * @param connection object; note: before calling this function,
     *                   ensure that the connection is already be open, and any writes to
     *                   the connection's output stream should have already been completed.
     * @return String containing the body of the connection response or
     * null if the input stream could not be read correctly
     */
    private String readInputStreamToString(URLConnection connection) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            is = new BufferedInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        } catch (Exception e) {
            Log.i(TAG, "Error reading InputStream");
            result = null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.i(TAG, "Error closing InputStream");
                }
            }
        }

        return result;
    }
}
