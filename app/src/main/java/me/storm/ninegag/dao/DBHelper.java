package me.storm.ninegag.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by storm on 14-4-8.
 */
public class DBHelper extends SQLiteOpenHelper {
    // 数据库名
    private static final String DB_NAME = "9gag.db";

    // 数据库版本
    private static final int VERSION = 1;

    //创建数据库
    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    //创建表格,这边使用的是自定义了一个工具类
    @Override
    public void onCreate(SQLiteDatabase db) {
//        FeedsDataHelper.FeedsDBInfo.TABLE.create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
