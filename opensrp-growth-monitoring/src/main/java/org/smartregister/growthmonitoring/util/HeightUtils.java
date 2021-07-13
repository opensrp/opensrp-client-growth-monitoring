package org.smartregister.growthmonitoring.util;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.listener.ViewMeasureListener;
import org.smartregister.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HeightUtils {
    public static void refreshPreviousHeightsTable(Context context,final  TableLayout tableLayout, Gender gender, Date dob, List<Height> heightList, Calendar maxHeighingDate) {
        for (Height height : heightList) {
            TableRow dividerRow = new TableRow(context);
            View divider = new View(context);
            TableRow.LayoutParams params = (TableRow.LayoutParams) divider.getLayoutParams();
            if (params == null) params = new TableRow.LayoutParams();
            params.width = TableRow.LayoutParams.MATCH_PARENT;
            params.height = context.getResources().getDimensionPixelSize(R.dimen.weight_table_divider_height);
            params.span = 3;
            divider.setLayoutParams(params);
            divider.setBackgroundColor(context.getResources().getColor(R.color.client_list_header_dark_grey));
            dividerRow.addView(divider);
            tableLayout.addView(dividerRow);

            TableRow curRow = new TableRow(context);

            TextView ageTextView = new TextView(context);
            ageTextView.setHeight(context.getResources().getDimensionPixelSize(R.dimen.table_contents_text_height));
            ageTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    context.getResources().getDimension(R.dimen.weight_table_contents_text_size));
            ageTextView.setText(DateUtil.getDuration(height.getDate().getTime() - dob.getTime()));
            ageTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            ageTextView.setTextColor(context.getResources().getColor(R.color.client_list_grey));
            curRow.addView(ageTextView);

            TextView HeightTextView = new TextView(context);
            HeightTextView.setHeight(context.getResources().getDimensionPixelSize(R.dimen.table_contents_text_height));
            HeightTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    context.getResources().getDimension(R.dimen.weight_table_contents_text_size));
            HeightTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            HeightTextView.setText(
                    String.format("%s %s", String.valueOf(height.getCm()), context.getString(R.string.cm)));
            HeightTextView.setTextColor(context.getResources().getColor(R.color.client_list_grey));
            curRow.addView(HeightTextView);

            TextView zScoreTextView = new TextView(context);
            zScoreTextView.setHeight(context.getResources().getDimensionPixelSize(R.dimen.table_contents_text_height));
            zScoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    context.getResources().getDimension(R.dimen.weight_table_contents_text_size));
            zScoreTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            if (height.getDate().compareTo(maxHeighingDate.getTime()) > 0) {
                zScoreTextView.setText("");
            } else {
                double zScore = ZScore.calculate(gender, dob, height.getDate(), height.getCm());
                zScore = ZScore.roundOff(zScore);
                zScoreTextView.setTextColor(context.getResources().getColor(ZScore.getZScoreColor(zScore)));
                zScoreTextView.setText(String.valueOf(zScore));
            }
            curRow.addView(zScoreTextView);
            tableLayout.addView(curRow);
        }


    }
}
