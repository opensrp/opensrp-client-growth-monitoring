package org.smartregister.growthmonitoring.util;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;

import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.domain.WeightZScore;
import org.smartregister.growthmonitoring.listener.ViewMeasureListener;

import java.util.Calendar;
import java.util.Date;

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

            if (WeightZScore.getAgeInMonths(dob, maxGraphTime.getTime()) > WeightZScore.MAX_REPRESENTED_AGE) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dob);
                cal.add(Calendar.MONTH, (int) Math.round(WeightZScore.MAX_REPRESENTED_AGE));
                maxGraphTime = cal;
                minGraphTime = (Calendar) maxGraphTime.clone();
            }

            minGraphTime.add(Calendar.MONTH, -GrowthMonitoringConstants.GRAPH_MONTHS_TIMELINE);
            GrowthMonitoringUtils.standardiseCalendarDate(minGraphTime);
            GrowthMonitoringUtils.standardiseCalendarDate(maxGraphTime);

            if (minGraphTime.getTimeInMillis() < dobCalendar.getTimeInMillis()) {
                minGraphTime.setTime(dob);
                GrowthMonitoringUtils.standardiseCalendarDate(minGraphTime);

                maxGraphTime = (Calendar) minGraphTime.clone();
                maxGraphTime.add(Calendar.MONTH, GrowthMonitoringConstants.GRAPH_MONTHS_TIMELINE);
            }
        }

        return new Calendar[]{minGraphTime, maxGraphTime};
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
