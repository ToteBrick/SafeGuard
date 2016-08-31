package com.zhj.safeguard.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPUtils {

	/**
	 * zip压缩流
	 * 
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	public static void zip(InputStream is, OutputStream os) throws IOException {

		// 写出zip文件
		GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(os);

			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = is.read(buffer)) != -1) {
				gos.write(buffer, 0, len);
				gos.flush();
			}
		} finally {
			closeIO(is);
			closeIO(gos);
		}
	}

	/**
	 * 解压gzip
	 * 
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	public static void unzip(InputStream is, OutputStream os)
			throws IOException {
		// 将zip还原
		GZIPInputStream gis = null;
		try {
			gis = new GZIPInputStream(is);

			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = gis.read(buffer)) != -1) {
				os.write(buffer, 0, len);
				os.flush();
			}
		} finally {
			closeIO(gis);
			closeIO(os);
		}
	}

	private static void closeIO(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			io = null;
		}
	}
}
