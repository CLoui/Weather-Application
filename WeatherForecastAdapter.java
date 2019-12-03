package com.example.weatherapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.Common.Common;
import com.example.weatherapp.Model.WeatherForecastResult;
import com.example.weatherapp.R;
import com.squareup.picasso.Picasso;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.MyViewHolder>{

    Context context;
    WeatherForecastResult weatherForecastResult;

    public WeatherForecastAdapter(Context context, WeatherForecastResult weatherForecastResult) {
        this.context = context;
        this.weatherForecastResult = weatherForecastResult;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_weather_forecast, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //Load Icons
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/wn/") // /04n@2x.png
                .append(weatherForecastResult.list.get(position).weather.get(0).getIcon())
                .append(".png").toString()).into(holder.img_weather); //https:// icons

        holder.text_forecast_date.setText(new StringBuilder(Common.convertUnixToForecastDate(weatherForecastResult.list.get(position).dt)));
        holder.text_forecast_time.setText(new StringBuilder(Common.convertUnixToHour(weatherForecastResult.list.get(position).dt)));
        holder.text_details.setText(new StringBuilder(weatherForecastResult.list.get(position).weather.get(0).getDescription()));
        holder.text_temperature.setText((new StringBuilder(String.valueOf(weatherForecastResult.list.get(position).main.getTemp()))).append("°C"));

    }

    @Override
    public int getItemCount() { return weatherForecastResult.list.size(); }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView text_forecast_date, text_forecast_time, text_details, text_temperature;
        ImageView img_weather;
        public MyViewHolder (View itemView) {
            super(itemView);

            img_weather = itemView.findViewById(R.id.img_weather);
            text_forecast_time = itemView.findViewById(R.id.text_forecast_time);
            text_forecast_date = itemView.findViewById(R.id.text_forecast_date);
            text_details = itemView.findViewById(R.id.text_details);
            text_temperature = itemView.findViewById(R.id.text_temperature);

        }

    }
}
