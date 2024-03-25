/* 最初の画面*/
/* 説明
 * スタートボタンと記録ボタンの画面遷移 */
package com.example.eyeprotection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    // カメラ用リクエストコード
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // チップスボタンを押したとき
        findViewById(R.id.tipsButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // チップス画面へ遷移用intent
                        Intent intent = new Intent(MainActivity.this, Tips1Activity.class);
                        // チップス起動
                        startActivity(intent);
                    }
                }
        );

        // スタートボタンを押したとき
        findViewById(R.id.startButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // カメラのパーミッションを確認
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            //  パーミッションをリクエスト
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
                        } else {
                            // カメラ画面へ遷移用intent
                            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                            // カメラ起動
                            startActivity(intent);
                        }
                    }
                }
        );

        // きろくボタンを押したとき
        findViewById(R.id.diaryButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // きろく画面へ遷移用intent
                        Intent intent = new Intent(MainActivity.this, DiaryActivity.class);
                        // きろく起動
                        startActivity(intent);
                    }
                }
        );
    }

    // パーミッションダイアログでボタンが押されたとき
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            // 許可を押したとき
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // カメラ画面へ遷移用intent
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                //  カメラを起動
                startActivity(intent);
            }
            // 許可しないを押したとき
            else {
                Toast.makeText(this, "カメラのパーミッションが許可されていません", Toast.LENGTH_SHORT).show();
            }
        }
    }
}