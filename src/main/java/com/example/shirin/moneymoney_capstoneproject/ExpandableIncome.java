package com.example.shirin.moneymoney_capstoneproject;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExpandableIncome extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    static final String FETCH_URL = "http://moneymoney.zapto.org:8080";
    private static final String TAG_TYPE = "type";
    private static final String TAG_AMOUNT = "amount";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_DESC = "desc";
    private static final String TAG_DATE = "date";
    private static final String TAG_SUCCESS = "success";
    String amount = null;
    String desc = null;
    String type = null;
    String date = null;
    String category = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable_list_view);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.expenseList);

        // preparing list data
         prepareListData();
        //new GetTransactionTask().execute();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new android.widget.ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(android.widget.ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new android.widget.ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                //get the adapter
                ExpandableListAdapter adapter = (ExpandableListAdapter)expListView.getExpandableListAdapter();
                if(adapter == null){
                    return;
                }
                //run the whole list and collaps other groups except for the one
                //are currently on
                for(int i=0; i<adapter.getGroupCount(); i++){
                    if(i != groupPosition){
                        expListView.collapseGroup(i);
                    }
                }
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new android.widget.ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
               /** Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();**/

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new android.widget.ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
                /*AlertDialog.Builder alert = new AlertDialog.Builder(getBaseContext());
                final EditText edittext = new EditText(getBaseContext());
                alert.setMessage("Would you like to update or delete?");
                alert.setTitle("Warning!");
                String bla= listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition);
                edittext.setText(bla);
                alert.setView(edittext);

                alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Editable edit = edittext.getText();
                    }
                });

                alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = alert.create();
                alert.show();**/
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });
    }

    /*
     * Preparing the list data
     */

    /**private class GetTransactionTask extends AsyncTask<String, String, String> {
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
            }**/
        public void prepareListData() {
            double amt = 0.0;
            //If reading from a local file
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
                JSONObject newJSON = new JSONObject();
                JSONObject newJSON1 = new JSONObject();

                //loop through the array and break the JSONObject into String
                listDataHeader = new ArrayList<String>();
                listDataChild = new HashMap<String, List<String>>();
                List<String> descList = new ArrayList<String>();
                List<String> descList2 = new ArrayList<String>();
                List<String> descList3 = new ArrayList<String>();
                List<String> descList4 = new ArrayList<String>();
                List<String> descList5 = new ArrayList<String>();
                List<String> descList6 = new ArrayList<String>();
                List<String> descList7 = new ArrayList<String>();

                for (int i = 0; i < result.length(); i++) {
                    jsonObject = result.getJSONObject(i);
                    amount = jsonObject.getString(TAG_AMOUNT);
                    amount = amount.replaceAll(",", "");
                    for (int k = 0; k < amount.length(); k++) {
                        if ((amount == "") || amount.equalsIgnoreCase("m")) {
                            return;
                        } else {
                            amt = Double.valueOf(amount);
                        }
                    }
                    desc = jsonObject.getString(TAG_DESC);
                    type = jsonObject.getString(TAG_TYPE);
                    //getting date as string from database
                    date = jsonObject.getString(TAG_DATE);
                    category = jsonObject.getString(TAG_CATEGORY);

                    //List<String> descList = new ArrayList<String>();
                    //descList.add(desc);
                    //Log.d("Child", String.valueOf(descList));

                    //listDataChild.put(listDataHeader.get(0), descList);
                    newJSON.put(type, category);
                    newJSON1.put(category, desc);
                    //if there are duplicate category it will remove them and show
                    //one of each category
                    if (type.equalsIgnoreCase("income")) {
                        //nice now need to add date formatter
                        if (category.equalsIgnoreCase("osap")) {
                            descList.add(desc + " " + date + " " + '\n' + amount);
                        }
                        if (category.equalsIgnoreCase("allowance")) {
                            String desc2 = desc;
                            descList2.add(desc2 + " " + date + " " + '\n' + amount);
                        }
                        if (category.equalsIgnoreCase("one time")) {
                            String desc3 = desc;
                            descList3.add(desc3 + " " + date + " " + '\n' + amount);
                        }
                        if (category.equalsIgnoreCase("loan")) {
                            String desc4 = desc;
                            descList4.add(desc4 + " " + date + " " + '\n' + amount);
                        }
                        if (category.equalsIgnoreCase("paycheque")) {
                            String desc5 = desc;
                            descList5.add(desc5 + " " + date + " " + '\n' + amount);
                        }
                        if (listDataHeader.contains(category)) {
                            continue;
                        } else {
                            listDataHeader.add(category);
                        }
                    }
                }
                Log.d("Header", String.valueOf(listDataHeader));
                listDataChild.put(listDataHeader.get(0), descList);
                listDataChild.put(listDataHeader.get(1), descList2);
                listDataChild.put(listDataHeader.get(2), descList3);
                listDataChild.put(listDataHeader.get(3), descList4);
                listDataChild.put(listDataHeader.get(4), descList5);
                //listDataChild.put(listDataHeader.get(5), descList6);
               // listDataChild.put(listDataHeader.get(6), descList7);






                     //System.out.println(amount + date);
                     //SimpleDateFormat readFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a");

                     //check it date string for null or empty string or else it will give Unparseable date: "" (at offset 0) error
                     /*if (!date.equalsIgnoreCase("")) {
                        try {
                         Date dt = readFormat.parse(date);  //parse the date string in the read format
                         Log.d("Date: ", date);
                         double amt = Double.valueOf(amount.replace(",", ""));
                         //System.out.println(amt);
                         Log.d("Amount: ", String.valueOf(amt));

                         setupChart();
                         transactionSeries.add(dt.getTime(),amt);
                         mChart.repaint();
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }
                     }
                     else {
                        return;
                     }*/
                 } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    public void update(){
        System.out.println(desc + " " + date + " " + '\n' + amount);
    }
}
         /**@Override
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

                        listDataHeader = new ArrayList<String>();
                        listDataChild = new HashMap<String, List<String>>();

                        // Adding child data
                        listDataHeader.add(category);
                        Log.d("Header", String.valueOf(listDataHeader));
                       // listDataHeader.add("Now Showing");
                        //listDataHeader.add("Coming Soon..");

                        // Adding child data
                        List<String> top250 = new ArrayList<String>();
                        top250.add("The Shawshank Redemption");
                        top250.add("The Godfather");
                        top250.add("The Godfather: Part II");
                        top250.add("Pulp Fiction");
                        top250.add("The Good, the Bad and the Ugly");
                        top250.add("The Dark Knight");
                        top250.add("12 Angry Men");

                        List<String> nowShowing = new ArrayList<String>();
                        nowShowing.add("The Conjuring");
                        nowShowing.add("Despicable Me 2");
                        nowShowing.add("Turbo");
                        nowShowing.add("Grown Ups 2");
                        nowShowing.add("Red 2");
                        nowShowing.add("The Wolverine");

                        List<String> comingSoon = new ArrayList<String>();
                        comingSoon.add("2 Guns");
                        comingSoon.add("The Smurfs 2");
                        comingSoon.add("The Spectacular Now");
                        comingSoon.add("The Canyons");
                        comingSoon.add("Europa Report");

                        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
                        listDataChild.put(listDataHeader.get(1), nowShowing);
                        listDataChild.put(listDataHeader.get(2), comingSoon);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }**/





