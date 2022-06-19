package pt.ua.deti.icm.android.health_spike.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import pt.ua.deti.icm.android.health_spike.R;
import pt.ua.deti.icm.android.health_spike.viewmodels.DashboardViewModel;
import pt.ua.deti.icm.android.health_spike.viewmodels.HeartRateViewModel;
import pt.ua.deti.icm.android.health_spike.viewmodels.StepsViewModel;

public class DashboardFragment extends Fragment {

    int dailyStepsGoal = 10000;

    private HeartRateViewModel heartRateViewModel;
    private StepsViewModel stepsViewModel;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        DashboardViewModel mViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
        heartRateViewModel = new ViewModelProvider(this).get(HeartRateViewModel.class);
        stepsViewModel = new ViewModelProvider(this).get(StepsViewModel.class);

        setPedometerData(view);

        registerHRObservers(view);
        registerPedometerObservers(view);

    }

    private void setPedometerData(View view) {

        TextView mainPanelStepsCount = view.findViewById(R.id.mainPanelStepsPlaceholder);
        TextView progressBarStepsLeftPlaceholder = view.findViewById(R.id.progressBarStepsLeftPlaceholder);
        ProgressBar progressBarStepsLeft = view.findViewById(R.id.progressBarStepsLeft);

        Integer dailyStepsCount = stepsViewModel.getDailySteps().getValue();

        if (dailyStepsCount == null)
            return;

        mainPanelStepsCount.setText(String.valueOf(dailyStepsCount));
        progressBarStepsLeft.setMax(dailyStepsGoal);

        if (dailyStepsGoal > dailyStepsCount) {
            progressBarStepsLeftPlaceholder.setText(String.valueOf(dailyStepsGoal - dailyStepsCount));
            progressBarStepsLeft.setProgress(dailyStepsGoal -(dailyStepsGoal - dailyStepsCount));
        } else {
            progressBarStepsLeftPlaceholder.setText(String.valueOf(dailyStepsCount));
            progressBarStepsLeft.setProgress(dailyStepsGoal);
        }

    }

    private void registerPedometerObservers(View view) {

        stepsViewModel.getDailySteps().observe(getViewLifecycleOwner(), (value) -> {

            TextView mainPanelStepsCount = view.findViewById(R.id.mainPanelStepsPlaceholder);
            TextView progressBarStepsLeftPlaceholder = view.findViewById(R.id.progressBarStepsLeftPlaceholder);
            ProgressBar progressBarStepsLeft = view.findViewById(R.id.progressBarStepsLeft);

            if (value == null) return;

            if (mainPanelStepsCount == null || progressBarStepsLeftPlaceholder == null || progressBarStepsLeft == null) return;

            int dailyStepsCount = value;

            mainPanelStepsCount.setText(String.valueOf(dailyStepsCount));
            progressBarStepsLeft.setMax(dailyStepsGoal);

            if (dailyStepsGoal > dailyStepsCount) {
                progressBarStepsLeftPlaceholder.setText(String.valueOf(dailyStepsGoal - dailyStepsCount));
                progressBarStepsLeft.setProgress(dailyStepsGoal -(dailyStepsGoal - dailyStepsCount));
            } else {
                progressBarStepsLeftPlaceholder.setText(String.valueOf(dailyStepsCount));
                progressBarStepsLeft.setProgress(dailyStepsGoal);
            }

        });

        stepsViewModel.getIsWalking().observe(getViewLifecycleOwner(), (value) -> {
            ImageView mainPanelStepsCount = view.findViewById(R.id.walkingStatusImage);
            if (value)
                mainPanelStepsCount.setImageResource(R.drawable.walking);
            else
                mainPanelStepsCount.setImageResource(R.drawable.stopped);
        });

    }

    private void registerHRObservers(View view) {

        heartRateViewModel.getMostRecentHeartRate().observe(getViewLifecycleOwner(), currentHeartRate ->
                ((TextView) view.findViewById(R.id.stepsMainPlaceholder)).setText(currentHeartRate != null ? String.valueOf(currentHeartRate.value) : "0.0")
        );

        heartRateViewModel.getMaxHeartRate().observe(getViewLifecycleOwner(), maxHeartRate ->
                ((TextView) view.findViewById(R.id.yesterdayStepsPlaceholder)).setText(maxHeartRate != null ? String.valueOf(maxHeartRate) : "0.0")
        );

        heartRateViewModel.getMinHeartRate().observe(getViewLifecycleOwner(), minHeartRate ->
                ((TextView) view.findViewById(R.id.weekStepsPlaceholder)).setText(minHeartRate != null ? String.valueOf(minHeartRate) : "0.0")
        );

        heartRateViewModel.getAverageHeartRate().observe(getViewLifecycleOwner(), avgHeartRate ->
                ((TextView) view.findViewById(R.id.allStepsPlaceholder)).setText(avgHeartRate != null ? new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.ENGLISH)).format(avgHeartRate) : "0.0")
        );

    }

}