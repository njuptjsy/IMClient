package com.njuptjsy.imclient;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SessionFragment extends Fragment{
	private Activity mActivity;
	private ListView mListView;
	private ArrayAdapter<SessionContext> mArrayAdapter;
	private List<SessionContext> datas = new ArrayList<SessionContext>();
	private String[] sessionNames = new String[]{"Miss Sun","刘德华","隔壁老王","小强","张学友","科比"};
	private String[] lastMsgs = new String[]{"你好，是你吗？","忘情水来一杯","在家吗","我是凤凰","你认识刘德华吗？","凌晨四点见"};
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
		mListView = (ListView) mActivity.findViewById(R.id.id_session_lv);
		initDatas();
		mArrayAdapter = new SessionAdapter(mActivity, datas);
		mListView.setAdapter(mArrayAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				ViewGroup viewGroup = (ViewGroup) SessionFragment.this.getView().getParent();
				if (viewGroup != null) {
					viewGroup.removeAllViews();
				}
				((MainActivity)mActivity).toChatFragmet(SessionFragment.this,position);
			}
		});
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
	}

	
	
	
}