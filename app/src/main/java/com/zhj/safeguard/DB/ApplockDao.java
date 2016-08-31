package com.zhj.safeguard.DB;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * @项目名:safe
 * @包名:com.example.safe.db
 * @类名:ApplockDao
 * @创建者:zhj
 * @描述:TODO:
 * @版本号:$Rev$
 * @更新者:$Author$
 * @更新时间:$Date$
 * @更新内容:TODO:
 */
public class ApplockDao
{
	private AppDBHelper	mHelper;
	private Context mContext;
	public ApplockDao(Context context) {
		
		this.mContext = context;

		mHelper = new AppDBHelper(context);
	}

	/*
	 * 添加包名
	 */
	public boolean add(String packageName)
	{

		SQLiteDatabase db = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(AppLockDB.Applock.COLUMN_PACKAGENAME, packageName);
		long insert = db.insert(AppLockDB.Applock.TABLE_NAME, null, values);
		db.close();
		ContentResolver cr = mContext.getContentResolver();
		cr.notifyChange(Uri.parse("content://applock"), null);
		return insert != -1;
	}

	public boolean delete(String packageName)
	{
		SQLiteDatabase db = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(AppLockDB.Applock.COLUMN_PACKAGENAME, packageName);
		String whereClause = AppLockDB.Applock.COLUMN_PACKAGENAME + "=?";
		long delete = db.delete(AppLockDB.Applock.TABLE_NAME, whereClause, new String[] { packageName });
		db.close();
		ContentResolver cr = mContext.getContentResolver();
		cr.notifyChange(Uri.parse("content://applock"), null);
		return delete > 0;

	}

	/*
	 * 查询包是否上锁。
	 */
	public boolean findLock(String packageName)
	{

		SQLiteDatabase db = mHelper.getReadableDatabase();

		String sql = "select count(1) from " + AppLockDB.Applock.TABLE_NAME + " where " + AppLockDB.Applock.COLUMN_PACKAGENAME + "=?";

		Cursor cursor = db.rawQuery(sql, new String[] { packageName });

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
	
	/*
	 * 查询所有上锁的包名
	 */
	public List<String> findAll(){
		
		SQLiteDatabase db = mHelper.getReadableDatabase();

		String sql = "select "+AppLockDB.Applock.COLUMN_PACKAGENAME+" from " + AppLockDB.Applock.TABLE_NAME ;
		Cursor cursor = db.rawQuery(sql, null);
		
		List<String> list = new ArrayList<String>();
		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				list.add(cursor.getString(0));
			}
			cursor.close();
		}
		db.close();

		return list;
		
	}
	
	
}
