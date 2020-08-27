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
import org.joda.time.DateTime;
import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightZScore;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightForHeightZScore;
import org.smartregister.growthmonitoring.domain.WeightHeight;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.listener.ViewMeasureListener;
import org.smartregister.growthmonitoring.util.GrowthMonitoringUtils;
import org.smartregister.util.DateUtil;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import timber.log.Timber;

public class WeightForHeightMonitoringFragment extends Fragment {
    private List<Weight> weights;
    private List<Height> heights;
    private boolean isExpanded = false;
    private Gender gender;
    private Date dob;
    private List<WeightHeight> weightHeights;
    private Height currentHeight;

    public static WeightForHeightMonitoringFragment createInstance(Gender gender, String dobString, List<Weight> weights, List<Height> heights) {
        WeightForHeightMonitoringFragment weightMonitoringFragment = new WeightForHeightMonitoringFragment();
        weightMonitoringFragment.setGender(gender);
        weightMonitoringFragment.setDob(dobString);
        weightMonitoringFragment.setWeights(weights);
        weightMonitoringFragment.setHeights(heights);
        weightMonitoringFragment.setWeightHeights(new ArrayList<WeightHeight>());
        if (heights.size() > 0) {
            weightMonitoringFragment.setCurrentHeight(heights.get(0));
        }
        return weightMonitoringFragment;
    }

    private void setGender(Gender gender) {
        this.gender = gender;
    }

    private void setDob(String dobString) {
        if (StringUtils.isNotBlank(dobString)) {
            DateTime dateTime = new DateTime(dobString);
            dob = dateTime.toDate();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View weightHeightTableView = inflater.inflate(R.layout.growth_monitoring_fragment, container, false);
        weightHeightTableView.setFilterTouchesWhenObscured(true);
        if (getActivity() != null) {
            final ImageButton scrollButton = weightHeightTableView.findViewById(R.id.scroll_button);
            CustomFontTextView weightMetric = weightHeightTableView.findViewById(R.id.column_one_metric);
            weightMetric.setText(getActivity().getString(R.string.weight));
            CustomFontTextView heightMetric = weightHeightTableView.findViewById(R.id.metric_label);
            heightMetric.setText(getActivity().getString(R.string.height));
            scrollButtonClickAction(weightHeightTableView, scrollButton);
            try {
                refreshGrowthChart(weightHeightTableView);
                refreshPreviousWeightHeightsTable(weightHeightTableView);
            } catch (Exception e) {
                Timber.e(Log.getStackTraceString(e));
            }
        }
        return weightHeightTableView;
    }

    private void refreshPreviousWeightHeightsTable(View weightHeightTableView) {
        CustomFontTextView weighHeightTableHeader = weightHeightTableView.findViewById(R.id.growth_table_header);
        weighHeightTableHeader.setText(R.string.previous_weight_heights);
        TableLayout tableLayout = weightHeightTableView.findViewById(R.id.growth_table);
        for (WeightHeight weightHeight : weightHeights) {
            TableRow dividerRow = new TableRow(weightHeightTableView.getContext());
            View divider = new View(weightHeightTableView.getContext());
            TableRow.LayoutParams params = (TableRow.LayoutParams) divider.getLayoutParams();
            if (params == null) params = new TableRow.LayoutParams();
            params.width = TableRow.LayoutParams.MATCH_PARENT;
            params.height = getResources().getDimensionPixelSize(R.dimen.table_divider_height);
            params.span = 3;
            divider.setLayoutParams(params);
            divider.setBackgroundColor(getResources().getColor(R.color.client_list_header_dark_grey));
            dividerRow.addView(divider);
            tableLayout.addView(dividerRow);

            TableRow curRow = new TableRow(weightHeightTableView.getContext());

            TextView weightTextView = new TextView(weightHeightTableView.getContext());
            weightTextView.setHeight(getResources().getDimensionPixelSize(R.dimen.table_contents_text_height));
            weightTextView
                    .setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_contents_text_size));
            weightTextView.setText(String.format("%s %s", String.valueOf(weightHeight.getWeight().getKg()), getString(R.string.kg)));
            weightTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            weightTextView.setTextColor(getResources().getColor(R.color.client_list_grey));
            curRow.addView(weightTextView);

            TextView heightTextView = new TextView(weightHeightTableView.getContext());
            heightTextView.setHeight(getResources().getDimensionPixelSize(R.dimen.table_contents_text_height));
            heightTextView
                    .setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_contents_text_size));
            heightTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            heightTextView.setText(String.format("%s %s", String.valueOf(weightHeight.getHeight().getCm()), getString(R.string.cm)));
            heightTextView.setTextColor(getResources().getColor(R.color.client_list_grey));
            curRow.addView(heightTextView);

            TextView zScoreTextView = new TextView(weightHeightTableView.getContext());
            zScoreTextView.setHeight(getResources().getDimensionPixelSize(R.dimen.table_contents_text_height));
            zScoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_contents_text_size));
            zScoreTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

            double zScore = WeightForHeightZScore.getZScore(gender.equals(Gender.MALE) ? "1" : "2", weightHeight.getWeight().getKg(), weightHeight.getHeight().getCm());
            zScore = HeightZScore.roundOff(zScore);
            zScoreTextView.setTextColor(getResources().getColor(HeightZScore.getZScoreColor(zScore)));
            zScoreTextView.setText(String.valueOf(zScore));

            curRow.addView(zScoreTextView);
            tableLayout.addView(curRow);
        }

        //Now set the expand button if items are too many
        final ScrollView heightsTableScrollView = weightHeightTableView.findViewById(R.id.growth_scroll_view);
        GrowthMonitoringUtils.getHeight(heightsTableScrollView, new ViewMeasureListener() {
            @Override
            public void onCompletedMeasuring(int height) {
                int childHeight = heightsTableScrollView.getChildAt(0).getMeasuredHeight();
                ImageButton scrollButton = heightsTableScrollView.findViewById(R.id.scroll_button);
                if (childHeight > height) {
                    scrollButton.setVisibility(View.VISIBLE);
                } else {
                    scrollButton.setVisibility(View.GONE);
                }
            }
        });

    }

    private void refreshGrowthChart(View weightHeightTableView) {
        if (dob == null) {
            return;
        }

        if (weightHeights.isEmpty()) {
            for (Weight weight : weights) {
                for (Height height : heights) {
                    if (currentHeight.getDate() != null && currentHeight.getDate().before(height.getDate())) {
                        currentHeight = height;
                    }
                    if (DateUtil.getDuration(height.getDate().getTime() - dob.getTime())
                            .equalsIgnoreCase(DateUtil.getDuration(weight.getDate().getTime() - dob.getTime()))) {
                        weightHeights.add(new WeightHeight(weight, height));
                        break;
                    }
                }
            }
        }

        LineChartView growthChart = weightHeightTableView.findViewById(R.id.growth_chart);
        LineChartData data = new LineChartData();
        List<Line> lines = new ArrayList<>();

        plotZScoreLines(lines);

        Collections.sort(weightHeights, Collections.<WeightHeight>reverseOrder());

        List<PointValue> pointValues = new ArrayList<>();
        if (!weightHeights.isEmpty()) {
            for (WeightHeight weightHeight : weightHeights) {
                if (weightHeight.getHeight().getCm() >= WeightForHeightZScore.MIN_REPRESENTED_HEIGHT) {
                    pointValues.add(new PointValue(weightHeight.getHeight().getCm(), weightHeight.getWeight().getKg()));
                    lines.add(getLine(getResources().getColor(android.R.color.black), pointValues, false, 4, true));
                }
            }
            lines.add(getTodayLine());
        }

        data.setLines(lines);

        modifyChartAxis(data);

        growthChart.setLineChartData(data);
    }

    private void modifyChartAxis(LineChartData data) {
        List<AxisValue> bottomAxisValues = new ArrayList<>();
        for (int i = (int) Math.round(Math.floor(WeightForHeightZScore.MIN_REPRESENTED_HEIGHT));
             i <= (int) Math.round(Math.ceil(WeightForHeightZScore.MAX_REPRESENTED_HEIGHT)); i++) {
            AxisValue curValue = new AxisValue((float) i);
            curValue.setLabel(String.valueOf(i));
            bottomAxisValues.add(curValue);
        }

        Axis bottomAxis = new Axis(bottomAxisValues);
        bottomAxis.setHasLines(true);
        bottomAxis.setHasTiltedLabels(false);
        bottomAxis.setName(getString(R.string.cm));
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
    }

    private void plotZScoreLines(List<Line> lines) {
        for (int currentZScore = -3; currentZScore <= 3; currentZScore++) {
            if (currentZScore != 1 && currentZScore != -1) {
                Line curLine = getZScoreLine(WeightForHeightZScore.MIN_REPRESENTED_HEIGHT,
                        WeightForHeightZScore.MAX_REPRESENTED_HEIGHT, currentZScore,
                        getResources().getColor(WeightForHeightZScore.getZScoreColor(currentZScore)));
                if (currentZScore == -3) {
                    curLine.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
                }
                lines.add(curLine);
            }
        }
    }

    private Line getZScoreLine(double minHeight, double maxHeight, double zScore, int color) {
        List<PointValue> values = new ArrayList<>();
        double zScoreHeight = minHeight;
        while (zScoreHeight <= maxHeight) {
            Double weight = getVariableWeight(zScoreHeight, zScore);
            if (weight != null) {
                values.add(new PointValue((float) zScoreHeight, (float) weight.doubleValue()));
            }
            zScoreHeight++;
        }

        return getLine(color, values, true, 2, false);
    }

    private Line getTodayLine() {
        Double minVariableWeight = getVariableWeight(WeightForHeightZScore.MIN_REPRESENTED_HEIGHT, -3);
        double minY = minVariableWeight != null ? minVariableWeight : 0.0d;
        Double maxVariableWeight = getVariableWeight(WeightForHeightZScore.MAX_REPRESENTED_HEIGHT, 3);
        double maxY = maxVariableWeight != null ? maxVariableWeight : 0.0d;

        if (currentHeight.getCm() > WeightForHeightZScore.MAX_REPRESENTED_HEIGHT) {
            currentHeight.setCm(WeightForHeightZScore.MAX_REPRESENTED_HEIGHT);
        }

        List<PointValue> values = new ArrayList<>();
        values.add(new PointValue(currentHeight.getCm(), (float) minY));
        values.add(new PointValue(currentHeight.getCm(), (float) maxY));

        return getLine(getResources().getColor(R.color.growth_today_color), values, false, 4, false);
    }

    private Double getVariableWeight(double height, double zScoreValue) {
        List<ZScore> zScoreValues = GrowthMonitoringLibrary.getInstance().weightForHeightRepository().findZScoreVariables(
                gender.equals(Gender.MALE) ? "1" : "2", height);
        if (zScoreValues.size() > 0) {
            ZScore currentZScore = zScoreValues.get(0);
            if (currentZScore.getL() != 0) {
                return currentZScore.getM() * Math.pow(Math.E, Math.log((zScoreValue * currentZScore.getL() * currentZScore.getS()) + 1) / currentZScore.getL());
            } else {
                return currentZScore.getM() * Math.pow(Math.E, zScoreValue * currentZScore.getS());
            }
        }
        return null;
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

    @NotNull
    private Line getLine(int color, List<PointValue> values, boolean hasLabels, int strokeWidth, boolean hasPoints) {
        Line line = new Line(values);
        line.setColor(color);
        line.setHasPoints(hasPoints);
        line.setHasLabels(hasLabels);
        line.setStrokeWidth(strokeWidth);
        return line;
    }


    public void setHeights(List<Height> heights) {
        this.heights = heights;
    }

    public void setWeights(List<Weight> weights) {
        this.weights = weights;
    }

    public void setWeightHeights(List<WeightHeight> weightHeights) {
        this.weightHeights = weightHeights;
    }

    public void setCurrentHeight(Height currentHeight) {
        this.currentHeight = currentHeight;
    }
}