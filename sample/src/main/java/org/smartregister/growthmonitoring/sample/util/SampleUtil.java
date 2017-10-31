package org.smartregister.growthmonitoring.sample.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Photo;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.fragment.EditWeightDialogFragment;
import org.smartregister.growthmonitoring.fragment.RecordWeightDialogFragment;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.sample.R;
import org.smartregister.growthmonitoring.util.ImageUtils;
import org.smartregister.util.DateUtil;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.smartregister.util.Utils.getName;
import static org.smartregister.util.Utils.getValue;

/**
 * Created by keyman on 28/07/2017.
 */
public class SampleUtil {
    // Dummpy values, Can be changed manually
    public static final String ENTITY_ID = "1";
    public static final double BIRTH_WEIGHT = 3.8d;
    public static final String DOB_STRING = "2012-01-01T00:00:00.000Z";
    public static final String GENDER = (new Random()).nextBoolean() ? "male" : "female";

    public static void showWeightDialog(Activity context, View view, String tag) {
        WeightWrapper weightWrapper = view.getTag() != null ? (WeightWrapper) view.getTag() : new WeightWrapper();
        weightWrapper.setDateOfBirth(DOB_STRING);
        RecordWeightDialogFragment recordWeightDialogFragment = RecordWeightDialogFragment.newInstance(weightWrapper);
        recordWeightDialogFragment.show(initFragmentTransaction(context, tag), tag);

    }

    public static void showEditWeightDialog(Activity context, int i, String tag) {
        CommonPersonObjectClient childDetails = dummyDetatils();

        String firstName = Utils.getValue(childDetails.getColumnmaps(), "first_name", true);
        String lastName = Utils.getValue(childDetails.getColumnmaps(), "last_name", true);
        String childName = getName(firstName, lastName).trim();

        String gender = getValue(childDetails.getColumnmaps(), "gender", true);

        String zeirId = getValue(childDetails.getColumnmaps(), "zeir_id", false);
        String duration = "";
        String dobString = getValue(childDetails.getColumnmaps(), "dob", false);
        if (StringUtils.isNotBlank(dobString)) {
            DateTime dateTime = new DateTime(getValue(childDetails.getColumnmaps(), "dob", false));
            duration = DateUtil.getDuration(dateTime);
        }

        Photo photo = ImageUtils.profilePhotoByClient(childDetails);

        WeightWrapper weightWrapper = new WeightWrapper();
        weightWrapper.setId(childDetails.entityId());
        WeightRepository wp = GrowthMonitoringLibrary.getInstance().weightRepository();
        List<Weight> weightlist = wp.findLast5(childDetails.entityId());
        if (!weightlist.isEmpty()) {
            weightWrapper.setWeight(weightlist.get(i).getKg());
            weightWrapper.setUpdatedWeightDate(new DateTime(weightlist.get(i).getDate()), false);
            weightWrapper.setDbKey(weightlist.get(i).getId());
        }

        weightWrapper.setGender(gender);
        weightWrapper.setPatientName(childName);
        weightWrapper.setPatientNumber(zeirId);
        weightWrapper.setPatientAge(duration);
        weightWrapper.setPhoto(photo);
        weightWrapper.setDateOfBirth(DOB_STRING);
        weightWrapper.setPmtctStatus(getValue(childDetails.getColumnmaps(), "pmtct_status", false));

        EditWeightDialogFragment editWeightDialogFragment = EditWeightDialogFragment.newInstance(context, weightWrapper);
        editWeightDialogFragment.show(initFragmentTransaction(context, tag), tag);

    }

    public static FragmentTransaction initFragmentTransaction(Activity context, String tag) {
        FragmentTransaction ft = context.getFragmentManager().beginTransaction();
        Fragment prev = context.getFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        return ft;
    }

    public static void createWeightWidget(Activity context, View fragmentContainer, HashMap<Long, Pair<String, String>> last_five_weight_map, ArrayList<View.OnClickListener> listeners, ArrayList<Boolean> editenabled) {

        LinearLayout tableLayout = (LinearLayout) fragmentContainer.findViewById(R.id.weightvalues);
        tableLayout.removeAllViews();

        int i = 0;
        for (Map.Entry<Long, Pair<String, String>> entry : last_five_weight_map.entrySet()) {
            Pair<String, String> pair = entry.getValue();
            View view = createTableRowForWeight(context, tableLayout, pair.first, pair.second, editenabled.get(i), listeners.get(i));

            tableLayout.addView(view);
            i++;
        }
    }

    public static View createTableRowForWeight(Activity context, ViewGroup container, String labelString, String valueString, boolean editenabled, View.OnClickListener listener) {
        View rows = context.getLayoutInflater().inflate(R.layout.tablerows_weight, container, false);
        TextView label = (TextView) rows.findViewById(R.id.label);
        TextView value = (TextView) rows.findViewById(R.id.value);
        Button edit = (Button) rows.findViewById(R.id.edit);
        if (editenabled) {
            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(listener);
        } else {
            edit.setVisibility(View.INVISIBLE);
        }
        label.setText(labelString);
        value.setText(valueString);
        return rows;
    }

    public static CommonPersonObjectClient dummyDetatils() {
        HashMap<String, String> columnMap = new HashMap<String, String>();
        columnMap.put("first_name", "Test");
        columnMap.put("last_name", "Doe");
        columnMap.put("zeir_id", "1");
        columnMap.put("dob", DOB_STRING);
        columnMap.put("gender", GENDER);


        CommonPersonObjectClient personDetails = new CommonPersonObjectClient(ENTITY_ID, columnMap, "Test");
        personDetails.setColumnmaps(columnMap);

        return personDetails;
    }
}
