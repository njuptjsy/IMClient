package com.njuptjsy.imclient.view;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Author JSY.
 * 播放录音
 */
public class AudioManager {
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;
    private boolean isPrepared;

    private static AudioManager mInstance;
    public static AudioManager getmInstance(String dir){
        if(mInstance == null) {
            synchronized (AudioManager.class){
                if(mInstance == null) {
                    mInstance = new AudioManager(dir);
                }
            }
        }
        return mInstance;
    }

    public AudioManager(String dir) {
        this.mDir = dir;
    }

    private AudioStateListener mListener;

    /**
     * 回调准备完毕
     */
    public interface AudioStateListener{
        void wellPrepared();
    }

    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

    /**
     * 录音前的准备
     * 设置输出格式，录音源设备等
     */
    public void prepareAudio(){

        try {
            isPrepared = false;

            File dir = new File(mDir);
            if(!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = generateFileName();
            File file = new File(dir,fileName);

            mCurrentFilePath = file.getAbsolutePath();
            mMediaRecorder = new MediaRecorder();
            //设置输出文件
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            //设置MediaRecorder的音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频的格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //设置音频的编码为amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //准备录音
            mMediaRecorder.prepare();
            //开始录音
            mMediaRecorder.start();
            //准备结束
            isPrepared = true;
            if(mListener != null) {
                mListener.wellPrepared();
            }
        } catch (IOException e) {
        	Log.e("AudioManager:prepareAudio", "something bad happend!");
            e.printStackTrace();
        }
    }

    /**
     * 随机生成文件的名称
     * @return
     */
    private String generateFileName() {
        return UUID.randomUUID().toString()+".amr";
    }

    /**
     * 获得音量等级
     * @return
     */
    public int getVoiceLevel(int maxLevel){
        if(isPrepared){
            try{
                //mMediaRecorder.getMaxAmplitude() 范围:1-32767之间
                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            }catch (Exception e){

            }
        }
        return 1;
    }

    /**
     * 停止录音并释放录音设备资源
     */
    public void release(){
    	mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    /**
     * 取消
     */
    public void cancel(){
        release();
        //删除文件
        if(mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    /**
     * 获取当前文件存储路径
     * @return
     */
    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }
}