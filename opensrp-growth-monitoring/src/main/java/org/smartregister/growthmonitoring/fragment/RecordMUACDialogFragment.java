package org.smartregister.growthmonitoring.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.MUACWrapper;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.listener.HeightActionListener;
import org.smartregister.growthmonitoring.listener.MUACActionListener;
import org.smartregister.growthmonitoring.util.ImageUtils;
import org.smartregister.util.DatePickerUtils;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@SuppressLint ("ValidFragment")
public class RecordMUACDialogFragment extends DialogFragment {
    public static final String HEIGHT_WRAPPER_TAG = "muacWrapper";
    public static final String DATE_OF_BIRTH_TAG = "dob";
    private MUACWrapper muacWrapper;
    private MUACActionListener muacActionListener;
    private Date dateOfBirth;

    public static RecordMUACDialogFragment newInstance(Date dateOfBirth, MUACWrapper heightWrapper) {

        MUACWrapper heightToSend;
        if (heightWrapper == null) {
            heightToSend = new MUACWrapper();
        } else {
            heightToSend = heightWrapper;
        }

        RecordMUACDialogFragment recordGrowthDialogFragment = new RecordMUACDialogFragment();

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
            muacActionListener = (MUACActionListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement GMActionListener");
        }
    }

    @Override
    public void onViewCreated(View dialogView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(dialogView, savedInstanceState);
        final EditText editHeight = dialogView.findViewById(R.id.edit_height);
        ((TextView)dialogView.findViewById(R.id.record_height)).setText("Record MUAC");
        editHeight.setTextColor(getResources().getColor(R.color.white));
        editHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if(!TextUtils.isEmpty(text)){
                    int color = ZScore.getMuacColor(Double.parseDouble(text));
                    editHeight.setBackgroundColor(ContextCompat.getColor(editHeight.getContext(),color));
                }

            }
        });
        final DatePicker earlierDatePicker = dialogView.findViewById(R.id.earlier_date_picker);
        earlierDatePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
        if (dateOfBirth != null) {
            earlierDatePicker.setMinDate(dateOfBirth.getTime());
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
                muacWrapper.setUpdatedHeightDate(new DateTime(calendar.getTime()), false);

                Float height = Float.valueOf(heightString);
                muacWrapper.setHeight(height);

                muacActionListener.onMUACTaken(muacWrapper);

            }
        });
        final Button weightTakenToday = dialogView.findViewById(R.id.height_taken_today);
        weightTakenToday.setText("MUAC taken today");
        weightTakenToday.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                String heightString = editHeight.getText().toString();
                if (StringUtils.isBlank(heightString) || Float.valueOf(heightString) <= 0f) {
                    return;
                }

                dismiss();

                Calendar calendar = Calendar.getInstance();
                muacWrapper.setUpdatedHeightDate(new DateTime(calendar.getTime()), true);
                Float height = Float.valueOf(heightString);
                muacWrapper.setHeight(height);
                muacActionListener.onMUACTaken(muacWrapper);

            }
        });

        final Button weightTakenEarlier = dialogView.findViewById(R.id.height_taken_earlier);
        weightTakenEarlier.setText("MUAC taken earlier");
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
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        Serializable heightBundleSerializable = bundle.getSerializable(HEIGHT_WRAPPER_TAG);

        if (heightBundleSerializable instanceof MUACWrapper) {
            muacWrapper = (MUACWrapper) heightBundleSerializable;
        }
        if (muacWrapper == null) {
            return null;
        }

        Serializable dateSerializable = bundle.getSerializable(DATE_OF_BIRTH_TAG);
        if (dateSerializable instanceof Date) {
            dateOfBirth = (Date) dateSerializable;
        }

        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.record_height_dialog_view, container, false);


        final EditText editHeight = dialogView.findViewById(R.id.edit_height);

//        if (muacWrapper.getHeight() != null) {
//            editHeight.setText(muacWrapper.getHeight().toString());
//            editHeight.setSelection(editHeight.getText().length());
//        }


        TextView nameView = dialogView.findViewById(R.id.child_name);
        nameView.setText(muacWrapper.getPatientName());

        TextView numberView = dialogView.findViewById(R.id.child_zeir_id);
        if (StringUtils.isNotBlank(muacWrapper.getPatientNumber())) {
            numberView.setText(String.format("%s: %s", getString(R.string.label_zeir), muacWrapper.getPatientNumber()));
        } else {
            numberView.setText("");
        }

        TextView ageView = dialogView.findViewById(R.id.child_age);
        if (StringUtils.isNotBlank(muacWrapper.getPatientAge())) {
            ageView.setText(String.format("%s: %s", getString(R.string.age), muacWrapper.getPatientAge()));
        } else {
            ageView.setText("");
        }

        TextView pmtctStatusView = dialogView.findViewById(R.id.pmtct_status);
        pmtctStatusView.setText(muacWrapper.getPmtctStatus());

        if (muacWrapper.getId() != null) {
            ImageView mImageView = dialogView.findViewById(R.id.child_profilepic);

            if (muacWrapper.getId() != null) {//image already in local storage most likey ):
                //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
                mImageView.setTag(R.id.entity_id, muacWrapper.getId());
                DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(muacWrapper.getId(),
                        OpenSRPImageLoader.getStaticImageListener(mImageView,
                                ImageUtils.profileImageResourceByGender(muacWrapper.getGender()),
                                ImageUtils.profileImageResourceByGender(muacWrapper.getGender())));
            }
        }





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
