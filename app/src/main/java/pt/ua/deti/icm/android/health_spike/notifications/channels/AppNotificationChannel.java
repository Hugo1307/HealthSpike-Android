package pt.ua.deti.icm.android.health_spike.notifications.channels;

import android.content.Context;

public abstract class AppNotificationChannel {

    protected final String channelId;
    protected final String channelName;
    protected final String channelDescription;
    protected final int importance;

    public AppNotificationChannel(String channelId, String channelName, String channelDescription, int importance) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.importance = importance;
    }

    public abstract void registerChannel(Context context);

    public abstract void sendNotification(Context context, String title, String body, boolean update);

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

}
