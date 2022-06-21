package pt.ua.deti.icm.android.health_spike.data.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import pt.ua.deti.icm.android.health_spike.data.dao.HeartRateMeasurementDao;
import pt.ua.deti.icm.android.health_spike.data.database.AppDatabase;

public class HeartRateMeasurementRepository {

    private static HeartRateMeasurementRepository instance;

    private final HeartRateMeasurementDao heartRateMeasurementDao;

    private HeartRateMeasurementRepository(Context context) {
        this.heartRateMeasurementDao = AppDatabase.getInstance(context).heartRateMeasurementDao();
    }

    private LiveData<Double> getDailyAverageHeartRate(int dayOffset) {
        return heartRateMeasurementDao.getLiveDailyAverage(dayOffset, dayOffset-1);
    }

    public static HeartRateMeasurementRepository getInstance(Context context) {
        if (instance == null)
            instance = new HeartRateMeasurementRepository(context);
        return instance;
    }

}
