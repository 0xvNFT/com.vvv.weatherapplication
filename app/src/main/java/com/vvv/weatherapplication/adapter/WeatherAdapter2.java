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
import com.vvv.weatherapplication.model.WeatherModel2;

import java.util.ArrayList;

public class WeatherAdapter2 extends RecyclerView.Adapter<WeatherAdapter2.ViewHolder> {
    private final Context context;
    private final ArrayList<WeatherModel2> weatherRvModelArrayList1;

    public WeatherAdapter2(Context context, ArrayList<WeatherModel2> weatherRvModelArrayList1) {
        this.context = context;
        this.weatherRvModelArrayList1 = weatherRvModelArrayList1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WeatherModel2 model = weatherRvModelArrayList1.get(position);
        holder.temperatureTV.setText(model.getTemperature() + "°");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.contitionTV);
        //Picasso.get().load("http:".concat(model.getIcon())).into(holder.conditionTop);
//        holder.windTV.setText(model.getWindSpeed() + "km/h");
//        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
//        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
//        try {
//            Date t = input.parse(model.getTime());
//            holder.timeTV.setText(output.format(t));
//        }catch (ParseException e){
//            e.printStackTrace();
//        }

    }

    @Override
    public int getItemCount() {
        return weatherRvModelArrayList1.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView windTV;
        private final TextView temperatureTV;
        private TextView timeTV;
        private final ImageView contitionTV;
        private ImageView conditionTop;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //windTV = itemView.findViewById(R.id.IdTvWindspeed);
            temperatureTV = itemView.findViewById(R.id.IdTvTempearture);
            //timeTV = itemView.findViewById(R.id.IdTvTime);
            contitionTV = itemView.findViewById(R.id.IDtVCondition);
            //conditionTop = itemView.findViewById(R.id.IDtVConditionTop);
        }
    }
}
