package com.zhj.safeguard.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import com.zhj.safeguard.bean.ContactBean;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ContactUtils {

	/**
	 *
	 * @param context
	 * @return 获得所有联系人。
     */
	public static List<ContactBean> getAllContacts(Context context) {

		List<ContactBean> list = new ArrayList<ContactBean>();

		ContentResolver resolver = context.getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.NUMBER,// 电话号码
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,// 用户名
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID, // 联系人id
		};
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		Cursor cursor = resolver.query(uri, projection, selection,
				selectionArgs, sortOrder);

		if (cursor != null) {
			while (cursor.moveToNext()) {
				String number = cursor.getString(0); // 电话号码
				String name = cursor.getString(1);
				long id = cursor.getLong(2);
				ContactBean object = new ContactBean();
				object.name = name;
				object.number = number;
				object.contactId = id;
				list.add(object);
			}
			cursor.close();
		}
		return list;
	}

	/**
	 * 获得联系人的图片
	 */
	public static Bitmap getContactIcon(Context context, long contactId) {
		ContentResolver resolver = context.getContentResolver();

		Uri contactUri = Uri.withAppendedPath(
				ContactsContract.Contacts.CONTENT_URI, contactId + "");

		InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(
				resolver, contactUri);
		Bitmap bitmap = BitmapFactory.decodeStream(is);

		return bitmap;
	}
   /**
    * 获得游标集合
    */
	public static Cursor getAllCursor(Context context) {

		List<ContactBean> list = new ArrayList<ContactBean>();

		ContentResolver resolver = context.getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.NUMBER,// 电话号码
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,// 用户名
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID, // 联系人id
		};
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		return resolver.query(uri, projection, selection, selectionArgs,
				sortOrder);

	}

	/**
	 *
	 * @param cursor
	 * @return 通过cursor获得联系人。
     */
	public static ContactBean getContactBean (Cursor cursor){
		
		ContactBean bean = new ContactBean();
		
			bean.number = cursor.getString(1);
			bean.name = cursor.getString(2);
			bean.contactId = cursor.getLong(3);
		return bean;
	}
}
