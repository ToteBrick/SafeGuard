package com.zhj.safeguard.DB;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class AddressDao {

	public static String findAddress(Context context, String number) {

		// 读取数据库
		// *
		// * Open the database according to the flags {@link #OPEN_READWRITE}
		// * {@link #OPEN_READONLY} {@link #CREATE_IF_NECESSARY} and/or {@link
		// #NO_LOCALIZED_COLLATORS}.
		String path = new File(context.getFilesDir(), "address.db")
				.getAbsolutePath();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		// 获得号码前缀

		String address = "";

		// 手机号码判断 1[34578]
		// 正则表达式^ 1[35578]\d{9}$

		boolean isPhone = number.matches("^ 1[35578]\\d{9}$");
		if (isPhone) {

			String prefix = number.substring(0, 7);
			String sql = "select cardtype from info where mobileprefix = ?";
			Cursor cursor = db.rawQuery(sql, new String[] { prefix });
			if (cursor != null) {
				if (cursor.moveToNext()) {
					address = cursor.getString(0);
				}
				cursor.close();
			}
		} else {
			//其他号码
			switch (number.length()) {
			case 3:
				address = "报警电话";
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "服务号码";
				break;
			case 7:
			case 8:
				address = "本地座机";
				break;
			case 10:
			case 11:
			case 12:
				String sql = "select city from info where area = ?";
				String prefix = number.substring(0,3);
				Cursor cursor = db.rawQuery(sql, new String[]{prefix});
				if (cursor != null) {
					if (cursor.moveToNext()) {
						address = cursor.getString(0);
					}
					cursor.close();
				}
				if (TextUtils.isEmpty(address)) {
					 prefix = number.substring(0,4);
					 cursor = db.rawQuery(sql, new String[]{prefix});
					if (cursor != null) {
						if (cursor.moveToNext()) {
							address = cursor.getString(0);
						}
						cursor.close();
					}
				}
				if (TextUtils.isEmpty(address)){
					
					address = "未知号码";
				}
				break;
			default:
				if (number.length()>16) {
					address = "警惕号码";
				} else{
					address = "未知号码";
				}
				break;
			}
		}
		db.close();

		return address;
	}

}
