package pt.ua.deti.icm.android.health_spike.connectivity;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;

import pt.ua.deti.icm.android.health_spike.data.database.AppDatabase;
import pt.ua.deti.icm.android.health_spike.data.entities.HeartRateMeasurement;
import pt.ua.deti.icm.android.health_spike.notifications.channels.HeartRateNotificationChannel;

public class ConnectivityListenerService extends WearableListenerService {

    private AppDatabase appDatabase;

    private long lastWarningTimestamp = 0;

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

            // Only send notification every 30 seconds
            if (heartRate >= 100 && (System.currentTimeMillis() - lastWarningTimestamp) > 30*1000) {

                HeartRateNotificationChannel heartRateNotificationChannel = new HeartRateNotificationChannel();
                heartRateNotificationChannel.registerChannel(this);
                heartRateNotificationChannel.sendNotification(this, "Heart Rate Monitor", "Be Careful! Your heart rate is very high.", true);

                lastWarningTimestamp = System.currentTimeMillis();

            }

            appDatabase.heartRateMeasurementDao().insertMeasurement(new HeartRateMeasurement(Date.from(Instant.now()), heartRate));

        }

    }

}
