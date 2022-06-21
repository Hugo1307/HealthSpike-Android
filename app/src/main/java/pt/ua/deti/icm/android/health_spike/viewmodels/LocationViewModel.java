package pt.ua.deti.icm.android.health_spike.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import pt.ua.deti.icm.android.health_spike.data.entities.LocationMeasurement;
import pt.ua.deti.icm.android.health_spike.data.repositories.LocationMeasurementRepository;

public class LocationViewModel extends AndroidViewModel {

    private final LiveData<Double> todayDistance;
    private final LiveData<Double> yesterdayDistance;
    private final LiveData<Double> weeklyDistance;
    private final LiveData<LocationMeasurement> lastLocation;

    public LocationViewModel(@NonNull Application application) {
        super(application);

        LocationMeasurementRepository locationMeasurementRepository = LocationMeasurementRepository.getInstance(application);

        this.todayDistance = locationMeasurementRepository.getTotalDistanceForDay(1);
        this.yesterdayDistance = locationMeasurementRepository.getTotalDistanceForDay(2);
        this.weeklyDistance = locationMeasurementRepository.getTotalDistanceForWeek();
        this.lastLocation = locationMeasurementRepository.getLastLocation();

    }

    public LiveData<Double> getTodayDistance() {
        return todayDistance;
    }

    public LiveData<Double> getYesterdayDistance() {
        return yesterdayDistance;
    }

    public LiveData<Double> getWeeklyDistance() {
        return weeklyDistance;
    }

    public LiveData<LocationMeasurement> getLastLocation() {
        return lastLocation;
    }

}
