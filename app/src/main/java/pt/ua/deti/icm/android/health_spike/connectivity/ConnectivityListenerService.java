package pt.ua.deti.icm.android.health_spike.connectivity;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;

import pt.ua.deti.icm.android.health_spike.data.database.AppDatabase;
import pt.ua.deti.icm.android.health_spike.data.entities.HeartRateMeasurement;

public class ConnectivityListenerService extends WearableListenerService {

    private static final String TAG = "HealthSpike";
    private AppDatabase appDatabase;

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {

        if (appDatabase == null)
            appDatabase = AppDatabase.getInstance(getApplicationContext());

        String topic = messageEvent.getPath();
        byte[] messageBytePayload = messageEvent.getData();
        String messagePayload = new String(messageBytePayload, StandardCharsets.UTF_8);

        if (topic.equals(ConnectivityTopics.HEART_RATE_TOPIC.getTopic())) {

            double heartRate = Double.parseDouble(messagePayload);

            if (heartRate == 0) return;
            appDatabase.heartRateMeasurementDao().insertMeasurement(new HeartRateMeasurement(Date.from(Instant.now()), heartRate));

        }

    }

}
