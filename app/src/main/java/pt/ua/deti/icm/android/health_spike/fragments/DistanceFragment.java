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
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pt.ua.deti.icm.android.health_spike.R;
import pt.ua.deti.icm.android.health_spike.data.repositories.HeartRateMeasurementRepository;
import pt.ua.deti.icm.android.health_spike.data.repositories.LocationMeasurementRepository;
import pt.ua.deti.icm.android.health_spike.viewmodels.LocationViewModel;

public class DistanceFragment extends Fragment {

    private LocationViewModel locationViewModel;

    private final List<BarEntry> chartData = new ArrayList<>();

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
        initDistanceChart(view);

    }

    private void registerLocationObservers(View view) {

        locationViewModel.getTodayDistance().observe(getViewLifecycleOwner(), todayDistance -> {

            if (todayDistance == null) return;

            TextView todayDistancePlaceholder = view.findViewById(R.id.todayDistanceMainPlaceholder);
            todayDistancePlaceholder.setText(new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.ENGLISH)).format(todayDistance/1000));

        });

        locationViewModel.getLastLocation().observe(getViewLifecycleOwner(), location -> {

            if (location == null) return;

            TextView locationLatitudePlaceholder = view.findViewById(R.id.locationLatitudePlaceholder);
            TextView locationLongitudePlaceholder = view.findViewById(R.id.locationLongitudePlaceholder);

            locationLatitudePlaceholder.setText(String.valueOf(location.latitude));
            locationLongitudePlaceholder.setText(String.valueOf(location.longitude));

        });

        locationViewModel.getWeeklyDistance().observe(getViewLifecycleOwner(), distance -> {

            if (distance == null) return;

            TextView weekDistancePlaceholder = view.findViewById(R.id.weekDistancePlaceholder);
            weekDistancePlaceholder.setText(new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.ENGLISH)).format(distance != null ? distance/1000 : 0));

        });

        locationViewModel.getYesterdayDistance().observe(getViewLifecycleOwner(), distance -> {

            if (distance == null) return;

            TextView yesterdayDistancePlaceholder = view.findViewById(R.id.yesterdayDistancePlaceholder);
            yesterdayDistancePlaceholder.setText(new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.ENGLISH)).format(distance != null ? distance/1000 : 0));

        });

    }

    private void setupDistanceChart(View view) {

        BarChart barChart = view.findViewById(R.id.distanceBarChart);
        BarDataSet barDataSet = new BarDataSet(chartData, "Distance Average");
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

        barDataSet.setColor(ContextCompat.getColor(view.getContext(), R.color.primary_blue));
        barDataSet.setDrawValues(true);

        barChart.notifyDataSetChanged();
        barChart.invalidate();

    }

    private void initDistanceChart(View view) {

        int maxDayOffset = 8;

        for (int dayOffset = 1; dayOffset <= maxDayOffset; dayOffset++) {

            int finalDayOffset = dayOffset;
            LocationMeasurementRepository.getInstance(view.getContext()).getTotalDistanceForDay(finalDayOffset).observe(getViewLifecycleOwner(), value -> {

                if (value != null) {
                    chartData.add(new BarEntry((float) LocalDate.now().getDayOfMonth()-finalDayOffset+1, value.floatValue()));
                } else {
                    chartData.add(new BarEntry((float) LocalDate.now().getDayOfMonth()-finalDayOffset+1, 0f));
                }

                if (chartData.size() == maxDayOffset) {
                    setupDistanceChart(view);
                }

            });

        }

    }

}