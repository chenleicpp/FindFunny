package com.sanshisoft.findfunny.ui.fragment;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sanshisoft.findfunny.R;
import com.sanshisoft.findfunny.ui.MainActivity;
import com.sanshisoft.findfunny.util.Utils;
import com.umeng.update.UmengUpdateAgent;

public class SettingsFragment extends Fragment{
	
	public static final int CALC_CACHE_FINISH = 1;
	
	public static final int DEL_CACHE_FINISH = 2;
	
	private MainActivity mActivity;
	
	@ViewInject(R.id.item_clear_cache)
	private LinearLayout mBtnClearCache;
	
	@ViewInject(R.id.cache_size)
	private TextView mTvCacheSize;
	
	@ViewInject(R.id.item_checkupdate)
	private TextView mBtnChkUpdate;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CALC_CACHE_FINISH:
				if (msg.arg1 != 0) {
					mTvCacheSize.setText(msg.arg1+"M");
				}
				break;
			case DEL_CACHE_FINISH:
				mTvCacheSize.setText("");
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (MainActivity)getActivity();
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ViewUtils.inject(this, rootView);
        int i = getArguments().getInt(MainActivity.ARG_DRAWER_NUMBER);
        String title = getResources().getStringArray(R.array.drawer_array)[i];
        mActivity.setTitle(title);
        
        mBtnClearCache.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				new AlertDialog.Builder(getActivity()).setTitle("警告")
				.setMessage("是否删除图片缓存文件夹？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								try {
									Utils.deleteFolderFile(Utils.getCachePath(), false);
									mHandler.sendEmptyMessage(DEL_CACHE_FINISH);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}).start();
					}
				})
				.setNegativeButton("返回", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();
			}
		});
        
        new Thread(calcCacheSize).start();
        
        mBtnChkUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UmengUpdateAgent.update(getActivity());
			}
		});
        
        return rootView;
	}
	
	private Runnable calcCacheSize = new Runnable() {
		
		@Override
		public void run() {
			Message msg = null;
			String path = Utils.getCachePath();
			if (path != null) {
				File file = new File(path);
				try {
					long size = Utils.getFolderSize(file);
					msg = mHandler.obtainMessage();
					msg.what = CALC_CACHE_FINISH;
					msg.arg1 = (int)size;
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
}
