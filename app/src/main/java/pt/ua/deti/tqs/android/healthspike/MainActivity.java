package pt.ua.deti.tqs.android.healthspike;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pt.ua.deti.tqs.android.healthspike.fragments.DashboardFragment;
import pt.ua.deti.tqs.android.healthspike.permissions.ActivityRecognitionPermission;
import pt.ua.deti.tqs.android.healthspike.permissions.AppPermission;
import pt.ua.deti.tqs.android.healthspike.viewmodels.PedometerViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "HealthSpike";
    private static final int dailySteps = 10000;

    private PedometerViewModel pedometerViewModel;

    private TransitionsReceiver mTransitionsReceiver;
    private List<ActivityTransition> activityTransitionList;
    private PendingIntent mActivityTransitionsPendingIntent;
    private boolean activityTrackingEnabled;

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
        registerTransitions();

        // Initialize PendingIntent that will be triggered when a activity transition occurs.
        Intent intent = new Intent(TRANSITIONS_RECEIVER_ACTION);
        mActivityTransitionsPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        mTransitionsReceiver = new TransitionsReceiver();

    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.values == null || sensorEvent.values.length == 0)
                return;
            pedometerViewModel.getStepsCount().setValue((int) sensorEvent.values[0]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) { }

    };

    private void initSensors() {

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor != null)
            sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void addPedometerObservers() {

        pedometerViewModel = new ViewModelProvider(this).get(PedometerViewModel.class);

        pedometerViewModel.getStepsCount().observe(this, (value) -> {

            TextView mainPanelStepsCount = findViewById(R.id.mainPanelStepsPlaceholder);
            TextView progressBarStepsLeftPlaceholder = findViewById(R.id.progressBarStepsLeftPlaceholder);
            ProgressBar progressBarStepsLeft = findViewById(R.id.progressBarStepsLeft);

            mainPanelStepsCount.setText(String.valueOf(value));
            progressBarStepsLeft.setMax(dailySteps);

            if (dailySteps > value) {
                progressBarStepsLeftPlaceholder.setText(String.valueOf(dailySteps-value));
                progressBarStepsLeft.setProgress(dailySteps-(dailySteps-value));
            } else {
                progressBarStepsLeftPlaceholder.setText(String.valueOf(value));
                progressBarStepsLeft.setProgress(dailySteps);
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