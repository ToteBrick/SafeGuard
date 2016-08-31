package com.zhj.safeguard.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhj.safeguard.bean.BlackBean;

import java.util.ArrayList;
import java.util.List;


public class BlackDao {
	private BlackDBHelper mHelper;

	public BlackDao(Context context) {
		mHelper = new BlackDBHelper(context);
	}

	/**
	 * 添加黑名单
	 * 
	 * @param number
	 * @param type
	 */
	public boolean add(String number, int type) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(BlackListDB.BlackTable.COLUMN_NUMBER, number);
		values.put(BlackListDB.BlackTable.COLUMN_TYPE, type);
		long insert = db.insert(BlackListDB.BlackTable.TABLE_NAME, null, values);
		db.close();
		return insert != -1;
	}

	public boolean delete(String number) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		String whereClause = BlackListDB.BlackTable.COLUMN_NUMBER + "=?";
		String[] whereArgs = new String[] { number };
		int delete = db.delete(BlackListDB.BlackTable.TABLE_NAME, whereClause,
				whereArgs);
		db.close();
		return delete > 0;
	}

	public boolean update(String number, int type) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(BlackListDB.BlackTable.COLUMN_NUMBER, number);
		values.put(BlackListDB.BlackTable.COLUMN_TYPE, type);

		String whereClause = BlackListDB.BlackTable.COLUMN_NUMBER + "= ?";
		String[] whereArgs = new String[] { number };
		int update = db.update(BlackListDB.BlackTable.TABLE_NAME, values,
				whereClause, whereArgs);
		db.close();

		return update > 0;
	}

	/**
	 * 查询数据
	 * 
	 * @return
	 */
	public List<BlackBean> findAll() {
		List<BlackBean> list = new ArrayList<BlackBean>();

		SQLiteDatabase db = mHelper.getReadableDatabase();

		String sql = "select " + BlackListDB.BlackTable.COLUMN_NUMBER + ","
				+ BlackListDB.BlackTable.COLUMN_TYPE + " from "
				+ BlackListDB.BlackTable.TABLE_NAME;
		String[] args = null;
		Cursor cursor = db.rawQuery(sql, args);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String number = cursor.getString(0);
				int type = cursor.getInt(1);

				BlackBean bean = new BlackBean();
				bean.number = number;
				bean.type = type;
				list.add(bean);
			}

			cursor.close();
		}

		db.close();

		return list;
	}

	/**
	 * 分页查询
	 * 
	 * @param pageSize
	 *            ：每页显示的数据数量
	 * @param index
	 *            :从什么位置开始查询
	 * @return
	 */
	public List<BlackBean> findPart(int pageSize, int index) {
		List<BlackBean> list = new ArrayList<BlackBean>();

		SQLiteDatabase db = mHelper.getReadableDatabase();

		String sql = "select " + BlackListDB.BlackTable.COLUMN_NUMBER + ","
				+ BlackListDB.BlackTable.COLUMN_TYPE + " from "
				+ BlackListDB.BlackTable.TABLE_NAME + " limit " + pageSize
				+ " offset " + index;
		String[] args = null;
		Cursor cursor = db.rawQuery(sql, args);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				
				String number = cursor.getString(0);
				int type = cursor.getInt(1);

				BlackBean bean = new BlackBean();
				bean.number = number;
				bean.type = type;
				list.add(bean);
			}

			cursor.close();
		}

		db.close();

		return list;
	}

	/**
	 * 根据号码查询拦截类型
	 * 
	 * @param address
	 * @return 如果没有此号码，返回-1
	 */
	public int findType(String address) {
		SQLiteDatabase db = mHelper.getReadableDatabase();

		String sql = "select " + BlackListDB.BlackTable.COLUMN_TYPE + " from "
				+ BlackListDB.BlackTable.TABLE_NAME + " where "
				+ BlackListDB.BlackTable.COLUMN_NUMBER + "=?";

		Cursor cursor = db.rawQuery(sql, new String[] { address });

		int type = -1;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				type = cursor.getInt(0);
			}
			cursor.close();
		}

		db.close();

		return type;
	}
}
