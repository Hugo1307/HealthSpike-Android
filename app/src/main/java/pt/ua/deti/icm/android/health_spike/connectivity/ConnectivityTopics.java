package pt.ua.deti.icm.android.health_spike.connectivity;

public enum ConnectivityTopics {

    HEART_RATE_TOPIC("/heart_rate");

    private final String topic;

    ConnectivityTopics(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

}
