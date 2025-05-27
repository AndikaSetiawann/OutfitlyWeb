package com.example.outfitly;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class WeatherHelper {

    private static final String API_KEY = "b223053ef506d6f25b29d2461e3e030f";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=Jakarta&units=metric&appid=" + API_KEY;

    @FunctionalInterface
    public interface WeatherCallback {
        void onWeatherReceived(String description, String temperature);
    }

    public static void getCurrentWeather(Context context, WeatherCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                response -> {
                    try {
                        JSONObject weather = response.getJSONArray("weather").getJSONObject(0);
                        String description = weather.getString("description");

                        JSONObject main = response.getJSONObject("main");
                        String temperature = String.valueOf(main.getDouble("temp"));

                        callback.onWeatherReceived(description, temperature);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onWeatherReceived(null, null);
                    }
                },
                error -> {
                    error.printStackTrace();
                    callback.onWeatherReceived(null, null);
                }
        );

        queue.add(request);
    }
}
