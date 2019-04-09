package com.droidapps.testmysenses;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.droidapps.shakysnake.SnakeGameActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.xw.repo.BubbleSeekBar;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor accelerometer;
    protected SensorEventListener sensorEventListener;
    private float currentSample;


    private float x, y, z;

    private long lastUpdate = -1;
    private float valueToCompare=-1;
    private Vibrator vibrator;
    int threshold=50;
    private Chronometer chronometer;
    static final int INIT=-1;
    static final int RESET=-2;
    private LineChart mChart;
    private ImageButton startButton;


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

    @Override // SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override // SensorEventListener
    //public void onSensorChanged(SensorEvent sensorEvent, float[] values) {
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms, otherwise updates
            // come way too fast and the phone gets bogged down
            // with garbage collection
            if (lastUpdate == -1 || (curTime - lastUpdate) > 100) {
                lastUpdate = curTime;

                x = sensorEvent.values[0];
                y = sensorEvent.values.length > 1 ? sensorEvent.values[1] : 0;
                z = sensorEvent.values.length > 2 ? sensorEvent.values[2] : 0;
                float abs = new Float(Math.sqrt(x * x + y * y + z * z));
                currentSample = abs;
                if (valueToCompare == RESET) {
                    valueToCompare = currentSample;
                }
               float diff= Math.abs(valueToCompare-currentSample)*100;
                if(valueToCompare!=INIT && valueToCompare!=RESET){
                    addEntry(diff);
                }

                checkAndStop(diff);
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startButton =findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Started", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                start();

            }
        });
        BubbleSeekBar seekBar=
                ((BubbleSeekBar)findViewById(R.id.seekBar));
        seekBar.setProgress(threshold);
        seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                if(fromUser) {
                    threshold=progress;
                    newLimitLine();
                    mChart.invalidate();
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if(fromUser) {
//                     threshold=progress;
//                    newLimitLine();
//                    mChart.invalidate();
//                }
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) { }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {}
//        });
        chronometer = findViewById(R.id.chronometer);
        vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
        initChart();
    }

    private void initChart() {
        mChart = (LineChart) findViewById(R.id.chart1);

        // enable description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
//        mChart.setTouchEnabled(true);

        // enable scaling and dragging
//        mChart.setDragEnabled(true);
//        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
//        mChart.setPinchZoom(true);

        // set an alternative background color
//        mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setEnabled(false);
//        // modify the legend ...
//        l.setForm(Legend.LegendForm.LINE);
//        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
//        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        newLimitLine();

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(true);
    }

    public void newLimitLine(){
        YAxis leftAxis = mChart.getAxisLeft();
        if(leftAxis.getLimitLines()!=null && leftAxis.getLimitLines().size()>0){
            leftAxis.getLimitLines().remove(0);
        }
        LimitLine ll = new LimitLine(threshold);
        leftAxis.addLimitLine(ll);

        ll.enableDashedLine(10f, 10f, 0f);
        ll.setLabel(""+threshold);
    }


    private void addEntry(Float value) {
        System.out.println("Value: "+value);

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
            data.addEntry(new Entry(set.getEntryCount(), value), 0);

            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);

        return set;
    }

    public void register(boolean register){
        if(register) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }else{
            sensorManager.unregisterListener(this);
            valueToCompare=INIT;
        }
    }

    public void start(){
        valueToCompare=RESET;
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setStarted(true);
        startButton.setVisibility(View.GONE);
        startButton.invalidate();
        mChart.clearValues();
        
    }
    public void checkAndStop(float diff){
        if(valueToCompare!=INIT && valueToCompare!=RESET && diff>threshold){
            valueToCompare=-1;
            vibrator.vibrate(500);
            chronometer.setStarted(false);
            startButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SnakeGameActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
