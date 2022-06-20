package pt.ua.deti.icm.android.health_spike.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import pt.ua.deti.icm.android.health_spike.data.converters.DateConverter;

@Entity(tableName = "activity_measurements")
@TypeConverters(DateConverter.class)
public class ActivityMeasurement {

    @PrimaryKey
    public Long id;

    @ColumnInfo(name = "timestamp")
    public Date timestamp;

    @ColumnInfo(name = "activity_status")
    public int activityId;

    public ActivityMeasurement(Date timestamp, int activityId) {
        this.id = null;
        this.timestamp = timestamp;
        this.activityId = activityId;
    }

}
