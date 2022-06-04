package pt.ua.deti.icm.android.health_spike.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.List;

import pt.ua.deti.icm.android.health_spike.data.converters.DateConverter;
import pt.ua.deti.icm.android.health_spike.data.entities.HeartRateMeasurement;

@Dao
@TypeConverters(DateConverter.class)
public interface HeartRateMeasurementDao {

    @Query("SELECT * FROM heart_rate_measurements;")
    List<HeartRateMeasurement> getAll();

    @Query("SELECT * FROM heart_rate_measurements WHERE timestamp > :timestamp")
    List<HeartRateMeasurement> getDailyMeasurementsByTimestamp(Date timestamp);

    @Query("SELECT * FROM heart_rate_measurements ORDER BY timestamp DESC;")
    LiveData<HeartRateMeasurement> getMostRecentMeasurement();

    @Query("SELECT min(heart_rate_value) FROM heart_rate_measurements")
    LiveData<Double> getMinMeasurement();

    @Query("SELECT max(heart_rate_value) FROM heart_rate_measurements")
    LiveData<Double> getMaxMeasurement();

    @Query("SELECT avg(heart_rate_value) FROM heart_rate_measurements")
    LiveData<Double> getAverageMeasurement();

    @Insert
    void insertMeasurement(HeartRateMeasurement heartRateMeasurement);

}
