package pt.ua.deti.icm.android.health_spike.fragments;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pt.ua.deti.icm.android.health_spike.R;
import pt.ua.deti.icm.android.health_spike.data.repositories.ActivityMeasurementRepository;
import pt.ua.deti.icm.android.health_spike.viewmodels.DashboardViewModel;
import pt.ua.deti.icm.android.health_spike.viewmodels.HeartRateViewModel;
import pt.ua.deti.icm.android.health_spike.viewmodels.StepsViewModel;

public class DashboardFragment extends Fragment {

    int dailyStepsGoal = 10000;

    private HeartRateViewModel heartRateViewModel;
    private StepsViewModel stepsViewModel;

    private final List<BarEntry> chartData = new ArrayList<>();

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

        initStepsChart(view);

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

    private void registerActivityObservers(View view) {



    }

    private void setupStepsChart(View view) {

        BarChart barChart = view.findViewById(R.id.activityBarChart);
        BarDataSet barDataSet = new BarDataSet(chartData, "Hourly Activity");
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
        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return LocalDateTime.now().minusHours((int) value-1).format(DateTimeFormatter.ofPattern("HH:00"));
            }
        });

        barChart.setData(barData);
        barChart.setDrawGridBackground(false);

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        barDataSet.setColor(ContextCompat.getColor(view.getContext(), R.color.primary_green));
        barDataSet.setDrawValues(false);

        barChart.notifyDataSetChanged();
        barChart.invalidate();

    }

    private void initStepsChart(View view) {

        int maxHourOffset = 6;

        for (int hourOffset = 1; hourOffset <= maxHourOffset; hourOffset++) {

            int finalHourOffset = hourOffset;
            ActivityMeasurementRepository.getInstance(view.getContext()).getHourlyAverage(finalHourOffset).observe(getViewLifecycleOwner(), value -> {

                if (value != null) {
                    chartData.add(new BarEntry((float) finalHourOffset, value.floatValue()));
                } else {
                    chartData.add(new BarEntry((float) finalHourOffset, 0f));
                }

                if (chartData.size() == maxHourOffset) {
                    setupStepsChart(view);
                }

            });

        }

    }

}