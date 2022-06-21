package pt.ua.deti.icm.android.health_spike.data.repositories;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;

import java.sql.Date;
import java.time.Instant;

import pt.ua.deti.icm.android.health_spike.data.dao.LocationMeasurementDao;
import pt.ua.deti.icm.android.health_spike.data.database.AppDatabase;
import pt.ua.deti.icm.android.health_spike.data.entities.LocationMeasurement;

public class LocationMeasurementRepository {

    private static LocationMeasurementRepository instance;

    private final LocationMeasurementDao locationMeasurementDao;

    private LocationMeasurementRepository(Context context) {
        this.locationMeasurementDao = AppDatabase.getInstance(context).locationMeasurementDao();
    }

    public void insertLocation(Location currentLocation) {

        LocationMeasurement lastLocationMeasurement = locationMeasurementDao.getLastLocationMeasurement();

        double distanceToLastPoint = 0d;

        if (lastLocationMeasurement != null) {

            Location lastLocation = new Location("lastLocation");

            lastLocation.setLatitude(lastLocationMeasurement.latitude);
            lastLocation.setLongitude(lastLocationMeasurement.longitude);

            distanceToLastPoint = Math.abs(lastLocation.distanceTo(currentLocation));

        }

        LocationMeasurement measurementToInsert = new LocationMeasurement(Date.from(Instant.now()), currentLocation.getLatitude(), currentLocation.getLongitude());
        measurementToInsert.setDistance(distanceToLastPoint);

        locationMeasurementDao.insertMeasurement(measurementToInsert);

    }

    public LiveData<Double> getTotalDistanceForDay(int dayOffset) {
        return locationMeasurementDao.getTotalDistanceForDay(dayOffset, dayOffset-1);
    }

    public LiveData<Double> getTotalDistanceForWeek() {
        return locationMeasurementDao.getTotalDistanceForWeek();
    }

    public LiveData<LocationMeasurement> getLastLocation() {
        return locationMeasurementDao.getLastLiveLocation();
    }

    public static LocationMeasurementRepository getInstance(Context context) {
        if (instance == null)
            instance = new LocationMeasurementRepository(context);
        return instance;
    }

}
