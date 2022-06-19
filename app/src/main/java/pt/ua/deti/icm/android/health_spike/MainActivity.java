package pt.ua.deti.icm.android.health_spike;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import pt.ua.deti.icm.android.health_spike.data.dao.StepsDao;
import pt.ua.deti.icm.android.health_spike.data.database.AppDatabase;
import pt.ua.deti.icm.android.health_spike.data.entities.StepRegister;
import pt.ua.deti.icm.android.health_spike.fragments.DashboardFragment;
import pt.ua.deti.icm.android.health_spike.fragments.DistanceFragment;
import pt.ua.deti.icm.android.health_spike.fragments.HeartRateFragment;
import pt.ua.deti.icm.android.health_spike.fragments.StepsFragment;
import pt.ua.deti.icm.android.health_spike.notifications.channels.HeartRateNotificationChannel;
import pt.ua.deti.icm.android.health_spike.permissions.ActivityRecognitionPermission;
import pt.ua.deti.icm.android.health_spike.permissions.AppPermission;
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

    private int notificationId = 0;

    private WeatherForecastViewModel weatherForecastViewModel;
    private StepsViewModel stepsViewModel;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Enable the activity transitions updates when the application resumes
        if (!activityTrackingEnabled)
            enableActivityTransitions();
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
        activityRecognitionPermission.askPermission();

        new HeartRateNotificationChannel().registerChannel(this);

        initSensors();
        initBottomNavBar();
        registerTransitions();

        // Initialize PendingIntent that will be triggered when a activity transition occurs.
        Intent intent = new Intent(TRANSITIONS_RECEIVER_ACTION);
        mActivityTransitionsPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        mTransitionsReceiver = new TransitionsReceiver();

        stepsDao = AppDatabase.getInstance(getApplication()).dailyStepsDao();

        weatherForecastViewModel = new ViewModelProvider(this).get(WeatherForecastViewModel.class);
        stepsViewModel = new ViewModelProvider(this).get(StepsViewModel.class);

        IPMAWeatherClient ipmaWeatherClient = new IPMAWeatherClient();

        new Thread(() -> {

            ipmaWeatherClient.retrieveForecastForCity(1010500, new CityForecastListener() {

                @Override
                public void receiveForecastList(List<WeatherModel> forecast) {


                    ipmaWeatherClient.retrieveWeatherConditionsDescriptions(new WeatherTypesListener() {

                        @Override
                        public void receiveWeatherTypesList(HashMap<Integer, WeatherTypeModel> weatherTypeModelHashMap) {

                            ipmaWeatherClient.retrieveWindConditionsDescriptions(new WindTypesListener() {

                                @Override
                                public void receiveWeatherTypesList(HashMap<String, WindModel> windModelHashMap) {

                                    weatherForecastViewModel.getForecastWeather().setValue(forecast.stream().map(weatherModel -> new WeatherForecastEntity("Aveiro", weatherModel.getForecastDate(), weatherModel.getPrecipitaProb(),
                                            weatherModel.getTMin(), weatherModel.getTMax(), weatherModel.getPredWindDir(),
                                            weatherTypeModelHashMap.get(weatherModel.getIdWeatherType()).getDescIdWeatherTypeEN(), windModelHashMap.get(String.valueOf(weatherModel.getClassWindSpeed())).getWindSpeedDescription(), weatherModel.getLastRefresh()))
                                            .collect(Collectors.toList()));

                                }

                                @Override
                                public void onFailure(Throwable cause) {
                                    Log.w(TAG, "Failed to get wind types list.");
                                }

                            });

                        }

                        @Override
                        public void onFailure(Throwable cause) {
                            Log.w(TAG, "Failed to get weather types list.");
                        }

                    });
                }

                @Override
                public void onFailure(Throwable cause) {
                    Log.w(TAG, "Failed to get weather prediction.");
                }
            });

        }).start();

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

}