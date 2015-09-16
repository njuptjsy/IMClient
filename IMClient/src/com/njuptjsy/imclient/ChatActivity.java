package com.njuptjsy.imclient;
import java.util.ArrayList;
import java.util.List;

import com.njuptjsy.imclient.bean.ChatContext;
import com.njuptjsy.imclient.utils.HttpUtils;
import com.njuptjsy.imclient.view.AudioRecorderButton;
import com.njuptjsy.imclient.view.AudioRecorderButton.AudioFinishRecorderListener;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

/**
 * Author JSY.
 * 聊天视图的实现Activity
 */
public class ChatActivity extends FragmentActivity implements TextWatcher{
	//	private static final int RESULT_CANCELED = 0;
	//	private static final int RESULT_OK = -1;
	//	private static final int RESULT_FIRST_USER = 1;用来作为setResult函数中的resultCode

	private ListView mListView;
	private ArrayAdapter<Recorder> mAdapter;
	private List<Recorder> mDatas = new ArrayList<Recorder>();

	private AudioRecorderButton mAudioRecorderButton;
	private Button switcherButton,sendTextButton,plusButton;
	private EditText inputText;
	private View mAnimView;
	private View audioRecordBtnView,sendMsgBtnView;
	private LinearLayout mInputMsgLinearLayout,mPlusSendLinearLayout;
	private RelativeLayout mRelativeLayout;
	private boolean showRecorder,showSendBtn;
	private InputMethodManager imm;
	private Handler mHandler;

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); 
		setContentView(R.layout.chat_layout);

		mListView = (ListView) findViewById(R.id.id_listview);
		switcherButton = (Button) findViewById(R.id.id_swtich_button);
		plusButton = (Button) findViewById(R.id.id_plus_button);//TODO 添加监听事件，显示图片选择器
		inputText = (EditText) findViewById(R.id.id_input_et);
		inputText.addTextChangedListener(this);
		mInputMsgLinearLayout = (LinearLayout) findViewById(R.id.id_input_msg_lv);
		mPlusSendLinearLayout = (LinearLayout) findViewById(R.id.id_plus_send_lv);
		mRelativeLayout = (RelativeLayout) findViewById(R.id.id_input_view);
		
		//实例化两个动态添加的view组件
		LayoutInflater inflater = LayoutInflater.from(this);
		sendMsgBtnView = inflater.inflate(R.layout.send_msg_btn_layout, null);
		sendTextButton = (Button) sendMsgBtnView.findViewById(R.id.id_send_text_button);

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
				//等待接受子线程返回的，会话返回的数据
				//				ChatContext receiveMsg = (ChatContext) msg.obj;
				//				chatContexts.add(receiveMsg);
				//				chatAdapter.notifyDataSetChanged();
			}
		};
	}

	private void initListener() {
		sendTextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String inputMsg = inputText.getText().toString();
				if (TextUtils.isEmpty(inputMsg)) {
					Toast.makeText(ChatActivity.this, "发送消息不能为空", Toast.LENGTH_SHORT).show();
					return;
				}

				//				ChatContext sendMsg = new ChatContext();
				//				sendMsg.setDate(new Date());
				//				sendMsg.setMsg(inputMsg);
				//				sendMsg.setType(Type.OUTCOMING);
				//				chatContexts.add(sendMsg);
				//				chatAdapter.notifyDataSetChanged();
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
		//		inputText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		//		inputText.setSingleLine(false);
		//		inputText.setHorizontallyScrolling(false);

		initSwtichBtn();
		initRecorderBtn();
		initListview();
	}

	private void initSwtichBtn() {
		switcherButton.setOnClickListener(new OnClickListener() {

			/**
			 * 监听切换按钮的点击事件
			 * 动态的切换是文字输入或是语音输入
			 * */
			@Override
			public void onClick(View v) {
				if (!showRecorder) {
					mInputMsgLinearLayout.removeAllViews();
					//为包含了audioRecordBtn的相对布局在代码中添加属性，使其占满父控件
					RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT); 
					lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE); 
					lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE); 
					audioRecordBtnView.setLayoutParams(lp);

					mInputMsgLinearLayout.addView(audioRecordBtnView);
					switcherButton.setBackgroundResource(R.drawable.switcher_keyboard_bg);
					sendTextButton.setVisibility(View.GONE);
					//设置软键盘在录音界面自动隐藏
					if (imm.isActive()) {
						imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					}
					showRecorder = true;
				}else {
					mInputMsgLinearLayout.removeAllViews();
					mInputMsgLinearLayout.addView(inputText);
					switcherButton.setBackgroundResource(R.drawable.switcher_recorder_bg);
					sendTextButton.setVisibility(View.VISIBLE);
					showRecorder = false;
				}

			}
		});
	}

	private void initListview() {
		mAdapter = new RecorderAdapter(this, mDatas);

		//		chatContexts = new ArrayList<ChatContext>();
		//		//TODO for text
		//		chatContexts.add(new ChatContext("你好，我是小图", Type.INCOMING, new Date()));
		//		chatAdapter = new ChatMessageAdapter(this, chatContexts);
		mListView.setAdapter(mAdapter);

		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if (mAnimView != null) {
					mAnimView.setBackgroundResource(R.drawable.adj);;
				}
				//播放动画
				mAnimView = view.findViewById(R.id.recorder_anim);
				mAnimView.setBackgroundResource(R.drawable.play_anim);
				AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
				anim.start();
				//播放音频
				MediaManager.playSound(mDatas.get(position).filePath, new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {//音频播放结束，取消动画
						mAnimView.setBackgroundResource(R.drawable.adj);
					}
				});
			}
		});
	}

	private void initRecorderBtn() {
		mAudioRecorderButton.setAudioFinishRecorderListener(new AudioFinishRecorderListener() {

			@Override
			public void onFinish(float seconds, String filePath) {
				Recorder recorder = new Recorder(seconds, filePath);
				mDatas.add(recorder);
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mDatas.size() - 1);//Sets the currently selected item
			}
		});
	}

	class Recorder{
		float time;
		String filePath;
		public Recorder(float time, String filePath) {
			super();
			this.time = time;
			this.filePath = filePath;
		}

		public float getTime() {
			return time;
		}

		public void setTime(float time) {
			this.time = time;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

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

	@Override
	public void onDestroy() {
		MediaManager.release();
		super.onDestroy();
	}
	@Override
	public void onPause() {
		MediaManager.pause();
		super.onPause();
	}
	@Override
	public void onResume() {
		MediaManager.resume();
		super.onResume();
	}

	/**
	 * 监听消息输入框中的输入情况变化
	 * */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		Log.i("ChatActivity:beforeTextChanged", "输入文本之前的状态");  
	}

	/**
	 * 当输入的字符大于0 即用户输入时切换到发送按钮
	 * */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Log.i("ChatActivity:onTextChanged", "输入文字中的状态s: "+s.toString());
		if ((!s.toString().isEmpty()) && (!showSendBtn)) {
			changeToSendBtn();
		}else if(s.toString().isEmpty() && showSendBtn){
			changeToPlusBtn();
		}
	}

	/**
	 * 将发送按钮替换成plus按钮
	 * */
	private void changeToPlusBtn() {
		Log.i("ChatActivity:changeToPlusBtn", "This is changeToPlusBtn");
		mRelativeLayout.removeView(sendMsgBtnView);
		mRelativeLayout.addView(mPlusSendLinearLayout);
		showSendBtn = false;
	}

	/**
	 * 将plus按钮替换成发送按钮
	 * */
	private void changeToSendBtn() {
		Log.i("ChatActivity:changeToSendBtn", "This is changeToSendBtn");
		mRelativeLayout.removeView(mPlusSendLinearLayout);
		mRelativeLayout.addView(sendMsgBtnView);
		showSendBtn = true;
		//			mRelativeLayout.removeView(sendMsgBtnView);
		//			mRelativeLayout.addView(plusButton);

	}

	@Override
	public void afterTextChanged(Editable s) {
		Log.i("ChatActivity:afterTextChanged", "输入文字后的状态");  

	}

}
