package pt.ua.deti.icm.android.health_spike.weather_api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CityGroupModel {

    @Expose
    @SerializedName("owner")
    private String owner;

    @Expose
    @SerializedName("country")
    private String country;

    @Expose
    @SerializedName( "data")
    private List<CityModel> cities;

    public CityGroupModel(String owner, String country, List<CityModel> cities) {
        this.owner = owner;
        this.country = country;
        this.cities = cities;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<CityModel> getCities() {
        return cities;
    }

    public void setCities(List<CityModel> cities) {
        this.cities = cities;
    }


}
