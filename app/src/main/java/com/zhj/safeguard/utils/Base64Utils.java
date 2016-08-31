package com.zhj.safeguard.utils;

import android.util.Base64;

public class Base64Utils
{

	public static String encode(String pwd)
	{
		return Base64.encodeToString(pwd.getBytes(), Base64.DEFAULT);
	}

	public static String decode(String str)
	{
		byte[] decode = Base64.decode(str, Base64.DEFAULT);
		return new String(decode);
	}
}
