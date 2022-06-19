package pt.ua.deti.icm.android.health_spike.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import pt.ua.deti.icm.android.health_spike.data.dao.StepsDao;
import pt.ua.deti.icm.android.health_spike.data.database.AppDatabase;

public class StepsViewModel extends AndroidViewModel {

    private final LiveData<Integer> dailySteps;
    private final LiveData<Integer> yesterdaySteps;
    private final LiveData<Integer> weeklySteps;
    private final LiveData<Integer> allTimeSteps;

    private MutableLiveData<Boolean> isWalking;

    public StepsViewModel(Application application) {
        super(application);

        StepsDao stepsDao = AppDatabase.getInstance(application).dailyStepsDao();

        dailySteps = stepsDao.getTodaySteps();
        yesterdaySteps = stepsDao.getYesterdaySteps();
        weeklySteps = stepsDao.getWeeklySteps();
        allTimeSteps = stepsDao.getAllTimeSteps();

    }

    public LiveData<Integer> getDailySteps() {
        return dailySteps;
    }

    public LiveData<Integer> getYesterdaySteps() {
        return yesterdaySteps;
    }

    public LiveData<Integer> getWeeklySteps() {
        return weeklySteps;
    }

    public LiveData<Integer> getAllTimeSteps() {
        return allTimeSteps;
    }

    public MutableLiveData<Boolean> getIsWalking() {
        if (isWalking == null)
            isWalking = new MutableLiveData<>();
        return isWalking;
    }

}
