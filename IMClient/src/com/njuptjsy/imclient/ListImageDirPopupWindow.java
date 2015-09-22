package com.njuptjsy.imclient;

import java.util.List;

import com.njuptjsy.imclient.adapter.PicDirListViewAdapter;
import com.njuptjsy.imclient.bean.FolderBean;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

/**
 * @author  JSY
 * @version：2015年9月17日 下午11:56:59
 * 类说明：图片选择器中的 目录选择window
 */
public class ListImageDirPopupWindow extends PopupWindow{
	private int mWidth;
	private int mHeight;
	private View mContentView;
	private ListView mListView;
	private List<FolderBean> mDatas;
	private Context mContext;
	private OnDirSelectedListener mListener;
	private  View lastSelectedView;
	
	public ListImageDirPopupWindow(Context context, List<FolderBean> datas){
		calculateWidthAndHeight(context);
		
		mContentView = LayoutInflater.from(context).inflate(R.layout.popup_main, null);
		setContentView(mContentView);
		mDatas = datas;
		mContext = context;
		
		setWidth(mWidth);
		setHeight(mHeight);
		//设置可以获得焦点 可触摸 可点击外部 
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());
		
		setTouchInterceptor(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {//监听外部触摸事件 如果发生 则popupWindow消失
					dismiss();
					return true;
				}else {
					return false;
				}
				
			}
		});
		initViews();
		initEvent();
	}

	/**
	 * 实现listView的点击事件
	 * */
	private void initEvent() {
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if (mListener != null) {
					mListener.onSeleted(mDatas.get(position));
					if (lastSelectedView != null) {
						lastSelectedView.findViewById(R.id.id_folder_selector).setVisibility(View.GONE);
					}
					lastSelectedView = view;
					if (view.findViewById(R.id.id_folder_selector).getVisibility() == View.VISIBLE) {
						view.findViewById(R.id.id_folder_selector).setVisibility(View.GONE);
					}else {
						view.findViewById(R.id.id_folder_selector).setVisibility(View.VISIBLE);
					}
					
				}
			}
		});
	}

	
	/**
	 * 为popupWindow中的listView绑定监听器
	 * */
	private void initViews() {
		mListView = (ListView) mContentView.findViewById(R.id.id_list_dir);
		Log.v("ListImageDirPopupWindow:initViews", "mListView is null:" + (mListView == null));
		mListView.setAdapter(new PicDirListViewAdapter(mContext, mDatas));
	}

	/**
	 * 计算popUpwindow的高度和宽度
	 * */
	private void calculateWidthAndHeight(Context context) {
		//获得屏幕的尺寸
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		
		mWidth = outMetrics.widthPixels;
		mHeight = (int) (outMetrics.heightPixels*0.7);
	}
	
	/**
	 * 声明一个接口 在选中popupwindow的item时调用接口中的方法
	 * Activity中实现这个接口 override这个方法就可以实现回调
	 * */
	public interface OnDirSelectedListener{
		void onSeleted(FolderBean folderBean);
	}

	public OnDirSelectedListener getmListener() {
		return mListener;
	}

	public void setOnDirSelectedListener(OnDirSelectedListener mListener) {
		this.mListener = mListener;
	}
	
	
}
