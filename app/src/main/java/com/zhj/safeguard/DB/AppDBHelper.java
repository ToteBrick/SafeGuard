package com.zhj.safeguard.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @项目名:safe
 * @包名:com.example.safe.db
 * @类名:AppDBHelper
 * @创建者:zhj
 * @描述:TODO:
 * @版本号:$Rev$
 * @更新者:$Author$ 
 * @更新时间:$Date$
 * @更新内容:TODO:  
 */
public class AppDBHelper extends SQLiteOpenHelper
{

	public AppDBHelper(Context context) {
		super(context, AppLockDB.DB_NAME, null, AppLockDB.VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(AppLockDB.Applock.CREATE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
	}

}
