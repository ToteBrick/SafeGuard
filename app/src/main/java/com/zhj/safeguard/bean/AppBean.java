package com.zhj.safeguard.bean;

import android.graphics.drawable.Drawable;

/**
 * @项目名:safe
 * @包名:com.example.safe.bean
 * @类名:AppBean
 * @创建者:zhj
 * @描述:app对应的bean
 * @版本号:$Rev$
 * @更新者:$Author$ 
 * @更新时间:$Date$
 * @更新内容:TODO:  
 */
public class AppBean
{
	public Drawable icon ;//应用图标
	public String name ; //应用名称
	public String packageName ; //包名
	public long size ; //应用大小
	public boolean isInstallSD ; //是否安装在SD卡
	public boolean isSystem ;  //是否是系统程序
}
