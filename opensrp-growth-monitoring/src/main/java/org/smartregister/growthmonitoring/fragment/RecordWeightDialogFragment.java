package org.smartregister.growthmonitoring.fragment;

import static org.smartregister.growthmonitoring.BuildConfig.MAX_WEIGHT;
import static org.smartregister.growthmonitoring.BuildConfig.MIN_WEIGHT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.listener.WeightActionListener;
import org.smartregister.growthmonitoring.util.ImageUtils;
import org.smartregister.util.DatePickerUtils;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("ValidFragment")
public class RecordWeightDialogFragment extends DialogFragment {
    private WeightWrapper tag;
    private WeightActionListener listener;
    private Date dateOfBirth;

    public static final String WRAPPER_TAG = "tag";
    public static final String DATE_OF_BIRTH_TAG = "dob";

    public static RecordWeightDialogFragment newInstance(
            Date dateOfBirth, WeightWrapper tag) {

        WeightWrapper tagToSend;
        if (tag == null) {
            tagToSend = new WeightWrapper();
        } else {
            tagToSend = tag;
        }

        RecordWeightDialogFragment recordWeightDialogFragment = new RecordWeightDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(DATE_OF_BIRTH_TAG, dateOfBirth);
        args.putSerializable(WRAPPER_TAG, tagToSend);
        recordWeightDialogFragment.setArguments(args);

        return recordWeightDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        Serializable serializable = bundle.getSerializable(WRAPPER_TAG);
        if (serializable != null && serializable instanceof WeightWrapper) {
            tag = (WeightWrapper) serializable;
        }

        if (tag == null) {
            return null;
        }

        Serializable dateSerializable = bundle.getSerializable(DATE_OF_BIRTH_TAG);
        if (dateSerializable != null && dateSerializable instanceof Date) {
            dateOfBirth = (Date) dateSerializable;
        }

        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.record_weight_dialog_view, container, false);

        final EditText editWeight = (EditText) dialogView.findViewById(R.id.edit_weight);
        editWeight.setTextColor(getResources().getColor(R.color.white));
        editWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    String text = editable.toString();
                    if(!TextUtils.isEmpty(text)){
                        if(Float.parseFloat(text)>MAX_WEIGHT){
                            editWeight.setError(getString(R.string.weight_max_msg));
                            return;
                        }
                        if(Float.parseFloat(text)<MIN_WEIGHT){
                            editWeight.setError(getString(R.string.weight_less_msg));
                            return;
                        }
                        Gender gender = Gender.MALE;
                        if (tag.getGender() != null && tag.getGender().equalsIgnoreCase("female")) {
                            gender = Gender.FEMALE;
                        } else if (tag.getGender() != null && tag.getGender().equalsIgnoreCase("male")) {
                            gender = Gender.MALE;
                        }
                        double d = Double.parseDouble(text);
                        Log.v("WEIGHT_DIALOG","zScore>>"+text);
                        double zScore = ZScore.calculate(gender, dateOfBirth, new Date(), d);
                        zScore = ZScore.roundOff(zScore);
                        int color = ZScore.getZScoreColor(zScore);
                        Log.v("WEIGHT_DIALOG","value>>"+text+":zScore:"+zScore+":color:>>"+color);

                        editWeight.setBackgroundColor(ContextCompat.getColor(editWeight.getContext(),color));
                    }
                }catch (NumberFormatException np){

                }


            }
        });
//        if (tag.getWeight() != null) {
//            editWeight.setText(tag.getWeight().toString());
//            editWeight.setSelection(editWeight.getText().length());
//        }
        //formatEditWeightView(editWeight, "");

        final DatePicker earlierDatePicker = (DatePicker) dialogView.findViewById(R.id.earlier_date_picker);
        earlierDatePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
        if (dateOfBirth != null) {
            earlierDatePicker.setMinDate(dateOfBirth.getTime());
        }

        TextView nameView = (TextView) dialogView.findViewById(R.id.child_name);
        nameView.setText(tag.getPatientName());

        TextView numberView = (TextView) dialogView.findViewById(R.id.child_zeir_id);
        if (StringUtils.isNotBlank(tag.getPatientNumber())) {
            numberView.setText(String.format("%s: %s", getString(R.string.label_zeir), tag.getPatientNumber()));
        } else {
            numberView.setText("");
        }

        TextView ageView = (TextView) dialogView.findViewById(R.id.child_age);
        if (StringUtils.isNotBlank(tag.getPatientAge())) {
            ageView.setText(String.format("%s: %s", getString(R.string.age), tag.getPatientAge()));
        } else {
            ageView.setText("");
        }

        TextView pmtctStatusView = (TextView) dialogView.findViewById(R.id.pmtct_status);
        pmtctStatusView.setText(tag.getPmtctStatus());

        if (tag.getId() != null) {
            ImageView mImageView = (ImageView) dialogView.findViewById(R.id.child_profilepic);

            if (tag.getId() != null) {//image already in local storage most likey ):
                //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
                mImageView.setTag(R.id.entity_id, tag.getId());
                DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(tag.getId(),
                        OpenSRPImageLoader.getStaticImageListener((ImageView) mImageView,
                                ImageUtils.profileImageResourceByGender(tag.getGender()),
                                ImageUtils.profileImageResourceByGender(tag.getGender())));
            }
        }

        final Button set = (Button) dialogView.findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weightString = editWeight.getText().toString();
                if (StringUtils.isBlank(weightString) || Float.valueOf(weightString) <= 0f) {
                    return;
                }
                if(Float.parseFloat(weightString)>MAX_WEIGHT){
                    editWeight.setError(getString(R.string.weight_max_msg));
                    return;
                }
                if(Float.parseFloat(weightString)<MIN_WEIGHT){
                    editWeight.setError(getString(R.string.weight_less_msg));
                    return;
                }
                dismiss();

                int day = earlierDatePicker.getDayOfMonth();
                int month = earlierDatePicker.getMonth();
                int year = earlierDatePicker.getYear();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                tag.setUpdatedWeightDate(new DateTime(calendar.getTime()), false);

                Float weight = Float.valueOf(weightString);
                tag.setWeight(weight);

                listener.onWeightTaken(tag);

            }
        });

        final Button weightTakenToday = (Button) dialogView.findViewById(R.id.weight_taken_today);
        weightTakenToday.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                String weightString = editWeight.getText().toString();
                if (StringUtils.isBlank(weightString) || Float.valueOf(weightString) <= 0f) {
                    return;
                }
                if(Float.parseFloat(weightString)>MAX_WEIGHT){
                    editWeight.setError(getString(R.string.weight_max_msg));
                    return;
                }
                if(Float.parseFloat(weightString)<MIN_WEIGHT){
                    editWeight.setError(getString(R.string.weight_less_msg));
                    return;
                }

                dismiss();

                Calendar calendar = Calendar.getInstance();
                tag.setUpdatedWeightDate(new DateTime(calendar.getTime()), true);

                Float weight = Float.valueOf(weightString);
                tag.setWeight(weight);

                listener.onWeightTaken(tag);

            }
        });

        final Button weightTakenEarlier = (Button) dialogView.findViewById(R.id.weight_taken_earlier);
        weightTakenEarlier.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                weightTakenEarlier.setVisibility(View.GONE);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                earlierDatePicker.setVisibility(View.VISIBLE);
                earlierDatePicker.requestFocus();
                set.setVisibility(View.VISIBLE);

                DatePickerUtils.themeDatePicker(earlierDatePicker, new char[]{'d', 'm', 'y'});
            }
        });

        Button cancel = (Button) dialogView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return dialogView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the WeightActionListener so we can send events to the host
            listener = (WeightActionListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement WeightActionListener");
        }
    }

    private void formatEditWeightView(EditText editWeight, String userInput) {
        StringBuilder stringBuilder = new StringBuilder(userInput);

        while (stringBuilder.length() > 2 && stringBuilder.charAt(0) == '0') {
            stringBuilder.deleteCharAt(0);
        }
        while (stringBuilder.length() < 2) {
            stringBuilder.insert(0, '0');
        }
        stringBuilder.insert(stringBuilder.length() - 1, '.');

        editWeight.setText(stringBuilder.toString());
        // keeps the cursor always to the right
        Selection.setSelection(editWeight.getText(), stringBuilder.toString().length());
    }

    @Override
    public void onStart() {
        super.onStart();
        // without a handler, the window size itself correctly
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
                window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

            }

        });

    }
}
