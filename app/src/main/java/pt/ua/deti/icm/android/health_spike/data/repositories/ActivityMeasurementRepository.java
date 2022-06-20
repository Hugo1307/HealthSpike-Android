package pt.ua.deti.icm.android.health_spike.data.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import pt.ua.deti.icm.android.health_spike.data.dao.ActivityMeasurementDao;
import pt.ua.deti.icm.android.health_spike.data.database.AppDatabase;
import pt.ua.deti.icm.android.health_spike.data.entities.ActivityMeasurement;

public class ActivityMeasurementRepository {

    private static ActivityMeasurementRepository instance;

    private final ActivityMeasurementDao activityMeasurementDao;

    private ActivityMeasurementRepository(Context appContext) {
        this.activityMeasurementDao = AppDatabase.getInstance(appContext).activityMeasurementDao();
    }

    public void insert(ActivityMeasurement activityMeasurement) {
        activityMeasurementDao.insertMeasurement(activityMeasurement);
    }

    public LiveData<Double> getHourlyAverage(int hourOffset) {
        return activityMeasurementDao.getAverageActivityHourly(hourOffset, hourOffset-1);
    }

    public LiveData<Integer> getCurrentActivityStatus() {
        return activityMeasurementDao.getCurrentActivityStatus();
    }

    public Double getCurrentActivityIndex() {
        return activityMeasurementDao.getCurrentActivityIndex();
    }

    public static ActivityMeasurementRepository getInstance(Context appContext) {
        if (instance == null)
            instance = new ActivityMeasurementRepository(appContext);
        return instance;
    }

}
