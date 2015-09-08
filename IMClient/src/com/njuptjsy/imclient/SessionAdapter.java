package com.njuptjsy.imclient;

import java.util.List;

import com.jauker.widget.BadgeView;
import com.njuptjsy.imclient.bean.SessionContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 会话界面ListView的Controller
 * */
public class SessionAdapter extends ArrayAdapter<SessionContext>{
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private BadgeView mBadgeView;//头像边上的数字提示
	
	public SessionAdapter(Context context, List<SessionContext> datas) {
		super(context, -1,datas);
		mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_session, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.sessionPic = (ImageView) convertView.findViewById(R.id.session_pic);
			viewHolder.sessionName = (TextView) convertView.findViewById(R.id.session_name);
			viewHolder.lastMsg = (TextView) convertView.findViewById(R.id.session_lastmsg);
			viewHolder.sessionPicFrame = (FrameLayout) convertView.findViewById(R.id.session_frame);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (mBadgeView != null) {
			viewHolder.sessionPicFrame.removeView(mBadgeView);
		}
		mBadgeView = new BadgeView(mContext);
		mBadgeView.setBadgeCount(getItem(position).getUnReadMsg());
		viewHolder.sessionPicFrame.addView(mBadgeView);
		
		viewHolder.sessionPic.setImageResource(getItem(position).getPicResId());
		viewHolder.sessionName.setText(getItem(position).getSessionName());
		viewHolder.lastMsg.setText(getItem(position).getLastMsg());
		return convertView;
	}

	private class ViewHolder{
		ImageView sessionPic;
		TextView sessionName;
		TextView lastMsg;
		FrameLayout sessionPicFrame;
	}
}