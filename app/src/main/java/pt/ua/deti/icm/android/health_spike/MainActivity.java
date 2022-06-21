package pt.ua.deti.icm.android.health_spike;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import pt.ua.deti.icm.android.health_spike.data.dao.StepsDao;
import pt.ua.deti.icm.android.health_spike.data.database.AppDatabase;
import pt.ua.deti.icm.android.health_spike.data.entities.StepRegister;
import pt.ua.deti.icm.android.health_spike.data.repositories.LocationMeasurementRepository;
import pt.ua.deti.icm.android.health_spike.fragments.DashboardFragment;
import pt.ua.deti.icm.android.health_spike.fragments.DistanceFragment;
import pt.ua.deti.icm.android.health_spike.fragments.HeartRateFragment;
import pt.ua.deti.icm.android.health_spike.fragments.StepsFragment;
import pt.ua.deti.icm.android.health_spike.notifications.channels.HeartRateNotificationChannel;
import pt.ua.deti.icm.android.health_spike.permissions.ActivityRecognitionPermission;
import pt.ua.deti.icm.android.health_spike.permissions.AppPermission;
import pt.ua.deti.icm.android.health_spike.permissions.CoarseLocationPermission;
import pt.ua.deti.icm.android.health_spike.permissions.FineLocationPermission;
import pt.ua.deti.icm.android.health_spike.viewmodels.BodyActivityViewModel;
import pt.ua.deti.icm.android.health_spike.viewmodels.LocationViewModel;
import pt.ua.deti.icm.android.health_spike.viewmodels.StepsViewModel;
import pt.ua.deti.icm.android.health_spike.viewmodels.WeatherForecastViewModel;
import pt.ua.deti.icm.android.health_spike.weather_api.entities.WeatherForecastEntity;
import pt.ua.deti.icm.android.health_spike.weather_api.model.WeatherModel;
import pt.ua.deti.icm.android.health_spike.weather_api.model.WeatherTypeModel;
import pt.ua.deti.icm.android.health_spike.weather_api.model.WindModel;
import pt.ua.deti.icm.android.health_spike.weather_api.network.IPMAWeatherClient;
import pt.ua.deti.icm.android.health_spike.weather_api.network.listeners.CityForecastListener;
import pt.ua.deti.icm.android.health_spike.weather_api.network.listeners.WeatherTypesListener;
import pt.ua.deti.icm.android.health_spike.weather_api.network.listeners.WindTypesListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "HealthSpike";
    private static final String CHANNEL_ID = "HealthSpike_NOTIFICATIONS";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private StepsViewModel stepsViewModel;
    private LocationViewModel locationViewModel;

    private TransitionsReceiver mTransitionsReceiver;
    private List<ActivityTransition> activityTransitionList;
    private PendingIntent mActivityTransitionsPendingIntent;
    private boolean activityTrackingEnabled;

    private StepsDao stepsDao;

    private final String TRANSITIONS_RECEIVER_ACTION = BuildConfig.APPLICATION_ID + "TRANSITIONS_RECEIVER_ACTION";

    @Override
    protected void onStart() {
        super.onStart();
        // Register the transitions receiver to start receiving transitions results
        registerReceiver(mTransitionsReceiver, new IntentFilter(TRANSITIONS_RECEIVER_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister the transitions receiver
        unregisterReceiver(mTransitionsReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Disable the activity transitions updates as they're not needed when the app is closed
        if (activityTrackingEnabled)
            disableActivityTransitions();

        fusedLocationClient.removeLocationUpdates(locationCallback);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Enable the activity transitions updates when the application resumes
        if (!activityTrackingEnabled)
            enableActivityTransitions();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(createLocationRequest());

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(MainActivity.this, 0x1);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }

        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.healthSpikeToolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.main_fragment_container, DashboardFragment.class, null)
                    .commit();
        }

        AppPermission activityRecognitionPermission = new ActivityRecognitionPermission(this);
        AppPermission fineLocationPermission = new FineLocationPermission(this);

        activityRecognitionPermission.askPermission();
        fineLocationPermission.askPermission();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        createLocationRequest();

        initSensors();
        initBottomNavBar();
        registerTransitions();
        registerSettingsBtnListener();

        // Initialize PendingIntent that will be triggered when a activity transition occurs.
        Intent intent = new Intent(TRANSITIONS_RECEIVER_ACTION);
        mActivityTransitionsPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        mTransitionsReceiver = new TransitionsReceiver();

        stepsDao = AppDatabase.getInstance(getApplication()).dailyStepsDao();

        stepsViewModel = new ViewModelProvider(this).get(StepsViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                LocationMeasurementRepository locationMeasurementRepository = LocationMeasurementRepository.getInstance(getApplicationContext());

                for (Location location : locationResult.getLocations()) {
                    CompletableFuture.runAsync(() -> locationMeasurementRepository.insertLocation(location));
                    Log.i("HealthSpike", "Location added to database: " + location);
                }

            }

        };

    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.values == null || sensorEvent.values.length == 0)
                return;
            CompletableFuture.runAsync(() -> stepsDao.addStep(new StepRegister(null, Date.from(Instant.now()))));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            Log.i(TAG, "Steps Sensor accuracy changed.");
        }

    };

    private void initSensors() {

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor != null)
            sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void initBottomNavBar() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (item.getItemId() == R.id.page_1) {
                transaction.replace(R.id.main_fragment_container, DashboardFragment.newInstance());
            } else if (item.getItemId() == R.id.page_2) {
                transaction.replace(R.id.main_fragment_container, HeartRateFragment.newInstance());
            } else if (item.getItemId() == R.id.page_3) {
                transaction.replace(R.id.main_fragment_container, StepsFragment.newInstance());
            } else if (item.getItemId() == R.id.page_4) {
                transaction.replace(R.id.main_fragment_container, DistanceFragment.newInstance());
            }

            transaction.commit();
            return true;

        });
    }

    /**
     * Registers callbacks for {@link ActivityTransition} events via a custom
     * {@link BroadcastReceiver}
     */
    private void enableActivityTransitions() {

        // Create request and listen for activity changes.
        ActivityTransitionRequest request = new ActivityTransitionRequest(activityTransitionList);

        // Register for Transitions Updates.
        Task<Void> task = ActivityRecognition.getClient(this).requestActivityTransitionUpdates(request, mActivityTransitionsPendingIntent);

        task.addOnSuccessListener(result -> activityTrackingEnabled = true);
        task.addOnFailureListener(exception -> Log.e(TAG, "Transitions Api could NOT be registered: " + exception));

    }


    /**
     * Unregisters callbacks for {@link ActivityTransition} events via a custom
     * {@link BroadcastReceiver}
     */
    private void disableActivityTransitions() {

        ActivityRecognition.getClient(this).removeActivityTransitionUpdates(mActivityTransitionsPendingIntent)
                .addOnSuccessListener(aVoid -> {
                    activityTrackingEnabled = false;
                    // Toast.makeText(getApplicationContext(), "Transitions successfully unregistered.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    // Toast.makeText(getApplicationContext(), "Transitions could not be unregistered: " + exception, Toast.LENGTH_SHORT).show();
                    Log.e(TAG,"Transitions could not be unregistered: " + exception);
                });


    }

    private void registerTransitions() {

        activityTransitionList = new ArrayList<>();

        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

    }


    public class TransitionsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (!TextUtils.equals(TRANSITIONS_RECEIVER_ACTION, intent.getAction())) {
                Toast.makeText(getApplicationContext(), "Received an unsupported action in TransitionsReceiver: action = ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Extract activity transition information from listener.
            if (ActivityTransitionResult.hasResult(intent)) {

                ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);

                if (result == null) return;

                for (ActivityTransitionEvent event : result.getTransitionEvents()) {

                    if (event.getActivityType() == DetectedActivity.WALKING && event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                        stepsViewModel.getIsWalking().setValue(true);
                    } else if (event.getActivityType() == DetectedActivity.WALKING && event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                        stepsViewModel.getIsWalking().setValue(false);
                    }

                }

            }

        }

    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void registerSettingsBtnListener() {
        findViewById(R.id.settingsBtnImageView).setOnClickListener(view -> {
            Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivityIntent);
        });
    }

}