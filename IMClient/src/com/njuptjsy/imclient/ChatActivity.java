package com.njuptjsy.imclient;
import java.util.ArrayList;
import java.util.List;

import com.njuptjsy.imclient.view.AudioRecorderButton;
import com.njuptjsy.imclient.view.AudioRecorderButton.AudioFinishRecorderListener;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Author JSY.
 * 聊天视图的实现Activity
 */
public class ChatActivity extends FragmentActivity {
//	private static final int RESULT_CANCELED = 0;
//	private static final int RESULT_OK = -1;
//	private static final int RESULT_FIRST_USER = 1;用来作为setResult函数中的resultCode

	private ListView mListView;
	private ArrayAdapter<Recorder> mAdapter;
	private List<Recorder> mDatas = new ArrayList<Recorder>();
	private AudioRecorderButton mAudioRecorderButton;
	
	private View mAnimView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_layout);
		mListView = (ListView) findViewById(R.id.id_listview);
		mAudioRecorderButton = (AudioRecorderButton) findViewById(R.id.id_record_button);
		Log.i("ChatFragment.onCreare", (mAudioRecorderButton == null)+"");
		mAudioRecorderButton.setAudioFinishRecorderListener(new AudioFinishRecorderListener() {

			@Override
			public void onFinish(float seconds, String filePath) {
				Recorder recorder = new Recorder(seconds, filePath);
				mDatas.add(recorder);
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mDatas.size() - 1);//Sets the currently selected item
			}
		});

		mAdapter = new RecorderAdapter(this, mDatas);
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

}
