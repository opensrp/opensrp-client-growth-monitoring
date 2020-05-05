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
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.util.AppProperties;
import org.smartregister.growthmonitoring.util.GrowthMonitoringUtils;
import org.smartregister.growthmonitoring.util.ImageUtils;
import org.smartregister.util.DatePickerUtils;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.Calendar;
import java.util.Date;

@SuppressLint("ValidFragment")
public class EditGrowthDialogFragment extends DialogFragment {
    private final WeightWrapper weightWrapper;
    private final HeightWrapper heightWrapper;
    private GrowthMonitoringActionListener GrowthMonitoringActionListener;
    private DateTime currentGrowthDate;
    private Float currentWeight;
    private Float currentHeight;
    private Date dateOfBirth;
    private EditText editWeight;
    private EditText editHeight;
    private DatePicker earlierDatePicker;
    private TextView nameView;
    private TextView numberView;
    private TextView ageView;
    private TextView pmtctStatusView;
    private ImageView mImageView;
    private Button set;
    private Button growthRecordDelete;
    private Button cancel;
    private LinearLayout heightEntryLayout;
    private CustomFontTextView recordHeight;
    private final boolean monitorGrowth = GrowthMonitoringLibrary.getInstance().getAppProperties().isTrue(AppProperties.KEY.MONITOR_GROWTH);
    private boolean isNumericDatePicker = GrowthMonitoringLibrary.getInstance().getAppProperties().isTrue(NativeFormsProperties.KEY.WIDGET_DATEPICKER_IS_NUMERIC);

    private EditGrowthDialogFragment(Date dateOfBirth, WeightWrapper weightWrapper, HeightWrapper heightWrapper) {
        HeightWrapper heightWrapperTemp;

        this.dateOfBirth = dateOfBirth;
        if (weightWrapper == null) {
            this.weightWrapper = new WeightWrapper();
        } else {
            this.weightWrapper = weightWrapper;
        }

        heightWrapperTemp = null;
        if (monitorGrowth) {
            if (heightWrapper == null) {
                heightWrapperTemp = new HeightWrapper();
            } else {
                heightWrapperTemp = heightWrapper;
            }
        }
        this.heightWrapper = heightWrapperTemp;
    }

    public static EditGrowthDialogFragment newInstance(Date dateOfBirth, WeightWrapper weightWrapper,
                                                       HeightWrapper heightWrapper) {
        return new EditGrowthDialogFragment(dateOfBirth, weightWrapper, heightWrapper);
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

        ViewGroup dialogView = setUpViews(inflater, container);

        if (monitorGrowth) {
            recordHeight.setVisibility(View.VISIBLE);
            heightEntryLayout.setVisibility(View.VISIBLE);

            if (heightWrapper.getHeight() != null) {
                editHeight.setText(heightWrapper.getHeight().toString());
                editHeight.setSelection(editWeight.getText().length());
                currentHeight = heightWrapper.getHeight();
            }
        }

        if (weightWrapper.getWeight() != null) {
            editWeight.setText(weightWrapper.getWeight().toString());
            editWeight.setSelection(editWeight.getText().length());
            currentWeight = weightWrapper.getWeight();
        }

        if (weightWrapper.getUpdatedWeightDate() != null) {
            currentGrowthDate = weightWrapper.getUpdatedWeightDate();
        }

        //Will be removed once height and weight features are decoupled
        if (this.heightWrapper != null && heightWrapper.getUpdatedHeightDate() != null) {
            currentGrowthDate = heightWrapper.getUpdatedHeightDate();
        }

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

        earlierDatePicker.setVisibility(View.VISIBLE);
        earlierDatePicker.requestFocus();
        set.setVisibility(View.VISIBLE);

        DatePickerUtils.themeDatePicker(earlierDatePicker, new char[]{'d', 'm', 'y'});

        if (currentGrowthDate != null) {
            earlierDatePicker.updateDate(currentGrowthDate.year().get(), currentGrowthDate.monthOfYear().get() - 1, currentGrowthDate.dayOfMonth().get());
        }

        setClientImage();
        setButtonAction();
        growthRecordDeleteAction();
        setDateRecorded(dialogView);
        cancelAction();

        return dialogView;
    }

    @NotNull
    private ViewGroup setUpViews(LayoutInflater inflater, ViewGroup container) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.edit_growth_dialog_view, container, false);
        dialogView.setFilterTouchesWhenObscured(true);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        editWeight = dialogView.findViewById(R.id.edit_weight);
        editHeight = dialogView.findViewById(R.id.edit_height);
        earlierDatePicker = dialogView.findViewById(isNumericDatePicker ? R.id.earlier_date_picker_numeric : R.id.earlier_date_picker);
        nameView = dialogView.findViewById(R.id.child_name);
        numberView = dialogView.findViewById(R.id.child_zeir_id);
        ageView = dialogView.findViewById(R.id.child_age);
        pmtctStatusView = dialogView.findViewById(R.id.pmtct_status);
        mImageView = dialogView.findViewById(R.id.child_profilepic);
        set = dialogView.findViewById(R.id.set);
        growthRecordDelete = dialogView.findViewById(R.id.weight_delete);
        cancel = dialogView.findViewById(R.id.cancel);
        heightEntryLayout = dialogView.findViewById(R.id.edit_height_layout);
        recordHeight = dialogView.findViewById(R.id.record_height);

        return dialogView;
    }

    private void setClientImage() {
        if (weightWrapper.getId() != null) {//image already in local storage most likely ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download
            // and save locally
            mImageView.setTag(R.id.entity_id, weightWrapper.getId());
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(weightWrapper.getId(), OpenSRPImageLoader
                    .getStaticImageListener(mImageView, ImageUtils.profileImageResourceByGender(weightWrapper.getGender()),
                            ImageUtils.profileImageResourceByGender(weightWrapper.getGender())));
        }
    }

    private void setButtonAction() {
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weightString = editWeight.getText().toString();
                if (StringUtils.isBlank(weightString) || Float.valueOf(weightString) <= 0f) {
                    Toast.makeText(getActivity(), R.string.weight_is_required, Toast.LENGTH_LONG).show();
                    return;
                }
                String heightString = null;
                if (monitorGrowth) {
                    heightString = editHeight.getText().toString();
                }
                dismiss();

                boolean weightChanged = false;
                boolean heightChanged = false;
                boolean dateChanged = false;

                if (earlierDatePicker.getVisibility() == View.VISIBLE) {
                    int day = earlierDatePicker.getDayOfMonth();
                    int month = earlierDatePicker.getMonth();
                    int year = earlierDatePicker.getYear();

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);

                    DateTime updateTime = new DateTime(calendar.getTime());
                    if (currentGrowthDate != null && !org.apache.commons.lang3.time.DateUtils.isSameDay(calendar.getTime(), currentGrowthDate.toDate())) {
                        weightWrapper.setUpdatedWeightDate(updateTime, false);
                        if (monitorGrowth) {
                            heightWrapper.setUpdatedHeightDate(updateTime, false);
                        }
                        dateChanged = true;
                    }

                    if (monitorGrowth && heightString != null && !heightString.isEmpty()) {
                        updateHeightWrapperForBlankHeightEdit(updateTime);
                    }
                }

                setGrowthVariables(weightString, heightString, weightChanged, heightChanged, dateChanged);
            }
        });
    }

    private void setGrowthVariables(String weightString, String heightString, boolean weightChanged, boolean heightChanged,
                                    boolean dateChanged) {
        if (isHeightChanged(heightString, heightChanged) || isWeightChanged(weightString, weightChanged) || dateChanged) {
            GrowthMonitoringActionListener.onGrowthRecorded(weightWrapper, heightWrapper);
        }
    }

    private boolean isHeightChanged(String heightString, boolean heightChanged) {
        boolean isHeightChanged = heightChanged;
        if (heightString != null && !heightString.isEmpty()) {
            Float height = Float.valueOf(heightString);
            if (!height.equals(currentHeight)) {
                heightWrapper.setHeight(height);
                isHeightChanged = true;
            }
        } else {
            deleteHeight();
        }
        return isHeightChanged;
    }

    private boolean isWeightChanged(String weightString, boolean weightChanged) {
        boolean isWeightChanged = weightChanged;
        Float weight = Float.valueOf(weightString);
        if (!weight.equals(currentWeight)) {
            weightWrapper.setWeight(weight);
            isWeightChanged = true;
        }
        return isWeightChanged;
    }

    private void updateHeightWrapperForBlankHeightEdit(DateTime updateTime) {
        if (heightWrapper.getDbKey() == null) {
            heightWrapper.setUpdatedHeightDate(updateTime, false);
            heightWrapper.setDob(weightWrapper.getDob());
            heightWrapper.setGender(weightWrapper.getGender());
            heightWrapper.setId(weightWrapper.getId());
            heightWrapper.setPatientAge(weightWrapper.getPatientAge());
            heightWrapper.setPatientName(weightWrapper.getPatientName());
            heightWrapper.setPatientNumber(weightWrapper.getPatientNumber());
            heightWrapper.setPhoto(weightWrapper.getPhoto());
            heightWrapper.setPmtctStatus(weightWrapper.getPmtctStatus());

        }
    }


    private void growthRecordDeleteAction() {
        growthRecordDelete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                deleteWeight();
                deleteHeight();

                GrowthMonitoringActionListener.onGrowthRecorded(null, null);
            }
        });
    }

    private void deleteHeight() {
        if (heightWrapper != null) {
            HeightRepository heightRepository = GrowthMonitoringLibrary.getInstance().heightRepository();
            heightRepository.delete(String.valueOf(heightWrapper.getDbKey()));
        }
    }

    private void deleteWeight() {
        WeightRepository weightRepository = GrowthMonitoringLibrary.getInstance().weightRepository();
        weightRepository.delete(String.valueOf(weightWrapper.getDbKey()));
    }

    private void setDateRecorded(ViewGroup dialogView) {
        if (weightWrapper.getUpdatedWeightDate() != null) {
            ((TextView) dialogView.findViewById(R.id.service_date)).setText(
                    getString(R.string.date_recorded, weightWrapper.getUpdatedWeightDate().dayOfMonth().get() + "-" + weightWrapper.getUpdatedWeightDate().monthOfYear().get() + "-" + weightWrapper.getUpdatedWeightDate().year().get() + ""));
        } else if (heightWrapper != null && heightWrapper.getUpdatedHeightDate() != null) {
            ((TextView) dialogView.findViewById(R.id.service_date)).setText(
                    getString(R.string.date_recorded, heightWrapper.getUpdatedHeightDate().dayOfMonth().get() + "-" + heightWrapper.getUpdatedHeightDate().monthOfYear().get() + "-" + heightWrapper.getUpdatedHeightDate().year().get() + ""));
        } else {
            dialogView.findViewById(R.id.service_date).setVisibility(View.GONE);
            growthRecordDelete.setVisibility(View.GONE);
        }
    }

    private void cancelAction() {
        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
