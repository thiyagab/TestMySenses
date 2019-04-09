package com.droidapps.shakysnake;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.droidapps.testmysenses.R;

public class SnakeGameActivity extends com.codepath.simplegame.GameActivity implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor accelerometer;
    protected SensorEventListener sensorEventListener;
    private float currentSample;


    private float x, y, z;

    private long lastUpdate = -1;
    private float valueToCompare=-1;


    static final int INIT=-1;
    static final int RESET=-2;

    SnakeGamePanel gamePanel;



	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		
		//before activity is created : cold start
        //switch back to original Theme (App Theme)
        setTheme(R.style.AppTheme);

		switchFullscreen();
		gamePanel=new SnakeGamePanel(this);
        setContentView(gamePanel);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}


    @Override
    protected void onResume() {
        super.onResume();
        register(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        register(false);

    }

    public void register(boolean register){
        if(register) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }else{
            sensorManager.unregisterListener(this);
            valueToCompare=INIT;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms, otherwise updates
            // come way too fast and the phone gets bogged down
            // with garbage collection
//            if (lastUpdate == -1 || (curTime - lastUpdate) > 100) {
//                lastUpdate = curTime;

                x = sensorEvent.values[0];
                y = sensorEvent.values.length > 1 ? sensorEvent.values[1] : 0;
                z = sensorEvent.values.length > 2 ? sensorEvent.values[2] : 0;
                float abs = new Float(Math.sqrt(x * x + y * y + z * z));
                currentSample = x;
                if (valueToCompare == INIT) {
                    valueToCompare = currentSample;
                }
                float diff= (valueToCompare-currentSample)*10;
                if(valueToCompare!=INIT && valueToCompare!=RESET){
                    valueChanged(diff);
                }
//            }
        }
    }


    public void valueChanged(float diff){
        gamePanel.updateSnakePositionOffset(diff);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
