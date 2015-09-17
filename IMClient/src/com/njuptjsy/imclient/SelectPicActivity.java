package com.njuptjsy.imclient;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.njuptjsy.imclient.ListImageDirPopupWindow.OnDirSelectedListener;
import com.njuptjsy.imclient.adapter.ImageAdapter;
import com.njuptjsy.imclient.bean.FolderBean;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author  JSY
 * @version：2015年9月17日 下午2:20:24
 * 类说明：用于图片选择器的Activity
 */
public class SelectPicActivity extends FragmentActivity {
	private Handler mHandler;
	private GridView mGridView;
	private ImageAdapter mImageAdapter;
	private List<String> mImgs;
	private RelativeLayout mBottomLy;
	private TextView mDirName;
	private TextView mDirCount;
	private File mCurrentDir;
	private int mMaxCount;
	private List<FolderBean> mFolderBeans = new ArrayList<FolderBean>();
	private ProgressDialog mProgressDialog;
	private static final int PIC_LOADED = 0X110;
	private ListImageDirPopupWindow mDirPopupWindow;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.pic_selector);
		initHandler();
		initDatas();
		initView();
		initEvent();
	}

	private void initEvent() {
		mBottomLy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDirPopupWindow.setAnimationStyle(R.style.dir_popupwindow_anim);
				mDirPopupWindow.showAsDropDown(mBottomLy,0,0);
				lightOff();
			}
		});
	}

	/**
	 * 内容区域变暗
	 * */
	protected void lightOff() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.3f;
		getWindow().setAttributes(lp);
	}

	/**
	 * 将popup中的内容区域变亮
	 * */
	protected void lightOn() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);
	}

	private void initView() {
		mGridView = (GridView) findViewById(R.id.id_gridView);
		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
		mDirName = (TextView) findViewById(R.id.id_dir_name);
		mDirCount = (TextView) findViewById(R.id.id_dir_count);
	}

	/**
	 * 初始化 handler
	 * */
	private void initHandler() {
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				//等待接受子线程返回的，会话返回的数据
				//				ChatContext receiveMsg = (ChatContext) msg.obj;
				//				chatContexts.add(receiveMsg);
				//				chatAdapter.notifyDataSetChanged();
				if (msg.what == PIC_LOADED) {
					mProgressDialog.dismiss();
					dataToView();//为gridview设置数据
					initDirPopupWindow();
				}
			}
		};
	}

	protected void initDirPopupWindow() {
		mDirPopupWindow = new ListImageDirPopupWindow(this, mFolderBeans);
		mDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				lightOn();
			}
		});
		mDirPopupWindow.setOnDirSelectedListener(new OnDirSelectedListener() {

			@Override
			public void onSeleted(FolderBean folderBean) {//更新文件夹 和图片
				mCurrentDir = new File(folderBean.getCurrentDirPath());
				mImgs = Arrays.asList(mCurrentDir.list(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String filename) {
						if (filename.endsWith(".jpg")||filename.endsWith(".png")||filename.endsWith(".jpeg")) {
							return true;
						}else {
							return false;
						}
					}
				}));
				//也可不new adapter 更新里面的datas 接着notify
				mImageAdapter = new ImageAdapter(SelectPicActivity.this, mImgs, mCurrentDir.getAbsolutePath());
				mGridView.setAdapter(mImageAdapter);
				
				mDirCount.setText(mImgs.size() + "");
				mDirName.setText(folderBean.getCurrentDirName());
				
				mDirPopupWindow.dismiss();
			}
		});
	}

	/**
	 * 为gridview设置数据
	 * */

	private void dataToView() {
		if(mCurrentDir == null){
			Toast.makeText(this, R.string.pic_not_found, Toast.LENGTH_SHORT).show();
			return;
		}else {
			mImgs = Arrays.asList(mCurrentDir.list());
			mImageAdapter = new ImageAdapter(this, mImgs, mCurrentDir.getAbsolutePath());
			mGridView.setAdapter(mImageAdapter);

			mDirCount.setText(mMaxCount +"");
			mDirName.setText(mCurrentDir.getName());
		}
	}

	/**
	 * 通过ContentProvider扫描手机中的所有图片
	 * 在独立的线程中完成
	 * */
	private void initDatas() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, R.string.no_sdcard, Toast.LENGTH_SHORT).show();
			return;
		}

		mProgressDialog = ProgressDialog.show(this, null, getString(R.string.loading_now));
		new Thread(){

			public void run() {//利用ContentResolver查询手机中所有的图片的位置和其父文件夹的路径
				Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;//代表图片的uri
				ContentResolver contentResolver =  SelectPicActivity.this.getContentResolver();
				Cursor cursor = contentResolver.query(mImgUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or " + 
						MediaStore.Images.Media.MIME_TYPE + "=?", new String[]{"image/jpeg","image/png"}, MediaStore.Images.Media.DATE_MODIFIED);

				Set<String> mDirPaths = new HashSet<String>();
				while (cursor.moveToNext()) {
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//更加query中设置的排序方法得到最近修改的图片的路径

					File parentFile = new File(path).getParentFile();//得到这个图片的父文件路径
					if (parentFile == null) {
						continue;
					}
					String dirPath = parentFile.getAbsolutePath();

					FolderBean folderBean = null;

					if (mDirPaths.contains(dirPath)) {//如果这个文件夹已经加入set，则不遍历 跳过
						continue;
					}else {
						mDirPaths.add(dirPath);
						folderBean = new FolderBean();
						folderBean.setCurrentDirPath(dirPath);
						folderBean.setFirstImgPath(path);
					}

					if (parentFile.list() == null) {
						continue;
					}

					int picNum = parentFile.list(new FilenameFilter() {

						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg")||filename.endsWith(".png")||filename.endsWith(".jpeg")) {
								return true;
							}else {
								return false;
							}
						}
					}).length;
					folderBean.setDirPicCount(picNum);
					mFolderBeans.add(folderBean);

					if (picNum > mMaxCount) {
						mMaxCount = picNum;
						mCurrentDir = parentFile;
					}

				}
				//扫描完成 释放内存
				cursor.close();
				mHandler.sendEmptyMessage(PIC_LOADED);//通知handler扫描完成
			}
		}.start();
	}
}
