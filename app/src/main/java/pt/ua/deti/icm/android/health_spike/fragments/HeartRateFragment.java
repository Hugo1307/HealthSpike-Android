package pt.ua.deti.icm.android.health_spike.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import pt.ua.deti.icm.android.health_spike.viewmodels.HeartRateViewModel;

public class HeartRateFragment extends Fragment {

    private HeartRateViewModel heartRateViewModel;

    private final List<BarEntry> chartData = new ArrayList<>();

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

        initHRChart(view);

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

    private void setupHRChart(View view) {

        BarChart barChart = view.findViewById(R.id.heartRateBarChart);
        BarDataSet barDataSet = new BarDataSet(chartData, "Heart Rate Average");
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

        barDataSet.setColor(ContextCompat.getColor(view.getContext(), R.color.primary_red));
        barDataSet.setDrawValues(true);

        barChart.notifyDataSetChanged();
        barChart.invalidate();

    }

    private void initHRChart(View view) {

        int maxDayOffset = 8;

        for (int dayOffset = 1; dayOffset <= maxDayOffset; dayOffset++) {

            int finalDayOffset = dayOffset;
            HeartRateMeasurementRepository.getInstance(view.getContext()).getDailyAverageHeartRate(finalDayOffset).observe(getViewLifecycleOwner(), value -> {

                if (value != null) {
                    chartData.add(new BarEntry((float) LocalDate.now().getDayOfMonth()-finalDayOffset+1, value.floatValue()));
                } else {
                    chartData.add(new BarEntry((float) LocalDate.now().getDayOfMonth()-finalDayOffset+1, 0f));
                }

                if (chartData.size() >= maxDayOffset) {
                    setupHRChart(view);
                }

            });

        }

    }

}