package com.njuptjsy.imclient.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.njuptjsy.imclient.R;
import com.njuptjsy.imclient.utils.ImageLoader;
import com.njuptjsy.imclient.utils.ImageLoader.Type;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	private static Set<String> mSelectedImg = new HashSet<String>();
	
	
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_pic_gridview, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.id_item_image);
			viewHolder.mImageButton = (ImageButton) convertView.findViewById(R.id.id_image_item_selet);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		//重置状态
		viewHolder.mImageView.setImageResource(R.drawable.pictures_no);
		viewHolder.mImageButton.setImageResource(R.drawable.picture_unselected);
		viewHolder.mImageView.setColorFilter(null);
		
		ImageLoader.getInstance(3, Type.LIFO).loadImage(mDirPath+"/"+mImgPaths.get(position), viewHolder.mImageView);
		final String filePath = mDirPath + "/" + mImgPaths.get(position);
		viewHolder.mImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mSelectedImg.contains(filePath)) {//已经被选中 清除选中状态
					mSelectedImg.remove(filePath);
					viewHolder.mImageView.setColorFilter(null);
					viewHolder.mImageButton.setImageResource(R.drawable.picture_unselected);
				}else {//未被选择
					mSelectedImg.add(filePath);
					viewHolder.mImageView.setColorFilter(Color.parseColor("#77000000"));
					viewHolder.mImageButton.setImageResource(R.drawable.pictures_selected);
				}
				//notifyDataSetChanged();导致闪屏问题
			}
		});
		
		if (mSelectedImg.contains(filePath)) {
			viewHolder.mImageView.setColorFilter(Color.parseColor("#77000000"));
			viewHolder.mImageButton.setImageResource(R.drawable.pictures_selected);
		}
		
		return convertView;
	}

	private class ViewHolder{
		ImageView mImageView;
		ImageButton mImageButton;
		
	}
	
}
