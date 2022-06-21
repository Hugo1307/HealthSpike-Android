package pt.ua.deti.icm.android.health_spike.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import pt.ua.deti.icm.android.health_spike.data.converters.DateConverter;

@Entity(tableName = "location_measurements")
@TypeConverters(DateConverter.class)
public class LocationMeasurement {

    @PrimaryKey
    public Long id;

    @ColumnInfo(name = "timestamp")
    public Date timestamp;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "distance_to_last")
    public double distance;

    public LocationMeasurement(Date timestamp, double latitude, double longitude) {
        this.id = null;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

}
