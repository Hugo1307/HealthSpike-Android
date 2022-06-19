package pt.ua.deti.icm.android.health_spike.weather_api.entities;

import java.util.Date;

public class WeatherForecastEntity {

    private final String cityName;
    private final String forecastDate;
    private final double precipitationProb;
    private final double minTemp;
    private final double maxTemp;
    private final String windDirection;
    private final String weatherType;
    private final String windSpeed;
    private final Date lastRefresh;

    public WeatherForecastEntity(String cityName, String forecastDate, double precipitationProb, double minTemp, double maxTemp, String windDirection, String weatherType, String windSpeed, Date lastRefresh) {
        this.cityName = cityName;
        this.forecastDate = forecastDate;
        this.precipitationProb = precipitationProb;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.windDirection = windDirection;
        this.weatherType = weatherType;
        this.windSpeed = windSpeed;
        this.lastRefresh = lastRefresh;
    }

    public String getCityName() {
        return cityName;
    }

    public String getForecastDate() {
        return forecastDate;
    }

    public double getPrecipitationProb() {
        return precipitationProb;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getWeatherType() {
        return weatherType;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public Date getLastRefresh() {
        return lastRefresh;
    }
}
