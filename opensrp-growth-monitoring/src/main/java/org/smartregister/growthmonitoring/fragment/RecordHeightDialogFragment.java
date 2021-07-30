package org.smartregister.growthmonitoring.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Selection;
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
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.listener.HeightActionListener;
import org.smartregister.growthmonitoring.util.ImageUtils;
import org.smartregister.util.DatePickerUtils;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@SuppressLint ("ValidFragment")
public class RecordHeightDialogFragment extends DialogFragment {
    public static final String HEIGHT_WRAPPER_TAG = "heightWrapper";
    public static final String DATE_OF_BIRTH_TAG = "dob";
    private HeightWrapper heightWrapper;
    private HeightActionListener heightActionListener;
    private Date dateOfBirth;

    public static RecordHeightDialogFragment newInstance(Date dateOfBirth,HeightWrapper heightWrapper) {

        HeightWrapper heightToSend;
        if (heightWrapper == null) {
            heightToSend = new HeightWrapper();
        } else {
            heightToSend = heightWrapper;
        }

        RecordHeightDialogFragment recordGrowthDialogFragment = new RecordHeightDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(DATE_OF_BIRTH_TAG, dateOfBirth);
        args.putSerializable(HEIGHT_WRAPPER_TAG, heightToSend);
        recordGrowthDialogFragment.setArguments(args);

        return recordGrowthDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the GMActionListener so we can send events to the host
            heightActionListener = (HeightActionListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement GMActionListener");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        Serializable heightBundleSerializable = bundle.getSerializable(HEIGHT_WRAPPER_TAG);

        if (heightBundleSerializable instanceof HeightWrapper) {
            heightWrapper = (HeightWrapper) heightBundleSerializable;
        }
        if (heightWrapper == null) {
            return null;
        }

        Serializable dateSerializable = bundle.getSerializable(DATE_OF_BIRTH_TAG);
        if (dateSerializable instanceof Date) {
            dateOfBirth = (Date) dateSerializable;
        }

        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.record_height_dialog_view, container, false);


        final EditText editHeight = dialogView.findViewById(R.id.edit_height);
//        if (heightWrapper.getHeight() != null) {
//            editHeight.setText(heightWrapper.getHeight().toString());
//            editHeight.setSelection(editHeight.getText().length());
//        }

        final DatePicker earlierDatePicker = dialogView.findViewById(R.id.earlier_date_picker);
        earlierDatePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
        if (dateOfBirth != null) {
            earlierDatePicker.setMinDate(dateOfBirth.getTime());
        }

        TextView nameView = dialogView.findViewById(R.id.child_name);
        nameView.setText(heightWrapper.getPatientName());

        TextView numberView = dialogView.findViewById(R.id.child_zeir_id);
        if (StringUtils.isNotBlank(heightWrapper.getPatientNumber())) {
            numberView.setText(String.format("%s: %s", getString(R.string.label_zeir), heightWrapper.getPatientNumber()));
        } else {
            numberView.setText("");
        }

        TextView ageView = dialogView.findViewById(R.id.child_age);
        if (StringUtils.isNotBlank(heightWrapper.getPatientAge())) {
            ageView.setText(String.format("%s: %s", getString(R.string.age), heightWrapper.getPatientAge()));
        } else {
            ageView.setText("");
        }

        TextView pmtctStatusView = dialogView.findViewById(R.id.pmtct_status);
        pmtctStatusView.setText(heightWrapper.getPmtctStatus());

        if (heightWrapper.getId() != null) {
            ImageView mImageView = dialogView.findViewById(R.id.child_profilepic);

            if (heightWrapper.getId() != null) {//image already in local storage most likey ):
                //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
                mImageView.setTag(R.id.entity_id, heightWrapper.getId());
                DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(heightWrapper.getId(),
                        OpenSRPImageLoader.getStaticImageListener(mImageView,
                                ImageUtils.profileImageResourceByGender(heightWrapper.getGender()),
                                ImageUtils.profileImageResourceByGender(heightWrapper.getGender())));
            }
        }

        final Button set = dialogView.findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String heightString = editHeight.getText().toString();
                if (StringUtils.isBlank(heightString) || Float.valueOf(heightString) <= 0f) {
                    return;
                }

                dismiss();

                int day = earlierDatePicker.getDayOfMonth();
                int month = earlierDatePicker.getMonth();
                int year = earlierDatePicker.getYear();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                heightWrapper.setUpdatedHeightDate(new DateTime(calendar.getTime()), false);

                Float height = Float.valueOf(heightString);
                heightWrapper.setHeight(height);

                heightActionListener.onHeightTaken(heightWrapper);

            }
        });

        final Button weightTakenToday = dialogView.findViewById(R.id.height_taken_today);
        weightTakenToday.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                String heightString = editHeight.getText().toString();
                if (StringUtils.isBlank(heightString) || Float.valueOf(heightString) <= 0f) {
                    return;
                }

                dismiss();

                Calendar calendar = Calendar.getInstance();
                heightWrapper.setUpdatedHeightDate(new DateTime(calendar.getTime()), true);
                Float height = Float.valueOf(heightString);
                heightWrapper.setHeight(height);
                heightActionListener.onHeightTaken(heightWrapper);

            }
        });

        final Button weightTakenEarlier = dialogView.findViewById(R.id.height_taken_earlier);
        weightTakenEarlier.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                weightTakenEarlier.setVisibility(View.GONE);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                earlierDatePicker.setVisibility(View.VISIBLE);
                earlierDatePicker.requestFocus();
                set.setVisibility(View.VISIBLE);

                DatePickerUtils.themeDatePicker(earlierDatePicker, new char[] {'d', 'm', 'y'});
            }
        });

        Button cancel = dialogView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return dialogView;
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
}