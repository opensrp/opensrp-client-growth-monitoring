package org.smartregister.growthmonitoring.fragment;

import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightZScore;
import org.smartregister.growthmonitoring.listener.ViewMeasureListener;
import org.smartregister.growthmonitoring.util.GrowthMonitoringConstants;
import org.smartregister.growthmonitoring.util.GrowthMonitoringUtils;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import timber.log.Timber;

public class HeightMonitoringFragment extends Fragment {
    private static final String TAG = HeightMonitoringFragment.class.getName();
    private List<Height> heights;
    private Calendar maxRecordingDate = null;
    private Calendar minRecordingDate = null;
    private boolean isExpanded = false;
    private String dobString;
    private Gender gender;

    public static HeightMonitoringFragment createInstance(String dobString, Gender gender, List<Height> heights) {
        HeightMonitoringFragment heightMonitoringFragment = new HeightMonitoringFragment();
        heightMonitoringFragment.setDobString(dobString);
        heightMonitoringFragment.setGender(gender);
        heightMonitoringFragment.setHeights(heights);
        return heightMonitoringFragment;
    }

    public static String getTAG() {
        return TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup heightTabView = (ViewGroup) inflater.inflate(R.layout.height_monitoring_fragment, container, false);
        final ImageButton scrollButton = heightTabView.findViewById(R.id.scroll_button);

        Date dob = getDate();
        scrollButtonClickAction(heightTabView, scrollButton);

        try {
            refreshGrowthChart(heightTabView, getGender(), dob);
            refreshPreviousHeightsTable(heightTabView, getGender(), dob);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return heightTabView;
    }

    @Nullable
    private Date getDate() {
        Date dob = null;
        if (StringUtils.isNotBlank(getDobString())) {
            DateTime dateTime = new DateTime(getDobString());
            dob = dateTime.toDate();
            Calendar[] recordingDates = GrowthMonitoringUtils.getMinAndMaxRecordingDates(dob);
            minRecordingDate = recordingDates[0];
            maxRecordingDate = recordingDates[1];
        }
        return dob;
    }

    private void scrollButtonClickAction(final ViewGroup heightTabView, final ImageButton scrollButton) {
        scrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Prior implementation
                if (!isExpanded) {
                    isExpanded = true;
                    GrowthMonitoringUtils.getHeight(heightTabView.findViewById(R.id.height_growth_chart),
                            new ViewMeasureListener() {
                                @Override
                                public void onCompletedMeasuring(int Height) {
                                    heightTabView.findViewById(R.id.growth_dialog_height_table_layout)
                                            .getLayoutParams().height =
                                            getResources().getDimensionPixelSize(R.dimen.table_height) + Height;
                                }
                            });
                    heightTabView.findViewById(R.id.height_growth_chart).setVisibility(View.GONE);
                    scrollButton.setImageResource(R.drawable.ic_icon_expand);
                } else {
                    isExpanded = false;
                    heightTabView.findViewById(R.id.height_growth_chart).setVisibility(View.VISIBLE);
                    heightTabView.findViewById(R.id.growth_dialog_height_table_layout).getLayoutParams().height =
                            getResources().getDimensionPixelSize(R.dimen.table_height);
                    scrollButton.setImageResource(R.drawable.ic_icon_collapse);
                }
            }
        });
    }

    private void refreshGrowthChart(View parent, Gender gender, Date dob) {
        if (minRecordingDate == null || maxRecordingDate == null) {
            return;
        }

        if (gender != Gender.UNKNOWN && dob != null) {
            LineChartView growthChart = parent.findViewById(R.id.height_growth_chart);
            double minAge = HeightZScore.getAgeInMonths(dob, minRecordingDate.getTime());
            double maxAge = minAge + GrowthMonitoringConstants.GRAPH_MONTHS_TIMELINE;
            List<Line> lines = new ArrayList<>();
            for (int z = -3; z <= 3; z++) {
                if (z != 1 && z != -1) {
                    Line curLine = getZScoreLine(gender, minAge, maxAge, z,
                            getActivity().getResources().getColor(HeightZScore.getZScoreColor(z)));
                    if (z == -3) {
                        curLine.setPathEffect(new DashPathEffect(new float[] {10, 20}, 0));
                    }
                    lines.add(curLine);
                }
            }

            lines.add(getTodayLine(gender, dob, minAge, maxAge));
            lines.add(getPersonHeightLine(dob));

            List<AxisValue> bottomAxisValues = new ArrayList<>();
            for (int i = (int) Math.round(Math.floor(minAge)); i <= (int) Math.round(Math.ceil(maxAge)); i++) {
                AxisValue curValue = new AxisValue((float) i);
                curValue.setLabel(String.valueOf(i));
                bottomAxisValues.add(curValue);
            }

            LineChartData data = new LineChartData();
            data.setLines(lines);

            Axis bottomAxis = new Axis(bottomAxisValues);
            bottomAxis.setHasLines(true);
            bottomAxis.setHasTiltedLabels(false);
            bottomAxis.setName(getString(R.string.months));
            data.setAxisXBottom(bottomAxis);

            Axis leftAxis = new Axis();
            leftAxis.setHasLines(true);
            leftAxis.setHasTiltedLabels(false);
            leftAxis.setAutoGenerated(true);
            leftAxis.setName(getString(R.string.cm));
            data.setAxisYLeft(leftAxis);

            Axis topAxis = new Axis();
            topAxis.setHasTiltedLabels(false);
            topAxis.setAutoGenerated(false);
            data.setAxisXTop(topAxis);

            Axis rightAxis = new Axis();
            rightAxis.setHasTiltedLabels(false);
            rightAxis.setAutoGenerated(false);
            data.setAxisYRight(rightAxis);

            growthChart.setLineChartData(data);
        }
    }

    public Gender getGender() {
        return gender;
    }

    private void refreshPreviousHeightsTable(final View heightTabView, Gender gender, Date dob) {
        if (minRecordingDate == null || maxRecordingDate == null) {
            return;
        }

        TableLayout tableLayout = heightTabView.findViewById(R.id.heights_table);
        for (Height height : getHeights()) {
            TableRow dividerRow = new TableRow(heightTabView.getContext());
            View divider = new View(heightTabView.getContext());
            TableRow.LayoutParams params = (TableRow.LayoutParams) divider.getLayoutParams();
            if (params == null) params = new TableRow.LayoutParams();
            params.width = TableRow.LayoutParams.MATCH_PARENT;
            params.height = getResources().getDimensionPixelSize(R.dimen.table_divider_height);
            params.span = 3;
            divider.setLayoutParams(params);
            divider.setBackgroundColor(getResources().getColor(R.color.client_list_header_dark_grey));
            dividerRow.addView(divider);
            tableLayout.addView(dividerRow);

            TableRow curRow = new TableRow(heightTabView.getContext());

            TextView ageTextView = new TextView(heightTabView.getContext());
            ageTextView.setHeight(getResources().getDimensionPixelSize(R.dimen.table_contents_text_height));
            ageTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.table_contents_text_size));
            ageTextView.setText(DateUtil.getDuration(height.getDate().getTime() - dob.getTime()));
            ageTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            ageTextView.setTextColor(getResources().getColor(R.color.client_list_grey));
            curRow.addView(ageTextView);

            TextView heightTextView = new TextView(heightTabView.getContext());
            heightTextView.setHeight(getResources().getDimensionPixelSize(R.dimen.table_contents_text_height));
            heightTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.table_contents_text_size));
            heightTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            heightTextView.setText(
                    String.format("%s %s", String.valueOf(height.getCm()), getString(R.string.cm)));
            heightTextView.setTextColor(getResources().getColor(R.color.client_list_grey));
            curRow.addView(heightTextView);

            TextView zScoreTextView = new TextView(heightTabView.getContext());
            zScoreTextView.setHeight(getResources().getDimensionPixelSize(R.dimen.table_contents_text_height));
            zScoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.table_contents_text_size));
            zScoreTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            if (height.getDate().compareTo(maxRecordingDate.getTime()) > 0) {
                zScoreTextView.setText("");
            } else {
                double zScore = HeightZScore.calculate(gender, dob, height.getDate(), height.getCm());
                zScore = HeightZScore.roundOff(zScore);
                zScoreTextView.setTextColor(getResources().getColor(HeightZScore.getZScoreColor(zScore)));
                zScoreTextView.setText(String.valueOf(zScore));
            }
            curRow.addView(zScoreTextView);
            tableLayout.addView(curRow);
        }

        //Now set the expand button if items are too many
        final ScrollView heightsTableScrollView = heightTabView.findViewById(R.id.height_scroll_view);
        GrowthMonitoringUtils.getHeight(heightsTableScrollView, new ViewMeasureListener() {
            @Override
            public void onCompletedMeasuring(int height) {
                int childHeight = heightsTableScrollView.getChildAt(0).getMeasuredHeight();
                ImageButton scrollButton = heightTabView.findViewById(R.id.scroll_button);
                if (childHeight > height) {
                    scrollButton.setVisibility(View.VISIBLE);
                } else {
                    scrollButton.setVisibility(View.GONE);
                }
            }
        });
    }

    public String getDobString() {
        return dobString;
    }

    private Line getZScoreLine(Gender gender, double startAgeInMonths, double endAgeInMonths, double z, int color) {
        List<PointValue> values = new ArrayList<>();
        while (startAgeInMonths <= endAgeInMonths) {
            Double height = HeightZScore.reverse(gender, startAgeInMonths, z);

            if (height != null) {
                values.add(new PointValue((float) startAgeInMonths, (float) height.doubleValue()));
            }

            startAgeInMonths++;
        }

        Line line = new Line(values);
        line.setColor(color);
        line.setHasPoints(false);
        line.setHasLabels(true);
        line.setStrokeWidth(2);
        return line;
    }

    private Line getTodayLine(Gender gender, Date dob, double minAge, double maxAge) {
        double personsAgeInMonthsToday = HeightZScore.getAgeInMonths(dob, Calendar.getInstance().getTime());
        double maxY = getMaxY(maxAge, gender);
        double minY = getMinY(minAge, gender);

        if (personsAgeInMonthsToday > HeightZScore.MAX_REPRESENTED_AGE) {
            personsAgeInMonthsToday = HeightZScore.MAX_REPRESENTED_AGE;
        }

        List<PointValue> values = new ArrayList<>();
        values.add(new PointValue((float) personsAgeInMonthsToday, (float) minY));
        values.add(new PointValue((float) personsAgeInMonthsToday, (float) maxY));

        Line todayLine = new Line(values);
        todayLine.setColor(getResources().getColor(R.color.growth_today_color));
        todayLine.setHasPoints(false);
        todayLine.setHasLabels(false);
        todayLine.setStrokeWidth(4);

        return todayLine;
    }

    private Line getPersonHeightLine(Date dob) {
        if (minRecordingDate == null || maxRecordingDate == null) {
            return null;
        }

        List<PointValue> values = new ArrayList<>();
        for (Height curHeight : getHeights()) {
            if (isHeightOkToDisplay(minRecordingDate, maxRecordingDate, curHeight)) {
                Calendar recordingDate = Calendar.getInstance();
                recordingDate.setTime(curHeight.getDate());
                GrowthMonitoringUtils.standardiseCalendarDate(recordingDate);
                double x = HeightZScore.getAgeInMonths(dob, recordingDate.getTime());
                double y = curHeight.getCm();
                values.add(new PointValue((float) x, (float) y));
            }
        }

        Line line = new Line(values);
        line.setColor(getResources().getColor(android.R.color.black));
        line.setStrokeWidth(4);
        line.setHasPoints(true);
        line.setHasLabels(false);
        return line;
    }

    public List<Height> getHeights() {
        return heights;
    }

    private double getMaxY(double maxAge, Gender gender) {
        if (minRecordingDate == null || maxRecordingDate == null) {
            return 0d;
        }

        double maxY = HeightZScore.reverse(gender, maxAge, 3d);

        for (Height curHeight : getHeights()) {
            if (isHeightOkToDisplay(minRecordingDate, maxRecordingDate, curHeight) && curHeight.getCm() > maxY) {
                maxY = curHeight.getCm();
            }
        }

        return maxY;
    }

    private double getMinY(double minAge, Gender gender) {
        if (minRecordingDate == null || maxRecordingDate == null) {
            return 0d;
        }

        double minY = HeightZScore.reverse(gender, minAge, -3d);

        for (Height curHeight : getHeights()) {
            if (isHeightOkToDisplay(minRecordingDate, maxRecordingDate, curHeight) && curHeight.getCm() < minY) {
                minY = curHeight.getCm();
            }
        }

        return minY;
    }

    private boolean isHeightOkToDisplay(Calendar minRecordingDate, Calendar maxRecordingDate, Height height) {
        if (minRecordingDate != null && maxRecordingDate != null
                && minRecordingDate.getTimeInMillis() <= maxRecordingDate.getTimeInMillis()
                && height.getDate() != null) {
            Calendar recordingDate = Calendar.getInstance();
            recordingDate.setTime(height.getDate());
            GrowthMonitoringUtils.standardiseCalendarDate(recordingDate);

            return recordingDate.getTimeInMillis() >= minRecordingDate.getTimeInMillis()
                    && recordingDate.getTimeInMillis() <= maxRecordingDate.getTimeInMillis();
        }

        return false;
    }

    public void setHeights(List<Height> heights) {
        this.heights = heights;
    }

    public void setDobString(String dobString) {
        this.dobString = dobString;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}