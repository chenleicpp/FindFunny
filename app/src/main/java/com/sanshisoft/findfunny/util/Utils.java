package com.sanshisoft.findfunny.util;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.sanshisoft.findfunny.App;
import com.sanshisoft.findfunny.AppConfig;

public class Utils {

	public static String getInterfaceUrl(int page, int size) {
		int start_index = (page - 1) * size + 1;
		return "http://image.baidu.com/channel/listjson?pn=" + start_index
				+ "&rn=" + size + "&tag1=搞笑&tag2=找亮点&ie=utf8";
	}

	public static String getRandomUrl(int size) {
		int totalNums = App.getTotalNumber();
		int totalPages = 0;
		if (totalNums != 0) {
			if (totalNums % size == 0) {
				totalPages = totalNums / size;
			} else {
				totalPages = totalNums / size + 1;
			}
		}
		int randomPage = (int) Math.floor(Math.random() * totalPages + 1);
		int start_index = (randomPage - 1) * size + 1;
		return "http://image.baidu.com/channel/listjson?pn=" + start_index
				+ "&rn=" + size + "&tag1=搞笑&tag2=找亮点&ie=utf8";
	}

	public static boolean isConn(Context context) {
		boolean bisConnFlag = false;
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = conManager.getActiveNetworkInfo();
		if (network != null) {
			bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
		}
		return bisConnFlag;
	}

	public static boolean hasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static String getUrlImgName(String url) {
		String name = null;
		int index = url.lastIndexOf("/");
		int end = url.lastIndexOf(".");
		name = url.substring(index + 1, end);
		return name;
	}

	// 获取文件大小,单位为M
	public static long getFolderSize(File file) throws Exception {
		long size = 0;
		java.io.File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isDirectory()) {
				size = size + getFolderSize(fileList[i]);
			} else {
				size = size + fileList[i].length();
			}
		}
		return size / 1048576;
	}

	public static void deleteFolderFile(String filePath, boolean deleteThisPath)  
            throws IOException {  
        if (!TextUtils.isEmpty(filePath)) {  
            File file = new File(filePath);  
  
            if (file.isDirectory()) {
                File files[] = file.listFiles();  
                for (int i = 0; i < files.length; i++) {  
                    deleteFolderFile(files[i].getAbsolutePath(), true);  
                }
            }  
            if (deleteThisPath) {  
                if (!file.isDirectory()) {
                    file.delete();  
                } else {
                    if (file.listFiles().length == 0) {
                        file.delete();  
                    }  
                }  
            }  
        }  
    }
	
	public static String getCachePath(){
		String sdCachePath = null;
		if (Utils.hasSdcard()) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			sdCachePath = sdcardDir.getPath() + AppConfig.IMAGE_CACHE_PATH;
		}
		return sdCachePath;
	}
}
