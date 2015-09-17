package com.njuptjsy.imclient.adapter;

import java.util.List;

import com.njuptjsy.imclient.R;
import com.njuptjsy.imclient.utils.ImageLoader;
import com.njuptjsy.imclient.utils.ImageLoader.Type;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * @author  JSY
 * @version：2015年9月17日 上午11:41:28
 * 类说明：图片显示器中gridView的适配器
 */
public class ImageAdapter extends BaseAdapter {
	
	private String mDirPath;
	private List<String> mImgPaths;
	private LayoutInflater mInflater;

	public ImageAdapter(Context context,List<String> mDatas, String dirPath){
		this.mDirPath = dirPath;
		this.mImgPaths = mDatas;
		mInflater = LayoutInflater.from(context);
	}
	
	
	@Override
	public int getCount() {
		return mImgPaths.size();
	}

	@Override
	public Object getItem(int position) {
		return mImgPaths.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_pic_gridview, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.id_item_image);
			viewHolder.mImageButton = (ImageButton) convertView.findViewById(R.id.id_image_item_selet);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		viewHolder.mImageView.setImageResource(R.drawable.pictures_no);
		viewHolder.mImageButton.setImageResource(R.drawable.picture_unselected);
		
		ImageLoader.getInstance(3, Type.LIFO).loadImage(mDirPath+"/"+mImgPaths.get(position), viewHolder.mImageView);
		
		return convertView;
	}

	private class ViewHolder{
		ImageView mImageView;
		ImageButton mImageButton;
		
	}
	
}
