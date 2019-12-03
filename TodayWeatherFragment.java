package com.example.weatherapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.weatherapp.Common.Common;
import com.example.weatherapp.Model.WeatherResult;
import com.example.weatherapp.Retrofit.IOpenWeatherApp;
import com.example.weatherapp.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayWeatherFragment extends Fragment {

    private static TodayWeatherFragment instance;

    private ImageView img_weather;
    private TextView text_city, text_temperature, text_details, text_date, text_windSpeed, text_windDeg, text_pressure,
             text_humidity, text_sunrise, text_sunset, text_coord;
    private LinearLayout weather_panel;
    private ProgressBar loading;

    private CompositeDisposable compositeDisposable;
    private IOpenWeatherApp mService;

    static TodayWeatherFragment getInstance() {
        if (instance == null) {
            instance = new TodayWeatherFragment();
        }
        System.out.println("getInstance - Instance sent to MainActivity");
        return instance;
    }

    public TodayWeatherFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherApp.class);
        System.out.println ("TodayWeatherFragment Running ...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("OnCreateView running ...");
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_today_weather, container, false);

        text_city = itemView.findViewById(R.id.text_city);
        text_temperature = itemView.findViewById(R.id.text_temperature);
        text_details = itemView.findViewById(R.id.text_details);
        text_date = itemView.findViewById(R.id.text_date);
        text_windSpeed = itemView.findViewById(R.id.text_wind);
        text_pressure = itemView.findViewById(R.id.text_pressure);
        text_humidity = itemView.findViewById(R.id.text_humidity);
        text_sunrise = itemView.findViewById(R.id.text_sunrise);
        text_sunset = itemView.findViewById(R.id.text_sunset);
        text_coord = itemView.findViewById(R.id.text_coord);

        img_weather = itemView.findViewById(R.id.img_weather);
        weather_panel = itemView.findViewById(R.id.weather_panel);
        loading = itemView.findViewById(R.id.loading);
        
        System.out.println("OnCreateView - loading ...");
        getWeatherInformation();

        return itemView;
    }

    private void getWeatherInformation() {
        System.out.println ("getWeatherInformation running ...");
        compositeDisposable.add(mService.getWeather(String.valueOf(Common.current_location.getLatitude()), String.valueOf(Common.current_location.getLongitude()),
                                Common.API_ID, "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {

                        // Load Image
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/wn/") // /04n@2x.png
                                .append(weatherResult.getWeather().get(0).getIcon())
                                .append(".png").toString()).into(img_weather); //https:// icons

                        // Load Value of Texts
                        text_city.setText(weatherResult.getName());
                        text_temperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).append("°C").toString());
                        text_details.setText(new StringBuilder("Weather In ").append(weatherResult.getName()).toString());
                        text_date.setText(Common.convertUnixToDate(weatherResult.getDt()));
                        text_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());
                        text_humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append("%").toString());
                        text_sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        text_sunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
                        text_coord.setText(new StringBuilder(weatherResult.getCoord().toString()));

                        // Display Panel
                        weather_panel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                    }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                ));
    }

    public void onStop(){
        compositeDisposable.clear();
        super.onStop();
    }

}
