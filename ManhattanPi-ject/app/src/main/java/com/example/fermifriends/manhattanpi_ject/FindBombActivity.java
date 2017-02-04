package com.example.fermifriends.manhattanpi_ject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class FindBombActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_bomb);
    }

    //Called when the user presses the disarm button
    public void disarmBomb(View view) {
        Intent intent = new Intent(this, DisarmBombActivity.class);
        startActivity(intent);
    }
}
