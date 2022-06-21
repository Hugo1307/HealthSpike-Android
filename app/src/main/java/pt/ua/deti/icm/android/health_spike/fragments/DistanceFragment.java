package pt.ua.deti.icm.android.health_spike.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import pt.ua.deti.icm.android.health_spike.R;
import pt.ua.deti.icm.android.health_spike.viewmodels.DashboardViewModel;
import pt.ua.deti.icm.android.health_spike.viewmodels.DistanceViewModel;
import pt.ua.deti.icm.android.health_spike.viewmodels.HeartRateViewModel;
import pt.ua.deti.icm.android.health_spike.viewmodels.LocationViewModel;
import pt.ua.deti.icm.android.health_spike.viewmodels.StepsViewModel;

public class DistanceFragment extends Fragment {

    private LocationViewModel locationViewModel;

    public static DistanceFragment newInstance() {
        return new DistanceFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_distance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        registerLocationObservers(view);

    }

    private void registerLocationObservers(View view) {

        locationViewModel.getTodayDistance().observe(getViewLifecycleOwner(), todayDistance -> {

            TextView todayDistancePlaceholder = view.findViewById(R.id.todayDistanceMainPlaceholder);
            todayDistancePlaceholder.setText(new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.ENGLISH)).format(todayDistance/1000));

        });

        locationViewModel.getLastLocation().observe(getViewLifecycleOwner(), location -> {

            TextView locationLatitudePlaceholder = view.findViewById(R.id.locationLatitudePlaceholder);
            TextView locationLongitudePlaceholder = view.findViewById(R.id.locationLongitudePlaceholder);

            locationLatitudePlaceholder.setText(String.valueOf(location.latitude));
            locationLongitudePlaceholder.setText(String.valueOf(location.longitude));

        });

        locationViewModel.getWeeklyDistance().observe(getViewLifecycleOwner(), distance -> {

            TextView weekDistancePlaceholder = view.findViewById(R.id.weekDistancePlaceholder);
            weekDistancePlaceholder.setText(new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.ENGLISH)).format(distance != null ? distance/1000 : 0));

        });

        locationViewModel.getYesterdayDistance().observe(getViewLifecycleOwner(), distance -> {

            TextView yesterdayDistancePlaceholder = view.findViewById(R.id.yesterdayDistancePlaceholder);
            yesterdayDistancePlaceholder.setText(new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.ENGLISH)).format(distance != null ? distance/1000 : 0));

        });

    }

}