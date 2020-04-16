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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightZScore;
import org.smartregister.growthmonitoring.listener.ViewMeasureListener;
import org.smartregister.growthmonitoring.util.GrowthMonitoringConstants;
import org.smartregister.growthmonitoring.util.GrowthMonitoringUtils;
import org.smartregister.util.DateUtil;
import org.smartregister.view.customcontrols.CustomFontTextView;

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

public class WeightMonitoringFragment extends Fragment {
    private static final String TAG = WeightMonitoringFragment.class.getName();
    private List<Weight> weights;
    private Calendar maxWeighingDate = null;
    private Calendar minWeighingDate = null;
    private boolean isExpanded = false;
    private String dobString;
    private Gender gender;

    public static WeightMonitoringFragment createInstance(String dobString, Gender gender, List<Weight> weights) {
        WeightMonitoringFragment weightMonitoringFragment = new WeightMonitoringFragment();
        weightMonitoringFragment.setDobString(dobString);
        weightMonitoringFragment.setGender(gender);
        weightMonitoringFragment.setWeights(weights);
        return weightMonitoringFragment;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View weightTabView = inflater.inflate(R.layout.growth_monitoring_fragment, container, false);
        weightTabView.setFilterTouchesWhenObscured(true);
        final ImageButton scrollButton = weightTabView.findViewById(R.id.scroll_button);
        CustomFontTextView textMetricLabel = weightTabView.findViewById(R.id.metric_label);
        textMetricLabel.setText(getActivity().getString(R.string.weight));
        Date dob = getDate();
        scrollButtonClickAction(weightTabView, scrollButton);
        try {
            refreshGrowthChart(weightTabView, getGender(), dob);
            refreshPreviousWeightsTable(weightTabView, getGender(), dob);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return weightTabView;
    }

    @Nullable
    private Date getDate() {
        Date dob = null;
        if (StringUtils.isNotBlank(getDobString())) {
            DateTime dateTime = new DateTime(getDobString());
            dob = dateTime.toDate();
            Calendar[] weighingDates = GrowthMonitoringUtils.getMinAndMaxRecordingDates(dob);
            minWeighingDate = weighingDates[0];
            maxWeighingDate = weighingDates[1];
        }
        return dob;
    }

    private void scrollButtonClickAction(final View weightTabView, final ImageButton scrollButton) {
        scrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Prior implementation
                if (!isExpanded) {
                    isExpanded = true;
                    weightTabView.findViewById(R.id.growth_chart_layout).setVisibility(View.GONE);
                    scrollButton.setImageResource(R.drawable.ic_icon_expand);
                } else {
                    isExpanded = false;
                    weightTabView.findViewById(R.id.growth_chart_layout).setVisibility(View.VISIBLE);
                    scrollButton.setImageResource(R.drawable.ic_icon_collapse);
                }
            }
        });
    }

    private void refreshGrowthChart(View parent, Gender gender, Date dob) {
        if (minWeighingDate == null || maxWeighingDate == null) {
            return;
        }

        if (gender != Gender.UNKNOWN && dob != null && minWeighingDate != null) {
            LineChartView growthChart = parent.findViewById(R.id.growth_chart);
            double minAge = WeightZScore.getAgeInMonths(dob, minWeighingDate.getTime());
            double maxAge = minAge + GrowthMonitoringConstants.GRAPH_MONTHS_TIMELINE;
            List<Line> lines = new ArrayList<>();
            for (int z = -3; z <= 3; z++) {
                if (z != 1 && z != -1) {
                    Line curLine = getZScoreLine(gender, minAge, maxAge, z,
                            getActivity().getResources().getColor(WeightZScore.getZScoreColor(z)));
                    if (z == -3) {
                        curLine.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
                    }
                    lines.add(curLine);
                }
            }

            lines.add(getTodayLine(gender, dob, minAge, maxAge));
            lines.add(getPersonWeightLine(dob));

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
            leftAxis.setName(getString(R.string.kg));
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

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    private void refreshPreviousWeightsTable(final View weightTabView, Gender gender, Date dob) {
        if (minWeighingDate == null || maxWeighingDate == null) {
            return;
        }

        CustomFontTextView customFontTextView = weightTabView.findViewById(R.id.growth_table_header);
        customFontTextView.setText(R.string.previous_weights);

        TableLayout tableLayout = weightTabView.findViewById(R.id.growth_table);
        for (Weight weight : getWeights()) {
            TableRow dividerRow = new TableRow(weightTabView.getContext());
            View divider = new View(weightTabView.getContext());
            TableRow.LayoutParams params = (TableRow.LayoutParams) divider.getLayoutParams();
            if (params == null) params = new TableRow.LayoutParams();
            params.width = TableRow.LayoutParams.MATCH_PARENT;
            params.height = getResources().getDimensionPixelSize(R.dimen.table_divider_height);
            params.span = 3;
            divider.setLayoutParams(params);
            divider.setBackgroundColor(getResources().getColor(R.color.client_list_header_dark_grey));
            dividerRow.addView(divider);
            tableLayout.addView(dividerRow);

            TableRow curRow = new TableRow(weightTabView.getContext());

            TextView ageTextView = new TextView(weightTabView.getContext());
            ageTextView.setHeight(getResources().getDimensionPixelSize(R.dimen.table_contents_text_height));
            ageTextView
                    .setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_contents_text_size));
            ageTextView.setText(DateUtil.getDuration(weight.getDate().getTime() - dob.getTime()));
            ageTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            ageTextView.setTextColor(getResources().getColor(R.color.client_list_grey));
            curRow.addView(ageTextView);

            TextView weightTextView = new TextView(weightTabView.getContext());
            weightTextView.setHeight(getResources().getDimensionPixelSize(R.dimen.table_contents_text_height));
            weightTextView
                    .setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_contents_text_size));
            weightTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            weightTextView.setText(String.format("%s %s", String.valueOf(weight.getKg()), getString(R.string.kg)));
            weightTextView.setTextColor(getResources().getColor(R.color.client_list_grey));
            curRow.addView(weightTextView);

            TextView zScoreTextView = new TextView(weightTabView.getContext());
            zScoreTextView.setHeight(getResources().getDimensionPixelSize(R.dimen.table_contents_text_height));
            zScoreTextView
                    .setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_contents_text_size));
            zScoreTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            if (weight.getDate().compareTo(maxWeighingDate.getTime()) > 0) {
                zScoreTextView.setText("");
            } else {
                double zScore = WeightZScore.calculate(gender, dob, weight.getDate(), weight.getKg());
                zScore = WeightZScore.roundOff(zScore);
                zScoreTextView.setTextColor(getResources().getColor(WeightZScore.getZScoreColor(zScore)));
                zScoreTextView.setText(String.valueOf(zScore));
            }
            curRow.addView(zScoreTextView);
            tableLayout.addView(curRow);
        }

        //Now set the expand button if items are too many
        final ScrollView weightsTableScrollView = weightTabView.findViewById(R.id.growth_scroll_view);
        GrowthMonitoringUtils.getHeight(weightsTableScrollView, new ViewMeasureListener() {
            @Override
            public void onCompletedMeasuring(int height) {
                int childHeight = weightsTableScrollView.getChildAt(0).getMeasuredHeight();
                ImageButton scrollButton = weightTabView.findViewById(R.id.scroll_button);
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

    public void setDobString(String dobString) {
        this.dobString = dobString;
    }

    private Line getZScoreLine(Gender gender, double startAgeInMonths, double endAgeInMonths, double z, int color) {
        List<PointValue> values = new ArrayList<>();
        double ageInMonths = startAgeInMonths;
        while (ageInMonths <= endAgeInMonths) {
            Double weight = WeightZScore.reverse(gender, ageInMonths, z);

            if (weight != null) {
                values.add(new PointValue((float) ageInMonths, (float) weight.doubleValue()));
            }

            ageInMonths++;
        }

        return getLine(color, values, true, 2);
    }

    @NotNull
    private Line getLine(int color, List<PointValue> values, boolean hasLabels, int strokeWidth) {
        Line line = new Line(values);
        line.setColor(color);
        line.setHasPoints(false);
        line.setHasLabels(hasLabels);
        line.setStrokeWidth(strokeWidth);
        return line;
    }

    private Line getTodayLine(Gender gender, Date dob, double minAge, double maxAge) {
        double personsAgeInMonthsToday = WeightZScore.getAgeInMonths(dob, Calendar.getInstance().getTime());
        double maxY = getMaxY(maxAge, gender);
        double minY = getMinY(minAge, gender);

        if (personsAgeInMonthsToday > WeightZScore.MAX_REPRESENTED_AGE) {
            personsAgeInMonthsToday = WeightZScore.MAX_REPRESENTED_AGE;
        }

        List<PointValue> values = new ArrayList<>();
        values.add(new PointValue((float) personsAgeInMonthsToday, (float) minY));
        values.add(new PointValue((float) personsAgeInMonthsToday, (float) maxY));

        return getLine(getResources().getColor(R.color.growth_today_color), values, false, 4);
    }

    private Line getPersonWeightLine(Date dob) {
        if (minWeighingDate == null || maxWeighingDate == null) {
            return null;
        }

        List<PointValue> values = new ArrayList<>();
        for (Weight curWeight : getWeights()) {
            if (isWeightOkToDisplay(minWeighingDate, maxWeighingDate, curWeight)) {
                Calendar weighingDate = Calendar.getInstance();
                weighingDate.setTime(curWeight.getDate());
                GrowthMonitoringUtils.standardiseCalendarDate(weighingDate);
                double x = WeightZScore.getAgeInMonths(dob, weighingDate.getTime());
                double y = curWeight.getKg();
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

    public List<Weight> getWeights() {
        return weights;
    }

    public void setWeights(List<Weight> weights) {
        this.weights = weights;
    }

    private double getMaxY(double maxAge, Gender gender) {
        if (minWeighingDate == null || maxWeighingDate == null) {
            return 0d;
        }

        double maxY = WeightZScore.reverse(gender, maxAge, 3d);

        for (Weight curWeight : getWeights()) {
            if (isWeightOkToDisplay(minWeighingDate, maxWeighingDate, curWeight) && curWeight.getKg() > maxY) {
                maxY = curWeight.getKg();
            }
        }

        return maxY;
    }

    private double getMinY(double minAge, Gender gender) {
        if (minWeighingDate == null || maxWeighingDate == null) {
            return 0d;
        }

        double minY = WeightZScore.reverse(gender, minAge, -3d);

        for (Weight curWeight : getWeights()) {
            if (isWeightOkToDisplay(minWeighingDate, maxWeighingDate, curWeight) && curWeight.getKg() < minY) {
                minY = curWeight.getKg();
            }
        }

        return minY;
    }

    private boolean isWeightOkToDisplay(Calendar minWeighingDate, Calendar maxWeighingDate, Weight weight) {
        if (minWeighingDate != null && maxWeighingDate != null &&
                minWeighingDate.getTimeInMillis() <= maxWeighingDate.getTimeInMillis() && weight.getDate() != null) {
            Calendar weighingDate = Calendar.getInstance();
            weighingDate.setTime(weight.getDate());
            GrowthMonitoringUtils.standardiseCalendarDate(weighingDate);

            return weighingDate.getTimeInMillis() >= minWeighingDate.getTimeInMillis() &&
                    weighingDate.getTimeInMillis() <= maxWeighingDate.getTimeInMillis();
        }

        return false;
    }
}