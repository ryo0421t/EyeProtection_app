package com.example.eyeprotection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DiaryActivity extends AppCompatActivity implements View.OnClickListener {

    // リストビュー
    private ListView listView;
    // リスト
    private List<String> dataList;
    // データベース内の日付格納
    private List<String> dateTimeList;

    // カレンダー
    private TextView yearMonthDayTV;
    private Button previousButton, nextButton;
    private Calendar calendar;
    // 曜日の下の数字テキストビュー
    private TextView ddSun, ddMon, ddTue, ddWed, ddThu, ddFri, ddSat;
    // 選択した日付
    private String selectListDate;

    // 今日の日付にジャンプ
    private FloatingActionButton nowDateBtn;
    // 今日の日付ボタンが押されたか
    private boolean isSelectedNowDateBtn;

    // 日付の下の丸テキストビュー
    private TextView ddSunTV, ddMonTV, ddTueTV, ddWedTV, ddThuTV, ddFriTV, ddSatTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        findViewById(R.id.homeButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // ホーム画面へ遷移用intent
                        Intent intent = new Intent(DiaryActivity.this, MainActivity.class);
                        // MainActivity起動
                        startActivity(intent);
                    }
                }
        );
        // リストを定義
        listView = findViewById(R.id.list);
        dataList = new ArrayList<>();
        dateTimeList = new ArrayList<>();

        // データベースを読み込みリストに入れる
        readSetListDatabase();

        // レイアウト内のビューを取得
        yearMonthDayTV = findViewById(R.id.yyyyMMddTextView);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);

        // 曜日の下にある数字7日分
        ddSun = findViewById(R.id.ddSun);
        ddMon = findViewById(R.id.ddMon);
        ddTue = findViewById(R.id.ddTue);
        ddWed = findViewById(R.id.ddWed);
        ddThu = findViewById(R.id.ddThu);
        ddFri = findViewById(R.id.ddFri);
        ddSat = findViewById(R.id.ddSat);

        // 今日の日付ボタン
        nowDateBtn = findViewById(R.id.nowDateButton);

        // 日付の下にある丸7日分
        ddSunTV = findViewById(R.id.ddSunTextView);
        ddMonTV = findViewById(R.id.ddMonTextView);
        ddTueTV = findViewById(R.id.ddTueTextView);
        ddWedTV = findViewById(R.id.ddWedTextView);
        ddThuTV = findViewById(R.id.ddThuTextView);
        ddFriTV = findViewById(R.id.ddFriTextView);
        ddSatTV = findViewById(R.id.ddSatTextView);

        // カレンダーのインスタンスを取得
        calendar = Calendar.getInstance();

        // 初期のカレンダーを表示
        updateCalendar();

        // リストに日付のものがあるか
        checkListChangeColor();

        // カレンダー更新
        updateCalendar();

        // 今日の日付更新
        nowDateText();

        // 前の週に移動するボタンのクリックリスナーを設定
        previousButton.setOnClickListener(this);

        // 次の週に移動するボタンのクリックリスナーを設定
        nextButton.setOnClickListener(this);

        // 各日のクリックリスナーを設定
        ddSun.setOnClickListener(this);
        ddMon.setOnClickListener(this);
        ddTue.setOnClickListener(this);
        ddWed.setOnClickListener(this);
        ddThu.setOnClickListener(this);
        ddFri.setOnClickListener(this);
        ddSat.setOnClickListener(this);

        // 今日の日付ボタンのクリックリスナーを設定
        nowDateBtn.setOnClickListener(this);
    }

    // クリックされたとき
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // ←ボタン
            case R.id.previousButton:
                ddSun.setBackgroundColor(Color.parseColor("#ffffff"));
                ddMon.setBackgroundColor(Color.parseColor("#ffffff"));
                ddTue.setBackgroundColor(Color.parseColor("#ffffff"));
                ddWed.setBackgroundColor(Color.parseColor("#ffffff"));
                ddThu.setBackgroundColor(Color.parseColor("#ffffff"));
                ddFri.setBackgroundColor(Color.parseColor("#ffffff"));
                ddSat.setBackgroundColor(Color.parseColor("#ffffff"));
                // 今日の日付ボタン解除
                isSelectedNowDateBtn = false;
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                updateCalendar();
                // リストに日付のものがあるか
                checkListChangeColor();
                break;
            // →ボタン
            case R.id.nextButton:
                ddSun.setBackgroundColor(Color.parseColor("#ffffff"));
                ddMon.setBackgroundColor(Color.parseColor("#ffffff"));
                ddTue.setBackgroundColor(Color.parseColor("#ffffff"));
                ddWed.setBackgroundColor(Color.parseColor("#ffffff"));
                ddThu.setBackgroundColor(Color.parseColor("#ffffff"));
                ddFri.setBackgroundColor(Color.parseColor("#ffffff"));
                ddSat.setBackgroundColor(Color.parseColor("#ffffff"));
                // 今日の日付ボタン解除
                isSelectedNowDateBtn = false;
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                updateCalendar();
                // リストに日付のものがあるか
                checkListChangeColor();
                break;
            // ddSunテキストビュー
            case R.id.ddSun:
                ddSun.setBackgroundColor(Color.parseColor("#f0f0f0"));
                ddMon.setBackgroundColor(Color.parseColor("#ffffff"));
                ddTue.setBackgroundColor(Color.parseColor("#ffffff"));
                ddWed.setBackgroundColor(Color.parseColor("#ffffff"));
                ddThu.setBackgroundColor(Color.parseColor("#ffffff"));
                ddFri.setBackgroundColor(Color.parseColor("#ffffff"));
                ddSat.setBackgroundColor(Color.parseColor("#ffffff"));
                updateSelectedDateText((String) ddSun.getText());
                break;
            // ddMonテキストビュー
            case R.id.ddMon:
                ddSun.setBackgroundColor(Color.parseColor("#ffffff"));
                ddMon.setBackgroundColor(Color.parseColor("#f0f0f0"));
                ddTue.setBackgroundColor(Color.parseColor("#ffffff"));
                ddWed.setBackgroundColor(Color.parseColor("#ffffff"));
                ddThu.setBackgroundColor(Color.parseColor("#ffffff"));
                ddFri.setBackgroundColor(Color.parseColor("#ffffff"));
                ddSat.setBackgroundColor(Color.parseColor("#ffffff"));
                updateSelectedDateText((String) ddMon.getText());
                break;
            // ddTueテキストビュー
            case R.id.ddTue:
                ddSun.setBackgroundColor(Color.parseColor("#ffffff"));
                ddMon.setBackgroundColor(Color.parseColor("#ffffff"));
                ddTue.setBackgroundColor(Color.parseColor("#f0f0f0"));
                ddWed.setBackgroundColor(Color.parseColor("#ffffff"));
                ddThu.setBackgroundColor(Color.parseColor("#ffffff"));
                ddFri.setBackgroundColor(Color.parseColor("#ffffff"));
                ddSat.setBackgroundColor(Color.parseColor("#ffffff"));
                updateSelectedDateText((String) ddTue.getText());
                break;
            // ddWedテキストビュー
            case R.id.ddWed:
                ddSun.setBackgroundColor(Color.parseColor("#ffffff"));
                ddMon.setBackgroundColor(Color.parseColor("#ffffff"));
                ddTue.setBackgroundColor(Color.parseColor("#ffffff"));
                ddWed.setBackgroundColor(Color.parseColor("#f0f0f0"));
                ddThu.setBackgroundColor(Color.parseColor("#ffffff"));
                ddFri.setBackgroundColor(Color.parseColor("#ffffff"));
                ddSat.setBackgroundColor(Color.parseColor("#ffffff"));
                updateSelectedDateText((String) ddWed.getText());
                break;
            // ddThuテキストビュー
            case R.id.ddThu:
                ddSun.setBackgroundColor(Color.parseColor("#ffffff"));
                ddMon.setBackgroundColor(Color.parseColor("#ffffff"));
                ddTue.setBackgroundColor(Color.parseColor("#ffffff"));
                ddWed.setBackgroundColor(Color.parseColor("#ffffff"));
                ddThu.setBackgroundColor(Color.parseColor("#f0f0f0"));
                ddFri.setBackgroundColor(Color.parseColor("#ffffff"));
                ddSat.setBackgroundColor(Color.parseColor("#ffffff"));
                updateSelectedDateText((String) ddThu.getText());
                break;
            // ddFriテキストビュー
            case R.id.ddFri:
                ddSun.setBackgroundColor(Color.parseColor("#ffffff"));
                ddMon.setBackgroundColor(Color.parseColor("#ffffff"));
                ddTue.setBackgroundColor(Color.parseColor("#ffffff"));
                ddWed.setBackgroundColor(Color.parseColor("#ffffff"));
                ddThu.setBackgroundColor(Color.parseColor("#ffffff"));
                ddFri.setBackgroundColor(Color.parseColor("#f0f0f0"));
                ddSat.setBackgroundColor(Color.parseColor("#ffffff"));
                updateSelectedDateText((String) ddFri.getText());
                break;
            // ddSatテキストビュー
            case R.id.ddSat:
                ddSun.setBackgroundColor(Color.parseColor("#ffffff"));
                ddMon.setBackgroundColor(Color.parseColor("#ffffff"));
                ddTue.setBackgroundColor(Color.parseColor("#ffffff"));
                ddWed.setBackgroundColor(Color.parseColor("#ffffff"));
                ddThu.setBackgroundColor(Color.parseColor("#ffffff"));
                ddFri.setBackgroundColor(Color.parseColor("#ffffff"));
                ddSat.setBackgroundColor(Color.parseColor("#f0f0f0"));
                updateSelectedDateText((String) ddSat.getText());
                break;
            // 今日の日付へ
            case R.id.nowDateButton:
                ddSun.setBackgroundColor(Color.parseColor("#ffffff"));
                ddSun.setBackgroundColor(Color.parseColor("#ffffff"));
                ddMon.setBackgroundColor(Color.parseColor("#ffffff"));
                ddMon.setBackgroundColor(Color.parseColor("#ffffff"));
                ddTue.setBackgroundColor(Color.parseColor("#ffffff"));
                ddTue.setBackgroundColor(Color.parseColor("#ffffff"));
                ddWed.setBackgroundColor(Color.parseColor("#ffffff"));
                ddWed.setBackgroundColor(Color.parseColor("#ffffff"));
                ddThu.setBackgroundColor(Color.parseColor("#ffffff"));
                ddThu.setBackgroundColor(Color.parseColor("#ffffff"));
                ddFri.setBackgroundColor(Color.parseColor("#ffffff"));
                ddFri.setBackgroundColor(Color.parseColor("#ffffff"));
                ddSat.setBackgroundColor(Color.parseColor("#ffffff"));
                ddSat.setBackgroundColor(Color.parseColor("#ffffff"));
                // updateCalendarを行う前に初期化
                calendar = Calendar.getInstance();
                // カレンダー更新
                updateCalendar();
                // 今日の日付更新
                nowDateText();
                // リストに日付のものがあるか
                checkListChangeColor();
                break;
        }
    }

    // データベースを読み込みリストに入れる
    private void readSetListDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String[] projection = {DatabaseHelper.COLUMN_NAME1, DatabaseHelper.COLUMN_NAME2, DatabaseHelper.COLUMN_NAME3};
        String sortOrder = DatabaseHelper.COLUMN_ID + " ASC";

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        while (cursor.moveToNext()) {
            String name1 = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME1));
            String name2 = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME2));
            String name3 = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME3));
            dataList.add(name3 + "\n近かった回数：" + name1 + "\n適切だった回数：" + name2);
            // name3（リストに登録された日付）を全てリストに格納
            dateTimeList.add(name3);
        }

        cursor.close();
        db.close();

        updateListView();
    }

    // 選択された日付のデータのみをリストに表示する
    private void updateListViewWithSelectedDate(String selectedDate) {
        List<String> filteredList = new ArrayList<>();

        for (String data : dataList) {
            if (data.contains(selectedDate)) {
                filteredList.add(data);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredList);
        listView.setAdapter(adapter);
    }

    // データをListViewに表示する
    private void updateListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
    }


    // カレンダーを更新して表示するメソッド
    private void updateCalendar() {
        // 年と月の表示を更新
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("YYYY年 MM月", Locale.getDefault());
        yearMonthDayTV.setText(monthYearFormat.format(calendar.getTime()));

        // 各日の表示を更新
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        ddSun.setText(dayFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        ddMon.setText(dayFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        ddTue.setText(dayFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        ddWed.setText(dayFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        ddThu.setText(dayFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        ddFri.setText(dayFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        ddSat.setText(dayFormat.format(calendar.getTime()));
    }

    // 選択された日付を表示するメソッド
    private void updateSelectedDateText(String selectedDate) {
        // 選択された日付を "〇月〇日" の形式に変換
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String yearDate = yearFormat.format(calendar.getTime());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        String monthDate = monthFormat.format(calendar.getTime());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        String dayDate = dayFormat.format(calendar.getTime());

        // 選択された日付の日を取得
        int selectedDay = Integer.parseInt(selectedDate);

        // 01日が存在するかチェックするため日曜日から土曜日までの数字のテキストビューをチェック
        boolean isTextViewContent01 = ddSun.getText().toString().equals("01")
                || ddMon.getText().toString().equals("01")
                || ddTue.getText().toString().equals("01")
                || ddWed.getText().toString().equals("01")
                || ddThu.getText().toString().equals("01")
                || ddFri.getText().toString().equals("01")
                || ddSat.getText().toString().equals("01");

        // 01日が存在する場合かつ、選択された日付が23日以上の場合かつ、今日の日付ボタンが押されなかった場合
        // 月を-1する
        if (isTextViewContent01 && selectedDay >= 23 && isSelectedNowDateBtn == false) {
            int month = Integer.parseInt(monthDate);
            month -= 1;
            // 12月の場合は1月を-1すると0月になるため12月に直す
            if (month == 0) {
                monthDate = String.valueOf(12);
                int year = Integer.parseInt(yearDate);
                yearDate = String.valueOf(year - 1);
            }
            // 1月～9月の場合は01～09に直す
            else if (month <= 9) {
                monthDate = String.valueOf("0" + month);
            }
            // 10～12月の場合はそのまま10～12
            else {
                monthDate = String.valueOf(month);
            }
        }
        // 今日の日付が23日以上かつ、01日が存在する場合かつ、選択された日付が6日以下の場合かつ、今日の日付ボタンが押されていた場合
        // 月を+1する
        // 週に23日～31日のどれかと1日～6日のどれかが存在した場合で今日の日付が23日～31日であるときに1日～6日を押すと月が+1されるようにする
        else if (Integer.parseInt(dayDate) >= 23 && isTextViewContent01 && selectedDay <= 6 && isSelectedNowDateBtn == true) {
            int month = Integer.parseInt(monthDate);
            month += 1;
            // 1月～9月の場合は01～09に直す
            if (month <= 9) {
                monthDate = String.valueOf("0" + month);
            }
            // 13月、つまり12月と1月が存在する場合 1月をタップすると13月になるため1月に直す
            else if (month == 13) {
                monthDate = String.valueOf("01");
                int year = Integer.parseInt(yearDate);
                yearDate = String.valueOf(year + 1);
            }
            // 10～12月の場合はそのまま10～12
            else {
                monthDate = String.valueOf(month);
            }
        }
        // 今日の日付が6日以下かつ、01日が存在する場合かつ、選択された日付が23日以上の場合かつ、今日の日付ボタンが押されていた場合
        // 月を-1する
        // 週に23日～31日のどれかと1日～6日のどれかが存在した場合で今日の日付が23日～31日であるときに1日～6日を押すと月が+1されるようにする
        else if (Integer.parseInt(dayDate) <= 6 && isTextViewContent01 && selectedDay >= 23 && isSelectedNowDateBtn == true) {
            int month = Integer.parseInt(monthDate);
            month -= 1;
            // 12月の場合は1月を-1すると0月になるため12月に直す
            if (month == 0) {
                monthDate = String.valueOf(12);
                int year = Integer.parseInt(yearDate);
                yearDate = String.valueOf(year - 1);
            }
            // 1月～9月の場合は01～09に直す
            else if (month <= 9) {
                monthDate = String.valueOf("0" + month);
            }
            // 10～12月の場合はそのまま10～12
            else {
                monthDate = String.valueOf(month);
            }
        }
        // 選択された日付を上部のTextViewに表示 年と月の後ろに空白がある
        yearMonthDayTV.setText(yearDate + "年 " + monthDate + "月 " + selectedDate + "日");

        // yyyy年MM月dd日形式で格納
        selectListDate = yearDate + "年" + monthDate + "月" + selectedDate + "日";
        // 選択された日付のデータのみを表示する
        updateListViewWithSelectedDate(selectListDate);
    }

    // 今日の日付を表示するメソッド
    private void nowDateText() {
        isSelectedNowDateBtn = true;
        // 現在の日付を取得する
        calendar = Calendar.getInstance();
        // 現在の日付を "〇年〇月〇日" の形式に変換
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String yearDate = yearFormat.format(calendar.getTime());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        String monthDate = monthFormat.format(calendar.getTime());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        String dayDate = dayFormat.format(calendar.getTime());

        // 日付を上部のTextViewに表示（年と月の後ろに空白がある）
        yearMonthDayTV.setText(yearDate + "年 " + monthDate + "月 " + dayDate + "日");
        // 今日の日にちが存在するかチェックするため日曜日から土曜日までの数字のテキストビューをチェックし
        // 同じ日付があった場合それに色を付ける
        if (ddSun.getText().toString().equals(dayDate)) {
            ddSun.setBackgroundColor(Color.parseColor("#f0f0f0"));
        } else if (ddMon.getText().toString().equals(dayDate)) {
            ddMon.setBackgroundColor(Color.parseColor("#f0f0f0"));
        } else if (ddTue.getText().toString().equals(dayDate)) {
            ddTue.setBackgroundColor(Color.parseColor("#f0f0f0"));
        } else if (ddWed.getText().toString().equals(dayDate)) {
            ddWed.setBackgroundColor(Color.parseColor("#f0f0f0"));
        } else if (ddThu.getText().toString().equals(dayDate)) {
            ddThu.setBackgroundColor(Color.parseColor("#f0f0f0"));
        } else if (ddFri.getText().toString().equals(dayDate)) {
            ddFri.setBackgroundColor(Color.parseColor("#f0f0f0"));
        } else if (ddSat.getText().toString().equals(dayDate)) {
            ddSat.setBackgroundColor(Color.parseColor("#f0f0f0"));
        }

        // yyyy年MM月dd日形式で格納
        selectListDate = yearDate + "年" + monthDate + "月" + dayDate + "日";
        // 選択された日付のデータのみを表示する
        updateListViewWithSelectedDate(selectListDate);
    }

    // きろくがある場合（データベースにデータがある場合）丸を付ける
    private void checkListChangeColor() {
        // 表示されてる一週間の日にちを取得
        String ddSunDate = ddSun.getText().toString();
        String ddMonDate = ddMon.getText().toString();
        String ddTueDate = ddTue.getText().toString();
        String ddWedDate = ddWed.getText().toString();
        String ddThuDate = ddThu.getText().toString();
        String ddFriDate = ddFri.getText().toString();
        String ddSatDate = ddSat.getText().toString();

        // 年を格納
        String ddSunYearDate = checkYear(Integer.parseInt(ddSunDate));
        String ddMonYearDate = checkYear(Integer.parseInt(ddMonDate));
        String ddTueYearDate = checkYear(Integer.parseInt(ddTueDate));
        String ddWedYearDate = checkYear(Integer.parseInt(ddWedDate));
        String ddThuYearDate = checkYear(Integer.parseInt(ddThuDate));
        String ddFriYearDate = checkYear(Integer.parseInt(ddFriDate));
        String ddSatYearDate = checkYear(Integer.parseInt(ddSatDate));

        // 月を格納
        String ddSunMonthDate = checkMonth(Integer.parseInt(ddSunDate));
        String ddMonMonthDate = checkMonth(Integer.parseInt(ddMonDate));
        String ddTueMonthDate = checkMonth(Integer.parseInt(ddTueDate));
        String ddWedMonthDate = checkMonth(Integer.parseInt(ddWedDate));
        String ddThuMonthDate = checkMonth(Integer.parseInt(ddThuDate));
        String ddFriMonthDate = checkMonth(Integer.parseInt(ddFriDate));
        String ddSatMonthDate = checkMonth(Integer.parseInt(ddSatDate));

        // yyyy年MM月dd日形式で格納
        ddSunDate = ddSunYearDate + "年" + ddSunMonthDate + "月" + ddSunDate + "日";
        ddMonDate = ddMonYearDate + "年" + ddMonMonthDate + "月" + ddMonDate + "日";
        ddTueDate = ddTueYearDate + "年" + ddTueMonthDate + "月" + ddTueDate + "日";
        ddWedDate = ddWedYearDate + "年" + ddWedMonthDate + "月" + ddWedDate + "日";
        ddThuDate = ddThuYearDate + "年" + ddThuMonthDate + "月" + ddThuDate + "日";
        ddFriDate = ddFriYearDate + "年" + ddFriMonthDate + "月" + ddFriDate + "日";
        ddSatDate = ddSatYearDate + "年" + ddSatMonthDate + "月" + ddSatDate + "日";

        // 丸テキストを初期化
        ddSunTV.setText("");
        ddMonTV.setText("");
        ddTueTV.setText("");
        ddWedTV.setText("");
        ddThuTV.setText("");
        ddFriTV.setText("");
        ddSatTV.setText("");
        // iを初期化
        int i = 0;
        // データベースに格納されている日付と表示されている一週間の日付を比較する
        for (String dateTime : dateTimeList) {
            if (dateTimeList.get(i).substring(0, 11).equals(ddSunDate)) {
                ddSunTV.setText("●");
            } else if (dateTimeList.get(i).substring(0, 11).equals(ddMonDate)) {
                ddMonTV.setText("●");
            } else if (dateTimeList.get(i).substring(0, 11).equals(ddTueDate)) {
                ddTueTV.setText("●");
            } else if (dateTimeList.get(i).substring(0, 11).equals(ddWedDate)) {
                ddWedTV.setText("●");
            } else if (dateTimeList.get(i).substring(0, 11).equals(ddThuDate)) {
                ddThuTV.setText("●");
            } else if (dateTimeList.get(i).substring(0, 11).equals(ddFriDate)) {
                ddFriTV.setText("●");
            } else if (dateTimeList.get(i).substring(0, 11).equals(ddSatDate)) {
                ddSatTV.setText("●");
            }
            // インクリメント
            i++;
        }
    }

    // 日付から月を取得
    private String checkMonth(int selectedDay) {
        // 選択された日付を "〇年〇月" の形式に変換
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String yearDate = yearFormat.format(calendar.getTime());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        String monthDate = monthFormat.format(calendar.getTime());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        String dayDate = dayFormat.format(calendar.getTime());
        // 01日が存在するかチェックするため日曜日から土曜日までの数字のテキストビューをチェック
        boolean isTextViewContent01 = ddSun.getText().toString().equals("01")
                || ddMon.getText().toString().equals("01")
                || ddTue.getText().toString().equals("01")
                || ddWed.getText().toString().equals("01")
                || ddThu.getText().toString().equals("01")
                || ddFri.getText().toString().equals("01")
                || ddSat.getText().toString().equals("01");

        // 01日が存在する場合かつ、選択された日付が23日以上の場合かつ、今日の日付ボタンが押されなかった場合
        // 月を-1する
        if (isTextViewContent01 && selectedDay >= 23 && isSelectedNowDateBtn == false) {
            int month = Integer.parseInt(monthDate);
            month -= 1;
            // 12月の場合は1月を-1すると0月になるため12月に直す
            if (month == 0) {
                monthDate = String.valueOf(12);
            }
            // 1月～9月の場合は01～09に直す
            else if (month <= 9) {
                monthDate = String.valueOf("0" + month);
            }
            // 10～12月の場合はそのまま10～12
            else {
                monthDate = String.valueOf(month);
            }
        }
        // 今日の日付が23日以上かつ、01日が存在する場合かつ、選択された日付が6日以下の場合かつ、今日の日付ボタンが押されていた場合
        // 月を+1する
        // 週に23日～31日のどれかと1日～6日のどれかが存在した場合で今日の日付が23日～31日であるときに1日～6日を押すと月が+1されるようにする
        else if (Integer.parseInt(dayDate) >= 23 && isTextViewContent01 && selectedDay <= 6 && isSelectedNowDateBtn == true) {
            int month = Integer.parseInt(monthDate);
            month += 1;
            // 1月～9月の場合は01～09に直す
            if (month <= 9) {
                monthDate = String.valueOf("0" + month);
            }
            // 13月、つまり12月と1月が存在する場合 1月をタップすると13月になるため1月に直す
            else if (month == 13) {
                monthDate = String.valueOf("01");
                int year = Integer.parseInt(yearDate);
            }
            // 10～12月の場合はそのまま10～12
            else {
                monthDate = String.valueOf(month);
            }
        }
        // 今日の日付が6日以下かつ、01日が存在する場合かつ、選択された日付が23日以上の場合かつ、今日の日付ボタンが押されていた場合
        // 月を-1する
        // 週に23日～31日のどれかと1日～6日のどれかが存在した場合で今日の日付が23日～31日であるときに1日～6日を押すと月が+1されるようにする
        else if (Integer.parseInt(dayDate) <= 6 && isTextViewContent01 && selectedDay >= 23 && isSelectedNowDateBtn == true) {
            int month = Integer.parseInt(monthDate);
            month -= 1;
            // 12月の場合は1月を-1すると0月になるため12月に直す
            if (month == 0) {
                monthDate = String.valueOf(12);
            }
            // 1月～9月の場合は01～09に直す
            else if (month <= 9) {
                monthDate = String.valueOf("0" + month);
            }
            // 10～12月の場合はそのまま10～12
            else {
                monthDate = String.valueOf(month);
            }
        }
        return monthDate;
    }

    // 日付から年を取得
    private String checkYear(int selectedDay) {
        // 選択された日付を "〇年〇月" の形式に変換
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String yearDate = yearFormat.format(calendar.getTime());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        String monthDate = monthFormat.format(calendar.getTime());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        String dayDate = dayFormat.format(calendar.getTime());
        // 01日が存在するかチェックするため日曜日から土曜日までの数字のテキストビューをチェック
        boolean isTextViewContent01 = ddSun.getText().toString().equals("01")
                || ddMon.getText().toString().equals("01")
                || ddTue.getText().toString().equals("01")
                || ddWed.getText().toString().equals("01")
                || ddThu.getText().toString().equals("01")
                || ddFri.getText().toString().equals("01")
                || ddSat.getText().toString().equals("01");

        // 01日が存在する場合かつ、選択された日付が23日以上の場合かつ、今日の日付ボタンが押されなかった場合
        // 月を-1する
        if (isTextViewContent01 && selectedDay >= 23 && isSelectedNowDateBtn == false) {
            int month = Integer.parseInt(monthDate);
            month -= 1;
            // 12月の場合は1月を-1すると0月になるため12月に直す
            if (month == 0) {
                int year = Integer.parseInt(yearDate);
                yearDate = String.valueOf(year - 1);
            }
        }
        // 今日の日付が23日以上かつ、01日が存在する場合かつ、選択された日付が6日以下の場合かつ、今日の日付ボタンが押されていた場合
        // 月を+1する
        // 週に23日～31日のどれかと1日～6日のどれかが存在した場合で今日の日付が23日～31日であるときに1日～6日を押すと月が+1されるようにする
        else if (Integer.parseInt(dayDate) >= 23 && isTextViewContent01 && selectedDay <= 6 && isSelectedNowDateBtn == true) {
            int month = Integer.parseInt(monthDate);
            month += 1;
            // 13月、つまり12月と1月が存在する場合 1月をタップすると13月になるため1月に直す
            if (month == 13) {
                int year = Integer.parseInt(yearDate);
                yearDate = String.valueOf(year + 1);
            }
        }
        // 今日の日付が6日以下かつ、01日が存在する場合かつ、選択された日付が23日以上の場合かつ、今日の日付ボタンが押されていた場合
        // 月を-1する
        // 週に23日～31日のどれかと1日～6日のどれかが存在した場合で今日の日付が23日～31日であるときに1日～6日を押すと月が+1されるようにする
        else if (Integer.parseInt(dayDate) <= 6 && isTextViewContent01 && selectedDay >= 23 && isSelectedNowDateBtn == true) {
            int month = Integer.parseInt(monthDate);
            month -= 1;
            // 12月の場合は1月を-1すると0月になるため12月に直す
            if (month == 0) {
                int year = Integer.parseInt(yearDate);
                yearDate = String.valueOf(year - 1);
            }
        }
        return yearDate;
    }
}