package pt.ua.deti.icm.android.health_spike.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import pt.ua.deti.icm.android.health_spike.data.dao.DailyStepsDao;
import pt.ua.deti.icm.android.health_spike.data.database.AppDatabase;
import pt.ua.deti.icm.android.health_spike.data.entities.DailySteps;

public class PedometerViewModel extends AndroidViewModel {

    private final LiveData<DailySteps> dailySteps;
    private MutableLiveData<Boolean> isWalking;

    public PedometerViewModel(Application application) {
        super(application);

        DailyStepsDao dailyStepsDao = AppDatabase.getInstance(application).dailyStepsDao();
        dailySteps = dailyStepsDao.getTodaySteps();

    }

    public LiveData<DailySteps> getDailySteps() {
        return dailySteps;
    }

    public MutableLiveData<Boolean> getIsWalking() {
        if (isWalking == null)
            isWalking = new MutableLiveData<>();
        return isWalking;
    }

}
