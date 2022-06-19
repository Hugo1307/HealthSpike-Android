package pt.ua.deti.icm.android.health_spike.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import pt.ua.deti.icm.android.health_spike.weather_api.entities.WeatherForecastEntity;

public class WeatherForecastViewModel extends ViewModel {

    private MutableLiveData<List<WeatherForecastEntity>> forecastWeather;

    public MutableLiveData<List<WeatherForecastEntity>> getForecastWeather() {
        if (forecastWeather == null)
            forecastWeather = new MutableLiveData<>(new ArrayList<>());
        return forecastWeather;
    }

}
