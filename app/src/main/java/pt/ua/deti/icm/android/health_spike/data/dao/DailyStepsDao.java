package pt.ua.deti.icm.android.health_spike.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import pt.ua.deti.icm.android.health_spike.data.converters.DateConverter;
import pt.ua.deti.icm.android.health_spike.data.entities.DailySteps;

@Dao
@TypeConverters(DateConverter.class)
public interface DailyStepsDao {

    @Query("SELECT * FROM daily_steps WHERE timestamp = date('start of day');")
    LiveData<DailySteps> getTodaySteps();

    @Query("UPDATE daily_steps SET steps_count = steps_count+1 WHERE timestamp = date('start of day');")
    void addStep();

    @Insert
    void createDailyStepsRegistry(DailySteps dailySteps);

}
