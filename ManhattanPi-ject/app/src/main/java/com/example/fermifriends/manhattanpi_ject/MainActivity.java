package com.example.fermifriends.manhattanpi_ject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Called when the user presses the play button
    public void findBomb(View view) {
        Intent intent = new Intent(this, FindBombActivity.class);
        startActivity(intent);
    }
}
