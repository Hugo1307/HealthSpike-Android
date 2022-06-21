package pt.ua.deti.icm.android.health_spike.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pt.ua.deti.icm.android.health_spike.data.dao.ActivityMeasurementDao;
import pt.ua.deti.icm.android.health_spike.data.dao.LocationMeasurementDao;
import pt.ua.deti.icm.android.health_spike.data.dao.StepsDao;
import pt.ua.deti.icm.android.health_spike.data.dao.HeartRateMeasurementDao;
import pt.ua.deti.icm.android.health_spike.data.entities.ActivityMeasurement;
import pt.ua.deti.icm.android.health_spike.data.entities.LocationMeasurement;
import pt.ua.deti.icm.android.health_spike.data.entities.StepRegister;
import pt.ua.deti.icm.android.health_spike.data.entities.HeartRateMeasurement;

@Database(entities = {HeartRateMeasurement.class, StepRegister.class, ActivityMeasurement.class, LocationMeasurement.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract HeartRateMeasurementDao heartRateMeasurementDao();
    public abstract StepsDao dailyStepsDao();
    public abstract ActivityMeasurementDao activityMeasurementDao();
    public abstract LocationMeasurementDao locationMeasurementDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context, AppDatabase.class, "health-spike").build();
        return instance;
    }

}
