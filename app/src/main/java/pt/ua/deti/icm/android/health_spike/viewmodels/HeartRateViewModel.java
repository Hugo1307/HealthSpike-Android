package pt.ua.deti.icm.android.health_spike.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import pt.ua.deti.icm.android.health_spike.data.dao.HeartRateMeasurementDao;
import pt.ua.deti.icm.android.health_spike.data.database.AppDatabase;
import pt.ua.deti.icm.android.health_spike.data.entities.HeartRateMeasurement;

public class HeartRateViewModel extends AndroidViewModel {

    private final LiveData<HeartRateMeasurement> mostRecentHeartRate;
    private final LiveData<Double> minHeartRate;
    private final LiveData<Double> maxHeartRate;
    private final LiveData<Double> averageHeartRate;

    public HeartRateViewModel(Application application) {
        super(application);

        HeartRateMeasurementDao heartRateMeasurementDao = AppDatabase.getInstance(application).heartRateMeasurementDao();

        mostRecentHeartRate = heartRateMeasurementDao.getMostRecentMeasurement();
        minHeartRate = heartRateMeasurementDao.getMinMeasurement();
        maxHeartRate = heartRateMeasurementDao.getMaxMeasurement();
        averageHeartRate = heartRateMeasurementDao.getAverageMeasurement();

    }

    public LiveData<HeartRateMeasurement> getMostRecentHeartRate() {
        return mostRecentHeartRate;
    }

    public LiveData<Double> getMinHeartRate() {
        return minHeartRate;
    }

    public LiveData<Double> getMaxHeartRate() {
        return maxHeartRate;
    }

    public LiveData<Double> getAverageHeartRate() {
        return averageHeartRate;
    }

}
