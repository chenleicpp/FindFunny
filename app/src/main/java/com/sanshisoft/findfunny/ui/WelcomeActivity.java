package com.sanshisoft.findfunny.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.sanshisoft.findfunny.App;
import com.sanshisoft.findfunny.AppConfig;
import com.sanshisoft.findfunny.R;
import com.sanshisoft.findfunny.util.MyToast;
import com.sanshisoft.findfunny.util.ParseJsonUtils;
import com.sanshisoft.findfunny.util.Utils;
import com.umeng.analytics.MobclickAgent;

public class WelcomeActivity extends Activity{
	
	private HttpUtils mHttpUtils;
	
	private long beginTime;
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mHttpUtils = new HttpUtils();
        mHttpUtils.configCurrentHttpCacheExpiry(1000*10);
        
        beginTime = System.currentTimeMillis();
        
		if (!Utils.isConn(this)) {
			setNetworkMethod(this);
		}else {
			//请求总页数
			mHttpUtils.send(HttpRequest.HttpMethod.GET, AppConfig.URL, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException e, String res) {
					// TODO Auto-generated method stub
                    MyToast.showToast(WelcomeActivity.this,"网络连接出问题了，请恢复网络后重试!");
				}

				@Override
				public void onSuccess(ResponseInfo<String> infos) {
					// TODO Auto-generated method stub
					long intervalTime = System.currentTimeMillis() - beginTime;
					int totalNum = ParseJsonUtils.parseTotalnumJson(infos.result);
					App.setTotalNumber(totalNum);
					if (intervalTime < 2 * 1000) {
						try {
							Thread.sleep(2000 - intervalTime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					Jump2Main();
				}
			});
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
	}
	
	public void setNetworkMethod(final Context context){
        AlertDialog.Builder builder=new Builder(context);
        builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            	Intent intent = null;
				if (android.os.Build.VERSION.SDK_INT > 10) {
					intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
				} else {
					intent = new Intent();
					ComponentName component = new ComponentName(
							"com.android.settings",
							"com.android.settings.WirelessSettings");
					intent.setComponent(component);
					intent.setAction("android.intent.action.VIEW");
				}
				startActivity(intent);
				dialog.dismiss();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                finish();
            }
        }).show();
    }
	
	private void Jump2Main(){
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	MobclickAgent.onPause(this);
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	MobclickAgent.onResume(this);
    }
}
