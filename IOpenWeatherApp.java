package com.example.weatherapp.Retrofit;

import com.example.weatherapp.Model.WeatherForecastResult;
import com.example.weatherapp.Model.WeatherResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherApp {
    @GET("weather")
    Observable<WeatherResult> getWeather(@Query("lat") String lat,
                                         @Query("lon") String lon,
                                         @Query("appid") String appid,
                                         @Query("units") String units);

    @GET("forecast")
    Observable<WeatherForecastResult> getWeatherForecast(@Query("lat") String lat,
                                                         @Query("lon") String lon,
                                                         @Query("appid") String appid,
                                                         @Query("units") String units);
}
