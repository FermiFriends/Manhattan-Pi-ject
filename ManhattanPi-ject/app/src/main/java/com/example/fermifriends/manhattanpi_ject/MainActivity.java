package com.example.fermifriends.manhattanpi_ject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "Settings";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onPlayClick(View view) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("pollServer", ((Switch) findViewById(R.id.pollSwitch)).isChecked());
        editor.apply();
        Intent intent = new Intent(this, FindBombActivity.class);
        startActivity(intent);
    }

}
