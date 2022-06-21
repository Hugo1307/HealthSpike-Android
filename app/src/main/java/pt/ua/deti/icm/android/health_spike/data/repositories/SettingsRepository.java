package pt.ua.deti.icm.android.health_spike.data.repositories;

import androidx.lifecycle.MutableLiveData;

public class SettingsRepository {

    private static SettingsRepository instance;

    private MutableLiveData<Integer> stepsDailyGoal;

    private SettingsRepository() { }

    public MutableLiveData<Integer> getStepsDailyGoal() {
        if (stepsDailyGoal == null)
            stepsDailyGoal = new MutableLiveData<>();
        return stepsDailyGoal;
    }

    public static SettingsRepository getInstance() {
        if (instance == null)
            instance = new SettingsRepository();
        return instance;
    }

}
