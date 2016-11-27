package com.example.shirin.moneymoney_capstoneproject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ExpenseActivity extends AppCompatActivity {
    TextView rent, food, living, transportation, clothing, entertainment, livingexpenses, paybackloan;

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
    ArrayList<HashMap<String, String>> arraylist;
    double sum1, sum2, sum3, sum4, sum5, sum6, sum7;
    String result1, result2, result3, result4, result5, result6, result7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        rent = (TextView) findViewById(R.id.txtOsap);
        food = (TextView) findViewById(R.id.txtFood);
        clothing = (TextView) findViewById(R.id.txtClothing);
         entertainment = (TextView) findViewById(R.id.txtEntertainment);
        paybackloan = (TextView) findViewById(R.id.txtPaybackloan);
        livingexpenses = (TextView) findViewById(R.id.txtLivingexpenses);
        transportation = (TextView) findViewById(R.id.txtTransportation);

        new GetTransactionTask().execute();
    }

    public class GetTransactionTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
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
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            double amt = 0.0;
            double totalMoney, balanceLeft;

            try {
                //initialize a JSONArray of Strings
                JSONArray jsonArray = new JSONArray(s);
                //JSONObject to hold ojects from JSONArray
                JSONObject jsonObject = null;
                //loop through the array to get object and strings
                JSONObject newJSON = new JSONObject();

                for (int i = 0; i < jsonArray.length(); i++) {
                    //get JSONObject from the JSONArray. For each jsonobject in jsonarray
                    jsonObject = jsonArray.getJSONObject(i);
                    //get all types
                    type = jsonObject.getString(TAG_TYPE);
                    //Log.d("type ", type);
                    //get all amounts
                    amount = jsonObject.getString(TAG_AMOUNT);
                    amount = amount.replaceAll(",", "");
                    for (int k = 0; k < amount.length(); k++) {
                        if ((amount == "") || amount.equalsIgnoreCase("m")) {
                            return;
                        } else {
                            amt = Double.valueOf(amount);
                        }
                    }
                    category = jsonObject.getString(TAG_CATEGORY);
                    //String[] typeArray = new String[] {type};

                    //creating a new JSON object to hold only corresponding types and amounts
                    newJSON.put(category, amount);
                    //Log.d("New ", String.valueOf(newJSON));
                    //show total osap received
                    if (category.equalsIgnoreCase("clothing")) {
                        sum1 += amt;
                        result1 = "Spend on Clothing: " + String.format("%.2f", sum1);
                        clothing.setText(result1);
                        Log.d("Total Osap ", String.valueOf(sum1));
                    }
                    //show total bursary
                    else if (category.equalsIgnoreCase("food")) {
                        sum2 += amt;
                        result2 = "Spend on food: " + String.format("%.2f", sum2);
                        Log.d("Total food ", String.valueOf(sum2));
                        food.setText(result2);
                        Log.d("Total Expense ", String.valueOf(sum2));
                    }
                    else if (category.equalsIgnoreCase("rent")) {
                        sum3 += amt;
                        result3 = "Rent paid: " + String.format("%.2f", sum3);
                        Log.d("Total Loan ", String.valueOf(sum3));
                        rent.setText(result2);
                        Log.d("Total Loan ", String.valueOf(sum3));
                    }
                    else if (category.equalsIgnoreCase("entertainment")) {
                        sum4 += amt;
                        result3 = "Spend on entertainment: " + String.format("%.2f", sum4);
                        Log.d("Total Scholarship ", String.valueOf(sum4));
                        entertainment.setText(result3);
                        Log.d("Total Scholarship ", String.valueOf(sum4));
                    }
                    else if (category.equalsIgnoreCase("living expenses")) {
                        sum5 += amt;
                        result5 = "Living expenses: " + String.format("%.2f", sum5);
                        Log.d("Total Allowance ", String.valueOf(sum5));
                        livingexpenses.setText(result5);
                        Log.d("Total Allowance ", String.valueOf(sum5));
                    }
                    else if (category.equalsIgnoreCase("payback loan")) {
                        sum6 += amt;
                        result6 = "Loan paid: " + String.format("%.2f", sum6);
                        Log.d("Total PayCheque Amount ", String.valueOf(sum6));
                        paybackloan.setText(result6);
                        Log.d("Total PayCheque ", String.valueOf(sum6));
                    }
                    else if (category.equalsIgnoreCase("transportation")) {
                        sum7 += amt;
                        result2 = "Transportation Cost: " + String.format("%.2f", sum7);
                        Log.d("Total Bursary ", String.valueOf(sum7));
                        transportation.setText(result7);
                        Log.d("Total Expense ", String.valueOf(sum7));
                    }
                    else {
                        return;
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

