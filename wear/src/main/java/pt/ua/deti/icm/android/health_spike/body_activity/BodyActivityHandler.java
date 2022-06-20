package pt.ua.deti.icm.android.health_spike.body_activity;

public class BodyActivityHandler {

    private static BodyActivityHandler instance;

    private BodyActivityStatus activityStatus;

    private BodyActivityHandler() { }

    public void calculateActivityStatus(float[] oldValues, float[] newValues) {

        double xChange = Math.abs(oldValues[0] - newValues[0]);
        double yChange = Math.abs(oldValues[1] - newValues[1]);
        double zChange = Math.abs(oldValues[2] - newValues[2]);

        if (xChange > 5 && yChange > 5 && zChange > 5 || xChange > 6 || yChange > 6 || zChange > 6) {
            activityStatus = BodyActivityStatus.HIGH_ACTIVITY;
        } else if (xChange > 2 && yChange > 2 && zChange > 2  || xChange > 3 || yChange > 3 || zChange > 3) {
            activityStatus = BodyActivityStatus.MEDIUM_ACTIVITY;
        } else {
            activityStatus = BodyActivityStatus.LOW_ACTIVITY;
        }

    }

    public BodyActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public static BodyActivityHandler getInstance() {
        if (instance == null)
            instance = new BodyActivityHandler();
        return instance;
    }

}
