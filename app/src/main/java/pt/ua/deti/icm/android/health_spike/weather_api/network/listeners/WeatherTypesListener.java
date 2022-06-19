package pt.ua.deti.icm.android.health_spike.weather_api.network.listeners;

import java.util.HashMap;

import pt.ua.deti.icm.android.health_spike.weather_api.model.WeatherTypeModel;

public interface WeatherTypesListener {

    void receiveWeatherTypesList(HashMap<Integer, WeatherTypeModel> descriptorsCollection);
    void onFailure(Throwable cause);

}
