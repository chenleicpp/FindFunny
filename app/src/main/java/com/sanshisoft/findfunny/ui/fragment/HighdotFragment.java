package com.sanshisoft.findfunny.ui.fragment;


import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.huewu.pla.lib.internal.PLA_AdapterView.OnItemClickListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sanshisoft.findfunny.AppConfig;
import com.sanshisoft.findfunny.R;
import com.sanshisoft.findfunny.adapter.StaggeredAdapter;
import com.sanshisoft.findfunny.model.Photo;
import com.sanshisoft.findfunny.ui.ImageViewActivity;
import com.sanshisoft.findfunny.ui.MainActivity;
import com.sanshisoft.findfunny.ui.MainActivity.OnMyRefreshListener;
import com.sanshisoft.findfunny.util.MyToast;
import com.sanshisoft.findfunny.util.ParseJsonUtils;
import com.sanshisoft.findfunny.util.Utils;
import com.sanshisoft.findfunny.view.XListView;
import com.sanshisoft.findfunny.view.XListView.IXListViewListener;

public class HighdotFragment extends Fragment implements IXListViewListener,OnMyRefreshListener{

    private MainActivity mActivity;
    
    @ViewInject(R.id.list)
    private XListView mWaterListview;
    
    private StaggeredAdapter mAdapter;
    
    private HttpUtils mHttpUtils;
    
    private int mCurrentPage = 1;
    
    @Override
    public void onAttach(Activity activity) {
    	// TODO Auto-generated method stub
    	super.onAttach(activity);
    	((MainActivity)activity).setOnMyRefreshListener(this);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = (MainActivity)getActivity();
        View rootView = inflater.inflate(R.layout.fragment_highdot, container, false);
        ViewUtils.inject(this,rootView);
        int i = getArguments().getInt(MainActivity.ARG_DRAWER_NUMBER);
        String title = getResources().getStringArray(R.array.drawer_array)[i];
        mActivity.setTitle(title);
        //初始化
        mWaterListview.setPullLoadEnable(true);
        mWaterListview.setPullRefreshEnable(false);
        mWaterListview.setXListViewListener(this);
        mAdapter = new StaggeredAdapter(getActivity());
        mWaterListview.setAdapter(mAdapter);
        mWaterListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(PLA_AdapterView<?> parent, View view,
					int position, long id) {
				Photo p = (Photo) mAdapter.getItem(position-1);
                Intent intent = new Intent(getActivity(), ImageViewActivity.class);
                intent.putExtra(ImageViewActivity.IMAGE_URL, p.getImageUrl());
                intent.putExtra(ImageViewActivity.IMAGE_ABS, p.getAbs());
                startActivity(intent);
			}
		});
        
        mHttpUtils = new HttpUtils();
        mHttpUtils.configCurrentHttpCacheExpiry(1000*10);
        
        loadPageData(mCurrentPage);
        
        return rootView;
    }
    
    public void loadPageData(final int page){
    	final String url = Utils.getRandomUrl(AppConfig.PAGE_SIZE);
        mHttpUtils.send(HttpRequest.HttpMethod.GET,
                url,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                        List<Photo> datas = ParseJsonUtils.parsePhotoJson(objectResponseInfo.result);
                        if (datas != null && datas.size()>0) {
                        	mWaterListview.stopLoadMore();
                            mAdapter.addItemLast(datas);
                            if (page > 1) {
								MyToast.showToast(getActivity(), "加载了"+AppConfig.PAGE_SIZE+"条信息");
							}
						}else {
							Toast.makeText(getActivity(), "远程服务器问题，获取数据失败！", Toast.LENGTH_LONG).show();
						}
                    }

					@Override
					public void onFailure(
							com.lidroid.xutils.exception.HttpException arg0,
							String errorMsg) {
						Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
					}
                });
    }


	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		loadPageData(++mCurrentPage);
	}

	@Override
	public void onRefreshData() {
		// TODO Auto-generated method stub
		mAdapter.clearItem();
		loadPageData(1);
	}

}
