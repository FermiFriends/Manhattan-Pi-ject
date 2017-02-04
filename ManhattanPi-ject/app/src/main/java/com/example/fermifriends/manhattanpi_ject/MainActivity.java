package com.example.fermifriends.manhattanpi_ject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Objects;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class MainActivity extends AppCompatActivity {

    private String BOMB_ADDRESS = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectToBomb();
    }

    private void connectToBomb() {
        BluetoothSPP bt = new BluetoothSPP(this);
        System.out.println("hi1");

        while (!bt.isBluetoothEnabled()) {
            bt.enable();
            System.out.println("hi");
        }
        System.out.println(bt.getConnectedDeviceName());
        System.out.println(bt.getConnectedDeviceAddress());
    }

    //Called when the user presses the play button
    public void findBomb(View view) {
        Intent intent = new Intent(this, FindBombActivity.class);
        startActivity(intent);
    }
}
