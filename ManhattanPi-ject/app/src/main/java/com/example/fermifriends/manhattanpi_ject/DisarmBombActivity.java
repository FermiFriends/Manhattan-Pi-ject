package com.example.fermifriends.manhattanpi_ject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DisarmBombActivity extends AppCompatActivity {
    private static final int NUM_SECONDS_FOR_READ_TIMEOUT = 10;
    private static final int NUM_TICKS_PER_SECOND = 1000;
    private static final String SERVER_URL = "http://129.31.192.121:5000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disarm_bomb);
    }

    public void getData(View view) {
        AsyncHttpTask asyncHttpTask = new AsyncHttpTask();
        asyncHttpTask.execute(SERVER_URL);
    }


    private class AsyncHttpTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String res = "";
            try {
                String url = params[0];
                res = parseInputStream(getInputStreamFromServer(url));
            } catch (Exception e) {
                Log.d("", e.getLocalizedMessage());
            }
            return res;
        }

        private InputStream getInputStreamFromServer(String urlString) throws Exception {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(NUM_SECONDS_FOR_READ_TIMEOUT * NUM_TICKS_PER_SECOND);
                connection.connect();
                return connection.getInputStream();
            } catch (Exception e) {
                Log.d("", e.getLocalizedMessage());
                return null;
            }
        }

        private String parseInputStream(InputStream inputStream) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        @Override
        protected void onPostExecute(String result) {
            updateData(result);
        }
    }

    private void updateData(String string) {
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(string);
    }
}
