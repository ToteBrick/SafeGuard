package com.zhj.safeguard.utils;


import java.io.IOException;
import java.io.InputStream;

public class StreamTool {

	/**
	 *输入流转字符串工具类。
	 */
	public static String readStrem(InputStream is) {
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		StringBuilder stringBuilder = new StringBuilder();
		String str = null;
		try {
			byte [] buffer = new byte[1024];
			int len =0 ;
			while(  (len = is.read(buffer) )  != -1){
				
				stringBuilder.append(new String(buffer, 0, len , "gbk"));
//				bos.write(buffer, 0, len);
			}
			is.close();
			str =  stringBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

}
