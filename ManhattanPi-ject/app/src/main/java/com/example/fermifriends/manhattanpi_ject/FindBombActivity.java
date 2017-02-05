package com.example.fermifriends.manhattanpi_ject;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FindBombActivity extends AppCompatActivity {
    private SharedPreferences settings;

    public static final double MAX_DISTANCE_COMPARATOR = 10000;
    public static final double MIN_PROGRESS_TO_PROCEED = 85;

    public static final String DEVICE_BLUETOOTH_NAME = "ubuntu-0";
    public static final BluetoothAdapter BLUETOOTH_ADAPTER = BluetoothAdapter.getDefaultAdapter();
    private final BroadcastReceiver RECEIVER = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                if (name != null) {
                    if (name.equals(DEVICE_BLUETOOTH_NAME)) {
                        double distance = getDistance(rssi, 0);
                        System.out.println(distance);
                        int progress = (int) Math.floor((1 - distance / MAX_DISTANCE_COMPARATOR) * 100);
                        ((ProgressBar) findViewById(R.id.bluetoothStrengthBar)).setProgress(progress);
                        if (progress >= MIN_PROGRESS_TO_PROCEED) {
                            Button button = (Button) (findViewById(R.id.disarmButton));
                            button.setVisibility(View.VISIBLE);
                        } else {
                            Button button = (Button) (findViewById(R.id.disarmButton));
                            button.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_bomb);
        System.out.println("VISIBILITY " + findViewById(R.id.disarmButton).getVisibility());

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        setIntent(registerReceiver(RECEIVER, intentFilter));
        settings = getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
    }

    //Called when the user presses the disarm button
    public void disarmBomb(View view) {
        ASyncHttpPostTask aSyncHttpPostTask = new ASyncHttpPostTask();
        aSyncHttpPostTask.execute(settings.getString("serverURL", null));
        Intent intent = new Intent(this, DisarmBombActivity.class);
        startActivity(intent);
    }

    //Called when the progress bar is clicked
    public void refreshRSSI(View view) {
        requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        System.out.println("startDiscovery called on adapter");
        BLUETOOTH_ADAPTER.startDiscovery();
    }

    double getDistance(int rssi, int txPower) {
    /*
     * RSSI = TxPower - 10 * n * lg(d)
     * n = 2 (in free space)
     *
     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
     */

        return Math.pow(10.0, ((double) txPower - rssi) / (10 * 2));
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
