package com.example.shirin.moneymoney_capstoneproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class GoogleChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_chart);

        WebView webView = (WebView) findViewById(R.id.chartView);
        String content = "<html>"
                + "  <head>"
                + "    <script type=\"text/javascript\" src=\"jsapi.js\"></script>"
                + "    <script type=\"text/javascript\">"
                + "      google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});"
                + "      google.setOnLoadCallback(drawChart);"
                + "      function drawChart() {"
                + "        var jsonData = $.ajax({"
                + "               url:'file:///android_asset/expenses.json',"
                + "               dataType: 'json',"
                + "               type: 'GET',  "
                + "               async: false}).responseText;"

                + "        var data = google.visualization.arrayToDataTable(jsonData);"
                + "        data.addColumn("
                + "        var options = {"
                + "          title: 'Spending over time',"
                + "          hAxis: {title: 'Year', titleTextStyle: {color: 'red'}}"
                + "        };"
                + "        var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));"
                + "        chart.draw(data, options);"
                + "      }"
                + "    </script>"
                + "  </head>"
                + "  <body>"
                + "    <div id=\"chart_div\" style=\"width: 1000px; height: 500px;\"></div>"
                + "  </body>" + "</html>";

        WebSettings webSettings = webView.getSettings();
        //javascript must be set to enabled
        webSettings.setJavaScriptEnabled(true);
        webView.requestFocusFromTouch();
        webView.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "utf-8", null );
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //case R.id.action_to_image:
                //Intent intent = new Intent(MainActivity.this, GoogleImageGraphActivity.class);
                //startActivity(intent);
                //return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
