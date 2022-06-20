package pt.ua.deti.icm.android.health_spike.data.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import pt.ua.deti.icm.android.health_spike.data.converters.DateConverter;
import pt.ua.deti.icm.android.health_spike.data.entities.ActivityMeasurement;

@Dao
@TypeConverters(DateConverter.class)
public interface ActivityMeasurementDao {

    @Insert
    void insertMeasurement(ActivityMeasurement activityMeasurement);

    @Query("SELECT avg(activity_status) FROM activity_measurements WHERE timestamp BETWEEN strftime('%s', 'now', '-' || :hour || ' hour') AND strftime('%s', 'now', '-' || :beforeHour || ' hour');")
    LiveData<Double> getAverageActivityHourly(int hour, int beforeHour);

    @Query("SELECT activity_status FROM activity_measurements ORDER BY timestamp DESC;")
    LiveData<Integer> getCurrentActivityStatus();

    @Query("SELECT avg(activity_status) FROM activity_measurements WHERE timestamp BETWEEN strftime('%s', 'now', '-1 hour') AND strftime('%s', 'now');")
    Double getCurrentActivityIndex();

}
