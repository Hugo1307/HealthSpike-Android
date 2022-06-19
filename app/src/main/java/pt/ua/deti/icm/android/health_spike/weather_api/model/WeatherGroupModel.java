package pt.ua.deti.icm.android.health_spike.weather_api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherGroupModel {

    @Expose
    @SerializedName("globalIdLocal")
    private int globalIdLocal;

    @Expose
    @SerializedName("dataUpdate")
    private String dataUpdate;

    @Expose
    @SerializedName("data")
    private List<WeatherModel> forecasts;

    public WeatherGroupModel(int globalIdLocal, String dataUpdate, List<WeatherModel> forecasts) {
        this.globalIdLocal = globalIdLocal;
        this.dataUpdate = dataUpdate;
        this.forecasts = forecasts;
    }

    public int getGlobalIdLocal() {
        return globalIdLocal;
    }

    public void setGlobalIdLocal(int globalIdLocal) {
        this.globalIdLocal = globalIdLocal;
    }

    public String getDataUpdate() {
        return dataUpdate;
    }

    public void setDataUpdate(String dataUpdate) {
        this.dataUpdate = dataUpdate;
    }

    public List<WeatherModel> getForecasts() {
        return forecasts;
    }

    public void setForecasts(List<WeatherModel> forecasts) {
        this.forecasts = forecasts;
    }


}
