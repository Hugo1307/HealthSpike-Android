package pt.ua.deti.icm.android.health_spike.notifications.channels;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.List;

import pt.ua.deti.icm.android.health_spike.R;

public class BodyActivityNotificationChannel extends AppNotificationChannel {

    public static final List<Integer> notificationsIds = new ArrayList<>();

    public BodyActivityNotificationChannel() {
        super("HealthSpike_BodyActivity_Channel", "Activity Alerts", "Alerts for user's body activity", NotificationManager.IMPORTANCE_LOW);
    }

    @Override
    public void sendNotification(Context context, String title, String body, boolean update) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.large_healthspike)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setPriority(importance);

        int nextNotificationId = notificationsIds.size();

        notificationManager.notify(nextNotificationId, builder.build());

        if (!update) {
            notificationsIds.add(nextNotificationId);
        }

    }

}
