package pt.ua.deti.icm.android.health_spike.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PedometerViewModel extends ViewModel {

    private MutableLiveData<Integer> stepsCount;
    private MutableLiveData<Boolean> isWalking;

    public MutableLiveData<Integer> getStepsCount() {
        if (stepsCount == null)
            stepsCount = new MutableLiveData<>();
        return stepsCount;
    }

    public MutableLiveData<Boolean> getIsWalking() {
        if (isWalking == null)
            isWalking = new MutableLiveData<>();
        return isWalking;
    }

}
