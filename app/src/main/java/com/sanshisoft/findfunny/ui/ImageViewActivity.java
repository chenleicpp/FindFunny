package com.sanshisoft.findfunny.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.sanshisoft.findfunny.AppConfig;
import com.sanshisoft.findfunny.R;
import com.sanshisoft.findfunny.util.MyToast;
import com.sanshisoft.findfunny.util.Utils;
import com.sanshisoft.findfunny.view.ProgressWheel;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.CustomPlatform;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.OnSnsPlatformClickListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewActivity extends BaseActivity {
	
	public static final String IMAGE_URL = "image_url";
	
	public static final String IMAGE_ABS = "image_abs";
	
	private static final int MSG_SUCCESS = 1001;
	
	private static final int MSG_FAILED = 1002;
	
	@ViewInject(R.id.photoView)
    PhotoView photoView;

	@ViewInject(R.id.progressWheel)
    ProgressWheel progressWheel;

    private PhotoViewAttacher mAttacher;
    
    private String imagePath;
    
    private String imageUrl,abs;
    
    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    
    
    private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case MSG_FAILED:
				MyToast.showToast(ImageViewActivity.this, "分享失败！");
				break;
			case MSG_SUCCESS:
				showShare();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
    	
    };
    
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageview);
		ViewUtils.inject(this);
		
		mAttacher = new PhotoViewAttacher(photoView);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });

        imageUrl = getIntent().getStringExtra(IMAGE_URL);
        abs = getIntent().getStringExtra(IMAGE_ABS);
        
        setTitle(abs);
        
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisc(true)
                .considerExifParams(true).build();
        ImageLoader.getInstance().displayImage(imageUrl, photoView, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressWheel.setVisibility(View.GONE);
                mAttacher.update();
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                progressWheel.setProgress(360 * current / total);
            }
        });
        //初始化分享平台
        configPlatforms();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.image, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_share:
			new Thread(saveImgToSd).start();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	Runnable saveImgToSd = new Runnable() {
		Message msg = Message.obtain();
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imageUrl);
			if (Utils.hasSdcard()) {
				File sdcardDir = Environment.getExternalStorageDirectory();
				imagePath = sdcardDir.getPath() + AppConfig.IMAGE_CACHE_PATH + File.separator + Utils.getUrlImgName(imageUrl)+".jpg";
				File file = new File(imagePath);
				try {
					file.createNewFile();
				} catch (IOException e) {
					Log.d(AppConfig.TAG,"error：在保存图片时出错！");
					e.printStackTrace();
				}
				FileOutputStream fOut = null;
				try {
					fOut = new FileOutputStream(file);
				} catch (FileNotFoundException e) {
					msg = mHandler.obtainMessage();
					msg.arg1 = MSG_FAILED;
					mHandler.sendMessage(msg);
					Log.d(AppConfig.TAG,"error：文件为找到！");
					e.printStackTrace();
				}
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
				try {
					fOut.flush();
				} catch (IOException e) {
					msg = mHandler.obtainMessage();
					msg.arg1 = MSG_FAILED;
					mHandler.sendMessage(msg);
					Log.d(AppConfig.TAG,"error：IO异常！");
					e.printStackTrace();
				}
				try {
					fOut.close();
					msg = mHandler.obtainMessage();
					msg.arg1 = MSG_SUCCESS;
					mHandler.sendMessage(msg);
				} catch (IOException e) {
					msg = mHandler.obtainMessage();
					msg.arg1 = MSG_FAILED;
					mHandler.sendMessage(msg);
					Log.d(AppConfig.TAG,"error：IO异常！");
					e.printStackTrace();
				}
			 }
		}
	};
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mAttacher != null) {
            mAttacher.cleanup();
        }
	}
	
	public void showShare(){
		//取消腾讯微博的显示
		mController.getConfig().removePlatform( SHARE_MEDIA.RENREN, SHARE_MEDIA.TENCENT);
		//取消微信显示，全部采用自定义
		mController.getConfig().removePlatform( SHARE_MEDIA.RENREN, SHARE_MEDIA.WEIXIN_CIRCLE);
		mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN.toString(),"朋友圈",SHARE_MEDIA.QZONE.toString(),SHARE_MEDIA.SINA.toString(),SHARE_MEDIA.QQ.toString());
		//设置分享内容
        setShareContent();
		mController.openShare(ImageViewActivity.this, false);
	}
	
	public void configPlatforms(){
		// 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
		addSupportWeixin();
		addSupportQQQZone();
		addSupportCustomWeixinCircle();
	}
	
	public void setShareContent(){
		//微信好友
		WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent("【"+abs+"】  (分享自找亮点)");
        weixinContent.setTitle("找亮点-搞笑趣图");
        weixinContent.setShareImage(new UMImage(this, 
              BitmapFactory.decodeFile(imagePath)));
        weixinContent.setTargetUrl(imageUrl);
        mController.setShareMedia(weixinContent);
        
        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        //朋友圈只显示标题不能显示内容
        circleMedia.setShareContent("【"+abs+"】  (分享自找亮点)下载地址：http://fir.im/8kf3");
        circleMedia.setTitle(abs);
        circleMedia.setShareImage(new UMImage(this, 
                BitmapFactory.decodeFile(imagePath)));
        circleMedia.setTargetUrl(imageUrl);
        mController.setShareMedia(circleMedia);
        
        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent("【"+abs+"】  (分享自找亮点)下载地址：http://fir.im/8kf3");
        qzone.setTargetUrl(imageUrl);
        qzone.setTitle("找亮点-搞笑趣图");
        qzone.setShareImage(new UMImage(this, 
                BitmapFactory.decodeFile(imagePath)));
        mController.setShareMedia(qzone);
        // qq消息
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent("【"+abs+"】  (分享自找亮点)");
        qqShareContent.setTitle("找亮点-搞笑趣图");
        qqShareContent.setShareImage(new UMImage(this, 
                BitmapFactory.decodeFile(imagePath)));
        qqShareContent.setTargetUrl(imageUrl);
        mController.setShareMedia(qqShareContent);
        //新浪微博
        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setShareImage(new UMImage(this, 
                BitmapFactory.decodeFile(imagePath)));
        sinaContent.setShareContent("【"+abs+"】  (分享自找亮点)下载地址：http://fir.im/8kf3");
        mController.setShareMedia(sinaContent);
	}
	
	private void addSupportWeixin(){
		String appID = "wx607f36b7a0c54ff2";
		String appSecret = "aab7fa4f8d71514e79a4c2cffd3b5990";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(this,appID,appSecret);
		wxHandler.addToSocialSDK();
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this,appID,appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}
	
	private void addSupportQQQZone(){
		String appId = "1103526767";
        String appKey = "887x75XteuRlkOSE";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,
                appId, appKey);
        qqSsoHandler.setTargetUrl(imageUrl);
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
	}
	
	//添加自定义分享模块，为了解决微信和朋友圈没有监听回调提示，需先取消默认微信显示
	private void addSupportCustomWeixinCircle(){
		CustomPlatform cp = new CustomPlatform("朋友圈", R.drawable.umeng_socialize_wxcircle);
		cp.mClickListener = new OnSnsPlatformClickListener() {
			
			@Override
			public void onClick(Context arg0, SocializeEntity arg1, SnsPostListener arg2) {
				// TODO Auto-generated method stub
				mController.postShare(ImageViewActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE, new SnsPostListener() {
					
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
					}
					
					@Override
					public void onComplete(SHARE_MEDIA platform, int eCode,SocializeEntity entity) {
						// TODO Auto-generated method stub
						if (eCode == 200) {
	                         Toast.makeText(ImageViewActivity.this, "分享成功.", Toast.LENGTH_SHORT).show();
	                     } else {
	                          String eMsg = "";
	                          if (eCode == -101){
	                              eMsg = "没有授权";
	                          }
	                          Toast.makeText(ImageViewActivity.this, "分享失败[" + eCode + "] " + 
	                                             eMsg,Toast.LENGTH_SHORT).show();
	                     }
					}
				});
			}
		};
		mController.getConfig().addCustomPlatform(cp);
		mController.getConfig().closeToast();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	    /**使用SSO授权必须添加如下代码 */
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
