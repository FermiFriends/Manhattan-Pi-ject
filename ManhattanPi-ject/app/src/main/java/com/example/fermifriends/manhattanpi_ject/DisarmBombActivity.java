package com.example.fermifriends.manhattanpi_ject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class DisarmBombActivity extends AppCompatActivity {
    private static final int NUM_SECONDS_FOR_READ_TIMEOUT = 10;
    private static final int NUM_TICKS_PER_SECOND = 1000;
    private static final int INVALID = -1;
    private SharedPreferences settings;
    private String server_url;
    private boolean doPoll = true;
    private boolean contactServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disarm_bomb);
        settings = getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
        contactServer = settings.getBoolean("pollServer", false);
        server_url = settings.getString("serverURL", null) + "/status";
        CountDownTimer countDownTimer = new CountDownTimer(settings.getInt("TIME_LIMIT", 30) * 1000,1000) {
            private TextView cdt = ((TextView)findViewById(R.id.countdownText));
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                seconds = seconds % 60;
                cdt.setText(minutes + ":" + seconds);
            }

            @Override
            public void onFinish() {
                cdt.setText("KABOOM!!!");
            }
        };
        countDownTimer.start();
        callAsynchronousTask();
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask getDataRegularly = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (doPoll) {
                            AsyncHttpTask asyncHttpTask = new AsyncHttpTask();
                            asyncHttpTask.execute(server_url);
                        }
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(getDataRegularly, 0, NUM_TICKS_PER_SECOND);
    }

    public void startPolling(View view) {
        AsyncHttpTask asyncHttpTask = new AsyncHttpTask();
        asyncHttpTask.execute(server_url);
    }


    private class AsyncHttpTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            System.out.println("Polling server...");
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
                connection.setConnectTimeout(NUM_SECONDS_FOR_READ_TIMEOUT * NUM_TICKS_PER_SECOND);
                if (contactServer) {
                    connection.connect();
                    return connection.getInputStream();
                } else {
                    return null;
                }
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
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(string);
            doPoll = true;
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            doPoll = false;
            Toast pollFailToast = Toast.makeText(this, "Polling server failed - press poll to retry", Toast.LENGTH_SHORT);
            pollFailToast.show();
            return;
        }


        double temp_delta = INVALID;
        int light_delta = INVALID;
        int prox_delta = INVALID;
        int knob_angle = INVALID;
        try {
            temp_delta = (double) jsonObject.get("TEMP_ACTUAL_DELTA");
            light_delta = (int) jsonObject.get("LIGHT_ACTUAL_DELTA");
            prox_delta = (int) jsonObject.get("PROXIMITY_ACTUAL_DELTA");
            knob_angle = (int) jsonObject.get("ACTUAL_NOB_ANGLE");

        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        setTextViewToInt(temp_delta, R.id.tempText);
        setTextViewToInt(light_delta, R.id.lightText);
        setTextViewToInt(prox_delta, R.id.proxText);
        setTextViewToInt(knob_angle, R.id.knobText);
    }

    private void setTextViewToInt(double value, int id) {
        ((TextView) findViewById(id)).setText(String.valueOf(value));
    }
}
