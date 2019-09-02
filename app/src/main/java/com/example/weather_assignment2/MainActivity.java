package com.example.weather_assignment2;


//Class imports for the application
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import org.json.JSONException;


public class MainActivity extends AppCompatActivity {

    //defining variables for the TextViews used to display temperature details and also textfield for the user to input city

    TextView providedCity,cityTemp, min_maxTemp, cityAtmosphere, cityAtmosphereDescription, cityHumidity, cityCloudCoverage;
    EditText entCityName;

    String weather_city;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialising and assigning the variables

        providedCity=(TextView) findViewById(R.id.providedCity);
        cityTemp = (TextView) findViewById(R.id.cityTemp);
        min_maxTemp = (TextView) findViewById(R.id.min_maxTemp);
        cityAtmosphere = (TextView) findViewById(R.id.cityAtmosphere);
        cityAtmosphereDescription=(TextView) findViewById(R.id.cityAtmosphereDescription);
        cityHumidity = (TextView) findViewById(R.id.cityHumidity);
        cityCloudCoverage = (TextView) findViewById(R.id.cityCloudCoverage);
        Button getWeather = findViewById(R.id.getWeather);
        entCityName = findViewById(R.id.entCityName);

        //Onclick method defined to get the city provided by the user and call the function to retrieve the openweather data

        getWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting the city the user entered
                weather_city = entCityName.getText().toString();

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        getWeatherDetails();
                    }
                };

                Thread thread = new Thread(null, runnable, "background");
                thread.start();

                //closing the keypad of phone
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


            }
        });


    }

    //method retrieving data from the openweather api

    public void getWeatherDetails() {

        final String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?q="; //url for the openweathermap


        String urlnUserInput= (weatherUrl.concat(TextUtils.isEmpty(weather_city)? "Halifax":weather_city))+"&appid=d6ea79a585bcf0872a12ba54a227a40f&units=metric"; //concatenate the user input; The data should come in celcius instead of the default kelvin units
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, urlnUserInput, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Toast.makeText(getApplicationContext(), "success!", Toast.LENGTH_SHORT).show();
                try {
                    //create different objects for respective data objects of interest

                    JSONObject mainDetails = response.getJSONObject("main");

                    JSONObject cloudDetails = response.getJSONObject("clouds");

                    JSONObject weatherMain = response.getJSONArray("weather").getJSONObject(0);

                    String atmospheredesc = weatherMain.getString("description");

                    Log.i("ALFRED", mainDetails.toString());

                    //defining variables to receive the actual data in the objects

                    String temp =String.format( String.valueOf(mainDetails.getInt("temp")));
                    String tempmin=String.valueOf(mainDetails.getInt("temp_min"));
                    String tempmax = String.valueOf(mainDetails.getInt("temp_max"));
                    String atmosphere = weatherMain.getString("main");

                    String humidity = String.valueOf(mainDetails.getInt("humidity"));
                    String cloudcoverage = String.valueOf(cloudDetails.getInt("all"));


                    //set the values from the variables to the variuos textviews for display
                    providedCity.setText(weather_city);

                    cityTemp.setText(temp+ (char) 0x00B0+"C") ;

                    min_maxTemp.setText("Min."+ tempmin +(char) 0x00B0+"C  Max."+ tempmax + (char) 0x00B0);

                    cityAtmosphere.setText(atmosphere);

                    cityAtmosphereDescription.setText(atmospheredesc);

                    cityHumidity.setText(humidity+"%");

                    cityCloudCoverage.setText(cloudcoverage+"%");


                    } catch (JSONException e) {
                    Log.i("ALFRED", "came in exception");
                    Log.i("ALFRED", e.getMessage());

                    e.printStackTrace();
                }

                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error retrieving data", Toast.LENGTH_SHORT
                ).show();
            }
        }
        );

        RequestQueue newrequest =Volley.newRequestQueue(this);
        newrequest.add(request);
        }
    }