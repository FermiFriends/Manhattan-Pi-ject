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

    private final BroadcastReceiver RECEIVER = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            requestPermissions(new String[] {"android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 0);
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
        registerReceiver(RECEIVER, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    public void refreshRSSI(View view) {
        BLUETOOTH_ADAPTER.startDiscovery();
    }

    public void onPlayClick(View view) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("pollServer", ((Switch) findViewById(R.id.pollSwitch)).isChecked());
        editor.commit();
        findBomb(view);
    }

    public void findBomb(View view) {
        Intent intent = new Intent(this, FindBombActivity.class);
        startActivity(intent);
    }
}
