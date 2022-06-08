package pt.ua.deti.icm.android.health_spike;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import pt.ua.deti.icm.android.health_spike.data.dao.DailyStepsDao;
import pt.ua.deti.icm.android.health_spike.data.database.AppDatabase;
import pt.ua.deti.icm.android.health_spike.data.entities.DailySteps;
import pt.ua.deti.icm.android.health_spike.fragments.DashboardFragment;
import pt.ua.deti.icm.android.health_spike.fragments.DistanceFragment;
import pt.ua.deti.icm.android.health_spike.fragments.HeartRateFragment;
import pt.ua.deti.icm.android.health_spike.fragments.StepsFragment;
import pt.ua.deti.icm.android.health_spike.permissions.ActivityRecognitionPermission;
import pt.ua.deti.icm.android.health_spike.permissions.AppPermission;
import pt.ua.deti.icm.android.health_spike.viewmodels.PedometerViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "HealthSpike";
    private static final int dailyStepsGoal = 10000;

    private PedometerViewModel pedometerViewModel;

    private TransitionsReceiver mTransitionsReceiver;
    private List<ActivityTransition> activityTransitionList;
    private PendingIntent mActivityTransitionsPendingIntent;
    private boolean activityTrackingEnabled;

    private DailyStepsDao dailyStepsDao;

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

        addPedometerObservers();
        initSensors();
        initBottomNavBar();
        registerTransitions();

        // Initialize PendingIntent that will be triggered when a activity transition occurs.
        Intent intent = new Intent(TRANSITIONS_RECEIVER_ACTION);
        mActivityTransitionsPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        mTransitionsReceiver = new TransitionsReceiver();

        dailyStepsDao = AppDatabase.getInstance(getApplication()).dailyStepsDao();

    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.values == null || sensorEvent.values.length == 0)
                return;
            dailyStepsDao.addStep();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

            if (dailyStepsDao.getTodaySteps().getValue() == null) {

                dailyStepsDao.createDailyStepsRegistry(
                    new DailySteps(null, Date.from(LocalDateTime.now()
                            .with(LocalDateTime.MIN)
                            .atZone(ZoneId.systemDefault()).toInstant()), 0)
                );

            }

        }

    };

    private void initSensors() {

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor != null)
            sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void addPedometerObservers() {

        pedometerViewModel = new ViewModelProvider(this).get(PedometerViewModel.class);

        pedometerViewModel.getDailySteps().observe(this, (value) -> {

            TextView mainPanelStepsCount = findViewById(R.id.mainPanelStepsPlaceholder);
            TextView progressBarStepsLeftPlaceholder = findViewById(R.id.progressBarStepsLeftPlaceholder);
            ProgressBar progressBarStepsLeft = findViewById(R.id.progressBarStepsLeft);

            if (value == null) return;

            if (mainPanelStepsCount == null || progressBarStepsLeftPlaceholder == null || progressBarStepsLeft == null) return;

            int dailyStepsCount = value.stepsCount;

            mainPanelStepsCount.setText(String.valueOf(dailyStepsCount));
            progressBarStepsLeft.setMax(dailyStepsGoal);

            if (dailyStepsGoal > dailyStepsCount) {
                progressBarStepsLeftPlaceholder.setText(String.valueOf(dailyStepsGoal - dailyStepsCount));
                progressBarStepsLeft.setProgress(dailyStepsGoal -(dailyStepsGoal - dailyStepsCount));
            } else {
                progressBarStepsLeftPlaceholder.setText(String.valueOf(dailyStepsCount));
                progressBarStepsLeft.setProgress(dailyStepsGoal);
            }

        });

        pedometerViewModel.getIsWalking().observe(this, (value) -> {
            ImageView mainPanelStepsCount = findViewById(R.id.walkingStatusImage);
            if (value)
                mainPanelStepsCount.setImageResource(R.drawable.walking);
            else
                mainPanelStepsCount.setImageResource(R.drawable.stopped);
        });

    }

    private void initBottomNavBar() {

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

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

        task.addOnSuccessListener(
                result -> {
                    activityTrackingEnabled = true;
                    // Toast.makeText(getApplicationContext(), "Transitions Api was successfully registered", Toast.LENGTH_SHORT).show();
                });

        task.addOnFailureListener(
                exception -> {
                    // Toast.makeText(getApplicationContext(), "Transitions Api could NOT be registered: " + exception, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Transitions Api could NOT be registered: " + exception);
                });

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
                        pedometerViewModel.getIsWalking().setValue(true);
                    } else if (event.getActivityType() == DetectedActivity.WALKING && event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                        pedometerViewModel.getIsWalking().setValue(false);
                    }

                }

            }

        }

    }

}