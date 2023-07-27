package com.vvv.weatherapplication.adapter;

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
import com.vvv.weatherapplication.model.WeatherModel1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter1 extends RecyclerView.Adapter<WeatherAdapter1.ViewHolder> {
    private final Context context;
    private final ArrayList<WeatherModel1> weatherModel1ArrayList;

    public WeatherAdapter1(Context context, ArrayList<WeatherModel1> weatherModel1ArrayList) {
        this.context = context;
        this.weatherModel1ArrayList = weatherModel1ArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WeatherModel1 model = weatherModel1ArrayList.get(position);
        holder.temperatureTV.setText(model.getTemperature() + "°");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.contitionTV);
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.conditionTop);
        holder.windTV.setText(model.getWindSpeed() + "km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(model.getTime());
            holder.timeTV.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return weatherModel1ArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView windTV;
        private final TextView temperatureTV;
        private final TextView timeTV;
        private final ImageView contitionTV;
        private final ImageView conditionTop;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTV = itemView.findViewById(R.id.IdTvWindspeed);
            temperatureTV = itemView.findViewById(R.id.IdTvTempearture);
            timeTV = itemView.findViewById(R.id.IdTvTime);
            contitionTV = itemView.findViewById(R.id.IDtVCondition);
            conditionTop = itemView.findViewById(R.id.IDtVConditionTop);
        }
    }
}
