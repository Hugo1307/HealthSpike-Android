package pt.ua.deti.icm.android.health_spike.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import pt.ua.deti.icm.android.health_spike.data.dao.StepsDao;
import pt.ua.deti.icm.android.health_spike.data.database.AppDatabase;

public class StepsViewModel extends AndroidViewModel {

    private final LiveData<Integer> dailySteps;
    private final LiveData<Integer> yesterdaySteps;
    private final LiveData<Integer> weeklySteps;
    private final LiveData<Integer> allTimeSteps;

    private final Map<Integer, LiveData<Integer>> daysOfWeekSteps;

    private MutableLiveData<Boolean> isWalking;

    public StepsViewModel(Application application) {

        super(application);

        StepsDao stepsDao = AppDatabase.getInstance(application).dailyStepsDao();

        dailySteps = stepsDao.getTodaySteps();
        yesterdaySteps = stepsDao.getYesterdaySteps();
        weeklySteps = stepsDao.getWeeklySteps();
        allTimeSteps = stepsDao.getAllTimeSteps();

        Map<Integer, LiveData<Integer>> daysOfWeekStepsBuilder = new HashMap<>();

        daysOfWeekStepsBuilder.put(LocalDate.now().getDayOfMonth(), stepsDao.getTodaySteps());
        daysOfWeekStepsBuilder.put(LocalDate.now().getDayOfMonth()-1, stepsDao.getYesterdaySteps());
        daysOfWeekStepsBuilder.put(LocalDate.now().getDayOfMonth()-2, stepsDao.getTwoDaysAgoSteps());
        daysOfWeekStepsBuilder.put(LocalDate.now().getDayOfMonth()-3, stepsDao.getThreeDaysAgoSteps());
        daysOfWeekStepsBuilder.put(LocalDate.now().getDayOfMonth()-4, stepsDao.getFourDaysAgoSteps());
        daysOfWeekStepsBuilder.put(LocalDate.now().getDayOfMonth()-5, stepsDao.getFiveDaysAgoSteps());
        daysOfWeekStepsBuilder.put(LocalDate.now().getDayOfMonth()-6, stepsDao.getSixDaysAgoSteps());
        daysOfWeekStepsBuilder.put(LocalDate.now().getDayOfMonth()-7, stepsDao.getSevenDaysAgoSteps());

        daysOfWeekSteps = daysOfWeekStepsBuilder;

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

    public Map<Integer, LiveData<Integer>> getDaysOfWeekSteps() {
        return daysOfWeekSteps;
    }

    public MutableLiveData<Boolean> getIsWalking() {
        if (isWalking == null)
            isWalking = new MutableLiveData<>();
        return isWalking;
    }

}
