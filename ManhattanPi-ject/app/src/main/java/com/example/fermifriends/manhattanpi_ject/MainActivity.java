package com.example.fermifriends.manhattanpi_ject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    public static final BluetoothAdapter BLUETOOTH_ADAPTER = BluetoothAdapter.getDefaultAdapter();
    public static final String PREFS_NAME = "Settings";
    private static final String SERVER_URL = "http://129.31.192.121:5000/status";
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

    private final BroadcastReceiver RECEIVER = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                System.out.println(name + " => " + rssi + "dBm\n");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        registerReceiver(RECEIVER, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    public void refreshRSSI(View view) {
        requestPermissions(new String[] {"android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 0);
        BLUETOOTH_ADAPTER.startDiscovery();
    }

    public void onPlayClick(View view) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        configurePreferences(settings.edit());
        findBomb(view);
    }

    public void configurePreferences(SharedPreferences.Editor editor) {
        editor.putBoolean("pollServer", ((Switch) findViewById(R.id.pollSwitch)).isChecked());
        editor.putString("serverURL", SERVER_URL);
        editor.putInt("TIME_LIMIT", 180);
        editor.putInt("TEMP_DELTA", 4);
        editor.putInt("TEMP_RANGE", 3);
        editor.putInt("LIGHT_DELTA", -50);
        editor.putInt("LIGHT_RANGE", 25);
        editor.putInt("PINS_IN_BITS", 3);
        editor.putInt("EXPECTED_PINS_OUT_IN_BITS", 6);
        editor.putInt("PROXIMITY_DELTA", 200);
        editor.putInt("PROXIMITY_RANGE", 40);
        editor.putInt("NOB_ANGLE", 4);
        editor.commit();
    }

    public void findBomb(View view) {
        Intent intent = new Intent(this, FindBombActivity.class);
        startActivity(intent);
    }
}
