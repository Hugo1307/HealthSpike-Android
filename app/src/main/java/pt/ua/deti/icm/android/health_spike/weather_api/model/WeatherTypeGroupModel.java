package pt.ua.deti.icm.android.health_spike.weather_api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class WeatherTypeGroupModel {

    @Expose
    @SerializedName("owner")
    private String owner;

    @Expose
    @SerializedName("data")
    private List<WeatherTypeModel> types;

    public WeatherTypeGroupModel(String owner, List<WeatherTypeModel> types) {
        this.owner = owner;
        this.types = types;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<WeatherTypeModel> getTypes() {
        return types;
    }

    public void setTypes(List<WeatherTypeModel> types) {
        this.types = types;
    }

    @Override
    public String toString() {
        return "WeatherTypeParent{" +
                "owner=" + owner +
                ", types=" + types +
                '}';
    }


}
