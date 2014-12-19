package com.sanshisoft.findfunny.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sanshisoft.findfunny.model.Photo;

/**
 * Created by chenleicpp on 2014/9/18.
 */
public class ParseJsonUtils {
    public static List<Photo> parsePhotoJson(String result){
		List<Photo> mPhotoList = new ArrayList<Photo>();
        try {
            if (result != null && !result.isEmpty()){
                JSONObject jsonObject = new JSONObject(result);
                int returnNum = jsonObject.getInt("return_number");
                if (returnNum != 0) {
                	JSONArray data = jsonObject.getJSONArray("data");
                    for(int i = 0;i < data.length()-1;i++){
                        JSONObject obj = data.getJSONObject(i);
                        Photo p = new Photo();
                        p.setId(obj.getString("id"));
                        p.setAbs(obj.getString("abs"));
                        p.setImageUrl(obj.getString("image_url"));
                        p.setThumbnailUrl(obj.getString("thumbnail_url"));
                        p.setThumbnailHeight(obj.getInt("thumbnail_height"));
                        p.setThumbLargeWidth(obj.getInt("thumb_large_width"));
                        p.setThumbLargeHeight(obj.getInt("thumb_large_height"));
                        mPhotoList.add(p);
                    }
				}
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mPhotoList;
    }
    
    public static int parseTotalnumJson(String result){
    	int totalPage = 0;
    	try {
    		if (result != null && !result.isEmpty()){
                JSONObject jsonObject = new JSONObject(result);
                totalPage = jsonObject.getInt("totalNum");
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return totalPage;
    }
}
