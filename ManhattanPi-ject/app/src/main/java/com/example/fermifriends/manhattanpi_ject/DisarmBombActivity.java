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
    public static int NUM_SECONDS_FOR_READ_TIMEOUT = 15;
    public static int NUM_TICKS_PER_SECOND = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disarm_bomb);
    }

    public void getData(View view) {
        AsyncHttpTask asyncHttpTask = new AsyncHttpTask();
        asyncHttpTask.execute("http://129.31.192.121:5000/");
    }


    private class AsyncHttpTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            String res = "";
            try {
                String url = params[0];
                res = connectToServer(url);
            } catch (Exception e) {
                Log.d("", e.getLocalizedMessage());
            }
            return res;
        }

        public String connectToServer(String urlString) throws Exception {
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
                    sb.append(line + "\n");
                }
                return sb.toString();
            } catch (Exception e) {
                Log.d("", e.getLocalizedMessage());
                return "";
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

        @Override
        protected void onPostExecute(String result) {
            DisarmBombActivity.this.updateData(result);
        }
    }

    public void updateData(String string) {
        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText(string);
    }
}
