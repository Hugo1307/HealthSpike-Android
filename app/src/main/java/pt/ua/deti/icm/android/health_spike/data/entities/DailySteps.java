package pt.ua.deti.icm.android.health_spike.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import pt.ua.deti.icm.android.health_spike.data.converters.DateConverter;

@Entity(tableName = "daily_steps")
@TypeConverters(DateConverter.class)
public class DailySteps {

    @PrimaryKey
    public Long id;

    @ColumnInfo(name = "timestamp")
    public Date timestamp;

    @ColumnInfo(name = "steps_count")
    public Integer stepsCount;

    public DailySteps(Long id, Date timestamp, Integer stepsCount) {
        this.id = id;
        this.timestamp = timestamp;
        this.stepsCount = stepsCount;
    }

}
