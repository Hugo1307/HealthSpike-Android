package pt.ua.deti.icm.android.health_spike.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import pt.ua.deti.icm.android.health_spike.R;
import pt.ua.deti.icm.android.health_spike.viewmodels.HeartRateViewModel;

public class HeartRateFragment extends Fragment {

    private HeartRateViewModel heartRateViewModel;

    public HeartRateFragment() { }

    public static HeartRateFragment newInstance() {
        return new HeartRateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_heart_rate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        heartRateViewModel = new ViewModelProvider(this).get(HeartRateViewModel.class);

        setHRData(view);
        registerObservers(view);

    }

    private void setHRData(View view) {

        TextView heartRateMainPlaceholder = view.findViewById(R.id.heartRateMainPlaceholder);
        TextView maxHeartRatePlaceholder = view.findViewById(R.id.maxHeartRatePlaceholder);
        TextView minHeartRatePlaceholder = view.findViewById(R.id.minHeartRatePlaceholder);
        TextView averageHeartRatePlaceholder = view.findViewById(R.id.avgHeartRatePlaceholder);

        String mostRecentHR = heartRateViewModel.getMostRecentHeartRate().getValue() != null ? String.valueOf(heartRateViewModel.getMostRecentHeartRate().getValue()) : "0.0";
        String maxHR = heartRateViewModel.getMaxHeartRate().getValue() != null ? String.valueOf(heartRateViewModel.getMaxHeartRate().getValue()) : "0.0";
        String minHR = heartRateViewModel.getMinHeartRate().getValue() != null ? String.valueOf(heartRateViewModel.getMinHeartRate().getValue()) : "0.0";
        String avgHR = heartRateViewModel.getAverageHeartRate().getValue() != null ? String.valueOf(heartRateViewModel.getAverageHeartRate().getValue()) : "0.0";

        heartRateMainPlaceholder.setText(mostRecentHR);
        maxHeartRatePlaceholder.setText(maxHR);
        minHeartRatePlaceholder.setText(minHR);
        averageHeartRatePlaceholder.setText(avgHR);

    }

    private void registerObservers(View view) {

        heartRateViewModel.getMostRecentHeartRate().observe(getViewLifecycleOwner(), currentHeartRate ->
            ((TextView) view.findViewById(R.id.heartRateMainPlaceholder)).setText(currentHeartRate != null ? String.valueOf(currentHeartRate.value) : "0.0")
        );

        heartRateViewModel.getMaxHeartRate().observe(getViewLifecycleOwner(), maxHeartRate ->
                ((TextView) view.findViewById(R.id.maxHeartRatePlaceholder)).setText(maxHeartRate != null ? String.valueOf(maxHeartRate) : "0.0")
        );

        heartRateViewModel.getMinHeartRate().observe(getViewLifecycleOwner(), minHeartRate ->
                ((TextView) view.findViewById(R.id.minHeartRatePlaceholder)).setText(minHeartRate != null ? String.valueOf(minHeartRate) : "0.0")
        );

        heartRateViewModel.getAverageHeartRate().observe(getViewLifecycleOwner(), avgHeartRate ->
                ((TextView) view.findViewById(R.id.avgHeartRatePlaceholder)).setText(avgHeartRate != null ? new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.ENGLISH)).format(avgHeartRate) : "0.0")
        );

    }

}