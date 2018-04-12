package org.smartregister.growthmonitoring.sample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.fragment.GrowthDialogFragment;
import org.smartregister.growthmonitoring.listener.WeightActionListener;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.sample.util.SampleUtil;
import org.smartregister.growthmonitoring.util.WeightUtils;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.util.DateUtil;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WeightActionListener {
    private static final String TAG = MainActivity.class.getCanonicalName();

    private static final String DIALOG_TAG = "DIALOG_TAG_DUUH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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


        ImageButton growthChartButton = (ImageButton) findViewById(R.id.growth_chart_button);
        growthChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                Utils.startAsyncTask(new ShowGrowthChartTask(), null);
                v.setEnabled(true);
            }
        });

        refreshEditWeightLayout();
    }

    private class ShowGrowthChartTask extends AsyncTask<Void, Void, List<Weight>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Weight> doInBackground(Void... params) {
            WeightRepository weightRepository = GrowthMonitoringLibrary.getInstance().weightRepository();
            List<Weight> allWeights = weightRepository.findByEntityId(SampleUtil.ENTITY_ID);
            try {
                DateTime dateTime = new DateTime(SampleUtil.DOB_STRING);

                Weight weight = new Weight(-1l, null, (float) SampleUtil.BIRTH_WEIGHT, dateTime.toDate(), null, null, null, Calendar.getInstance().getTimeInMillis(), null, null, 0);
                allWeights.add(weight);
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

            return allWeights;
        }

        @Override
        protected void onPostExecute(List<Weight> allWeights) {
            super.onPostExecute(allWeights);

            if (allWeights == null || allWeights.isEmpty()) {
                Toast.makeText(MainActivity.this, "Record atleast one weight", Toast.LENGTH_LONG).show();
            } else {
                GrowthDialogFragment growthDialogFragment = GrowthDialogFragment.newInstance(SampleUtil.dummyDetatils(), allWeights);
                growthDialogFragment.show(SampleUtil.initFragmentTransaction(MainActivity.this, DIALOG_TAG), DIALOG_TAG);
            }
        }
    }


    @Override
    public void onWeightTaken(WeightWrapper tag) {
        if (tag != null) {
            final WeightRepository weightRepository = GrowthMonitoringLibrary.getInstance().weightRepository();
            Weight weight = new Weight();
            if (tag.getDbKey() != null) {
                weight = weightRepository.find(tag.getDbKey());
            }
            weight.setBaseEntityId(SampleUtil.ENTITY_ID);
            weight.setKg(tag.getWeight());
            weight.setDate(tag.getUpdatedWeightDate().toDate());
            weight.setAnmId("sample");
            weight.setLocationId("Kenya");

            Gender gender = Gender.UNKNOWN;

            String genderString = SampleUtil.GENDER;

            if (genderString != null && genderString.toLowerCase().equals("female")) {
                gender = Gender.FEMALE;
            } else if (genderString != null && genderString.toLowerCase().equals("male")) {
                gender = Gender.MALE;
            }

            Date dob = null;
            if (!TextUtils.isEmpty(SampleUtil.DOB_STRING)) {
                DateTime dateTime = new DateTime(SampleUtil.DOB_STRING);
                dob = dateTime.toDate();
            }

            if (dob != null && gender != Gender.UNKNOWN) {
                weightRepository.add(dob, gender, weight);
            } else {
                weightRepository.add(weight);
            }

            tag.setDbKey(weight.getId());

        }

        refreshEditWeightLayout();
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
                DateTime birthday = new DateTime(SampleUtil.DOB_STRING);
                Date birth = birthday.toDate();
                long timeDiff = weighttaken.getTime() - birth.getTime();
                Log.v("timeDiff is ", timeDiff + "");
                if (timeDiff >= 0) {
                    formattedAge = DateUtil.getDuration(timeDiff);
                    Log.v("age is ", formattedAge);
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
                        SampleUtil.showEditWeightDialog(MainActivity.this, finalI, DIALOG_TAG);
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

}
