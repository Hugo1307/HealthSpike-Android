package pt.ua.deti.icm.android.health_spike.fragments;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import pt.ua.deti.icm.android.health_spike.R;
import pt.ua.deti.icm.android.health_spike.viewmodels.StepsViewModel;

public class StepsFragment extends Fragment {

    private static StepsViewModel stepsViewModel;

    private final List<BarEntry> chartData = new ArrayList<>();

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

        initStepsChart(view);

    }

    private void setupStepsChart(View view) {

        BarChart barChart = view.findViewById(R.id.stepsBarChart);
        BarDataSet barDataSet = new BarDataSet(chartData, "Weekly Steps");
        BarData barData = new BarData(barDataSet);

        barChart.setClickable(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);

        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);

        barChart.getXAxis().setDrawAxisLine(true);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisRight().setDrawAxisLine(false);

        barChart.getXAxis().setDrawLabels(true);
        barChart.getAxisLeft().setDrawLabels(false);
        barChart.getAxisRight().setDrawLabels(false);

        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.setData(barData);
        barChart.setDrawGridBackground(false);

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        barDataSet.setColor(ContextCompat.getColor(view.getContext(), R.color.primary_green));
        barDataSet.setDrawValues(true);

        barChart.notifyDataSetChanged();
        barChart.invalidate();

    }

    private void initStepsChart(View view) {

        stepsViewModel.getDaysOfWeekSteps().forEach((key, value) -> value.observe(getViewLifecycleOwner(), integer -> {
            chartData.add(new BarEntry(key, integer));
            // All Data was loaded
            if (chartData.size() >= 8)
                setupStepsChart(view);
            value.removeObservers(getViewLifecycleOwner());
        }));

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

        stepsViewModel.getIsWalking().observe(getViewLifecycleOwner(), (value) -> {

            ImageView stepsStatusImageView = view.findViewById(R.id.stepsWalkingStatusImage);
            TextView stepsWalkingStatusPlaceholder = view.findViewById(R.id.stepsPedometerStatusPlaceholder);

            if (value) {
                stepsWalkingStatusPlaceholder.setText(R.string.pedometer_status_walking);
                stepsStatusImageView.setImageResource(R.drawable.walking);
            } else {
                stepsWalkingStatusPlaceholder.setText(R.string.pedometer_status_stopped);
                stepsStatusImageView.setImageResource(R.drawable.stopped);
            }

        });

    }

}