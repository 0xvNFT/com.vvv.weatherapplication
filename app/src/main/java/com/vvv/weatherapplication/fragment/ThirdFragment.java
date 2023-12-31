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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;
import com.vvv.weatherapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ThirdFragment extends Fragment {

    private static final int PERMISSION_CODE = 1;
    private static final String API_KEY = "03dd97a21d0e43f6bd550527232905";
    private static final String BASE_URL = "http://api.weatherapi.com/v1/forecast.json?key=%s&q=%s&days=1&aqi=yes&alerts=yes";
    private RelativeLayout homeRL;
    private String cityName;
    private ProgressBar loadingPB;
    private TextView cityNameTV, temperatureTV, conditionTv;
    private TextInputEditText cityEDT;
    private ImageView iconIV;

    public ThirdFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        homeRL = view.findViewById(R.id.IdRlHome);
        loadingPB = view.findViewById(R.id.IdPbLoading);
        cityNameTV = view.findViewById(R.id.IdTvCityName);
        temperatureTV = view.findViewById(R.id.IdTvTempearture);
        conditionTv = view.findViewById(R.id.IDtVCondition);

        cityEDT = view.findViewById(R.id.Ideditcity);
        view.findViewById(R.id.Idivback);
        iconIV = view.findViewById(R.id.IdIvIcon);
        ImageView searchIV = view.findViewById(R.id.IdIvSearch);


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
            cityName = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(cityName);
        } else {
            cityName = "Hanoi";
            getWeatherInfo(cityName);
        }

        searchIV.setOnClickListener(view1 -> {
            String city = Objects.requireNonNull(cityEDT.getText()).toString();
            if (city.isEmpty()) {
                Toast.makeText(requireContext(), "Please Enter City Name", Toast.LENGTH_SHORT).show();
            } else {
                cityNameTV.setText(cityName);
                getWeatherInfo(city);
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
        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);

                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature + "°");
                    //int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTv.setText(condition);
//                    if (isDay == 1) {
//                        backIV.setBackgroundResource(R.drawable.empty_drawable);
//                    } else {
//                        //backIV.setBackgroundResource(R.drawable.main_night_bg);
//                        Drawable mainNightBgDrawable = getResources().getDrawable(R.drawable.main_night_bg);
//
//                        DisplayMetrics displayMetrics = new DisplayMetrics();
//                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                        int screenWidth = displayMetrics.widthPixels;
//                        int screenHeight = displayMetrics.heightPixels;
//
//                        mainNightBgDrawable.setBounds(0, 0, screenWidth, screenHeight);
//
//                        backIV.setBackground(mainNightBgDrawable);
//                    }
                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forcastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forcastO.getJSONArray("hour");

                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        hourObj.getString("time");
                        hourObj.getString("temp_c");
                        hourObj.getJSONObject("condition").getString("icon");
                        hourObj.getString("wind_kph");

                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(requireContext(), "Please enter valid city", Toast.LENGTH_SHORT).show());
        requestQueue.add(jsonObjectRequest);
    }
}
