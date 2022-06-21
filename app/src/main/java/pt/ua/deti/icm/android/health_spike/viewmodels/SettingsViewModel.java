package pt.ua.deti.icm.android.health_spike.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import pt.ua.deti.icm.android.health_spike.data.repositories.SettingsRepository;

public class SettingsViewModel extends ViewModel {

    private MutableLiveData<Integer> dailyGoal;

    public MutableLiveData<Integer> getDailyGoal() {
        if (dailyGoal == null)
            dailyGoal = SettingsRepository.getInstance().getStepsDailyGoal();
        return dailyGoal;
    }

}
