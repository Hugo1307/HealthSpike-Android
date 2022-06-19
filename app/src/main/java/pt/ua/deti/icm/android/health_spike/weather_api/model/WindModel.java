package pt.ua.deti.icm.android.health_spike.weather_api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WindModel {

    @Expose
    @SerializedName("classWindSpeed")
    private String windSpeedClass;

    @Expose
    @SerializedName("descClassWindSpeedDailyEN")
    private String windSpeedDescription;

    public WindModel(String windSpeedClass, String windSpeedDescription) {
        this.windSpeedClass = windSpeedClass;
        this.windSpeedDescription = windSpeedDescription;
    }

    public String getWindSpeedClass() {
        return windSpeedClass;
    }

    public void setWindSpeedClass(String windSpeedClass) {
        this.windSpeedClass = windSpeedClass;
    }

    public String getWindSpeedDescription() {
        return windSpeedDescription;
    }

    public void setWindSpeedDescription(String windSpeedDescription) {
        this.windSpeedDescription = windSpeedDescription;
    }

}
