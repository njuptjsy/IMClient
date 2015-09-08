package com.njuptjsy.imclient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.njuptjsy.imclient.bean.ChatContext;
import com.njuptjsy.imclient.bean.ChatContext.Type;
import com.njuptjsy.imclient.utils.HttpUtils;
import com.njuptjsy.imclient.view.AudioRecorderButton;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author  JSY
 * @version：2015年9月6日 上午10:45:40
 * 类说明：用于和机器人之间的对话
 */
public class TuringActivity extends FragmentActivity{
	private ListView mListView;

	private List<ChatContext> chatContexts = new ArrayList<ChatContext>();
	private ChatMessageAdapter chatAdapter;

	private AudioRecorderButton mAudioRecorderButton;
	private Button switcherButton,sendTextButton;
	private EditText inputText;
	private View audioRecordBtnView;
	private Handler mHandler;

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_layout);

		mListView = (ListView) findViewById(R.id.id_listview);
		switcherButton = (Button) findViewById(R.id.id_swtich_button);
		sendTextButton = (Button) findViewById(R.id.id_send_text_button);
		inputText = (EditText) findViewById(R.id.id_input_et);

		LayoutInflater inflater = LayoutInflater.from(this);
		audioRecordBtnView = inflater.inflate(R.layout.audiorecorderbtn_layout, null);
		mAudioRecorderButton = (AudioRecorderButton) audioRecordBtnView.findViewById(R.id.id_record_button);

		Log.i("ChatFragment.onCreare", (mAudioRecorderButton == null)+"");
		initHandler();
		initView();
		initListener();
	}

	private void initHandler() {
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				//等待接受子线程返回的，智能机器人的数据
				ChatContext receiveMsg = (ChatContext) msg.obj;
				chatContexts.add(receiveMsg);
				chatAdapter.notifyDataSetChanged();
			}
		};
	}

	private void initListener() {
		sendTextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String inputMsg = inputText.getText().toString();
				if (TextUtils.isEmpty(inputMsg)) {
					Toast.makeText(TuringActivity.this, "发送消息不能为空", Toast.LENGTH_SHORT).show();
					return;
				}

				ChatContext sendMsg = new ChatContext();
				sendMsg.setDate(new Date());
				sendMsg.setMsg(inputMsg);
				sendMsg.setType(Type.OUTCOMING);
				chatContexts.add(sendMsg);
				chatAdapter.notifyDataSetChanged();
				inputText.setText("");//清空输入框中的文本

				new Thread(){
					@Override
					public void run() {
						ChatContext receiveMsg = HttpUtils.sendMessage(inputMsg);
						Message msg = Message.obtain();
						msg.obj = receiveMsg;
						mHandler.sendMessage(msg);
					};
				}.start();
			}
		});
	}

	private void initView() {
		inputText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		inputText.setSingleLine(false);
		inputText.setHorizontallyScrolling(false);
		
		initBtn();
		initListview();
	}

	private void initBtn() {
		switcherButton.setVisibility(View.GONE);
	}

	private void initListview() {
		chatContexts = new ArrayList<ChatContext>();
		//TODO for text
		chatContexts.add(new ChatContext("你好，我是小图", Type.INCOMING, new Date()));
		chatAdapter = new ChatMessageAdapter(this, chatContexts);
		mListView.setAdapter(chatAdapter);
	}


	/**
	 * 点击返回键销毁Activity
	 * */
	@Override
	public void onBackPressed() {
		//TODO 把这个会话最近的结果返回给会话列表
		super.onBackPressed();
		//setResult(resultCode, data);
		finish();
	}
}


