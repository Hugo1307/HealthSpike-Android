package pt.ua.deti.icm.android.health_spike.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import pt.ua.deti.icm.android.health_spike.data.converters.DateConverter;
import pt.ua.deti.icm.android.health_spike.data.entities.LocationMeasurement;

@Dao
@TypeConverters(DateConverter.class)
public interface LocationMeasurementDao {

    @Query("SELECT * FROM location_measurements ORDER BY timestamp DESC LIMIT 1;")
    LocationMeasurement getLastLocationMeasurement();

    @Query("SELECT sum(distance_to_last) FROM location_measurements WHERE distance_to_last > 2 AND timestamp BETWEEN strftime('%s', 'now', 'start of day', '-' || :dayOffset || ' day') AND strftime('%s', 'now', '-' || :beforeDayOffset || ' day');")
    LiveData<Double> getTotalDistanceForDay(int dayOffset, int beforeDayOffset);

    @Query("SELECT sum(distance_to_last) FROM location_measurements WHERE  distance_to_last > 2 AND timestamp BETWEEN strftime('%s', 'now', 'start of day', '-7 day') AND strftime('%s', 'now');")
    LiveData<Double> getTotalDistanceForWeek();

    @Query("SELECT * FROM location_measurements ORDER BY timestamp DESC LIMIT 1;")
    LiveData<LocationMeasurement> getLastLiveLocation();

    @Insert
    void insertMeasurement(LocationMeasurement locationMeasurement);

}
