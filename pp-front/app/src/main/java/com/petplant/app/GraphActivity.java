package com.petplant.app;

import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.triggertrap.seekarc.SeekArc;

import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity{
    Toolbar myToolbar;

    FrameLayout moistureFrame;
    FrameLayout illuminationFrame;
    FrameLayout temperatureFrame;

    Fragment moistureFragment,temperatureFragment,illuminationFragment;

    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        myToolbar = (Toolbar)findViewById(R.id.graph_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menuicon);

        final Animation vanishAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha_vanish);

        fm = getSupportFragmentManager();

        fragmentSetting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.graph_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            default:
                DrawerLayout drawer = (DrawerLayout)findViewById(R.id.graph_drawer);
                if(!drawer.isDrawerOpen(Gravity.LEFT)){
                    drawer.openDrawer(Gravity.LEFT);
                }else{
                    drawer.closeDrawer(Gravity.LEFT);
                }
                return super.onOptionsItemSelected(item);
        }
    }

    protected void inflateGraph(){

        //표시할 수치 값
        List<double[]> values = new ArrayList<double[]>();
        values.add(new double[]{
                32, 15
        });

        //그래프 출력을 위한 그래픽 속성 지정객체
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

        mRenderer.setLabelsColor(Color.RED);
        mRenderer.setZoomEnabled(false);
        mRenderer.setZoomButtonsVisible(false);
    }

    private void fragmentSetting(){
        moistureFragment = new MoistureGraph();
        illuminationFragment = new IlluminationGraph();
        temperatureFragment = new TemperatureGraph();

        fm.beginTransaction()
                .add(R.id.moisture_frame,moistureFragment,"moisture")
                .commit();

        fm.beginTransaction()
                .add(R.id.illumination_frame,illuminationFragment,"illumination")
                .commit();

        fm.beginTransaction()
                .add(R.id.temperature_frame,temperatureFragment,"temperature")
                .commit();

        moistureFrame = (FrameLayout)findViewById(R.id.moisture_frame);

        moistureFrame.setOnClickListener(new FrameLayout.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(moistureFragment instanceof MoistureGraph){
                    moistureFragment = new FragTest();
                }else{
                    moistureFragment = new MoistureGraph();
                }

                fm.beginTransaction()
                        .replace(R.id.moisture_frame,moistureFragment)
                        .commit();
            }
        });

        illuminationFrame = (FrameLayout)findViewById(R.id.illumination_frame);

        illuminationFrame.setOnClickListener(new FrameLayout.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(illuminationFragment instanceof IlluminationGraph){
                    illuminationFragment = new FragTest();
                }else{
                    illuminationFragment = new IlluminationGraph();
                }

                fm.beginTransaction()
                        .replace(R.id.illumination_frame,illuminationFragment)
                        .commit();
            }
        });

        temperatureFrame = (FrameLayout)findViewById(R.id.temperature_frame);

        temperatureFrame.setOnClickListener(new FrameLayout.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(temperatureFragment instanceof TemperatureGraph){
                    temperatureFragment = new FragTest();
                }else{
                    temperatureFragment = new TemperatureGraph();
                }


                fm.beginTransaction()
                        .replace(R.id.temperature_frame,temperatureFragment)
                        .commit();
            }
        });

    }
}
