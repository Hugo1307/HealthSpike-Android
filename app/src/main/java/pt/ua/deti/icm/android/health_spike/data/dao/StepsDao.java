package pt.ua.deti.icm.android.health_spike.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import pt.ua.deti.icm.android.health_spike.data.converters.DateConverter;
import pt.ua.deti.icm.android.health_spike.data.entities.StepRegister;

@Dao
@TypeConverters(DateConverter.class)
public interface StepsDao {

    @Query("SELECT Count(*) FROM steps WHERE timestamp BETWEEN strftime('%s', 'now', 'start of day') AND  strftime('%s', 'now');")
    LiveData<Integer> getTodaySteps();

    @Query("SELECT Count(*) FROM steps WHERE timestamp BETWEEN strftime('%s', 'now', 'start of day', '-1 day') AND  strftime('%s', 'now', 'start of day');")
    LiveData<Integer> getYesterdaySteps();

    @Query("SELECT Count(*) FROM steps WHERE timestamp BETWEEN strftime('%s', 'now', 'start of day', '-2 day') AND  strftime('%s', 'now', 'start of day', '-1 day');")
    LiveData<Integer> getTwoDaysAgoSteps();

    @Query("SELECT Count(*) FROM steps WHERE timestamp BETWEEN strftime('%s', 'now', 'start of day', '-3 day') AND  strftime('%s', 'now', 'start of day', '-2 day');")
    LiveData<Integer> getThreeDaysAgoSteps();

    @Query("SELECT Count(*) FROM steps WHERE timestamp BETWEEN strftime('%s', 'now', 'start of day', '-4 day') AND  strftime('%s', 'now', 'start of day', '-3 day');")
    LiveData<Integer> getFourDaysAgoSteps();

    @Query("SELECT Count(*) FROM steps WHERE timestamp BETWEEN strftime('%s', 'now', 'start of day', '-5 day') AND  strftime('%s', 'now', 'start of day', '-4 day');")
    LiveData<Integer> getFiveDaysAgoSteps();

    @Query("SELECT Count(*) FROM steps WHERE timestamp BETWEEN strftime('%s', 'now', 'start of day', '-6 day') AND  strftime('%s', 'now', 'start of day', '-5 day');")
    LiveData<Integer> getSixDaysAgoSteps();

    @Query("SELECT Count(*) FROM steps WHERE timestamp BETWEEN strftime('%s', 'now', 'start of day', '-7 day') AND  strftime('%s', 'now', 'start of day', '-6 day');")
    LiveData<Integer> getSevenDaysAgoSteps();

    @Query("SELECT Count(*) FROM steps WHERE timestamp BETWEEN strftime('%s', 'now', 'start of day', '-7 day') AND  strftime('%s', 'now');")
    LiveData<Integer> getWeeklySteps();

    @Query("SELECT Count(*) FROM steps WHERE timestamp < strftime('%s', 'now');")
    LiveData<Integer> getAllTimeSteps();

    @Insert
    void addStep(StepRegister stepRegister);

}
