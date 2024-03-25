package com.example.eyeprotection;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    // 適正な距離の回数
    private int eyeCountGood = 0;
    // 近づいた回数
    private int eyeCountNg = 0;

    // goodテキストビュー
    private TextView goodTV;
    // ngテキストビュー
    private TextView ngTV;
    // 時刻テキストビュー
    private TextView ntTV;

    // ホームに戻るボタン
    Button homeB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // 適切な回数取得
        eyeCountGood = getIntent().getIntExtra("EyeCountGood", 0);
        // 不適切な回数
        eyeCountNg = getIntent().getIntExtra("EyeCountNg", 0);

        // 現在の時間
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒", Locale.getDefault());
        String currentDateTime = dateFormat.format(new Date(System.currentTimeMillis()));

        SimpleDateFormat nowDateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        String nowTime = nowDateFormat.format(new Date(System.currentTimeMillis()));

        // テキストビュー
        goodTV = findViewById(R.id.goodTextView);
        ngTV = findViewById(R.id.ngTextView);
        ntTV = findViewById(R.id.nowTimeTextView);
        goodTV.setText(" 適正：" + eyeCountGood + "回");
        ngTV.setText(" 不適正：" + eyeCountNg + "回");
        ntTV.setText(nowTime);

        // データベース
        dataBase(eyeCountGood, eyeCountNg, currentDateTime);

        // 円グラフ
        pieChart();

        // ホームボタン
        homeB = findViewById(R.id.homeButton);
        // ホームボタンを押したとき
        homeB.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // ホーム画面へ遷移用intent
                        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                        // ホーム起動
                        startActivity(intent);
                    }
                }
        );

    }

    // データベース
    private void dataBase(int count1, int count2, String text1) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME1, count1);
        values.put(DatabaseHelper.COLUMN_NAME2, count2);
        values.put(DatabaseHelper.COLUMN_NAME3, text1);


        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        // データベースを開く
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        long result = db.insert(DatabaseHelper.TABLE_NAME, null, values);

        if (result == -1) {
            // Insertion failed
            Toast.makeText(this, "きろくできませんでした", Toast.LENGTH_SHORT).show();
        } else {
            // Insertion successful
            Toast.makeText(this, "きろくしました", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    // 円グラフ
    private void pieChart() {
        // データセットの作成
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(eyeCountGood, "パーセント")); // 適正な距離の回数
        entries.add(new PieEntry(eyeCountNg, "パーセント")); // 近づいた回数

        // データセットの作成
        PieDataSet dataSet = new PieDataSet(entries, "");
        // グラフの色を設定
        int[] colors = new int[]{Color.parseColor("#b9db98"), Color.parseColor("#f390ad")};
        dataSet.setColors(colors);
        // グラフ内のテキストの色を設定
        dataSet.setValueTextColor(Color.WHITE);
        // グラフ内のテキストのサイズを設定
        dataSet.setValueTextSize(12f);
        // 値の表示形式を設定
        dataSet.setValueFormatter(new DefaultValueFormatter(0));
        // データの設定
        PieData data = new PieData(dataSet);

        // グラフの設定
        PieChart chart = findViewById(R.id.pieChart);
        // データの設定
        chart.setData(data);
        // 値をパーセンテージ表示
        chart.setUsePercentValues(true);
        // グラフの説明を無効化
        chart.getDescription().setEnabled(false);
        // グラフの余白を設定
        chart.setExtraOffsets(5, 10, 5, 5);
        // ドラッグ操作の減速係数を設定
        chart.setDragDecelerationFrictionCoef(0.95f);
        // 中央の穴を表示する
        chart.setDrawHoleEnabled(true);
        // 中央の穴の色を設定
        chart.setHoleColor(Color.WHITE);
        // 中央の穴の半径を設定
        chart.setTransparentCircleRadius(61f);
        // アニメーション効果を設定
        chart.animateY(1000);

        // 凡例の設定
        chart.getLegend().setEnabled(false);

        // 更新
        chart.invalidate();
    }
}