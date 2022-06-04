package pt.ua.deti.icm.android.health_spike;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.wear.ambient.AmbientModeSupport;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import pt.ua.deti.icm.android.health_spike.databinding.ActivityMainBinding;
import pt.ua.deti.icm.android.health_spike.permissions.ActivityRecognitionPermission;
import pt.ua.deti.icm.android.health_spike.permissions.AppPermission;
import pt.ua.deti.icm.android.health_spike.permissions.BodySensorsPermission;
import pt.ua.deti.icm.android.health_spike.viewmodels.HeartRateViewModel;

public class MainActivity extends Activity {

    private ActivityMainBinding binding;
    public static final String HR_MESSAGING_PATH = "/heart_rate";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppPermission bodySensorsPermission = new BodySensorsPermission(this);
        bodySensorsPermission.askPermission();

        initSensors();

    }


    private final SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.values == null || sensorEvent.values.length == 0)
                return;

            TextView heartRatePlaceholder = findViewById(R.id.heartRatePlaceholder);
            heartRatePlaceholder.setText(sensorEvent.values[0] + " bpm");

            new Thread(() -> getNodes().stream().findFirst().ifPresent(nodeId -> sendMessage(nodeId, String.valueOf(sensorEvent.values[0])))).start();

            Log.i("HealthSpike", "Heart rate value Changed");

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) { }

    };

    private void initSensors() {

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        if (stepSensor != null)
            sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void sendMessage(String nodeId, String message) {

        if (nodeId == null) {
            Log.i("HealthSpike", "Error forwarding message - node id was null.");
            return;
        }

        Task<Integer> sendTask = Wearable.getMessageClient(this).sendMessage(nodeId, HR_MESSAGING_PATH, message.getBytes(StandardCharsets.UTF_8));

        sendTask.addOnSuccessListener(integer -> Log.i("HealthSpike", "Successfully sent message."));
        sendTask.addOnFailureListener(e -> Log.i("HealthSpike", "Error sending message."));

    }

    private Set<String> getNodes() {

        List<Node> nodes;

        Log.i("HealthSpike", "Getting all nodes");

        try {
            nodes = Tasks.await(Wearable.getNodeClient(this).getConnectedNodes());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.i("HealthSpike", "Error");
            return new HashSet<>();
        }

        Log.i("HealthSpike", "Nodes amount: " + nodes.size());

        return nodes.stream().map(Node::getId).collect(Collectors.toSet());

    }


}
