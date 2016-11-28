
package com.example.shirin.moneymoney_capstoneproject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.example.shirin.moneymoney_capstoneproject.barchart.DayAxisValueFormatter;
import com.example.shirin.moneymoney_capstoneproject.barchart.MyAxisValueFormatter;
import com.example.shirin.moneymoney_capstoneproject.barchart.XYMarkerView;
import com.example.shirin.moneymoney_capstoneproject.barchart.DemoBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class BarChartActivity extends DemoBase implements OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    protected BarChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    //JSON node name
    private static final String TAG_TYPE = "type";
    private static final String TAG_AMOUNT = "amount";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_DESC = "desc";
    private static final String TAG_DATE = "date";
    private static final String TAG_SUCCESS = "success";
    static final String FETCH_URL = "http://moneymoney.zapto.org:8080";

    String amount = null;
    String desc = null;
    String type = null;
    String date = null;
    String category = null;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart);
        // tvX = (TextView) findViewById(R.id.tvXMax);
       // tvY = (TextView) findViewById(R.id.tvYMax);

       // mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        //mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);

        //setting the bar chart in view
        mChart = (BarChart) findViewById(R.id.chart1);
        //setting data from db
        new ChartTask().execute();

        //set and create bar chart with color and all
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
       // mChart.setMaxVisibleValueCount(10);
        mChart.setVisibleXRangeMaximum(20);
        mChart.moveViewToX(10);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(48, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(10f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        //leftAxis.setAxisMaximum(6000f);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(48, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(10f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(LegendForm.SQUARE);
        l.setFormSize(10f);
        l.setTextSize(14f);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        //setData(12, 50);
        //animate
        mChart.animateXY(3000, 3000);


       // mSeekBarY.setProgress(50);
        //mSeekBarX.setProgress(12);

        //mSeekBarY.setOnSeekBarChangeListener(this);
       // mSeekBarX.setOnSeekBarChangeListener(this);

        // mChart.setDrawLegend(false);
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

   @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        /**tvX.setText("" + (mSeekBarX.getProgress() + 2));
        tvY.setText("" + (mSeekBarY.getProgress()));

        setData(mSeekBarX.getProgress() + 1 , mSeekBarY.getProgress());
        mChart.invalidate();**/
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }
    //private void setData(int count, float range) {

    private class ChartTask extends AsyncTask<String, String, String> {
        float start = 1f;

        protected void onPreExecute() {
            super.onPreExecute();
            //    pDialog = new ProgressDialog(BarGraphActivity2.this);
            //  pDialog.setMessage("Loading data. Please wait...");
            //  pDialog.setIndeterminate(false);
            // pDialog.setCancelable(false);
            //   pDialog.show();
        }

        @Override
        protected String doInBackground(String... String) {
            return null;
        }
        SimpleDateFormat readFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a");
        //grab data and plug it in the chart
        @Override
        protected void onPostExecute(String s) {
            float amt = (float)0.00;
            ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

            super.onPostExecute(s);
            // reading from file
            StringBuffer sb = new StringBuffer();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(getAssets().open("expenses.json")));
                String temp;
                while ((temp = br.readLine()) != null) {
                    sb.append(temp);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                JSONArray result = new JSONArray(sb.toString());
                JSONObject jsonObject = null;
                for (int i = 0; i < result.length(); i++) {
                    jsonObject = result.getJSONObject(i);
                    amount = jsonObject.getString(TAG_AMOUNT);
                    desc = jsonObject.getString(TAG_DESC);
                    type = jsonObject.getString(TAG_TYPE);
                    category = jsonObject.getString(TAG_CATEGORY);
                    date = jsonObject.getString(TAG_DATE);
                    amt = Float.valueOf(amount.replace(",", ""));
                    yVals1.add(new BarEntry(i, amt));
                }




       /** ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = (int) start; i < start + count + 1; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult);
            yVals1.add(new BarEntry(i, val));
        }**/

            BarDataSet set1;

            if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
                //set the data on bar with yvalus which are amouns
                set1.setValues(yVals1);
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
            } else {
                set1 = new BarDataSet(yVals1, "Transactions");
                set1.setColors(ColorTemplate.LIBERTY_COLORS);

                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(set1);

                BarData data = new BarData(dataSets);
                data.setBarWidth(5f);
                data.setValueTextSize(10f);
                data.setValueTypeface(mTfLight);
                //data.setBarWidth(2f);

                mChart.setData(data);
            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected RectF mOnValueSelectedRectF = new RectF();

    @SuppressLint("NewApi")
    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = mOnValueSelectedRectF;
        mChart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = mChart.getPosition(e, AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + mChart.getLowestVisibleX() + ", high: "
                        + mChart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() { }
}
