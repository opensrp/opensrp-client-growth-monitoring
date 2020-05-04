package org.smartregister.growthmonitoring.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vijay.jsonwizard.utils.NativeFormsProperties;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.listener.GrowthMonitoringActionListener;
import org.smartregister.growthmonitoring.util.AppProperties;
import org.smartregister.growthmonitoring.util.GrowthMonitoringUtils;
import org.smartregister.growthmonitoring.util.ImageUtils;
import org.smartregister.util.DatePickerUtils;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("ValidFragment")
public class RecordGrowthDialogFragment extends DialogFragment {
    public static final String WEIGHT_WRAPPER_TAG = "weightWrapper";
    public static final String HEIGHT_WRAPPER_TAG = "heightWrapper";
    public static final String DATE_OF_BIRTH_TAG = "dob";
    private WeightWrapper weightWrapper;
    private HeightWrapper heightWrapper;
    private GrowthMonitoringActionListener GrowthMonitoringActionListener;
    private Date dateOfBirth;
    private EditText editWeight;
    private EditText editHeight;
    private DatePicker earlierDatePicker;
    private TextView nameView;
    private TextView numberView;
    private TextView ageView;
    private TextView pmtctStatusView;
    private ImageView mImageView;
    private Button growthTakenToday;
    private Button cancel;
    private Button growthRecordTakenEarlier;
    private Button set;
    private LinearLayout heightEntryLayout;
    private CustomFontTextView recordHeight;
    private static final boolean monitorGrowth = GrowthMonitoringLibrary.getInstance().getAppProperties().isTrue(AppProperties.KEY.MONITOR_GROWTH);
    private static final boolean isNumericDatePicker = GrowthMonitoringLibrary.getInstance().getAppProperties().isTrue(NativeFormsProperties.KEY.WIDGET_DATEPICKER_IS_NUMERIC);

    public static RecordGrowthDialogFragment newInstance(Date dateOfBirth, WeightWrapper weightWrapper, HeightWrapper heightWrapper) {

        WeightWrapper weightToSend;
        if (weightWrapper == null) {
            weightToSend = new WeightWrapper();
        } else {
            weightToSend = weightWrapper;
        }

        HeightWrapper heightToSend = null;
        if (monitorGrowth) {
            if (heightWrapper == null) {
                heightToSend = new HeightWrapper();
            } else {
                heightToSend = heightWrapper;
            }
        }

        RecordGrowthDialogFragment recordGrowthDialogFragment = new RecordGrowthDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(DATE_OF_BIRTH_TAG, dateOfBirth);
        args.putSerializable(WEIGHT_WRAPPER_TAG, weightToSend);
        if (monitorGrowth) {
            args.putSerializable(HEIGHT_WRAPPER_TAG, heightToSend);
        }
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
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the GrowthMonitoringActionListener so we can send events to the host
            GrowthMonitoringActionListener = (GrowthMonitoringActionListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement GrowthMonitoringActionListener");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        if (getBundle()) return null;
        ViewGroup dialogView = setUpViews(inflater, container);
        setVisibility();
        setWeight();
        setHeight();

        if (dateOfBirth != null) {
            long dobTime = GrowthMonitoringUtils.cleanTimestamp(dateOfBirth.getTime());
            earlierDatePicker.setMinDate(dobTime);
        }

        long today = GrowthMonitoringUtils.cleanTimestamp(Calendar.getInstance().getTimeInMillis());
        earlierDatePicker.setMaxDate(today);

        nameView.setText(weightWrapper.getPatientName());

        if (StringUtils.isNotBlank(weightWrapper.getPatientNumber())) {
            numberView.setText(String.format("%s: %s", getString(R.string.label_zeir), weightWrapper.getPatientNumber()));
        } else {
            numberView.setText("");
        }

        if (StringUtils.isNotBlank(weightWrapper.getPatientAge())) {
            ageView.setText(String.format("%s: %s", getString(R.string.age), weightWrapper.getPatientAge()));
        } else {
            ageView.setText("");
        }

        pmtctStatusView.setText(weightWrapper.getPmtctStatus());

        setClientImage();
        weightTakenTodayButtonAction();
        setButtonAction();
        growthEarlierAction();
        cancelAction();

        return dialogView;
    }

    private void setHeight() {
        if (monitorGrowth && heightWrapper.getHeight() != null) {
            editHeight.setText(heightWrapper.getHeight().toString());
            editHeight.setSelection(editHeight.getText().length());
        }
    }

    private void setWeight() {
        if (weightWrapper.getWeight() != null) {
            editWeight.setText(weightWrapper.getWeight().toString());
            editWeight.setSelection(editWeight.getText().length());
        }
    }

    private void setVisibility() {
        if (monitorGrowth) {
            recordHeight.setVisibility(View.VISIBLE);
            heightEntryLayout.setVisibility(View.VISIBLE);
        }
    }

    private boolean getBundle() {
        Bundle bundle = getArguments();
        if (getWeightBundle(bundle)) return true;
        if (monitorGrowth && getHeightBundle(bundle)) {
            return true;
        }

        getDateBundle(bundle);
        return false;
    }

    @NotNull
    private ViewGroup setUpViews(LayoutInflater inflater, ViewGroup container) {

        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.record_growth_dialog_view, container, false);
        dialogView.setFilterTouchesWhenObscured(true);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        editWeight = dialogView.findViewById(R.id.edit_weight);
        earlierDatePicker = dialogView.findViewById(isNumericDatePicker ? R.id.earlier_date_picker_numeric : R.id.earlier_date_picker);
        nameView = dialogView.findViewById(R.id.child_name);
        numberView = dialogView.findViewById(R.id.child_zeir_id);
        ageView = dialogView.findViewById(R.id.child_age);
        pmtctStatusView = dialogView.findViewById(R.id.pmtct_status);
        mImageView = dialogView.findViewById(R.id.child_profilepic);
        growthTakenToday = dialogView.findViewById(R.id.weight_taken_today);
        cancel = dialogView.findViewById(R.id.cancel);
        set = dialogView.findViewById(R.id.set);
        growthRecordTakenEarlier = dialogView.findViewById(R.id.weight_taken_earlier);
        heightEntryLayout = dialogView.findViewById(R.id.record_height_layout);
        editHeight = dialogView.findViewById(R.id.edit_height);
        recordHeight = dialogView.findViewById(R.id.record_height);


        return dialogView;
    }

    private void setClientImage() {
        if (weightWrapper.getId() != null) {//image already in local storage most likey ):
            //growthTakenToday profile image by passing the client id.If the image doesn't exist in the image repository
            // then download and save locally
            mImageView.setTag(R.id.entity_id, weightWrapper.getId());
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(weightWrapper.getId(), OpenSRPImageLoader
                    .getStaticImageListener(mImageView, ImageUtils.profileImageResourceByGender(weightWrapper.getGender()),
                            ImageUtils.profileImageResourceByGender(weightWrapper.getGender())));
        }
    }

    private void weightTakenTodayButtonAction() {
        growthTakenToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveGrowthRecord();

            }
        });
    }

    private void setButtonAction() {
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveGrowthRecord();

            }
        });
    }

    private void saveGrowthRecord() {
        String weightString = editWeight.getText().toString();
        if (StringUtils.isBlank(weightString) || Float.valueOf(weightString) <= 0f) {
            Toast.makeText(getActivity(), R.string.weight_is_required, Toast.LENGTH_LONG).show();
            return;
        }
        dismiss();

        int day = earlierDatePicker.getDayOfMonth();
        int month = earlierDatePicker.getMonth();
        int year = earlierDatePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        weightWrapper.setUpdatedWeightDate(new DateTime(calendar.getTime()), false);
        if (monitorGrowth) {
            heightWrapper.setUpdatedHeightDate(new DateTime(calendar.getTime()), false);
        }

        Float weight = Float.valueOf(weightString);
        weightWrapper.setWeight(weight);

        if (monitorGrowth) {
            String heightString = editHeight.getText().toString();
            if (!heightString.isEmpty()) {
                Float height = Float.valueOf(heightString);
                heightWrapper.setHeight(height);
            } else {
                heightWrapper = null;
            }
        }

        GrowthMonitoringActionListener.onGrowthRecorded(weightWrapper, heightWrapper);
    }


    private void growthEarlierAction() {
        growthRecordTakenEarlier.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                growthRecordTakenEarlier.setVisibility(View.GONE);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                earlierDatePicker.setVisibility(View.VISIBLE);
                earlierDatePicker.requestFocus();
                growthTakenToday.setVisibility(View.GONE);
                set.setVisibility(View.VISIBLE);

                DatePickerUtils.themeDatePicker(earlierDatePicker, new char[]{'d', 'm', 'y'});
            }
        });
    }

    private void cancelAction() {
        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private boolean getWeightBundle(Bundle bundle) {
        Serializable weightBundleSerializable = bundle.getSerializable(WEIGHT_WRAPPER_TAG);
        if (weightBundleSerializable instanceof WeightWrapper) {
            weightWrapper = (WeightWrapper) weightBundleSerializable;
        }

        return weightWrapper == null;
    }

    private boolean getHeightBundle(Bundle bundle) {
        Serializable heightBundleSerializable = bundle.getSerializable(HEIGHT_WRAPPER_TAG);
        if (heightBundleSerializable instanceof HeightWrapper) {
            heightWrapper = (HeightWrapper) heightBundleSerializable;
        }

        return heightWrapper == null;
    }

    private void getDateBundle(Bundle bundle) {
        Serializable dateSerializable = bundle.getSerializable(DATE_OF_BIRTH_TAG);
        if (dateSerializable instanceof Date) {
            dateOfBirth = (Date) dateSerializable;
        }
    }
}
