package com.njuptjsy.imclient.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.njuptjsy.imclient.ChatActivity;
import com.njuptjsy.imclient.R;
import com.njuptjsy.imclient.bean.Recorder;
import com.njuptjsy.imclient.R.id;
import com.njuptjsy.imclient.R.layout;

import java.util.List;
/**
 * Author JSY.
 * 显示会话窗口的ListView的Controller
 */
public class RecorderAdapter extends ArrayAdapter<Recorder>{
	private Context mContext;
	private int mMinItemWidth;
	private int mMaxItemWidth;
	private LayoutInflater mInflater;

	public RecorderAdapter(Context context, List<Recorder> datas) {
		super(context, -1,datas);
		mContext = context;

		//获取屏幕的宽度
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);

		mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f);
		mMinItemWidth = (int) (outMetrics.widthPixels * 0.15f);

		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.item_recorder,parent,false);
			holder = new ViewHolder();
			holder.seconds = (TextView) convertView.findViewById(R.id.recorder_time);
			holder.length = convertView.findViewById(R.id.recorder_length);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.seconds.setText(Math.round(getItem(position).getTime())+"\"");
		ViewGroup.LayoutParams lp = holder.length.getLayoutParams();
		if (getItem(position).getTime() < 60) {
			lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f)* getItem(position).getTime());
		}else {
			lp.width = (int) (1.25f*mMinItemWidth + mMaxItemWidth);
		}
		

		return convertView;
	}

	private final class ViewHolder {
		TextView seconds; //时间
		View length; //长度
	}

	
	
}
