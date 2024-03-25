/*データベース*/
/* 説明
 * 記録画面用データベース */

package com.example.eyeprotection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // データベースのバージョン
    private static final int DATABASE_VERSION = 1;

    // データベースの情報
    private static final String DATABASE_NAME = "dateBase.db";  // データベースの名前
    public static final String TABLE_NAME = "dateBaseTable";   // テーブル名
    public static final String COLUMN_ID = "_id";              // カラムid 主キー
    public static final String COLUMN_NAME1 = "_name1";         // カラム名1
    public static final String COLUMN_NAME2 = "_name2";         // カラム名2
    public static final String COLUMN_NAME3 = "_name3";         // カラム名3

    // データベースのテーブル作成 SQL
    private static final String createTableDB = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME1 + " INTEGER, " +
            COLUMN_NAME2 + " INTEGER," +
            COLUMN_NAME3 + " TEXT)";

    // データベースを起動
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // データベース実行
        db.execSQL(createTableDB);
    }

    // データベースのアップデート確認
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}