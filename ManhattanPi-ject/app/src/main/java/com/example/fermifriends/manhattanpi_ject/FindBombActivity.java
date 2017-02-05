package com.example.fermifriends.manhattanpi_ject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FindBombActivity extends AppCompatActivity {
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_bomb);
        settings = getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
    }

    //Called when the user presses the disarm button
    public void disarmBomb(View view) {
        ASyncHttpPostTask aSyncHttpPostTask = new ASyncHttpPostTask();
        aSyncHttpPostTask.execute(settings.getString("serverURL", null));
        Intent intent = new Intent(this, DisarmBombActivity.class);
        startActivity(intent);
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
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(json);
                    writer.flush();
                    writer.close();
                    return "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
