package com.vvv.weatherapplication.fragment;

import android.annotation.SuppressLint;
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
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.vvv.weatherapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(requireContext(), "Google Play Services is not available on this device.", Toast.LENGTH_LONG).show();
            return;
        }

        requestCurrentLocation();
//        LatLng hanoiLocation = new LatLng(21.0285, 105.8542);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hanoiLocation, 10f));
//
//        LatLng hanoiLocations = new LatLng(21.0285, 105.8542);
//        googleMap.addMarker(new MarkerOptions().position(hanoiLocations).title("Hanoi"));

//        googleMap.setOnMapLoadedCallback(() -> {
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
////            builder.include(hanoiLocations);
//            LatLngBounds bounds = builder.build();
//            int padding = 100;
//            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//
//            googleMap.moveCamera(cu);
//        });

        addWeatherOverlay();

        EditText searchEditText = getView().findViewById(R.id.searchEditText);
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

        Button btnSearch = getView().findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            String locationName = searchEditText.getText().toString().trim();
            if (!locationName.isEmpty()) {
                searchLocation(locationName);
            } else {
                Toast.makeText(requireContext(), "Please enter a location name.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addWeatherOverlay() {

        List<LatLng> latLngList = new ArrayList<>();
        latLngList.add(new LatLng(37.7749, -122.4194));


        HeatmapTileProvider heatmapTileProvider = new HeatmapTileProvider.Builder()
                .data(latLngList)
                .build();

        TileOverlay tileOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(requireContext());
        return resultCode == ConnectionResult.SUCCESS;
    }

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
                    } else {
                        Toast.makeText(requireContext(), "Failed to get current location.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to get current location: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

}