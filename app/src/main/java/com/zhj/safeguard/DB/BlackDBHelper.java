package com.zhj.safeguard.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BlackDBHelper extends SQLiteOpenHelper {

	private static final String TAG = "BlackDBHelper";

	public BlackDBHelper(Context context) {
		super(context, BlackListDB.DB_NAME, null, BlackListDB.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// 建表
		String sql = BlackListDB.BlackTable.CREATE_TABLE_SQL;
		Log.d(TAG, "sql : " + sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
