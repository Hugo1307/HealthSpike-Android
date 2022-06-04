package pt.ua.deti.icm.android.health_spike.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import pt.ua.deti.icm.android.health_spike.data.converters.DateConverter;

@Entity(tableName = "heart_rate_measurements")
@TypeConverters(DateConverter.class)
public class HeartRateMeasurement {

    @PrimaryKey
    public Long id;

    @ColumnInfo(name = "timestamp")
    public Date timestamp;

    @ColumnInfo(name = "heart_rate_value")
    public double value;

    public HeartRateMeasurement(Date timestamp, double value) {
        this.id = null;
        this.timestamp = timestamp;
        this.value = value;
    }

}
