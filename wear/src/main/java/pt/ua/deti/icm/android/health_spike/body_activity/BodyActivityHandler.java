package pt.ua.deti.icm.android.health_spike.body_activity;

public class BodyActivityHandler {

    private static BodyActivityHandler instance;

    private BodyActivityStatus activityStatus;

    private BodyActivityHandler() { }

    public void calculateActivityStatus(float[] oldValues, float[] newValues) {

        double xChange = Math.abs(oldValues[0] - newValues[0]);
        double yChange = Math.abs(oldValues[1] - newValues[1]);
        double zChange = Math.abs(oldValues[2] - newValues[2]);

        if (xChange > 2 && yChange > 2 && zChange > 2 || xChange > 4 || yChange > 4 || zChange > 4) {
            activityStatus = BodyActivityStatus.HIGH_ACTIVITY;
        } else if (xChange > 1 && yChange > 1 && zChange > 1  || xChange > 2 || yChange > 2 || zChange > 2) {
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
