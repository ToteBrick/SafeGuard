package com.zhj.safeguard.provider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.zhj.safeguard.bean.AppBean;
import com.zhj.safeguard.utils.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AppProvider {

	private static final String TAG = "AppProvider";

	public static List<AppBean> getAllApps(Context context) {
		List<AppBean> list = new ArrayList<AppBean>();

		// 获得包管理器
		PackageManager pm = context.getPackageManager();
		// 获得所有已经安装的应用
		List<PackageInfo> packages = pm.getInstalledPackages(0);

		for (PackageInfo pack : packages) {
			// PackageInfo:--->清单文件

			AppBean bean = new AppBean();
			bean.packageName = pack.packageName;

			ApplicationInfo applicationInfo = pack.applicationInfo;
			bean.name = applicationInfo.loadLabel(pm).toString();
			bean.icon = applicationInfo.loadIcon(pm);

			// data/app/xxxx.apk大小--》三方
			// system/app/xxxx.apk --》系统程序
			String sourceDir = applicationInfo.sourceDir;
			// String dataDir = applicationInfo.dataDir;//data/data/包名的文件夹
			File file = new File(sourceDir);
			bean.size = file.length();

			int flags = applicationInfo.flags;
			// 判断是否是是系统程序
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				// 是系统程序
				bean.isSystem = true;
			} else {
				bean.isSystem = false;
			}
			// bean.isSystem = ((flags & ApplicationInfo.FLAG_SYSTEM) ==
			// ApplicationInfo.FLAG_SYSTEM);

			// 判断是否具有sd能力
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				bean.isInstallSD = true;
			} else {
				bean.isInstallSD = false;
			}

			// bean.isInstallSD;
			// bean.isSystem;

			Logger.d(TAG, "name : " + bean.name);
			Logger.d(TAG, "system : " + bean.isSystem);
			Logger.d(TAG, "------------------------------------");

			list.add(bean);
		}

		return list;
	}

	public static List<AppBean> getAllLauncherApps(Context context) {
		List<AppBean> list = new ArrayList<AppBean>();

		// 获得包管理器
		PackageManager pm = context.getPackageManager();
		// 获得所有已经安装的应用
		List<PackageInfo> packages = pm.getInstalledPackages(0);

		for (PackageInfo pack : packages) {
			// PackageInfo:--->清单文件

			// 判断是否有启动的图标
			Intent intent = pm.getLaunchIntentForPackage(pack.packageName);
			if (intent == null) {
				continue;
			}

			AppBean bean = new AppBean();
			bean.packageName = pack.packageName;

			ApplicationInfo applicationInfo = pack.applicationInfo;
			bean.name = applicationInfo.loadLabel(pm).toString();
			bean.icon = applicationInfo.loadIcon(pm);

			// data/app/xxxx.apk大小--》三方
			// system/app/xxxx.apk --》系统程序
			String sourceDir = applicationInfo.sourceDir;
			// String dataDir = applicationInfo.dataDir;//data/data/包名的文件夹
			File file = new File(sourceDir);
			bean.size = file.length();

			int flags = applicationInfo.flags;
			// 判断是否是是系统程序
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				// 是系统程序
				bean.isSystem = true;
			} else {
				bean.isSystem = false;
			}
			// bean.isSystem = ((flags & ApplicationInfo.FLAG_SYSTEM) ==
			// ApplicationInfo.FLAG_SYSTEM);

			// 判断是否具有sd能力
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				bean.isInstallSD = true;
			} else {
				bean.isInstallSD = false;
			}

			// bean.isInstallSD;
			// bean.isSystem;

			Logger.d(TAG, "name : " + bean.name);
			Logger.d(TAG, "system : " + bean.isSystem);
			Logger.d(TAG, "------------------------------------");

			list.add(bean);
		}

		return list;
	}
}
