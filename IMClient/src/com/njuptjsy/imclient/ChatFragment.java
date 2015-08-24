package com.njuptjsy.imclient;

import java.util.ArrayList;
import java.util.List;

import com.njuptjsy.imclient.view.AudioRecorderButton;
import com.njuptjsy.imclient.view.AudioRecorderButton.AudioFinishRecorderListener;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChatFragment extends Fragment{
	
	private ListView mListView;
	private ArrayAdapter<Recorder> mAdapter;
	private List<Recorder> mDatas = new ArrayList<Recorder>();
	private AudioRecorderButton mAudioRecorderButton;
	private Activity mActivity;
	private View mAnimView;

	@Override
	public void onAttach(Activity activity) {
		Log.i("ChatFragment.onAttach", "now is in onAttach");
		super.onAttach(activity);
		mActivity = activity;
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		Log.i("ChatFragment.onCreateView", "now is in onCreateView");
		return inflater.inflate(R.layout.chat_layout, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i("ChatFragment.onActivityCreated", "now is in onActivityCreated");
		((MainActivity)mActivity).setChatView(getView());
		super.onActivityCreated(savedInstanceState);
		mListView = (ListView) mActivity.findViewById(R.id.id_listview);
		mAudioRecorderButton = (AudioRecorderButton) mActivity.findViewById(R.id.id_record_button);
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

		mAdapter = new RecorderAdapter(mActivity, mDatas);
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
