package com.petplant.app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.triggertrap.seekarc.SeekArc;

public class MoistureGraph extends Fragment {
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View root = inflater.inflate(R.layout.moisture_graph,container,false);

        SeekArc moistureArc = (SeekArc)root.findViewById(R.id.moisture_arc);
        final TextView moistureNumber = (TextView)root.findViewById(R.id.moisture_num);

        moistureArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
                moistureNumber.setText(String.valueOf(i)+"%");
            }
            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }
            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
            }
        });

        moistureArc.setProgress(60);

        return root;
    }
}
