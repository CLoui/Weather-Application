package com.example.weatherapp;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.Adapter.WeatherForecastAdapter;
import com.example.weatherapp.Common.Common;
import com.example.weatherapp.Model.WeatherForecastResult;
import com.example.weatherapp.Retrofit.IOpenWeatherApp;
import com.example.weatherapp.Retrofit.RetrofitClient;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    private CompositeDisposable compositeDisposable;
    private IOpenWeatherApp mService;

    private TextView text_city, text_coord;
    private RecyclerView recyclerView;
    private static ForecastFragment instance;

    public static ForecastFragment getInstance() {
        if(instance == null) {
            instance = new ForecastFragment();
        }
        return instance;
    }

    public ForecastFragment() {

        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherApp.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_forecast, container, false);

        text_city = itemView.findViewById(R.id.text_city_name);
        text_coord = itemView.findViewById(R.id.text_geo_coord);

        recyclerView = itemView.findViewById(R.id.recycler_forecast);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        getForecastInformation();
        return itemView;
    }

    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    private void getForecastInformation() {

        compositeDisposable.add(mService.getWeatherForecast(String.valueOf(Common.current_location.getLatitude()), String.valueOf(Common.current_location.getLongitude()),
                Common.API_ID, "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                        displayForecastWeather(weatherForecastResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("ERROR/getForecastInformation", Objects.requireNonNull(throwable.getMessage()));
                    }
                })
        );
    }

    private void displayForecastWeather(WeatherForecastResult weatherForecastResult) {
        text_city.setText(new StringBuilder(weatherForecastResult.city.name));
        text_coord.setText(new StringBuilder(weatherForecastResult.city.coord.toString()));

        WeatherForecastAdapter adapter = new WeatherForecastAdapter(getContext(), weatherForecastResult);
        recyclerView.setAdapter(adapter);
    }

}
