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

import pt.ua.deti.icm.android.health_spike.R;
import pt.ua.deti.icm.android.health_spike.viewmodels.StepsViewModel;

public class StepsFragment extends Fragment {

    private static StepsViewModel stepsViewModel;

    public static StepsFragment newInstance() {
        return new StepsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_steps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        stepsViewModel = new ViewModelProvider(this).get(StepsViewModel.class);

        setPedometerData(view);
        registerPedometerObservers(view);

    }

    private void setPedometerData(View view) {

        TextView stepsMainPlaceholder = view.findViewById(R.id.stepsMainPlaceholder);
        TextView yesterdayStepsPlaceholder = view.findViewById(R.id.yesterdayStepsPlaceholder);
        TextView weeklyStepsPlaceholder = view.findViewById(R.id.weekStepsPlaceholder);
        TextView allStepsPlaceholder = view.findViewById(R.id.allStepsPlaceholder);

        String dailyStepsCount = stepsViewModel.getDailySteps().getValue() != null ? String.valueOf(stepsViewModel.getDailySteps().getValue()) : "0";
        String yesterdayStepsCount = stepsViewModel.getYesterdaySteps().getValue() != null ? String.valueOf(stepsViewModel.getYesterdaySteps().getValue()) : "0";
        String weeklyStepsCount = stepsViewModel.getWeeklySteps().getValue() != null ? String.valueOf(stepsViewModel.getWeeklySteps().getValue()) : "0";
        String allTimeStepsCount = stepsViewModel.getAllTimeSteps().getValue() != null ? String.valueOf(stepsViewModel.getAllTimeSteps().getValue()) : "0";

        stepsMainPlaceholder.setText(dailyStepsCount);
        yesterdayStepsPlaceholder.setText(yesterdayStepsCount);
        weeklyStepsPlaceholder.setText(weeklyStepsCount);
        allStepsPlaceholder.setText(allTimeStepsCount);

    }

    private void registerPedometerObservers(View view) {

        TextView stepsMainPlaceholder = view.findViewById(R.id.stepsMainPlaceholder);
        TextView yesterdayStepsPlaceholder = view.findViewById(R.id.yesterdayStepsPlaceholder);
        TextView weeklyStepsPlaceholder = view.findViewById(R.id.weekStepsPlaceholder);
        TextView allStepsPlaceholder = view.findViewById(R.id.allStepsPlaceholder);

        stepsViewModel.getDailySteps().observe(getViewLifecycleOwner(), (value) -> {
            if (value == null)
                return;
            stepsMainPlaceholder.setText(String.valueOf(value));
        });

        stepsViewModel.getYesterdaySteps().observe(getViewLifecycleOwner(), (value) -> {
            if (value == null)
                return;
            yesterdayStepsPlaceholder.setText(String.valueOf(value));
        });

        stepsViewModel.getWeeklySteps().observe(getViewLifecycleOwner(), (value) -> {
            if (value == null)
                return;
            weeklyStepsPlaceholder.setText(String.valueOf(value));
        });

        stepsViewModel.getAllTimeSteps().observe(getViewLifecycleOwner(), (value) -> {
            if (value == null)
                return;
            allStepsPlaceholder.setText(String.valueOf(value));
        });

        /*
        stepsViewModel.getIsWalking().observe(getViewLifecycleOwner(), (value) -> {
            ImageView mainPanelStepsCount = view.findViewById(R.id.walkingStatusImage);
            if (value)
                mainPanelStepsCount.setImageResource(R.drawable.walking);
            else
                mainPanelStepsCount.setImageResource(R.drawable.stopped);
        });
        */

    }

}