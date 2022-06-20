package pt.ua.deti.icm.android.health_spike.connectivity;


import androidx.annotation.NonNull;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;

import pt.ua.deti.icm.android.health_spike.data.database.AppDatabase;
import pt.ua.deti.icm.android.health_spike.data.entities.ActivityMeasurement;
import pt.ua.deti.icm.android.health_spike.data.entities.HeartRateMeasurement;
import pt.ua.deti.icm.android.health_spike.data.repositories.ActivityMeasurementRepository;
import pt.ua.deti.icm.android.health_spike.notifications.channels.BodyActivityNotificationChannel;
import pt.ua.deti.icm.android.health_spike.notifications.channels.HeartRateNotificationChannel;
import pt.ua.deti.icm.android.health_spike.utils.BodyActivityStatus;

public class ConnectivityListenerService extends WearableListenerService {

    private AppDatabase appDatabase;

    private long lastHRNotificationTimestamp = 0;
    private long lastBodyActivityNotificationTimestamp = 0;

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
            if (heartRate >= 100 && (System.currentTimeMillis() - lastHRNotificationTimestamp) > 30*1000) {

                HeartRateNotificationChannel heartRateNotificationChannel = new HeartRateNotificationChannel();
                heartRateNotificationChannel.registerChannel(this);
                heartRateNotificationChannel.sendNotification(this, "Heart Rate Monitor", "Be Careful! Your heart rate is very high.", true);

                lastHRNotificationTimestamp = System.currentTimeMillis();

            }

            appDatabase.heartRateMeasurementDao().insertMeasurement(new HeartRateMeasurement(Date.from(Instant.now()), heartRate));

        } else if (topic.equals(ConnectivityTopics.ACTIVITY_STATUS_TOPIC.getTopic())) {

            Integer activityStatusId = BodyActivityStatus.getIdByName(messagePayload);

            if (activityStatusId == null) return;

            ActivityMeasurementRepository activityMeasurementRepository = ActivityMeasurementRepository.getInstance(getApplicationContext());

            activityMeasurementRepository.insert(new ActivityMeasurement(Date.from(Instant.now()), activityStatusId));

            Double currentActivityIndex = activityMeasurementRepository.getCurrentActivityIndex();

            if (currentActivityIndex != null && currentActivityIndex <= 1.25 && (System.currentTimeMillis() - lastBodyActivityNotificationTimestamp) > 60*60*1000) {

                BodyActivityNotificationChannel bodyActivityNotificationChannel = new BodyActivityNotificationChannel();
                bodyActivityNotificationChannel.registerChannel(this);
                bodyActivityNotificationChannel.sendNotification(this, "Activity Monitor", "You are still for too long. Maybe it's time to go for a walk?", true);

                lastBodyActivityNotificationTimestamp = System.currentTimeMillis();

            }

        }

    }

}
