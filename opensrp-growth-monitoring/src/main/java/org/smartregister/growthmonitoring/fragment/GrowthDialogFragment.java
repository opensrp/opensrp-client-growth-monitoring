package org.smartregister.growthmonitoring.fragment;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.opensrp.api.constants.Gender;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.adapter.GrowthMonitoringTabsAdapter;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.util.AppProperties;
import org.smartregister.growthmonitoring.util.GrowthMonitoringConstants;
import org.smartregister.growthmonitoring.util.GrowthMonitoringUtils;
import org.smartregister.growthmonitoring.util.ImageUtils;
import org.smartregister.util.DateUtil;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@SuppressLint("ValidFragment")
public class GrowthDialogFragment extends DialogFragment {
    private CommonPersonObjectClient personDetails;
    private List<Weight> weights;
    private List<Height> heights;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView nameView;
    private TextView numberView;
    private ImageView profilePic;
    private TextView ageView;
    private TextView pmtctStatus;
    private Button done;
    private static Boolean hasProperty;
    private static Boolean monitorGrowth = false;

    public static GrowthDialogFragment newInstance(CommonPersonObjectClient personDetails, List<Weight> weights,
                                                   List<Height> heights) {

        hasProperty = GrowthMonitoringLibrary.getInstance().getAppProperties().hasProperty(AppProperties.KEY.MONITOR_GROWTH);
        if (hasProperty) {
            monitorGrowth = GrowthMonitoringLibrary.getInstance().getAppProperties().getPropertyBoolean(AppProperties.KEY.MONITOR_GROWTH);
        }

        GrowthDialogFragment growthDialogFragment = new GrowthDialogFragment();
        growthDialogFragment.setPersonDetails(personDetails);
        growthDialogFragment.setWeights(weights);
        if (hasProperty && monitorGrowth) {
            growthDialogFragment.setHeights(heights);
        }

        return growthDialogFragment;
    }

    public void setPersonDetails(CommonPersonObjectClient personDetails) {
        this.personDetails = personDetails;
    }

    @VisibleForTesting
    protected void sortWeights() {
        HashMap<Long, Weight> weightHashMap = new HashMap<>();
        for (Weight curWeight : getWeights()) {
            if (curWeight.getDate() != null && curWeight.getBaseEntityId() != null) {
                Calendar curCalendar = Calendar.getInstance();
                curCalendar.setTime(curWeight.getDate());
                GrowthMonitoringUtils.standardiseCalendarDate(curCalendar);

                if (!weightHashMap.containsKey(curCalendar.getTimeInMillis())) {
                    weightHashMap.put(curCalendar.getTimeInMillis(), curWeight);
                } else if (curWeight.getUpdatedAt() > weightHashMap.get(curCalendar.getTimeInMillis()).getUpdatedAt()) {
                    weightHashMap.put(curCalendar.getTimeInMillis(), curWeight);
                }
            }
        }

        List<Long> keys = new ArrayList<>(weightHashMap.keySet());
        Collections.sort(keys, Collections.<Long>reverseOrder());

        List<Weight> weightList = new ArrayList<>();
        for (Long curKey : keys) {
            weightList.add(weightHashMap.get(curKey));
        }

        setWeights(weightList);
    }

    @VisibleForTesting
    protected void sortHeights() {
        HashMap<Long, Height> heightHashMap = new HashMap<>();
        for (Height curHeight : getHeights()) {
            if (curHeight.getDate() != null && curHeight.getBaseEntityId() != null) {
                Calendar curCalendar = Calendar.getInstance();
                curCalendar.setTime(curHeight.getDate());
                GrowthMonitoringUtils.standardiseCalendarDate(curCalendar);

                if (!heightHashMap.containsKey(curCalendar.getTimeInMillis())) {
                    heightHashMap.put(curCalendar.getTimeInMillis(), curHeight);
                } else if (curHeight.getUpdatedAt() > heightHashMap.get(curCalendar.getTimeInMillis()).getUpdatedAt()) {
                    heightHashMap.put(curCalendar.getTimeInMillis(), curHeight);
                }
            }
        }

        List<Long> keys = new ArrayList<>(heightHashMap.keySet());
        Collections.sort(keys, Collections.<Long>reverseOrder());

        List<Height> heightList = new ArrayList<>();
        for (Long curKey : keys) {
            heightList.add(heightHashMap.get(curKey));
        }

        setHeights(heightList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        // without a handler, the window sizes itself correctly
        // but the keyboard does not show up
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Window window = null;
                if (getDialog() != null) {
                    window = getDialog().getWindow();
                }

                if (window == null) {
                    return;
                }

                Point size = new Point();

                Display display = window.getWindowManager().getDefaultDisplay();
                display.getSize(size);

                window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                window.setGravity(Gravity.CENTER);
            }
        });
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        String firstName = Utils.getValue(personDetails.getColumnmaps(), GrowthMonitoringConstants.FIRST_NAME, true);
        String lastName = Utils.getValue(personDetails.getColumnmaps(), GrowthMonitoringConstants.LAST_NAME, true);

        final ViewGroup dialogView = setUpViews(inflater, container);
        nameView.setText(Utils.getName(firstName, lastName));

        String personId = Utils.getValue(personDetails.getColumnmaps(), GrowthMonitoringConstants.ZEIR_ID, false);
        if (StringUtils.isNotBlank(personId)) {
            numberView.setText(String.format("%s: %s", getActivity().getString(R.string.label_zeir), personId));
        } else {
            numberView.setText("");
        }

        String genderString = Utils.getValue(personDetails, GrowthMonitoringConstants.GENDER, false);
        String baseEntityId = personDetails.entityId();
        profilePic.setTag(R.id.entity_id, baseEntityId);
        DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(baseEntityId, OpenSRPImageLoader
                .getStaticImageListener(profilePic, ImageUtils.profileImageResourceByGender(genderString),
                        ImageUtils.profileImageResourceByGender(genderString)));

        String formattedAge = "";
        String dobString = Utils.getValue(personDetails.getColumnmaps(), GrowthMonitoringConstants.DOB, false);
        if (!TextUtils.isEmpty(dobString)) {
            DateTime dateTime = new DateTime(dobString);
            Date dob = dateTime.toDate();
            long timeDiff = Calendar.getInstance().getTimeInMillis() - dob.getTime();

            if (timeDiff >= 0) {
                formattedAge = DateUtil.getDuration(timeDiff);
            }
        }

        if (StringUtils.isNotBlank(formattedAge)) {
            ageView.setText(String.format("%s: %s", getString(R.string.age), formattedAge));
        } else {
            ageView.setText("");
        }

        String pmtctStatusString =
                Utils.getValue(personDetails.getColumnmaps(), GrowthMonitoringConstants.PMTCT_STATUS, true);
        if (!TextUtils.isEmpty(pmtctStatusString)) {
            pmtctStatus.setText(pmtctStatusString);
        } else {
            pmtctStatus.setText("");
        }

        Gender gender = getGender(genderString);
        int genderStringRes = R.string.boys;
        if (gender == Gender.FEMALE) {
            genderStringRes = R.string.girls;
        }

        GrowthMonitoringTabsAdapter adapter = new GrowthMonitoringTabsAdapter(getChildFragmentManager());

        sortWeights();
        adapter.addFragment(String.format(getString(R.string.weight_for_age), getString(genderStringRes).toUpperCase()),
                WeightMonitoringFragment.createInstance(dobString, gender, getWeights()));

        if (hasProperty && monitorGrowth) {
            sortHeights();
            adapter.addFragment(String.format(getString(R.string.height_for_age), getString(genderStringRes).toUpperCase()),
                    HeightMonitoringFragment.createInstance(dobString, gender, getHeights()));
        }

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        doneAction();
        return dialogView;
    }

    @NotNull
    private ViewGroup setUpViews(LayoutInflater inflater, ViewGroup container) {
        final ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.growth_dialog_view, container, false);
        dialogView.setFilterTouchesWhenObscured(true);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        tabLayout = dialogView.findViewById(R.id.growth_tab_layout);
        viewPager = dialogView.findViewById(R.id.growth_view_pager);
        nameView = dialogView.findViewById(R.id.child_name);
        numberView = dialogView.findViewById(R.id.child_zeir_id);
        profilePic = dialogView.findViewById(R.id.child_profilepic);
        ageView = dialogView.findViewById(R.id.child_age);
        pmtctStatus = dialogView.findViewById(R.id.pmtct_status);
        done = dialogView.findViewById(R.id.done);
        return dialogView;
    }

    @NotNull
    private Gender getGender(String genderString) {
        Gender gender = Gender.UNKNOWN;
        if (genderString != null && genderString.equalsIgnoreCase(GrowthMonitoringConstants.FEMALE)) {
            gender = Gender.FEMALE;
        } else if (genderString != null && genderString.equalsIgnoreCase(GrowthMonitoringConstants.MALE)) {
            gender = Gender.MALE;
        }
        return gender;
    }

    private void doneAction() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrowthDialogFragment.this.dismiss();
            }
        });
    }

    public List<Weight> getWeights() {
        return weights;
    }

    public void setWeights(List<Weight> weights) {
        this.weights = weights;
    }

    public List<Height> getHeights() {
        return heights;
    }

    public void setHeights(List<Height> heights) {
        this.heights = heights;
    }
}