package pt.ua.deti.icm.android.health_spike.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import pt.ua.deti.icm.android.health_spike.data.repositories.ActivityMeasurementRepository;

public class BodyActivityViewModel extends AndroidViewModel {

    private final LiveData<Double> lastHourActivityIndex;
    private final LiveData<Integer> currentActivityStatus;

    public BodyActivityViewModel(@NonNull Application application) {
        super(application);

        ActivityMeasurementRepository activityMeasurementRepository = ActivityMeasurementRepository.getInstance(application);

        this.lastHourActivityIndex = activityMeasurementRepository.getHourlyAverage(1);
        this.currentActivityStatus = activityMeasurementRepository.getCurrentActivityStatus();

    }

    public LiveData<Double> getLastHourActivityIndex() {
        return lastHourActivityIndex;
    }

    public LiveData<Integer> getCurrentActivityStatus() {
        return currentActivityStatus;
    }

}
