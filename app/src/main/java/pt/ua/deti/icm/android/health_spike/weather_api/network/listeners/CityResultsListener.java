package pt.ua.deti.icm.android.health_spike.weather_api.network.listeners;

import java.util.HashMap;

import pt.ua.deti.icm.android.health_spike.weather_api.model.CityModel;

public interface CityResultsListener {

    void receiveCitiesList(HashMap<String, CityModel> citiesCollection);
    void onFailure(Throwable cause);


}
