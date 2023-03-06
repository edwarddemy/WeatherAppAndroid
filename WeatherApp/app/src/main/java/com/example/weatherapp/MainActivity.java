package com.example.weatherapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    // Input and main display
    EditText cityInput, countryCodeInput;
    TextView cityNameLabel;
    TextView currentTempLabel;
    ImageView weatherIcon;
    // Display
    TextView titleTextView;
    TextView descriptionTextView;
    ImageView descriptionImage;
    TextView feelsLikeTextView;
    TextView humidityTextView;
    TextView windSpeedTextView;
    TextView cloudinessTextView;
    TextView pressureTextView;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "e6dab430a0bc5983cb1f6b9c603a82c9";
    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();


        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.etCity);
        countryCodeInput = findViewById(R.id.etCountry);
        cityNameLabel = findViewById(R.id.cityName);
        currentTempLabel = findViewById(R.id.currentTemp);
        weatherIcon = findViewById(R.id.weatherIcon);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.description);
        descriptionImage = findViewById(R.id.descriptionImage);
        feelsLikeTextView = findViewById(R.id.feelsLike);
        humidityTextView = findViewById(R.id.humidity);
        windSpeedTextView = findViewById(R.id.windSpeed);
        cloudinessTextView = findViewById(R.id.cloudiness);
        pressureTextView = findViewById(R.id.pressure);
    }

    public void getWeatherDetails(View view) {
        String tempUrl = "";
        String city = cityInput.getText().toString().trim();
        String country = countryCodeInput.getText().toString().trim();
        if(city.equals("")){
            Toast.makeText(MainActivity.this, "Please enter a valid city!", Toast.LENGTH_SHORT).show();
        }else{
            if(!country.equals("")){
                tempUrl = url + "?q=" + city + "," + country + "&appid=" + appid;
            }else{
                tempUrl = url + "?q=" + city + "&appid=" + appid;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String output = "";
                    try {
                        // Create the response, then try to extract data.
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonWeather = jsonArray.getJSONObject(0);
                        String description = (jsonWeather.getString("description"));
                        String mainDescription = jsonWeather.getString("main");
                        String id = jsonWeather.getString("id");
                        JSONObject jsonMain = jsonResponse.getJSONObject("main");
                        double temp = jsonMain.getDouble("temp") - 273.15;
                        double feelsLike = jsonMain.getDouble("feels_like") - 273.15;
                        float pressure = jsonMain.getInt("pressure");
                        int humidity = jsonMain.getInt("humidity");
                        JSONObject jsonWind = jsonResponse.getJSONObject("wind");
                        String wind = jsonWind.getString("speed");
                        JSONObject jsonClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonClouds.getString("all");
                        JSONObject jsonSys = jsonResponse.getJSONObject("sys");
                        String countryName = jsonSys.getString("country");
                        String cityName = jsonResponse.getString("name");

                        // Determine which icon to use
                        if (Integer.parseInt(id) >= 200 && Integer.parseInt(id) <= 531){
                            weatherIcon.setImageResource(R.drawable.raincloud);
                            descriptionImage.setImageResource(R.drawable.raincloud);
                        }
                        else if (Integer.parseInt(id) >= 600 && Integer.parseInt(id) <= 622){
                            weatherIcon.setImageResource(R.drawable.snow);
                            descriptionImage.setImageResource(R.drawable.snow);
                        }
                        else if (Integer.parseInt(id) == 800){
                            weatherIcon.setImageResource(R.drawable.sun);
                            descriptionImage.setImageResource(R.drawable.sun);
                        }
                        else if (Integer.parseInt(id) >= 801 && Integer.parseInt(id) <= 804){
                            weatherIcon.setImageResource(R.drawable.cloud);
                            descriptionImage.setImageResource(R.drawable.cloud);
                        }
                        else{
                            weatherIcon.setImageResource(R.drawable.cloud);
                            descriptionImage.setImageResource(R.drawable.cloud);
                        }

                        // Use extracted data to set display
                        cityNameLabel.setText(cityName);
                        currentTempLabel.setText(decimalFormat.format(temp) + " °C");

                        // Create the response from extracted data
                        titleTextView.setText("Current weather for " + cityName + ", " + countryName);
                        descriptionTextView.setText(mainDescription);
                        feelsLikeTextView.setText("Feels Like: " + decimalFormat.format(feelsLike) + " °C");
                        humidityTextView.setText("Humidity: " + humidity + "%");
                        windSpeedTextView.setText("Wind speed: " + wind + "m/s");
                        cloudinessTextView.setText("Cloudiness: " + clouds + "%");
                        pressureTextView.setText("Pressure: " + pressure + " hPa");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}