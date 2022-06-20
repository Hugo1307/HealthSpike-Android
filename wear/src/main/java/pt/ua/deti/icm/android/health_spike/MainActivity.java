package pt.ua.deti.icm.android.health_spike;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import pt.ua.deti.icm.android.health_spike.body_activity.BodyActivityHandler;
import pt.ua.deti.icm.android.health_spike.body_activity.BodyActivityStatus;
import pt.ua.deti.icm.android.health_spike.body_activity.SyncBodyActivityWorker;
import pt.ua.deti.icm.android.health_spike.connectivity.ConnectivityHelper;
import pt.ua.deti.icm.android.health_spike.connectivity.ConnectivityTopics;
import pt.ua.deti.icm.android.health_spike.databinding.ActivityMainBinding;
import pt.ua.deti.icm.android.health_spike.permissions.AppPermission;
import pt.ua.deti.icm.android.health_spike.permissions.BodySensorsPermission;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pt.ua.deti.icm.android.health_spike.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppPermission bodySensorsPermission = new BodySensorsPermission(this);
        bodySensorsPermission.askPermission();

        initSensors();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String value = BodyActivityHandler.getInstance().getActivityStatus() != null ? BodyActivityHandler.getInstance().getActivityStatus().toString() : BodyActivityStatus.LOW_ACTIVITY.toString();

                new Thread(() -> {
                    ConnectivityHelper connectivityHelper = new ConnectivityHelper(getApplicationContext());
                    connectivityHelper.getNodes()
                            .stream()
                            .findFirst()
                            .ifPresent(nodeId -> connectivityHelper.sendMessage(ConnectivityTopics.ACTIVITY_STATUS_TOPIC.getTopic(), nodeId, value));
                }).start();

                Log.i("HealthSpike", "SENDING ACTIVITY STATUS CHANGED.");
            }
        }, 0, 1000);//put here time 1000 milliseconds=1 second

    }

    private final SensorEventListener heartRateSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.values == null || sensorEvent.values.length == 0)
                return;

            TextView heartRatePlaceholder = findViewById(R.id.heartRatePlaceholder);
            heartRatePlaceholder.setText(sensorEvent.values[0] + " bpm");

            new Thread(() -> {
                ConnectivityHelper connectivityHelper = new ConnectivityHelper(getApplicationContext());
                connectivityHelper.getNodes()
                        .stream()
                        .findFirst()
                        .ifPresent(nodeId -> connectivityHelper.sendMessage(ConnectivityTopics.HEART_RATE_TOPIC.getTopic(), nodeId, String.valueOf(sensorEvent.values[0])));
            }).start();

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

    };

    private final SensorEventListener accelerometerSensorEventListener = new SensorEventListener() {

        private float[] oldValues = null;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            if (sensorEvent.values == null || sensorEvent.values.length != 3)
                return;

            float[] newValues = new float[3];

            if (oldValues == null) {
                oldValues = new float[]{sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]};
            }

            newValues[0] = sensorEvent.values[0];
            newValues[1] = sensorEvent.values[1];
            newValues[2] = sensorEvent.values[2];

            BodyActivityHandler.getInstance().calculateActivityStatus(oldValues, newValues);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

    };

    private void initSensors() {

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (stepSensor != null)
            sensorManager.registerListener(heartRateSensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);

        if (accelerometerSensor != null)
            sensorManager.registerListener(accelerometerSensorEventListener, accelerometerSensor, 5 * 1000000);

    }


}
