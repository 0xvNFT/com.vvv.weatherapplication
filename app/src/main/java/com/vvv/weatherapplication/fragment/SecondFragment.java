package com.vvv.weatherapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.vvv.weatherapplication.R;

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


        LatLng hanoiLocation = new LatLng(21.0285, 105.8542);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hanoiLocation, 10f));

        LatLng hanoiLocations = new LatLng(21.0285, 105.8542);
        googleMap.addMarker(new MarkerOptions().position(hanoiLocations).title("Hanoi"));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(hanoiLocations);
        LatLngBounds bounds = builder.build();
        int padding = 100;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cu);

        addWeatherOverlay();
    }

    private void addWeatherOverlay() {

        List<LatLng> latLngList = new ArrayList<>();
        latLngList.add(new LatLng(37.7749, -122.4194));


        HeatmapTileProvider heatmapTileProvider = new HeatmapTileProvider.Builder()
                .data(latLngList)
                .build();

        TileOverlay tileOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));
    }
}
