package com.example.eyeprotection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Tips2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips2);

        findViewById(R.id.homeButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // ホーム画面へ遷移用intent
                        Intent intent = new Intent(Tips2Activity.this, MainActivity.class);
                        // MainActivity起動
                        startActivity(intent);
                    }
                }
        );

        findViewById(R.id.next).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Tips3へ遷移用intent
                        Intent intent = new Intent(Tips2Activity.this, Tips3Activity.class);
                        // Tips3Activity起動
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }
        );
    }
}