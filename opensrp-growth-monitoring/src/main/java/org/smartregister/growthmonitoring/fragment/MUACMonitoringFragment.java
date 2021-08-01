package org.smartregister.growthmonitoring.fragment;

import android.app.DialogFragment;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.MUAC;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.listener.ViewMeasureListener;
import org.smartregister.growthmonitoring.util.GMConstants;
import org.smartregister.growthmonitoring.util.GrowthMonitoringUtils;
import org.smartregister.growthmonitoring.util.MUACUtils;
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

import static org.smartregister.growthmonitoring.fragment.GrowthDialogFragment.standardiseCalendarDate;

public class MUACMonitoringFragment extends DialogFragment {

    public static final String DIALOG_TAG = "MUACMonitoringFragment";
    public static final String WRAPPER_TAG = "tag";
    private static final String TAG = MUACMonitoringFragment.class.getName();
    List<MUAC> muacList;
    private Calendar maxHeighingDate = null;
    private Calendar minHeighingDate = null;
    private boolean isExpanded = false;
    private String dobString;
    private Gender gender;

    public static MUACMonitoringFragment createInstance(String dobString, Gender gender, List<MUAC> muacList) {
        MUACMonitoringFragment heightMonitoringFragment = new MUACMonitoringFragment();
        heightMonitoringFragment.setDobString(dobString);
        heightMonitoringFragment.setGender(gender);
        heightMonitoringFragment.setHeights(muacList);
        return heightMonitoringFragment;
    }

    public static String getTAG() {
        return TAG;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup heightTabView = (ViewGroup) inflater.inflate(R.layout.muac_monitoring_fragment, container, false);
        final ImageButton scrollButton = heightTabView.findViewById(R.id.scroll_button);

        Date dob = null;
        if (StringUtils.isNotBlank(getDobString())) {
            DateTime dateTime = new DateTime(getDobString());
            dob = dateTime.toDate();
            Calendar[] weighingDates = GrowthMonitoringUtils.getMinAndMaxRecordingDates(dob);
            minHeighingDate = weighingDates[0];
            maxHeighingDate = weighingDates[1];
        }

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
                                            getResources().getDimensionPixelSize(R.dimen.weight_table_height) + Height;
                                }
                            });
                    heightTabView.findViewById(R.id.height_growth_chart).setVisibility(View.GONE);
                    scrollButton.setImageResource(R.drawable.ic_icon_expand);
                } else {
                    isExpanded = false;
                    heightTabView.findViewById(R.id.height_growth_chart).setVisibility(View.VISIBLE);
                    heightTabView.findViewById(R.id.growth_dialog_height_table_layout).getLayoutParams().height =
                            getResources().getDimensionPixelSize(R.dimen.weight_table_height);
                    scrollButton.setImageResource(R.drawable.ic_icon_collapse);
                }
            }
        });

        try {
            refreshGrowthChart(heightTabView, getGender(), dob);
        } catch (Exception e) {
        }

        try {
            refreshPreviousHeightsTable(heightTabView, getGender(), dob);
        } catch (Exception e) {
        }
        return heightTabView;
    }

    public String getDobString() {
        return dobString;
    }

    private void refreshGrowthChart(View parent, Gender gender, Date dob) {
        if (minHeighingDate == null || maxHeighingDate == null) {
            return;
        }

        if (gender != Gender.UNKNOWN && dob != null) {
            LineChartView growthChart = parent.findViewById(R.id.height_growth_chart);
            double minAge = ZScore.getAgeInMonths(dob, minHeighingDate.getTime());
            double maxAge = minAge + GMConstants.GRAPH_MONTHS_TIMELINE;
            List<Line> lines = new ArrayList<>();
                    Line green = getZScoreLine(minAge, maxAge, 12.5,
                            getActivity().getResources().getColor(ZScore.getMuacColor(12.5)));

                    lines.add(green);
                Line yellow = getZScoreLine(minAge, maxAge, 11.5,
                        getActivity().getResources().getColor(ZScore.getMuacColor(11.5)));

                lines.add(yellow);
                Line red = getZScoreLine(minAge, maxAge, 11.4,
                        getActivity().getResources().getColor(ZScore.getMuacColor(11.4)));
                red.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
                lines.add(red);


            lines.add(getTodayLine(gender, dob, minAge, maxAge));
            lines.add(getPersonHeightLine(gender, dob));

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

    private void refreshPreviousHeightsTable(final View HeightTabView, Gender gender, Date dob) {
        if (minHeighingDate == null || maxHeighingDate == null) {
            return;
        }
        MUACUtils.refreshPreviousMuacTable(getActivity(),(TableLayout)HeightTabView.findViewById(R.id.muac_table),gender,dob,getHeights());

        //Now set the expand button if items are too many
        final ScrollView HeightsTableScrollView = HeightTabView.findViewById(R.id.height_scroll_view);
        GrowthMonitoringUtils.getHeight(HeightsTableScrollView, new ViewMeasureListener() {
            @Override
            public void onCompletedMeasuring(int height) {
                int childHeight = HeightsTableScrollView.getChildAt(0).getMeasuredHeight();
                ImageButton scrollButton = HeightTabView.findViewById(R.id.scroll_button);
                if (childHeight > height) {
                    scrollButton.setVisibility(View.VISIBLE);
                } else {
                    scrollButton.setVisibility(View.GONE);
                }
            }
        });
    }

    private Line getZScoreLine(double startAgeInMonths, double endAgeInMonths, double cm, int color) {
        List<PointValue> values = new ArrayList<>();
        while (startAgeInMonths <= endAgeInMonths) {

                values.add(new PointValue((float) startAgeInMonths, (float) cm));


            startAgeInMonths++;
        }

        Line line = new Line(values);
        line.setColor(color);
        line.setHasPoints(false);
        line.setHasLabels(true);
        line.setStrokeWidth(4);
        return line;
    }

    private Line getTodayLine(Gender gender, Date dob, double minAge, double maxAge) {
        double personsAgeInMonthsToday = ZScore.getAgeInMonths(dob, Calendar.getInstance().getTime());
        double maxY = getMaxY(dob, maxAge, gender);
        double minY = getMinY(dob, minAge, gender);

        if (personsAgeInMonthsToday > ZScore.MAX_REPRESENTED_AGE) {
            personsAgeInMonthsToday = ZScore.MAX_REPRESENTED_AGE;
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

    private Line getPersonHeightLine(Gender gender, Date dob) {
        if (minHeighingDate == null || maxHeighingDate == null) {
            return null;
        }

        List<PointValue> values = new ArrayList<>();
        for (MUAC curHeight : getHeights()) {
            if (isHeightOkToDisplay(minHeighingDate, maxHeighingDate, curHeight)) {
                Calendar weighingDate = Calendar.getInstance();
                weighingDate.setTime(curHeight.getDate());
                standardiseCalendarDate(weighingDate);
                double x = ZScore.getAgeInMonths(dob, weighingDate.getTime());
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

    public List<MUAC> getHeights() {
        return muacList;
    }

    private double getMaxY(Date dob, double maxAge, Gender gender) {
        if (minHeighingDate == null || maxHeighingDate == null) {
            return 0d;
        }

        double maxY = ZScore.reverse(gender, maxAge, 3d);

        for (MUAC curHeight : getHeights()) {
            if (isHeightOkToDisplay(minHeighingDate, maxHeighingDate, curHeight) && curHeight.getCm() > maxY) {
                maxY = curHeight.getCm();
            }
        }

        return maxY;
    }

    private double getMinY(Date dob, double minAge, Gender gender) {
        if (minHeighingDate == null || maxHeighingDate == null) {
            return 0d;
        }

        double minY = ZScore.reverse(gender, minAge, -3d);

        for (MUAC curHeight : getHeights()) {
            if (isHeightOkToDisplay(minHeighingDate, maxHeighingDate, curHeight) && curHeight.getCm() < minY) {
                minY = curHeight.getCm();
            }
        }

        return minY;
    }

    private boolean isHeightOkToDisplay(Calendar minHeighingDate, Calendar maxHeighingDate,
                                        MUAC Height) {
        if (minHeighingDate != null && maxHeighingDate != null
                && minHeighingDate.getTimeInMillis() <= maxHeighingDate.getTimeInMillis()
                && Height.getDate() != null) {
            Calendar weighingDate = Calendar.getInstance();
            weighingDate.setTime(Height.getDate());
            standardiseCalendarDate(weighingDate);

            return weighingDate.getTimeInMillis() >= minHeighingDate.getTimeInMillis()
                    && weighingDate.getTimeInMillis() <= maxHeighingDate.getTimeInMillis();
        }

        return false;
    }

    public void setHeights(List<MUAC> muacList) {
        this.muacList = muacList;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setDobString(String dobString) {
        this.dobString = dobString;
    }
}
