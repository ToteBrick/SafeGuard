package com.zhj.safeguard.utils;

/*
 * 加密的工具类
 */
public class EncryptUtils
{

	// -127-->128
	public static String encode(String pwd, int key)
	{

		// byte[] bytes = pwd.getBytes();
		// byte[] buffer = new byte[bytes.length];
		//
		// for (int i = 0; i < bytes.length; i++) {
		// buffer[i] = (byte) (bytes[i] ^ key);
		// }
		// return new String(buffer);

		key = key % 128;

		byte[] bytes = pwd.getBytes();
		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] ^= key;
		}

		return new String(bytes);
	}

	public static String decode(String pwd, int key)
	{
		// byte[] bytes = pwd.getBytes();
		// byte[] buffer = new byte[bytes.length];
		//
		// for (int i = 0; i < buffer.length; i++) {
		// buffer[i] = (byte) (bytes[i] ^ key);
		// }
		// return new String(buffer);

		key = key % 128;

		byte[] bytes = pwd.getBytes();
		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] ^= key;
		}

		return new String(bytes);
	}

}
