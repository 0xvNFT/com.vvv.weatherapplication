package com.vvv.weatherapplication.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.vvv.weatherapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class SecondFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;

    public SecondFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(requireContext(), "Google Play Services is not available on this device.", Toast.LENGTH_LONG).show();
            return;
        }

        requestCurrentLocation();
        //addWeatherOverlay();

        EditText searchEditText = requireView().findViewById(R.id.searchEditText);
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String locationName = searchEditText.getText().toString().trim();
                if (!locationName.isEmpty()) {
                    searchLocation(locationName);
                    return true;
                } else {
                    Toast.makeText(requireContext(), "Please enter a location name.", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });

        Button btnSearch = requireView().findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            String locationName = searchEditText.getText().toString().trim();
            if (!locationName.isEmpty()) {
                searchLocation(locationName);
            } else {
                Toast.makeText(requireContext(), "Please enter a location name.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //    private boolean isGooglePlayServicesAvailable() {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(requireContext());
//        return resultCode == ConnectionResult.SUCCESS;
//    }
    private void searchLocation(String locationName) {
        Geocoder geocoder = new Geocoder(requireContext());
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();
                LatLng location = new LatLng(latitude, longitude);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f));
                googleMap.addMarker(new MarkerOptions().position(location).title(locationName));
                addWeatherOverlay(latitude, longitude);
            } else {
                Toast.makeText(requireContext(), "Location not found.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to search for location.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestCurrentLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10f));

                        googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));

                        addWeatherOverlay(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(requireContext(), "Failed to get current location.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to get current location: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void addWeatherOverlay(double latitude, double longitude) {
        String WEATHER_API_KEY = "03dd97a21d0e43f6bd550527232905";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.weatherapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApiService weatherApiService = retrofit.create(WeatherApiService.class);
        Call<WeatherResponse> call = weatherApiService.getWeather(WEATHER_API_KEY, latitude, longitude);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    double temperature = weatherResponse.getTemperature();

                    List<LatLng> latLngList = new ArrayList<>();
                    latLngList.add(new LatLng(latitude, longitude));

                    int[] colors = {
                            Color.rgb(0, 0, 255),
                            Color.rgb(0, 255, 0),
                            Color.rgb(255, 255, 0),
                            Color.rgb(255, 0, 0)
                    };

                    float[] temperatureThresholds = {0f, 10f, 20f, 30f};

                    HeatmapTileProvider heatmapTileProvider = new HeatmapTileProvider.Builder()
                            .data(latLngList)
                            .gradient(new Gradient(colors, temperatureThresholds))
                            .build();

                    googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));

                    LatLng location = new LatLng(latitude, longitude);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f));
                    googleMap.addMarker(new MarkerOptions().position(location).title("Temperature: " + temperature));
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch weather data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Failed to fetch weather data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(requireContext());
        return resultCode == ConnectionResult.SUCCESS;
    }

    public interface WeatherApiService {
        @GET("current.json")
        Call<WeatherResponse> getWeather(@Query("key") String apiKey, @Query("q") double latitude, @Query("q") double longitude);
    }

    public static class WeatherResponse {
        @SerializedName("current")
        private CurrentWeather currentWeather;

        public WeatherResponse(CurrentWeather currentWeather) {
            this.currentWeather = currentWeather;
        }

        public double getTemperature() {
            return currentWeather.getTemperature();
        }

        private static class CurrentWeather {
            @SerializedName("temp_c")
            private double temperature;

            public double getTemperature() {
                return temperature;
            }
        }
    }
}