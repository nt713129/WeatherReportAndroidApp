package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button bt_wetather;
    EditText et_text;
    String city_name;
    TextView tv_cityname, tv_temp, tv_mintemp, tv_cloudy, tv_description, tv_humidity,
            tv_cloudsper, fetch_data, text_humidity, text_clouds;

    //OenWeatherMap API Key
    String API_KEY = "be5d694c3604ea7141867edaae206056";
    JSONObject data = null;
    InputMethodManager inputManager;
    ProgressBar progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_wetather = findViewById(R.id.btweather);
        et_text = findViewById(R.id.simpleEditText);
        tv_cityname = findViewById(R.id.tvcityname);
        tv_temp = findViewById(R.id.tvtemp);
        tv_mintemp = findViewById(R.id.tvmintemp);
        tv_cloudy = findViewById(R.id.maincloudy);
        tv_description = findViewById(R.id.tvdescription);
        tv_humidity = findViewById(R.id.tvhumidity);
        tv_cloudsper = findViewById(R.id.tvclouds);
        text_humidity = findViewById(R.id.texthumidity);
        text_clouds = findViewById(R.id.textclouds);

        //communicate with the input textfield
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        progBar = findViewById(R.id.progress_bar);
        fetch_data = findViewById(R.id.fetch);
        progBar.setVisibility(View.INVISIBLE);
        progBar.setIndeterminate(true);


        bt_wetather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                city_name = et_text.getText().toString().trim();
                getJSON(city_name);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                et_text.setText(" ");

            }
        });


    }

    @SuppressLint("StaticFieldLeak")
    public void getJSON(final String city) {

        new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPreExecute() {
                fetch_data.setText("  please wait ...");
                progBar.setVisibility(View.VISIBLE);
                super.onPreExecute();

            }


            @Override
            protected Void doInBackground(Void... params) {
                try {

                    //URL of the OpenWeatherMap API and the API Key
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=be5d694c3604ea7141867edaae206056&units=metric");

                    //Http request is made to API
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    // reading the response data
                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";

                    while ((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();

                    data = new JSONObject(json.toString());

                    Log.d("TAG", String.valueOf(data));
                    if (data.getInt("cod") != 200) {
                        System.out.println("Cancelled");
                        return null;
                    }


                } catch (Exception e) {
                    System.out.println(" Exception " + e.getMessage());
                }

                return null;
            }

            /*
            onPostExecute is a method used to display the data fetched from API and if dara is null
            it alerts the user with a message.
             */
            @Override
            protected void onPostExecute(Void Void) {
                progBar.setVisibility(View.INVISIBLE);
                fetch_data.setVisibility(View.INVISIBLE);
                if (data == null) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

                    //If typed a wrong city name, alert message is displayed
                    builder1.setMessage("There is no city " + city_name + ". Please enter valid city name");
                    builder1.setCancelable(false);

                    builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                } else if (data != null) try {
                    String cty_name = (String) data.get("name");
                    cty_name = cty_name.trim();

                    // creating out JSONObject from the data
                    JSONObject clouds = data.getJSONObject("clouds");
                    int cloud_per = (Integer) clouds.get("all");

                    // We get weather details and this is an array
                    JSONArray weather_Arr = data.getJSONArray("weather");
                    JSONObject weather_arrJSONObject = weather_Arr.getJSONObject(0);
                    String weth_main = String.valueOf(weather_arrJSONObject.get("main"));
                    String weth_des = String.valueOf(weather_arrJSONObject.get("description"));

                    JSONObject obj_main = data.getJSONObject("main");
                    double temp = (Double) obj_main.get("temp");

                    int humidity = (Integer) obj_main.get("humidity");
                    String tempmin = String.valueOf(obj_main.get("temp_min"));
                    String tempmax = String.valueOf(obj_main.get("temp_max"));
                    tv_cityname.setText(cty_name);
                    tv_temp.setText((temp) + "°C");
                    tv_mintemp.setText("Min." + String.valueOf(tempmin) + "°C" + "  Max." + String.valueOf(tempmax) + "°C");
                    tv_cloudy.setText(weth_main);
                    tv_description.setText(weth_des);
                    tv_humidity.setText(String.valueOf(humidity) + " %");
                    tv_cloudsper.setText(String.valueOf(cloud_per) + " %");
                    text_humidity.setText("Humidity");
                    text_clouds.setText("Clouds");
                    progBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                data = null;

            }
        }.execute();

    }


}
