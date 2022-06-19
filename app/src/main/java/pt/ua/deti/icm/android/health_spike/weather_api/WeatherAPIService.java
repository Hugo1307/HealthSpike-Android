package pt.ua.deti.icm.android.health_spike.weather_api;

import android.util.Log;


import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pt.ua.deti.icm.android.health_spike.weather_api.entities.WeatherForecastEntity;
import pt.ua.deti.icm.android.health_spike.weather_api.model.CityModel;
import pt.ua.deti.icm.android.health_spike.weather_api.model.WeatherModel;
import pt.ua.deti.icm.android.health_spike.weather_api.model.WeatherTypeModel;
import pt.ua.deti.icm.android.health_spike.weather_api.model.WindModel;
import pt.ua.deti.icm.android.health_spike.weather_api.network.IPMAWeatherClient;
import pt.ua.deti.icm.android.health_spike.weather_api.network.listeners.CityForecastListener;
import pt.ua.deti.icm.android.health_spike.weather_api.network.listeners.CityResultsListener;
import pt.ua.deti.icm.android.health_spike.weather_api.network.listeners.WeatherTypesListener;
import pt.ua.deti.icm.android.health_spike.weather_api.network.listeners.WindTypesListener;


public class WeatherAPIService {

    private static WeatherAPIService instance;

    private final IPMAWeatherClient ipmaWeatherClient;

    private WeatherAPIService() {
        ipmaWeatherClient = new IPMAWeatherClient();
    }

    public void updateAllCities(MutableLiveData<List<CityModel>> mutableLiveData) {

        List<CityModel> allCities = new ArrayList<>();

        ipmaWeatherClient.retrieveCitiesList(new CityResultsListener() {

            @Override
            public void receiveCitiesList(HashMap<String, CityModel> citiesCollection) {
                for (Map.Entry<String, CityModel> city : citiesCollection.entrySet())
                    allCities.add(city.getValue());
                mutableLiveData.setValue(allCities.stream().sorted(Comparator.comparing(CityModel::getLocal)).collect(Collectors.toList()));
            }

            @Override
            public void onFailure(Throwable cause) {
                Log.w("HW2", "Failed to get cities list!");
            }

        });

    }

    public void updateForecastForCity(CityModel city, int forecastIndex, MutableLiveData<WeatherForecastEntity> currentWeather) {

        ipmaWeatherClient.retrieveForecastForCity(city.getGlobalIdLocal(), new CityForecastListener() {

            @Override
            public void receiveForecastList(List<WeatherModel> forecast) {

                if (forecastIndex >= forecast.size()) return;

                WeatherModel currentForecast = forecast.get(forecastIndex);

                ipmaWeatherClient.retrieveWeatherConditionsDescriptions(new WeatherTypesListener() {

                    Map<Integer, WeatherTypeModel> weatherTypeModelMap = new HashMap<>();

                    @Override
                    public void receiveWeatherTypesList(HashMap<Integer, WeatherTypeModel> descriptorsCollection) {

                        weatherTypeModelMap = descriptorsCollection;

                        ipmaWeatherClient.retrieveWindConditionsDescriptions(new WindTypesListener() {

                            Map<String, WindModel> windTypeModelMap = new HashMap<>();

                            @Override
                            public void receiveWeatherTypesList(HashMap<String, WindModel> descriptorsCollection) {

                                windTypeModelMap = descriptorsCollection;
                                WeatherForecastEntity weatherForecastEntity;

                                weatherForecastEntity = new WeatherForecastEntity(city.getLocal(), currentForecast.getForecastDate(), currentForecast.getPrecipitaProb(),
                                        currentForecast.getTMin(), currentForecast.getTMax(), currentForecast.getPredWindDir(),
                                        weatherTypeModelMap.get(currentForecast.getIdWeatherType()).getDescIdWeatherTypeEN(), windTypeModelMap.get(String.valueOf(currentForecast.getClassWindSpeed())).getWindSpeedDescription(), currentForecast.getLastRefresh());

                                currentWeather.setValue(weatherForecastEntity);

                            }

                            @Override
                            public void onFailure(Throwable cause) {
                                Log.w("HW2", "Error getting wind types.");
                            }

                        });

                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        Log.w("HW2", "Error getting weather conditions description");
                    }
                });

            }

            @Override
            public void onFailure(Throwable cause) {
                Log.w("HW2", "Error getting weather forecast.");
            }

        });

    }

    public void updateForecastListForCity(CityModel city, MutableLiveData<List<WeatherModel>> mutableLiveData) {

        ipmaWeatherClient.retrieveForecastForCity(city.getGlobalIdLocal(), new CityForecastListener() {

            @Override
            public void receiveForecastList(List<WeatherModel> forecast) {
                mutableLiveData.setValue(forecast);
            }

            @Override
            public void onFailure(Throwable cause) {
                Log.w("HW2", "Error getting weather forecast.");
            }

        });

    }

    public static WeatherAPIService getInstance() {
        if (instance == null)
            instance = new WeatherAPIService();
        return instance;
    }

}
