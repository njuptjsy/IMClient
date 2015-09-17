package com.njuptjsy.imclient.adapter;

import java.util.List;

import com.njuptjsy.imclient.R;
import com.njuptjsy.imclient.bean.FolderBean;
import com.njuptjsy.imclient.utils.ImageLoader;
import com.njuptjsy.imclient.utils.ImageLoader.Type;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author  JSY
 * @version：2015年9月18日 上午12:12:07
 * 类说明：
 */
public class PicDirListViewAdapter extends ArrayAdapter<FolderBean> {

	private LayoutInflater mInflater;
	private List<FolderBean> mDatas;
	
	public PicDirListViewAdapter(Context context, List<FolderBean> objects) {
		super(context, 0, objects);
		mInflater = LayoutInflater.from(context);
		
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_popup_main, parent,false);
			
			viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.id_dir_item_image);
			viewHolder.mDirCount = (TextView) convertView.findViewById(R.id.id_dir_item_count);
			viewHolder.mDirName = (TextView) convertView.findViewById(R.id.id_dir_item_name);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		FolderBean bean = getItem(position);
		viewHolder.mImageView.setImageResource(R.drawable.pictures_no);
		ImageLoader.getInstance(3,Type.FIFO).loadImage(bean.getFirstImgPath(), viewHolder.mImageView);//重置 避免第二个item复用第一个item时 任然显示第一个item的图片 因为第二个item还在加载中
		
		viewHolder.mDirCount.setText(bean.getDirPicCount()+"");
		viewHolder.mDirName.setText(bean.getCurrentDirName());
		
		return convertView;
	}
	
	private class ViewHolder{
		ImageView mImageView;
		TextView mDirName;
		TextView mDirCount;
	}
}
