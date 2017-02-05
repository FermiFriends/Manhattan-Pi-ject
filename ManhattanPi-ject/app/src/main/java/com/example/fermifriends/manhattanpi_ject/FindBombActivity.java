package com.example.fermifriends.manhattanpi_ject;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


public class FindBombActivity extends AppCompatActivity {
    private SharedPreferences settings;

    public static final double MAX_DISTANCE_COMPARATOR = 10000;
    public static final double MIN_PROGRESS_TO_PROCEED = 85;

    public static final String DEVICE_BLUETOOTH_NAME = "pynamite";
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
                        if (progress >= MIN_PROGRESS_TO_PROCEED) {
                            Button button = (Button) (findViewById(R.id.disarmButton));
                            button.setBackgroundColor(Color.GREEN);
                            button.setTextColor(Color.WHITE);
                            button.setVisibility(View.VISIBLE);
                        } else {
                            Button button = (Button) (findViewById(R.id.disarmButton));
                            button.setVisibility(View.INVISIBLE);
                        }
                        ((ProgressBar) findViewById(R.id.bluetoothStrengthBar)).setProgress(progress);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_find_bomb);
        if (settings.getBoolean("useBluetooth", false)) {
            System.out.println("VISIBILITY " + findViewById(R.id.disarmButton).getVisibility());

            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            setIntent(registerReceiver(RECEIVER, intentFilter));
        } else {
            findViewById(R.id.disarmButton).setVisibility(View.VISIBLE);
        }
    }

    //Called when the user presses the disarm button
    public void disarmBomb(View view) {
        Intent intent = new Intent(this, DisarmBombActivity.class);
        startActivity(intent);
    }

    //Called when the progress bar is clicked
    public void refreshRSSI(View view) {
        requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        try {
            BLUETOOTH_ADAPTER.startDiscovery();
        } catch (Exception e) {
            e.printStackTrace();
            Toast pollFailToast = Toast.makeText(this, "Failed to refresh bluetooth strength", Toast.LENGTH_LONG);
            pollFailToast.show();
            return;
        }

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

}
