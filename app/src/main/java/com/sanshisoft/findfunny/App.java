package com.sanshisoft.findfunny;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sanshisoft.findfunny.util.Utils;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class App extends Application {

	private static Context sContext;
	private static int totalNumber;

	@Override
	public void onCreate() {
		super.onCreate();
		sContext = getApplicationContext();
		initImageLoader(getApplicationContext());
		// 初始化图片文件夹
		createImageFolder();
	}

	public static Context getContext() {
		return sContext;
	}

	// 初始化ImageLoader
	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.discCacheSize(10 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}

	private void createImageFolder() {
		if (Utils.hasSdcard()) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			String sdPath = sdcardDir.getPath() + AppConfig.IMAGE_CACHE_PATH;
			File folderPath = new File(sdPath);
			if (!folderPath.exists()) {
				folderPath.mkdirs();
				Log.d(AppConfig.TAG,"create Folder suc--"+ folderPath.getAbsolutePath());
			}
		}else {
			Log.d(AppConfig.TAG,"create Folder failed!");
			return;
		}
	}

	public static void setTotalNumber(int total) {
		totalNumber = total;
	}

	public static int getTotalNumber() {
		return totalNumber;
	}
}
