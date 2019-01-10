package com.petplant.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.triggertrap.seekarc.SeekArc;

public class IlluminationGraph extends Fragment {
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.illumination_graph,container,false);

        SeekArc illuminationArc = (SeekArc)root.findViewById(R.id.illumination_arc);
        final TextView illuminationNumber = (TextView)root.findViewById(R.id.illumination_num);

        illuminationArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
                illuminationNumber.setText(String.valueOf(i)+"%");
            }
            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }
            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
            }
        });

        illuminationArc.setProgress(30);

        return root;
    }
}
