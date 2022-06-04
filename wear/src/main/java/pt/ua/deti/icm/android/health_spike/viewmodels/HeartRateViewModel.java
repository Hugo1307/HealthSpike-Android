package pt.ua.deti.icm.android.health_spike.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HeartRateViewModel extends ViewModel {

    private MutableLiveData<Double> currentHeartRate;

    public MutableLiveData<Double> getCurrentHeartRate() {
        if (currentHeartRate == null)
            currentHeartRate = new MutableLiveData<>();
        return currentHeartRate;
    }

}
