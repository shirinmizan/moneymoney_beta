package com.example.shirin.moneymoney_capstoneproject;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.JsonParser;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BarGraphActivity extends AppCompatActivity {

    private GraphicalView mChart;
    private TimeSeries transactionSeries;
    private XYMultipleSeriesDataset dataset;
    private XYSeriesRenderer transactionRenderer;
    private XYMultipleSeriesRenderer multiRenderer;

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
    double amt;
    Date dt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_graph);
        //creating an Timeseries for Transactions

        transactionSeries = new TimeSeries("Transactions");

        //start plotting chart
        new ChartTask().execute();
    }

    public void setupChart() {

        //creating a dataset to hold each series
        dataset = new XYMultipleSeriesDataset();
        //adding transactionseries to the dataset
        Log.d("Count Before", String.valueOf(transactionSeries.getItemCount()));
        dataset.addSeries(transactionSeries);
        Log.d("Dataset: ", String.valueOf(transactionSeries.getItemCount()));

        //Creating a XYMultipleSeriesRenderer to customize transaction series
        transactionRenderer = new XYSeriesRenderer();
        transactionRenderer.setColor(Color.GREEN);
        //transactionRenderer.setPointStyle(PointStyle.CIRCLE);
        transactionRenderer.setFillPoints(true);
        transactionRenderer.setLineWidth(4);
        transactionRenderer.setDisplayChartValues(false);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        multiRenderer = new XYMultipleSeriesRenderer();

        multiRenderer.setChartTitle("Transaction Trends");
        multiRenderer.setXTitle("Date");
        multiRenderer.setYTitle("Amount");
        //multiRenderer.setZoomButtonsVisible(true);

        multiRenderer.setXAxisMin(0);
        multiRenderer.setXAxisMax(10);

        multiRenderer.setYAxisMin(0);
        multiRenderer.setYAxisMax(10);

        multiRenderer.setBarSpacing(2);

        // Adding transactionRenderer to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
        // should be same
        multiRenderer.addSeriesRenderer(transactionRenderer);

        // Getting a reference to LinearLayout of the bar graph activity Layout
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart_container);

        mChart = (GraphicalView) ChartFactory.getBarChartView(getBaseContext(), dataset, multiRenderer, BarChart.Type.DEFAULT);

        // Adding the Line Chart to the LinearLayout
        chartContainer.addView(mChart);
    }

    //reading from remote database. MongoDB on Node.js server
    private class ChartTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BarGraphActivity.this);
            pDialog.setMessage("Fetching data. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... String) {
            try {
                URL url = new URL(FETCH_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int responsecode = urlConnection.getResponseCode();

                if (responsecode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    return sb.toString();
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //fetching data from db
            try {
                //get JSONObject from JSONArray of String
                JSONArray result = new JSONArray();
                JSONObject jsonObject = null;
                //String[] values = new String[2];
                //loop through the array and break the JSONObject into String

                setupChart();
                for (int i = 0; i < result.length(); i++) {
                    jsonObject = result.getJSONObject(i);
                    amount = jsonObject.getString(TAG_AMOUNT);
                    desc = jsonObject.getString(TAG_DESC);
                    type = jsonObject.getString(TAG_TYPE);
                    category = jsonObject.getString(TAG_CATEGORY);
                    //getting date as string from database
                    date = jsonObject.getString(TAG_DATE);
                    //System.out.println(date);
                    //write it in the db in a different format like Wednesday, July 12, 2016 12:00 PM
                    SimpleDateFormat readFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a");
                    //SimpleDateFormat writeFormat = new SimpleDateFormat("MMMM dd, yyyy");

                    //check it date string for null or empty string or else it will give Unparseable date: "" (at offset 0) error
                    if (!date.equalsIgnoreCase("")) {
                        try {
                            dt = readFormat.parse(date);  //parse the date string in the read format
                            //String dtStr = writeFormat.format(dt);
                            //dt = writeFormat.parse(dtStr);
                            //System.out.println(dt);
                            Log.d("Date: ", date);
                            amt = Double.valueOf(amount.replace(",", ""));
                            //System.out.println(amt);
                            Log.d("Amount: ", java.lang.String.valueOf(amt));
                            //Log.d("Count: ", java.lang.String.valueOf((transactionSeries.getItemCount())));
                            transactionSeries.add(dt, amt);
                            // Log.d("Dataset After ", String.valueOf(transactionSeries.getItemCount()));
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        return null;
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        //grab data and plug it in the chart
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            setupChart();
            dataset.addSeries(transactionSeries);
            //Creating a XYMultipleSeriesRenderer to customize transaction series
            transactionRenderer = new XYSeriesRenderer();
            transactionRenderer.setColor(Color.GREEN);
            //transactionRenderer.setPointStyle(PointStyle.CIRCLE);
            transactionRenderer.setFillPoints(true);
            transactionRenderer.setLineWidth(4);
            transactionRenderer.setDisplayChartValues(false);

            // Creating a XYMultipleSeriesRenderer to customize the whole chart
            multiRenderer = new XYMultipleSeriesRenderer();
            multiRenderer.setChartTitle("Transaction Trends");
            multiRenderer.setXTitle("Date");
            multiRenderer.setYTitle("Amount");
            //multiRenderer.setZoomButtonsVisible(true);

            multiRenderer.setXAxisMin(0);
            multiRenderer.setXAxisMax(10);

            multiRenderer.setYAxisMin(0);
            multiRenderer.setYAxisMax(10);

            multiRenderer.setBarSpacing(2);

            // Adding transactionRenderer to multipleRenderer
            // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
            // should be same
            multiRenderer.addSeriesRenderer(transactionRenderer);

            // Getting a reference to LinearLayout of the bar graph activity Layout
            LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart_container);

            mChart = (GraphicalView) ChartFactory.getBarChartView(getBaseContext(), dataset, multiRenderer, BarChart.Type.DEFAULT);

            // Adding the Line Chart to the LinearLayout
            // chartContainer.addView(mChart);
            if(mChart != null){
                mChart.refreshDrawableState();
                mChart.repaint();
            }
        }
    }
}