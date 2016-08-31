package com.zhj.safeguard.DB;

public interface BlackListDB {

	// 数据名
	String DB_NAME = "black.db";
	int VERSION = 1;

	// 黑名单的表
	public interface BlackTable {
		String TABLE_NAME = "black";

		String COLUMN_ID = "_id";
		String COLUMN_NUMBER = "number";// 号码列
		String COLUMN_TYPE = "type";// 电话0，短信1，全部2

		String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "(" + COLUMN_ID
				+ " integer primary key autoincrement," + COLUMN_NUMBER
				+ " text unique," + COLUMN_TYPE + " integer" + ")";
	}
}
