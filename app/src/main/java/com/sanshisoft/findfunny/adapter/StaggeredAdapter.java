package com.sanshisoft.findfunny.adapter;


import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.sanshisoft.findfunny.R;
import com.sanshisoft.findfunny.model.Photo;
import com.sanshisoft.findfunny.widget.ScaleImageView;

public class StaggeredAdapter extends BaseAdapter{
	
	private static final int[] COLORS = {R.color.holo_blue_light, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_purple_light, R.color.holo_red_light};
	
	private Context mCtx;
	private LinkedList<Photo> mList;
	private Drawable mDefaultImageBg;
	
	public StaggeredAdapter(Context context){
		mCtx = context;
		mList = new LinkedList<Photo>();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		final ViewHolder holder;
        Photo photo = mList.get(arg0);

        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mCtx);
            convertView = layoutInflator.inflate(R.layout.item_photo, null);
            holder = new ViewHolder();
            holder.imageView = (ScaleImageView) convertView.findViewById(R.id.news_pic);
            holder.contentView = (TextView) convertView.findViewById(R.id.news_title);
            holder.progressBar = (ProgressBar)convertView.findViewById(R.id.img_pb);
            convertView.setTag(holder);
        }else {
        	holder = (ViewHolder) convertView.getTag();
		}
        
        mDefaultImageBg = new ColorDrawable(mCtx.getResources().getColor(COLORS[arg0 % COLORS.length]));
        
        holder.imageView.setImageWidth(photo.getThumbnailWidth());
        holder.imageView.setImageHeight(photo.getThumbnailHeight());
        holder.contentView.setText(photo.getAbs());
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisc(true).considerExifParams(true)
        		.showImageOnLoading(mDefaultImageBg).cacheInMemory(true).build();
        ImageLoader.getInstance().displayImage(photo.getThumbnailUrl(), holder.imageView,options,new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            	holder.progressBar.setVisibility(View.GONE);
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
            	holder.progressBar.setVisibility(View.VISIBLE);
            }
        });
        return convertView;
	}
	
	public void addItemLast(List<Photo> datas) {
        mList.addAll(datas);
        notifyDataSetChanged();
    }
	
	public void clearItem(){
		mList.clear();
		notifyDataSetChanged();
	}

    public void addItemTop(List<Photo> datas) {
        for (Photo info : datas) {
        	mList.addFirst(info);
        }
        notifyDataSetChanged();
    }
	
	static class ViewHolder {
        ScaleImageView imageView;
        TextView contentView;
        ProgressBar progressBar;
    }
	
}
