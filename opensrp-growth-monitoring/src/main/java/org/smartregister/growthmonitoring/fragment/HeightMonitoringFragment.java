package org.smartregister.growthmonitoring.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.growthmonitoring.R;

public class HeightMonitoringFragment extends Fragment {
    public static HeightMonitoringFragment createInstance() {
        return new HeightMonitoringFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.height_monitoring_fragment, container, false);
        return view;
    }
}
