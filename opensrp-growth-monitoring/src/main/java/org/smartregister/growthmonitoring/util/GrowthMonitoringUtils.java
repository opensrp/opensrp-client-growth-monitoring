package org.smartregister.growthmonitoring.util;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.listener.ViewMeasureListener;
import org.smartregister.util.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GrowthMonitoringUtils {
    public static Calendar[] getMinAndMaxRecordingDates(Date dob) {
        Calendar minGraphTime = null;
        Calendar maxGraphTime = null;
        if (dob != null) {
            Calendar dobCalendar = Calendar.getInstance();
            dobCalendar.setTime(dob);
            GrowthMonitoringUtils.standardiseCalendarDate(dobCalendar);

            minGraphTime = Calendar.getInstance();
            maxGraphTime = Calendar.getInstance();

            if (ZScore.getAgeInMonths(dob, maxGraphTime.getTime()) > ZScore.MAX_REPRESENTED_AGE) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dob);
                cal.add(Calendar.MONTH, (int) Math.round(ZScore.MAX_REPRESENTED_AGE));
                maxGraphTime = cal;
                minGraphTime = (Calendar) maxGraphTime.clone();
            }

            minGraphTime.add(Calendar.MONTH, -GMConstants.GRAPH_MONTHS_TIMELINE);
            GrowthMonitoringUtils.standardiseCalendarDate(minGraphTime);
            GrowthMonitoringUtils.standardiseCalendarDate(maxGraphTime);

            if (minGraphTime.getTimeInMillis() < dobCalendar.getTimeInMillis()) {
                minGraphTime.setTime(dob);
                GrowthMonitoringUtils.standardiseCalendarDate(minGraphTime);

                maxGraphTime = (Calendar) minGraphTime.clone();
                maxGraphTime.add(Calendar.MONTH, GMConstants.GRAPH_MONTHS_TIMELINE);
            }
        }

        return new Calendar[] {minGraphTime, maxGraphTime};
    }
    /**
     * Parse CSV file and build query String for persisting values in the DB
     *
     * @param gender              Gender for which the chart values belong to
     * @param context
     * @param filename            CSV file name
     * @param tableName           Table where values will be stored
     * @param csvHeadingColumnMap CSV Headings map
     * @return Query String
     */
    public static String getDumpCsvQuery(Gender gender, Context context, String filename, String tableName, Map<String, String> csvHeadingColumnMap) {
        StringBuilder queryString;
        try {
            if (filename != null) {
                CSVParser csvParser =
                        CSVParser.parse(Utils.readAssetContents(context, filename), CSVFormat.newFormat('\t'));

                HashMap<Integer, Boolean> columnStatus = new HashMap<>();

                queryString = new StringBuilder("INSERT INTO `" + tableName + "` ( `" + GrowthMonitoringConstants.ColumnHeaders.COLUMN_SEX + "`");
                for (CSVRecord record : csvParser) {
                    if (csvParser.getCurrentLineNumber() == 2) {// The second line
                        queryString.append(")\n VALUES (\"").append(gender.name()).append("\"");
                    } else if (csvParser.getCurrentLineNumber() > 2) {
                        queryString.append("),\n (\"").append(gender.name()).append("\"");
                    }

                    for (int columnIndex = 0; columnIndex < record.size(); columnIndex++) {
                        String curColumn = record.get(columnIndex);
                        if (csvParser.getCurrentLineNumber() == 1) {
                            if (csvHeadingColumnMap.containsKey(curColumn)) {
                                columnStatus.put(columnIndex, true);
                                queryString.append(", `").append(csvHeadingColumnMap.get(curColumn)).append("`");
                            } else {
                                columnStatus.put(columnIndex, false);
                            }
                        } else {
                            if (columnStatus.get(columnIndex)) {
                                queryString.append(", \"").append(curColumn).append("\"");
                            }
                        }
                    }
                }
                queryString.append(");");
                return queryString.toString();
            }
        } catch (Exception e) {
        }
        return null;
    }


    public static void standardiseCalendarDate(Calendar calendarDate) {
        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);
    }

    public static void getHeight(final View view, final ViewMeasureListener viewMeasureListener) {
        if (view == null) {
            if (viewMeasureListener != null) {
                viewMeasureListener.onCompletedMeasuring(0);
            }

            return;
        }

        int measuredHeight = view.getMeasuredHeight();

        if (measuredHeight > 0) {
            if (viewMeasureListener != null) {
                viewMeasureListener.onCompletedMeasuring(measuredHeight);
            }

            return;
        }

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (viewMeasureListener != null) {
                    viewMeasureListener.onCompletedMeasuring(view.getMeasuredHeight());
                }

                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
}
