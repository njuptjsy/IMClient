package com.njuptjsy.imclient.view;

import com.njuptjsy.imclient.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class RefreshListView extends ListView {
	private View header;
	private int headerHeight;//顶部view的高度
	
	public RefreshListView(Context context) {
		super(context);
		initView(context);
	}
	
	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	/**
	 * 初始化界面添加顶部布局文件到ListView
	 * */
	private void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		header = inflater.inflate(R.layout.header_layout,null);
		headerHeight = header.getMeasuredHeight();
		Log.i("RefreshListView:initView", "header view heigth: "+ headerHeight);
		topPadding(-headerHeight);
		this.addHeaderView(header);//将自定义的view作为顶部布局文件加入
	}
	/**
	 * @author JSY
	 * 设置header布局的上边距
	 * */
	private void topPadding(int topPadding){
		header.setPadding(header.getPaddingLeft(), topPadding, header.getPaddingRight(), header.getPaddingBottom());
	}
}
