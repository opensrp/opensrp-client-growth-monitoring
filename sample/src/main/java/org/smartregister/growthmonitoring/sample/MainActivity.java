package org.smartregister.growthmonitoring.sample;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.fragment.GrowthDialogFragment;
import org.smartregister.growthmonitoring.listener.GrowthMonitoringActionListener;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.sample.util.SampleUtil;
import org.smartregister.growthmonitoring.service.intent.HeightIntentService;
import org.smartregister.growthmonitoring.service.intent.WeightIntentService;
import org.smartregister.growthmonitoring.util.HeightUtils;
import org.smartregister.growthmonitoring.util.WeightUtils;
import org.smartregister.util.DateUtil;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements GrowthMonitoringActionListener {

    private static final String DIALOG_TAG = "DIALOG_TAG_DUUH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        View recordWeight = findViewById(R.id.record_weight);
        recordWeight.setClickable(true);
        recordWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                SampleUtil.showWeightDialog(MainActivity.this, view, DIALOG_TAG);
                view.setEnabled(true);
            }
        });


        ImageButton growthChartButton = findViewById(R.id.growth_chart_button);
        growthChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                Utils.startAsyncTask(new ShowGrowthChartTask(), null);
                v.setEnabled(true);
            }
        });

        refreshEditWeightLayout();
        refreshEditHeightLayout();
        startServices();
    }

    private void refreshEditWeightLayout() {
        View weightWidget = findViewById(R.id.weight_widget);

        LinkedHashMap<Long, Pair<String, String>> weightmap = new LinkedHashMap<>();
        ArrayList<Boolean> weighteditmode = new ArrayList<Boolean>();
        ArrayList<View.OnClickListener> listeners = new ArrayList<>();

        WeightRepository wp = GrowthMonitoringLibrary.getInstance().weightRepository();
        List<Weight> weightlist = wp.findLast5(SampleUtil.ENTITY_ID);

        for (int i = 0; i < weightlist.size(); i++) {
            Weight weight = weightlist.get(i);
            String formattedAge = "";
            if (weight.getDate() != null) {

                Date weighttaken = weight.getDate();
                DateTime birthday = new DateTime(SampleUtil.getDateOfBirth());
                Date birth = birthday.toDate();
                long timeDiff = weighttaken.getTime() - birth.getTime();
                Timber.tag("timeDiff is ").v(timeDiff + "");
                if (timeDiff >= 0) {
                    formattedAge = DateUtil.getDuration(timeDiff);
                    Timber.tag("age is ").v(formattedAge);
                }
            }
            if (!formattedAge.equalsIgnoreCase("0d")) {
                weightmap.put(weight.getId(), Pair.create(formattedAge, Utils.kgStringSuffix(weight.getKg())));

                boolean lessThanThreeMonthsEventCreated = WeightUtils.lessThanThreeMonths(weight);
                if (lessThanThreeMonthsEventCreated) {
                    weighteditmode.add(true);
                } else {
                    weighteditmode.add(false);
                }

                final int finalI = i;
                View.OnClickListener onclicklistener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        v.setEnabled(false);
                        SampleUtil.showEditGrowthMonitoringDialog(MainActivity.this, finalI, DIALOG_TAG);
                        v.setEnabled(true);
                    }
                };
                listeners.add(onclicklistener);
            }

        }
        if (weightmap.size() < 5) {
            weightmap.put(0l, Pair.create(DateUtil.getDuration(0), SampleUtil.BIRTH_WEIGHT + " kg"));
            weighteditmode.add(false);
            listeners.add(null);
        }

        if (weightmap.size() > 0) {
            SampleUtil.createWeightWidget(MainActivity.this, weightWidget, weightmap, listeners, weighteditmode);
        }
    }

    private void refreshEditHeightLayout() {
        View heightWidget = findViewById(R.id.height_widget);

        LinkedHashMap<Long, Pair<String, String>> heightmap = new LinkedHashMap<>();
        ArrayList<Boolean> heightEditMode = new ArrayList<>();
        ArrayList<View.OnClickListener> listeners = new ArrayList<>();

        HeightRepository wp = GrowthMonitoringLibrary.getInstance().heightRepository();
        List<Height> heightList = wp.findLast5(SampleUtil.ENTITY_ID);

        for (int i = 0; i < heightList.size(); i++) {
            Height height = heightList.get(i);
            String formattedAge = "";
            if (height.getDate() != null) {

                Date heightDate = height.getDate();
                DateTime birthday = new DateTime(SampleUtil.getDateOfBirth());
                Date birth = birthday.toDate();
                long timeDiff = heightDate.getTime() - birth.getTime();
                Timber.v("%s", timeDiff);
                if (timeDiff >= 0) {
                    formattedAge = DateUtil.getDuration(timeDiff);
                    Timber.v(formattedAge);
                }
            }
            if (!formattedAge.equalsIgnoreCase("0d")) {
                heightmap.put(height.getId(), Pair.create(formattedAge, Utils.cmStringSuffix(height.getCm())));

                boolean lessThanThreeMonthsEventCreated = HeightUtils.lessThanThreeMonths(height);
                heightEditMode.add(lessThanThreeMonthsEventCreated);

                final int finalI = i;
                View.OnClickListener onClickListener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        v.setEnabled(false);
                        SampleUtil.showEditGrowthMonitoringDialog(MainActivity.this, finalI, DIALOG_TAG);
                        v.setEnabled(true);
                    }
                };
                listeners.add(onClickListener);
            }

        }
        if (heightmap.size() < 5) {
            heightmap.put(0l, Pair.create(DateUtil.getDuration(0), SampleUtil.BIRTH_HEIGHT + " cm"));
            heightEditMode.add(false);
            listeners.add(null);
        }

        if (heightmap.size() > 0) {
            SampleUtil.createHeightWidget(MainActivity.this, heightWidget, heightmap, listeners, heightEditMode);
        }
    }

    public void startServices() {
        Intent vaccineIntent = new Intent(this, WeightIntentService.class);
        startService(vaccineIntent);

        Intent heightIntent = new Intent(this, HeightIntentService.class);
        startService(heightIntent);
    }

    @Override
    public void onGrowthRecorded(WeightWrapper weightWrapper, HeightWrapper heightWrapper) {
        updateWeightWrapper(weightWrapper);
        updateHeightWrapper(heightWrapper);

        refreshEditHeightLayout();
        refreshEditWeightLayout();
    }

    private void updateWeightWrapper(WeightWrapper weightWrapper) {
        if (weightWrapper != null) {
            final WeightRepository weightRepository = GrowthMonitoringLibrary.getInstance().weightRepository();
            Weight weight = new Weight();
            if (weightWrapper.getDbKey() != null) {
                weight = weightRepository.find(weightWrapper.getDbKey());
            }
            weight.setBaseEntityId(SampleUtil.ENTITY_ID);
            weight.setKg(weightWrapper.getWeight());
            weight.setDate(weightWrapper.getUpdatedWeightDate().toDate());
            weight.setAnmId("sample");
            weight.setLocationId("Kenya");
            weight.setTeam("testTeam");
            weight.setTeamId("testTeamId");
            weight.setChildLocationId("testChildLocationId");

            Gender gender = Gender.UNKNOWN;

            String genderString = SampleUtil.GENDER;

            if (genderString.toLowerCase().equals("female")) {
                gender = Gender.FEMALE;
            } else if (genderString.toLowerCase().equals("male")) {
                gender = Gender.MALE;
            }

            Date dob = SampleUtil.getDateOfBirth();


            if (dob != null && gender != Gender.UNKNOWN) {
                weightRepository.add(dob, gender, weight);
            } else {
                weightRepository.add(weight);
            }

            weightWrapper.setDbKey(weight.getId());

        }
    }

    private void updateHeightWrapper(HeightWrapper heightWrapper) {
        if (heightWrapper != null) {
            final HeightRepository heightRepository = GrowthMonitoringLibrary.getInstance().heightRepository();
            Height height = new Height();
            if (heightWrapper.getDbKey() != null) {
                height = heightRepository.find(heightWrapper.getDbKey());
            }
            height.setBaseEntityId(SampleUtil.ENTITY_ID);
            height.setCm(heightWrapper.getHeight());
            height.setDate(heightWrapper.getUpdatedHeightDate().toDate());
            height.setAnmId("sample");
            height.setLocationId("Kenya");
            height.setTeam("testTeam");
            height.setTeamId("testTeamId");
            height.setChildLocationId("testChildLocationId");

            Gender gender = Gender.UNKNOWN;

            String genderString = SampleUtil.GENDER;

            if (genderString.toLowerCase().equals("female")) {
                gender = Gender.FEMALE;
            } else if (genderString.toLowerCase().equals("male")) {
                gender = Gender.MALE;
            }

            Date dob = SampleUtil.getDateOfBirth();

            if (dob != null && gender != Gender.UNKNOWN) {
                heightRepository.add(dob, gender, height);
            } else {
                heightRepository.add(height);
            }

            heightWrapper.setDbKey(height.getId());

        }
    }

    private class ShowGrowthChartTask extends AsyncTask<Void, Void, Map<String, List>> {
        public static final String WEIGHT = "weight";
        public static final String HEIGHT = "height";

        @Override
        protected Map<String, List> doInBackground(Void... voids) {
            Map<String, List> growthMonitoring = new HashMap<>();
            growthMonitoring.put(WEIGHT, getWeights());
            growthMonitoring.put(HEIGHT, getHeights());

            return growthMonitoring;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Map<String, List> growthMonitoring) {
            super.onPostExecute(growthMonitoring);

            if (growthMonitoring == null || growthMonitoring.isEmpty()) {
                Toast.makeText(MainActivity.this, "Record at least one set of growth details (height, Weight)",
                        Toast.LENGTH_LONG).show();
            } else {
                List<Weight> weights = new ArrayList<>();
                List<Height> heights = new ArrayList<>();

                if (growthMonitoring.containsKey(WEIGHT)) {
                    weights = growthMonitoring.get(WEIGHT);
                }

                if (growthMonitoring.containsKey(HEIGHT)) {
                    heights = growthMonitoring.get(HEIGHT);
                }

                GrowthDialogFragment growthDialogFragment = GrowthDialogFragment
                        .newInstance(SampleUtil.dummydetails(), weights, heights);
                growthDialogFragment.show(SampleUtil.initFragmentTransaction(MainActivity.this, DIALOG_TAG), DIALOG_TAG);
            }
        }
    }

    private List<Weight> getWeights() {
        WeightRepository weightRepository = GrowthMonitoringLibrary.getInstance().weightRepository();
        List<Weight> allWeights = weightRepository.findByEntityId(SampleUtil.ENTITY_ID);
        try {
            DateTime dateTime = new DateTime(SampleUtil.getDateOfBirth());

            Weight weight = new Weight(-1l, null, (float) SampleUtil.BIRTH_WEIGHT, dateTime.toDate(), null, null, null,
                    Calendar.getInstance().getTimeInMillis(), null, null, 0);
            allWeights.add(weight);
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
        return allWeights;
    }

    private List<Height> getHeights() {
        HeightRepository heightRepository = GrowthMonitoringLibrary.getInstance().heightRepository();
        List<Height> allHeights = heightRepository.findByEntityId(SampleUtil.ENTITY_ID);
        try {
            DateTime dateTime = new DateTime(SampleUtil.getDateOfBirth());

            Height height = new Height(-1l, null, (float) SampleUtil.BIRTH_HEIGHT, dateTime.toDate(), null, null, null,
                    Calendar.getInstance().getTimeInMillis(), null, null, 0);
            allHeights.add(height);
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
        return allHeights;
    }

}
