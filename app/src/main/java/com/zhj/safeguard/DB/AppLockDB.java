package com.zhj.safeguard.DB;
/**
 * @项目名:safe
 * @包名:com.example.safe.db
 * @类名:AppLockDB
 * @创建者:zhj
 * @描述:TODO:
 * @版本号:$Rev$
 * @更新者:$Author$ 
 * @更新时间:$Date$
 * @更新内容:TODO:  
 */
public interface AppLockDB
{

	String DB_NAME = "applock.db";
	int VERSION = 1;
	
	public interface Applock{
		
		String TABLE_NAME = "applock";
		String COLUMN_ID = "_id";
		String COLUMN_PACKAGENAME = "packageName";
		String CREATE_TABLE_SQL = "create table "+TABLE_NAME+"("
				+COLUMN_ID+" integer primary key autoincrement,"
				+COLUMN_PACKAGENAME +" text unique"+
				")";
	}
}
