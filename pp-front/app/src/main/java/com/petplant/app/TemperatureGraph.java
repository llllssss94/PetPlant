package com.petplant.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.triggertrap.seekarc.SeekArc;

public class TemperatureGraph extends Fragment {
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.temperature_graph,container,false);

        SeekArc temperatureArc = (SeekArc)root.findViewById(R.id.temperature_arc);
        final TextView temperatureNumber = (TextView)root.findViewById(R.id.temperature_num);

        temperatureArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
                temperatureNumber.setText(String.valueOf(i)+"ºC");
            }
            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }
            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
            }
        });

        temperatureArc.setProgress(30);

        return root;
    }
}
