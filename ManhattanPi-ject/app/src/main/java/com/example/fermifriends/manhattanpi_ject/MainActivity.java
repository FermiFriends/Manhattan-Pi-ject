package com.example.fermifriends.manhattanpi_ject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "Settings";
    private static final String SERVER_URL = "http://129.31.192.121:5000";
    public static final String[] SETTING_NAMES_INTS = {
            "TIME_LIMIT",
            "TEMP_DELTA",
            "TEMP_RANGE",
            "LIGHT_DELTA",
            "LIGHT_RANGE",
            "PINS_IN_BITS",
            "EXPECTED_PINS_OUT_IN_BITS",
            "PROXIMITY_DELTA",
            "PROXIMITY_RANGE",
            "NOB_ANGLE"
    };
    private SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void onPlayClick(View view) {
        configurePreferences(settings.edit());
        ASyncHttpPostTask aSyncHttpPostTask = new ASyncHttpPostTask();
        aSyncHttpPostTask.execute(SERVER_URL);
        Intent intent = new Intent(this, FindBombActivity.class);
        startActivity(intent);
    }

    public void configurePreferences(SharedPreferences.Editor editor) {
        try {
            editor.putBoolean("pollServer", ((Switch) findViewById(R.id.pollSwitch)).isChecked());
            editor.putString("serverURL", SERVER_URL);
            editor.putInt("TIME_LIMIT", intFromTextEdit(R.id.timeLimitEdit));
            editor.putInt("TEMP_DELTA", intFromTextEdit(R.id.tempDeltaEdit));
            editor.putInt("TEMP_RANGE", intFromTextEdit(R.id.tempRangeEdit));
            editor.putInt("LIGHT_DELTA", intFromTextEdit(R.id.lightDeltaEdit));
            editor.putInt("LIGHT_RANGE", intFromTextEdit(R.id.lightRangeEdit));
            editor.putInt("PINS_IN_BITS", ((SeekBar) findViewById(R.id.pinsBar)).getProgress());
            editor.putInt("EXPECTED_PINS_OUT_IN_BITS", ((SeekBar) findViewById(R.id.pinsOutBar)).getProgress());
            editor.putInt("PROXIMITY_DELTA", intFromTextEdit(R.id.proxDeltaEdit));
            editor.putInt("PROXIMITY_RANGE", intFromTextEdit(R.id.proxRangeEdit));
            editor.putInt("NOB_ANGLE", ((SeekBar) findViewById(R.id.knobBar)).getProgress());
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        editor.commit();
    }

    private int intFromTextEdit(int id) throws NumberFormatException {
        String s = ((EditText) findViewById(id)).getText().toString();
        if (s.equals("")) {
            s = ((EditText) findViewById(id)).getHint().toString();
        }
        return Integer.parseInt(s);
    }


    private class ASyncHttpPostTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return postToServer(params[0], makeJson());
        }

        private String makeJson() {
            JSONObject jsonObject = new JSONObject();
            try {
                for (String settingName : MainActivity.SETTING_NAMES_INTS) {
                    jsonObject.put(settingName, settings.getInt(settingName, 0));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return jsonObject.toString();
        }

        private String postToServer(String desiredUrl, String json) {
            System.out.println(json);
            if (settings.getBoolean("pollServer", false)) {
                try {
                    URL url = new URL(desiredUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.connect();
                    connection.getOutputStream();
                    OutputStream os = connection.getOutputStream();
                    os.write(json.getBytes("UTF-8"));
                    os.flush();
                    os.close();
                    connection.getInputStream().close();
                    return "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
