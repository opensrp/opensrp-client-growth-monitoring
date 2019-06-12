package org.smartregister.growthmonitoring.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
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
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.listener.GMActionListener;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.util.ImageUtils;
import org.smartregister.util.DatePickerUtils;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Calendar;
import java.util.Date;

@SuppressLint ("ValidFragment")
public class EditGrowthDialogFragment extends DialogFragment {
    private final Context context;
    private final WeightWrapper weightWrapper;
    private final HeightWrapper heightWrapper;
    private GMActionListener GMActionListener;
    private DateTime currentWeightDate;
    private Float currentWeight;
    private Float currentHeight;

    private Date dateOfBirth;

    private EditGrowthDialogFragment(Context context, Date dateOfBirth, WeightWrapper weightWrapper,
                                     HeightWrapper heightWrapper) {
        this.context = context;
        this.dateOfBirth = dateOfBirth;
        if (weightWrapper == null) {
            this.weightWrapper = new WeightWrapper();
        } else {
            this.weightWrapper = weightWrapper;
        }

        if (heightWrapper == null) {
            this.heightWrapper = new HeightWrapper();
        } else {
            this.heightWrapper = heightWrapper;
        }
    }

    public static EditGrowthDialogFragment newInstance(Context context, Date dateOfBirth, WeightWrapper weightWrapper,
                                                       HeightWrapper heightWrapper) {
        return new EditGrowthDialogFragment(context, dateOfBirth, weightWrapper, heightWrapper);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the GMActionListener so we can send events to the host
            GMActionListener = (GMActionListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement GMActionListener");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.edit_growth_dialog_view, container, false);

        final EditText editWeight = dialogView.findViewById(R.id.edit_weight);
        if (weightWrapper.getWeight() != null) {
            editWeight.setText(weightWrapper.getWeight().toString());
            editWeight.setSelection(editWeight.getText().length());
            currentWeight = weightWrapper.getWeight();
        }

        final EditText editHeight = dialogView.findViewById(R.id.edit_height);
        if (heightWrapper.getHeight() != null) {
            editHeight.setText(heightWrapper.getHeight().toString());
            editHeight.setSelection(editWeight.getText().length());
            currentHeight = heightWrapper.getHeight();
        }

        if (weightWrapper.getUpdatedWeightDate() != null) {
            currentWeightDate = weightWrapper.getUpdatedWeightDate();
        }

        final DatePicker earlierDatePicker = dialogView.findViewById(R.id.earlier_date_picker);
        earlierDatePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
        if (dateOfBirth != null) {
            earlierDatePicker.setMinDate(dateOfBirth.getTime());
        }

        TextView nameView = dialogView.findViewById(R.id.child_name);
        nameView.setText(weightWrapper.getPatientName());

        TextView numberView = dialogView.findViewById(R.id.child_zeir_id);
        if (StringUtils.isNotBlank(weightWrapper.getPatientNumber())) {
            numberView.setText(String.format("%s: %s", getString(R.string.label_zeir), weightWrapper.getPatientNumber()));
        } else {
            numberView.setText("");
        }

        TextView ageView = dialogView.findViewById(R.id.child_age);
        if (StringUtils.isNotBlank(weightWrapper.getPatientAge())) {
            ageView.setText(String.format("%s: %s", getString(R.string.age), weightWrapper.getPatientAge()));
        } else {
            ageView.setText("");
        }

        TextView pmtctStatusView = dialogView.findViewById(R.id.pmtct_status);
        pmtctStatusView.setText(weightWrapper.getPmtctStatus());

        if (weightWrapper.getId() != null) {
            ImageView mImageView = dialogView.findViewById(R.id.child_profilepic);

            if (weightWrapper.getId() != null) {//image already in local storage most likely ):
                //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
                mImageView.setTag(R.id.entity_id, weightWrapper.getId());
                DrishtiApplication.getCachedImageLoaderInstance()
                        .getImageByClientId(weightWrapper.getId(), OpenSRPImageLoader.getStaticImageListener(
                                mImageView, ImageUtils.profileImageResourceByGender(weightWrapper.getGender()),
                                ImageUtils.profileImageResourceByGender(
                                        weightWrapper.getGender())));
            }
        }


        final Button set = dialogView.findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weightString = editWeight.getText().toString();
                if (StringUtils.isBlank(weightString) || Float.valueOf(weightString) <= 0f) {
                    return;
                }

                String heightString = editHeight.getText().toString();
                if (StringUtils.isBlank(heightString) || Float.valueOf(heightString) <= 0f) {
                    return;
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

                    if (!org.apache.commons.lang3.time.DateUtils.isSameDay(calendar.getTime(), currentWeightDate.toDate())) {
                        weightWrapper.setUpdatedWeightDate(new DateTime(calendar.getTime()), false);
                        dateChanged = true;
                    }
                }


                Float weight = Float.valueOf(weightString);
                if (!weight.equals(currentWeight)) {
                    weightWrapper.setWeight(weight);
                    weightChanged = true;
                }

                if (weightChanged || dateChanged) {
                    GMActionListener.onWeightTaken(weightWrapper);
                }

                Float height = Float.valueOf(heightString);
                if (!height.equals(currentHeight)) {
                    heightWrapper.setHeight(height);
                    heightChanged = true;
                }

                if (heightChanged || dateChanged) {
                    GMActionListener.onHeightTaken(heightWrapper);
                }

            }
        });

        final Button weightDelete = dialogView.findViewById(R.id.weight_delete);
        weightDelete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                WeightRepository weightRepository = GrowthMonitoringLibrary.getInstance().weightRepository();
                weightRepository.delete(String.valueOf(weightWrapper.getDbKey()));
                GMActionListener.onWeightTaken(null);

                HeightRepository heightRepository = GrowthMonitoringLibrary.getInstance().heightRepository();
                heightRepository.delete(String.valueOf(heightWrapper.getDbKey()));
                GMActionListener.onHeightTaken(null);

            }
        });
        if (weightWrapper.getUpdatedWeightDate() != null || heightWrapper.getUpdatedHeightDate() != null) {
            ((TextView) dialogView.findViewById(R.id.service_date)).setText(
                    getString(R.string.date_recorded) + " " + weightWrapper.getUpdatedWeightDate().dayOfMonth()
                            .get() + "-" + weightWrapper
                            .getUpdatedWeightDate().monthOfYear().get() + "-" + weightWrapper.getUpdatedWeightDate().year()
                            .get() + "");
            ((TextView) dialogView.findViewById(R.id.service_date)).setText(
                    getString(R.string.date_recorded) + " " + heightWrapper.getUpdatedHeightDate().dayOfMonth()
                            .get() + "-" + heightWrapper
                            .getUpdatedHeightDate().monthOfYear().get() + "-" + heightWrapper.getUpdatedHeightDate().year()
                            .get() + "");
        } else {
            dialogView.findViewById(R.id.service_date).setVisibility(View.GONE);
            weightDelete.setVisibility(View.GONE);
        }

        final Button weightTakenEarlier = dialogView.findViewById(R.id.weight_taken_earlier);
        weightTakenEarlier.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                weightTakenEarlier.setVisibility(View.GONE);

                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                earlierDatePicker.setVisibility(View.VISIBLE);
                earlierDatePicker.requestFocus();
                set.setVisibility(View.VISIBLE);

                DatePickerUtils.themeDatePicker(earlierDatePicker, new char[] {'d', 'm', 'y'});

                earlierDatePicker.updateDate(currentWeightDate.year().get(), currentWeightDate.monthOfYear().get() - 1,
                        currentWeightDate.dayOfMonth().get());
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
