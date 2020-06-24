package com.saram.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class nav_ubicacion extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ubicacion, container, false);

        Intent ubicacion = new Intent(getContext(), MapsActivity.class);
        startActivity(ubicacion);

        return view;
    }
}