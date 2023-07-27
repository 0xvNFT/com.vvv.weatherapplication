package com.vvv.weatherapplication.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.vvv.weatherapplication.R;

public class FourthFragment extends Fragment {

    private ImageView tg, z, fb, ms;

    public FourthFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);

        tg = view.findViewById(R.id.tg);
        z = view.findViewById(R.id.z);
        fb = view.findViewById(R.id.fb);
        ms = view.findViewById(R.id.ms);

        tg.setOnClickListener(v -> openWebpage("https://www.telegram.com/"));
        z.setOnClickListener(v -> openWebpage("https://www.zalo.com/"));
        fb.setOnClickListener(v -> openWebpage("https://www.facebook.com/"));
        ms.setOnClickListener(v -> openWebpage("https://www.messenger.com/"));
        return view;
    }

    public void openWebpage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}