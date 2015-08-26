package com.njuptjsy.imclient;

import java.util.ArrayList;
import java.util.List;

import com.njuptjsy.imclient.view.RefreshListView;
import com.njuptjsy.imclient.view.SlidingMenu;
import com.njuptjsy.imclient.view.RefreshListView.IReflashListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

/**
 * 包裹了会话列表的Fragment
 * */
public class SessionFragment extends Fragment implements IReflashListener{
	private Activity mActivity;
	private RefreshListView mListView;
	private ArrayAdapter<SessionContext> mArrayAdapter;
	private List<SessionContext> datas = new ArrayList<SessionContext>();
	private String[] sessionNames = new String[]{"Miss Sun","刘德华","隔壁老王","小强","张学友","科比"};
	private String[] lastMsgs = new String[]{"你好，是你吗？","忘情水来一杯","在家吗","我是凤凰","你认识刘德华吗？","凌晨四点见"};
	private static final int CHATACTIVITY = 1;
	private SlidingMenu mSlidingMenu;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.session_layout, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mListView = (RefreshListView) mActivity.findViewById(R.id.id_session_lv);
		mSlidingMenu = (SlidingMenu) mActivity.findViewById(R.id.id_sliding_menu);
		initDatas();
		mArrayAdapter = new SessionAdapter(mActivity, datas);
		mListView.setAdapter(mArrayAdapter);
		mListView.setOnReflashListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
//				ViewGroup viewGroup = (ViewGroup) SessionFragment.this.getView().getParent();
//				if (viewGroup != null) {
//					viewGroup.removeAllViews();
//				}
//				((MainActivity)mActivity).toChatFragmet(SessionFragment.this,position);
				Intent intent = new Intent(mActivity.getApplicationContext(),ChatActivity.class);
				startActivityForResult(intent,CHATACTIVITY);
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
	}
	
	private void initDatas() {
		if (datas.isEmpty()) {
			//int resId = mContext.getResources().getIdentifier("v"+level,"drawable",mContext.getPackageName());
			SessionContext sessionContext = null;
			for (int i = 0; i < sessionNames.length; i++) {
				sessionContext = new SessionContext(mActivity.getResources().getIdentifier("listview_icon"+(i+1),"drawable",mActivity.getPackageName()), sessionNames[i], lastMsgs[i]);
				datas.add(sessionContext);
			}
		}
		datas.get(1).setUnReadMsg(3);
		datas.get(3).setUnReadMsg(7);
		datas.get(4).setUnReadMsg(4);;
	}

	@Override
	public void onReflash() {
		//TODO 获取最新数据 通知界面显示 刷新数据完毕
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				reflashData();
				mArrayAdapter.notifyDataSetChanged();
				mListView.reflashComplete();
			}
		}, 2000);
		
	}

	private void reflashData() {
		SessionContext sessionContext = new SessionContext(mActivity.getResources().getIdentifier("icon","drawable",mActivity.getPackageName()),"Mr King","不一样的烟火");
		datas.add(0,sessionContext);
	}

	public void toggleSlidingMenu() {
		mSlidingMenu.toggle();
	}
	
	
}