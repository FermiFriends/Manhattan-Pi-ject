package com.example.fermifriends.manhattanpi_ject;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {
    public static int NUM_SECONDS_FOR_READ_TIMEOUT = 15;
    public static int NUM_TICKS_PER_SECOND = 1000;

    @Override
    protected Integer doInBackground(String... params) {
        InputStream inputStream = null;
        Integer res = 0;
        try {
            String url = params[0];    //TODO: Implement URL
            res = connectToServer(url);
        } catch (Exception e) {
            Log.d("", e.getLocalizedMessage());
        }
        return res;
    }

    public int connectToServer(String urlString) throws Exception {
        URL url = null;
        BufferedReader reader = null;
        StringBuilder sb;

        try {
            url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(NUM_SECONDS_FOR_READ_TIMEOUT * NUM_TICKS_PER_SECOND);
            connection.connect();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            return 1;
        } catch (Exception e) {
            Log.d("", e.getLocalizedMessage());
            return 0;
        } finally {
            if  (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}