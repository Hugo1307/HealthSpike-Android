package pt.ua.deti.icm.android.health_spike.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import pt.ua.deti.icm.android.health_spike.data.converters.DateConverter;

@Entity(tableName = "steps")
@TypeConverters(DateConverter.class)
public class StepRegister {

    @PrimaryKey
    public Long id;

    @ColumnInfo(name = "timestamp")
    public Date timestamp;

    public StepRegister(Long id, Date timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

}
