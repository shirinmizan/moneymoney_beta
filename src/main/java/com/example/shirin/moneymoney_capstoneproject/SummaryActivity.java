package com.example.shirin.moneymoney_capstoneproject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SummaryActivity extends Activity {
    TextView total, balance, income, expense;

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
    ArrayList<HashMap<String, String>> arraylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summery);

        total = (TextView) findViewById(R.id.txtTotal);
        balance = (TextView) findViewById(R.id.txtBalance);
        income = (TextView) findViewById(R.id.txtIncome);
        expense = (TextView) findViewById(R.id.txtExpense);

        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IncomeActivity.class);
                startActivity(intent);
            }
        });
        expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExpenseActivity.class);
                startActivity(intent);
            }
        });
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
            double sum1 = 0.0;
            double sum2 = 0.0;
            double amt = 0.0;
            String result1 = null;
            String result2 = null;
            double totalMoney;
            double balanceLeft;

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

                    // matcher = Pattern.compile(regex).matcher(amount);
                    //Number number = format.parse(amount);
                    //Log.d("haha", amount);
                    //parse the string objects amount to Double and replacing all commas with nothing
                    //or else it will give invalid double error because it will not be able to parse
                    //comma for double
                    //iterate the amount string and ignore all "" and m
                    for (int k = 0; k < amount.length(); k++) {
                        if ((amount == "") || amount.equalsIgnoreCase("m")) {
                            return;
                        } else {
                            amt = Double.valueOf(amount);
                            //sum += amt;
                        }
                    }
                    category = jsonObject.getString(TAG_CATEGORY);
                    //creating a new JSON object to hold only corresponding types and amounts
                    newJSON.put(type, amt);
                    //Log.d("New ", String.valueOf(newJSON));
                    //if type is income add all amount under it and then show total income
                    if(type.equalsIgnoreCase("income")){
                        sum1 += amt;
                        result1 = "Total Income: " + String.format("%.2f", sum1);
                        income.setText(result1);
                        Log.d("Total Income ", String.valueOf(sum1));
                    }
                    //show total expense
                    else if(type.equalsIgnoreCase("expense")){
                        sum2 += amt;
                        result2 = "Total Expense: " + String.format("%.2f", sum2);
                        Log.d("Total Enpense ", String.valueOf(sum2));
                        expense.setText(result2);
                        Log.d("Total Expense ", String.valueOf(sum2));
                    }
                    else{
                        return;
                    }
                    //total money
                    totalMoney = sum1 + sum2;
                    String res = "Total: " +String.format("%.2f", totalMoney);
                    //balance left
                    balanceLeft = sum1 - sum2;
                    String res2 = "Balance:" + String.format("%.2f", balanceLeft);

                    Log.d("Total ", res);
                    Log.d("Balance", res2);
                    //set the total and balance textview text
                    total.setText(res);
                    balance.setText(res2);




                    //  }
                    //Log.d("Sum ", String.valueOf(sum));
                    //format the result to 2 decimal point
                    //displaying the reult in the corresponding textview
                    /*if(type.equalsIgnoreCase("income")){
                        Log.d("type", type);
                    }*/
                    /**result = "Total: " + String.format("%.2f", sum);
                     total.setText(result);
                     Log.d("Result", result);**/


                    //catch (JSONException e1) {
                    // e1.printStackTrace();
                } //catch (ParseException e) {
                //catch (ParseException e) {
                // e.printStackTrace();

                //catch (ParseException e) {
                // e.printStackTrace();
                //  }
                //catch (ParseException e) {
                //e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //catch (ParseException e) {
            //e.printStackTrace();
        }
    }
}
            //e.printStackTrace();
            //}






