package com.vvv.weatherapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vvv.weatherapplication.R;
import com.vvv.weatherapplication.model.WeatherModel3;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter3 extends RecyclerView.Adapter<WeatherAdapter3.ViewHolder> {
    private final Context context;
    private final ArrayList<WeatherModel3> weatherRvModelArrayList3;

    public WeatherAdapter3(Context context, ArrayList<WeatherModel3> weatherRvModelArrayList3) {
        this.context = context;
        this.weatherRvModelArrayList3 = weatherRvModelArrayList3;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item3, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WeatherModel3 model = weatherRvModelArrayList3.get(position);
        holder.temperatureTV.setText(model.getTemperature() + "°");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.contitionTV);
        //Picasso.get().load("http:".concat(model.getIcon())).into(holder.conditionTop);
        //holder.windTV.setText(model.getWindSpeed() + "km/h");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(model.getTime());
            assert t != null;
            holder.timeTV.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return weatherRvModelArrayList3.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView temperatureTV;
        private final TextView timeTV;
        private final ImageView contitionTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            temperatureTV = itemView.findViewById(R.id.IdTvTempearture);
            timeTV = itemView.findViewById(R.id.IdTvTime);
            contitionTV = itemView.findViewById(R.id.IDtVCondition);
        }
    }
}
