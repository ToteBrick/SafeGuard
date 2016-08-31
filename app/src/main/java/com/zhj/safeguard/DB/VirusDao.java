package com.zhj.safeguard.DB;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @项目名:safe
 * @包名:com.example.safe.db
 * @类名:VirusDao
 * @创建者:zhj
 * @描述:
 * @版本号:$Rev$
 * @更新者:$Author$
 * @更新时间:$Date$
 * @更新内容:
 */
public class VirusDao
{

	public static boolean isVirus(Context context, String md5)
	{

		File file = new File(context.getFilesDir(), "antivirus.db");
		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);

		String sql = "select count(1) from datable where md5 = ?";
		Cursor cursor = db.rawQuery(sql, new String[] { md5 });

		int count = 0;
		if (cursor != null)
		{
			if (cursor.moveToNext())
			{
				count = cursor.getInt(0);
			}

			cursor.close();
		}
		db.close();

		return count > 0;
	}

	public static boolean add(Context context, String md5)
	{
		File file = new File(context.getFilesDir(), "antivirus.db");
		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
		ContentValues values = new ContentValues();
		values.put("md5", md5);
		values.put("type", 6);
		values.put("name", "xxskkd病毒");
		values.put("desc", "恶意扣费");

		long insert = db.insert("datable", null, values);

		db.close();
		return insert != -1;
	}
}
