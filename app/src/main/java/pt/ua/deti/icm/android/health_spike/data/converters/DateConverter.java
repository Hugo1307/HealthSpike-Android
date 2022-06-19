package pt.ua.deti.icm.android.health_spike.data.converters;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {

    @TypeConverter
    public static Date toDate(Long dateLong){
        return dateLong == null ? null: new Date(dateLong*1000);
    }

    @TypeConverter
    public static Long fromDate(Date date){
        return date == null ? null : date.getTime()/1000;
    }

}
