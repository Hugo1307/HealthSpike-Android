package pt.ua.deti.icm.android.health_spike.weather_api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WindGroupModel {

    @Expose
    @SerializedName("owner")
    private String owner;

    @Expose
    @SerializedName("data")
    private List<WindModel> types;

    public WindGroupModel(String owner, List<WindModel> types) {
        this.owner = owner;
        this.types = types;
    }

    public String getOwner() {
        return owner;
    }

    public List<WindModel> getTypes() {
        return types;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setTypes(List<WindModel> types) {
        this.types = types;
    }

}
