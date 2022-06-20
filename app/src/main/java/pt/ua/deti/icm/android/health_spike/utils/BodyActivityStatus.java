package pt.ua.deti.icm.android.health_spike.utils;

public enum BodyActivityStatus {

    LOW_ACTIVITY(0), MEDIUM_ACTIVITY(1), HIGH_ACTIVITY(2);

    private final int id;

    BodyActivityStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Integer getIdByName(String name) {

        for (BodyActivityStatus bodyActivityStatus : BodyActivityStatus.values()) {
            if (bodyActivityStatus.name().equalsIgnoreCase(name)) {
                return bodyActivityStatus.getId();
            }
        }

        return null;

    }

}
