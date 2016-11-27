package com.example.shirin.moneymoney_capstoneproject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.CollapsibleActionView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class SortDataActivity extends AppCompatActivity {
    String amount = null;
    String desc = null;
    String type = null;
    String date = null;
    String category = null;

    private static final String TAG_TYPE = "type";
    private static final String TAG_AMOUNT = "amount";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_DESC = "desc";
    private static final String TAG_DATE = "date";
    private static final String TAG_SUCCESS = "success";
    static final String FETCH_URL = "http://moneymoney.zapto.org:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_data);

        //new GetDataAsyncTask().execute();
        sortData();
    }


    public void sortData() {
        //super.onPostExecute(s);
        double sum1 = 0.0;
        double sum2 = 0.0;
        double amt = 0.0;
        String result1 = null;
        String result2 = null;
        double totalMoney;
        double balanceLeft;
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
            //initialize a JSONArray of Strings
            JSONArray jsonArray = new JSONArray(sb.toString());
            //JSONObject to hold ojects from JSONArray
            JSONObject jsonObject = null;
            //loop through the array to get object and strings
            JSONObject newJSON = new JSONObject();

            final ArrayList<Date> datearray = new ArrayList<Date>();
            final DateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd");

            for (int i = 0; i < jsonArray.length(); i++) {
                //get JSONObject from the JSONArray. For each jsonobject in jsonarray
                jsonObject = jsonArray.getJSONObject(i);
                //adding each json object to the arraylist

                //get all types
                type = jsonObject.getString(TAG_TYPE);
                //Log.d("type ", type);
                //get all amounts
                amount = jsonObject.getString(TAG_AMOUNT);
                amount = amount.replaceAll(",", "");

                date = jsonObject.getString(TAG_DATE);
                Date dt = sdf.parse(date);
                datearray.add(dt);
                //System.out.println( datearray);
            }
            //sort date in asceding order
            Collections.sort(datearray);
            Log.d("sorted ascending", String.valueOf(datearray));

            //sort date in descending order
            Collections.sort(datearray, new Comparator<Date>() {
                @Override
                public int compare(Date o1, Date o2) {
                    int res = (sdf.format(o2).compareTo(sdf.format(o1)));
                    return res;
                }
            });
            Log.d("sorted desceding", String.valueOf(datearray));


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

