package com.example.eyeprotection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Tips1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips1);

        findViewById(R.id.homeButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // ホーム画面へ遷移用intent
                        Intent intent = new Intent(Tips1Activity.this, MainActivity.class);
                        // MainActivity起動
                        startActivity(intent);
                    }
                }
        );

        findViewById(R.id.next).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Tips2へ遷移用intent
                        Intent intent = new Intent(Tips1Activity.this, Tips2Activity.class);
                        // Tips2Activity起動
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }
        );
    }
}