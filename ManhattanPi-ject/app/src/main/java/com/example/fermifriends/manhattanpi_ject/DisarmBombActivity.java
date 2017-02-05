package com.example.fermifriends.manhattanpi_ject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
    private boolean gotTime = false;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disarm_bomb);
        settings = getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
        contactServer = settings.getBoolean("pollServer", false);
        server_url = settings.getString("serverURL", null) + "/status";
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

    private enum Attribute {
        temp, light, prox, knob;
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
        if (!gotTime) {
            try {
                long time = (long) (settings.getInt("TIME_LIMIT", 30) - jsonObject.getDouble("TIME_ELAPSED"));
                countDownTimer = new CountDownTimer(time * 1000, 1000) {
                    private TextView cdt = ((TextView) findViewById(R.id.countdownText));

                    @Override
                    public void onTick(long millisUntilFinished) {
                        long seconds = millisUntilFinished / 1000;
                        long minutes = seconds / 60;
                        seconds = seconds % 60;
                        String prefixS = "";
                        String prefixM = "";
                        if (seconds < 10) {
                            prefixS = "0";
                        }
                        if (minutes < 10) {
                            prefixM = "0";
                        }
                        cdt.setText(prefixM + minutes + ":" + prefixS + seconds);
                    }

                    @Override
                    public void onFinish() {
                        cdt.setText("KABOOM!!!");
                    }
                };
                countDownTimer.start();
                gotTime = true;
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }
        }


        double temp_delta = INVALID;
        double light_delta = INVALID;
        double proximity_delta = INVALID;
        int knob_angle = INVALID;
        String state = "";
        try {
            temp_delta = (double) jsonObject.get("TEMP_ACTUAL_DELTA");
            light_delta = (int) jsonObject.get("LIGHT_ACTUAL_DELTA");
            proximity_delta = (int) jsonObject.get("PROXIMITY_ACTUAL_DELTA");
            knob_angle = (int) jsonObject.get("ACTUAL_NOB_ANGLE");
            state = jsonObject.getString("STATE");
            System.out.println(state);

        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        if (state.equals("DISARMED")) {
            endDialog("win!!!");
        } else if (state.equals("DETONATED")) {
            endDialog("lose!");
        }
        double tempCalc = calcTarget(settings.getInt("TEMP_DELTA", 0), temp_delta, settings.getInt("TEMP_RANGE", 0));
        double lightCalc = calcTarget(settings.getInt("LIGHT_DELTA", 0), light_delta, settings.getInt("LIGHT_RANGE", 0));
        double proxCalc = calcTarget(settings.getInt("PROXIMITY_DELTA", 0), proximity_delta, settings.getInt("PROXIMITY_RANGE", 0));
        int knobCalc = settings.getInt("NOB_ANGLE", 0) - knob_angle;
        configureText((int) tempCalc, R.id.tempText, Attribute.temp);
        configureText((int) lightCalc, R.id.lightText, Attribute.light);
        configureText((int) proxCalc, R.id.proxText, Attribute.prox);
        configureText(knobCalc, R.id.knobText, Attribute.knob);
    }

    private void endDialog(String state) {
        doPoll = false;
        contactServer = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You " + state)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        backToMain();
                    }
                })
                .setTitle("Game Complete!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void backToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private double calcTarget(double delta, double actual_delta, double range) {
        if (actual_delta <= delta) {
            return delta - actual_delta;
        } else if (actual_delta >= (range + delta)) {
            return actual_delta - range - delta;
        } else {
            return 0.0;
        }
    }

    private void configureText(double value, int id, Attribute type) {
        TextView textView = (TextView) findViewById(id);
        String prefix = null;
        switch (type) {
            case temp:
                prefix = "Temperature: ";
                break;
            case light:
                prefix = "Light: ";
                break;
            case prox:
                prefix = "Proximity: ";
                break;
            case knob:
                prefix = "Knob Angle: ";
                break;
        }
        textView.setText(prefix + String.valueOf(value));
        if (value == 0) {
            textView.setBackgroundColor(Color.parseColor("#84ffc5"));
        } else {
            textView.setBackgroundColor(Color.parseColor("#db5151"));
        }
    }
}
