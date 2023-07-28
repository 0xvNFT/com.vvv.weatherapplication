package com.vvv.weatherapplication.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;
import com.vvv.weatherapplication.R;
import com.vvv.weatherapplication.adapter.WeatherAdapter1;
import com.vvv.weatherapplication.adapter.WeatherAdapter2;
import com.vvv.weatherapplication.adapter.WeatherAdapter3;
import com.vvv.weatherapplication.model.WeatherModel1;
import com.vvv.weatherapplication.model.WeatherModel2;
import com.vvv.weatherapplication.model.WeatherModel3;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FirstFragment extends Fragment {

    private static final int PERMISSION_CODE = 1;
    private static final String API_KEY = "03dd97a21d0e43f6bd550527232905";
    private static final String BASE_URL = "http://api.weatherapi.com/v1/forecast.json?key=%s&q=%s&days=1&aqi=yes&alerts=yes";
    private RelativeLayout home;
    private String city_name;
    private ProgressBar loading;
    private TextView cityText, tempText, conditionText;
    private TextInputEditText cityEditText;
    private ImageView iconImage;
    private ArrayList<WeatherModel1> weatherModel1ArrayList1;
    private ArrayList<WeatherModel2> weatherModelArrayList2;
    private ArrayList<WeatherModel3> weatherModelArrayList3;
    private WeatherAdapter1 weatherAdapter1;
    private WeatherAdapter2 weatherAdapter2;
    private WeatherAdapter3 weatherAdapter3;

    public FirstFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        home = view.findViewById(R.id.IdRlHome);
        loading = view.findViewById(R.id.IdPbLoading);
        cityText = view.findViewById(R.id.IdTvCityName);
        tempText = view.findViewById(R.id.IdTvTempearture);
        conditionText = view.findViewById(R.id.IDtVCondition);
        RecyclerView weatherRv1 = view.findViewById(R.id.Idrvweather1);
        RecyclerView weatherRv2 = view.findViewById(R.id.Idrvweather2);
        RecyclerView weatherRv3 = view.findViewById(R.id.Idrvweather3);
        cityEditText = view.findViewById(R.id.Ideditcity);
        view.findViewById(R.id.Idivback);
        iconImage = view.findViewById(R.id.IdIvIcon);
        ImageView searchImageIC = view.findViewById(R.id.IdIvSearch);

        weatherModel1ArrayList1 = new ArrayList<>();
        weatherAdapter1 = new WeatherAdapter1(requireContext(), weatherModel1ArrayList1);
        weatherRv1.setAdapter(weatherAdapter1);

        weatherModelArrayList2 = new ArrayList<>();
        weatherAdapter2 = new WeatherAdapter2(requireContext(), weatherModelArrayList2);
        weatherRv2.setAdapter(weatherAdapter2);

        weatherModelArrayList3 = new ArrayList<>();
        weatherAdapter3 = new WeatherAdapter3(requireContext(), weatherModelArrayList3);
        weatherRv3.setAdapter(weatherAdapter3);

        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            city_name = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(city_name);
        } else {
            city_name = "Hanoi";
            getWeatherInfo(city_name);
        }

        searchImageIC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = Objects.requireNonNull(cityEditText.getText()).toString();
                if (city.isEmpty()) {
                    Toast.makeText(requireContext(), "Please Enter City Name", Toast.LENGTH_SHORT).show();
                } else {
                    cityText.setText(city_name);
                    getWeatherInfo(city);
                }
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission granted....", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Please Provide the permission...", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude) {
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);

            assert addresses != null;
            for (Address adr : addresses) {
                if (adr != null) {
                    String city = adr.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                    } else {
                        Log.d("TAG", "CITY NOT FOUND");
                        //Toast.makeText(requireContext(), "User city not found..", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName) {
        String url = String.format(BASE_URL, API_KEY, cityName);
        cityText.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onResponse(JSONObject response) {
                loading.setVisibility(View.GONE);
                home.setVisibility(View.VISIBLE);
                weatherModel1ArrayList1.clear();
                weatherModelArrayList2.clear();
                weatherModelArrayList3.clear();

                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    tempText.setText(temperature + "°");
                    //int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconImage);
                    conditionText.setText(condition);

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forcastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forcastO.getJSONArray("hour");

                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherModel1ArrayList1.add(new WeatherModel1(time, temper, img, wind));
                        weatherModelArrayList2.add(new WeatherModel2(time, temper, img, wind));
                        weatherModelArrayList3.add(new WeatherModel3(time, temper, img, wind));

                    }
                    weatherAdapter1.notifyDataSetChanged();
                    weatherAdapter2.notifyDataSetChanged();
                    weatherAdapter3.notifyDataSetChanged();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(requireContext(), "Please enter valid city", Toast.LENGTH_SHORT).show());
        requestQueue.add(jsonObjectRequest);
    }
}
