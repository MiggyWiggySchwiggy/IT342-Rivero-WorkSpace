package edu.cit.rivero.workspace.features.space;

public class WeatherDto {
    private Double temperature;
    private Integer weatherCode;

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(Integer weatherCode) {
        this.weatherCode = weatherCode;
    }
}
