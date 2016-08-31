package com.zhj.safeguard.DB;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @项目名:safe
 * @包名:com.example.safe.db
 * @类名:CommonNumberDao
 * @创建者:zhj
 * @描述:TODO:
 * @版本号:$Rev$
 * @更新者:$Author$
 * @更新时间:$Date$
 * @更新内容:TODO:
 */
public class CommonNumberDao
{
	public static int getGroupCount(Context context)
	{

		File file = new File(context.getFilesDir(), "commonnum.db");
		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		String sql = "select count(1) from classlist";
		Cursor cursor = db.rawQuery(sql, null);
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
		return count;
	}

	public static int getChildCount(Context context, int groupPosition)
	{

		File file = new File(context.getFilesDir(), "commonnum.db");
		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		String sql = "select count(1) from table" + (groupPosition + 1);
		Cursor cursor = db.rawQuery(sql, null);
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
		return count;
	}

	public static String getGroupTitle(Context context, int groupPosition)
	{

		File file = new File(context.getFilesDir(), "commonnum.db");
		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		String sql = "select name from classlist where idx=" + (groupPosition + 1);
		Cursor cursor = db.rawQuery(sql, null);
		String title = "";
		if (cursor != null)
		{
			if (cursor.moveToNext())
			{
				title = cursor.getString(0);
			}

			cursor.close();
		}
		db.close();
		return title;
	}

	public static String ChildContent(Context context, int groupPosition, int childPosition)
	{

		File file = new File(context.getFilesDir(), "commonnum.db");
		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		String sql = "select name , number from table" + (groupPosition + 1) + " where _id = " + (childPosition + 1);
		Cursor cursor = db.rawQuery(sql, null);
		String content = "";
		if (cursor != null)
		{
			if (cursor.moveToNext())
			{
				String name = cursor.getString(0);
				String number = cursor.getString(1);
				content = name + "\n" + number;
			}

			cursor.close();
		}
		db.close();
		return content;
	}

}
