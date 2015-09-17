package com.njuptjsy.imclient.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.njuptjsy.imclient.R;
import com.njuptjsy.imclient.R.id;
import com.njuptjsy.imclient.R.layout;
import com.njuptjsy.imclient.bean.ChatContext;
import com.njuptjsy.imclient.bean.ChatContext.Type;

/**
 * @author  JSY
 * @version：2015年9月2日 下午5:18:37
 * 类说明：负责聊天显示的ListView的Controller
 */
public class ChatMessageAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private List<ChatContext> mDatas;
	private static final int RECEIVE_ITEM = 0;
	private static final int SEND_ITEM = 1;

	public ChatMessageAdapter(Context context,  List<ChatContext> datas){
		mInflater = LayoutInflater.from(context);
		mDatas = datas;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatContext chatContext = mDatas.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			switch (getItemViewType(position)) {//通过itemTye设置不同的布局
			case RECEIVE_ITEM:
				convertView = mInflater.inflate(R.layout.item_receive, parent,false);
				viewHolder = new ViewHolder();
				viewHolder.mDate = (TextView) convertView.findViewById(R.id.id_receive_date);
				viewHolder.mMsg = (TextView) convertView.findViewById(R.id.id_receive_msg);
				break;
			case SEND_ITEM:
				convertView = mInflater.inflate(R.layout.item_send, parent,false);
				viewHolder = new ViewHolder();
				viewHolder.mDate = (TextView) convertView.findViewById(R.id.id_send_date);
				viewHolder.mMsg = (TextView) convertView.findViewById(R.id.id_send_msg);
				break;
			}
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		convertView.setTag(viewHolder);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		viewHolder.mDate.setText(dateFormat.format(chatContext.getDate()));
		viewHolder.mMsg.setText(chatContext.getMsg());
		return convertView;
	}

	private final class ViewHolder {
		TextView mDate; //时间
		TextView mMsg; //长度
	}

	//因为这里的item布局文件有两种，所以需要多复写getItemViewType和getViewTypeCount两个方法
	@Override
	public int getItemViewType(int position) {
		ChatContext chatContext = mDatas.get(position);
		if ( chatContext.getType() == Type.INCOMING) {
			return 0;
		}
		else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}
}
