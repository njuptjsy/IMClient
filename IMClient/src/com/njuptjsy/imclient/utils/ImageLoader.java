package com.njuptjsy.imclient.utils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * @author  JSY
 * @version：2015年9月9日 下午3:36:33
 * 类说明：图片加载工具类 单例
 */
public class ImageLoader {
	private static ImageLoader mInstance;
	private LruCache<String, Bitmap> mLruCache;//图片缓存
	private ExecutorService mThreadPool;//处理Task的线程池
	private static final int DEAFULT_THREAD_COUNT = 1;
	public enum Type{//线程的调度方式
		FIFO,LIFO
	}
	private Type mType = Type.LIFO;//线程队列调度方式
	private LinkedList<Runnable> mTaskQueue;//task队列
	private Thread mLoopThread;//后台的轮训线程
	private Handler mLoopThreadHandler;
	private Handler mUIHandler;//用于在后台更新图片
	private Semaphore mSemaphoreLoopThreadHandler = new Semaphore(0);
	private Semaphore mSemaphoreThreadPool;

	private ImageLoader(int threadCount,Type type){
		init(threadCount,type);
	}
	/**
	 * 进行各种初始化操作
	 * */
	private void init(int threadCount, Type type) {
		mLoopThread = new Thread(){//初始化后台轮训线程
			@Override
			public void run() {
				Looper.prepare();
				mLoopThreadHandler = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						//线程池取出一个任务进行执行
						mThreadPool.execute(getTask());
						try {
							mSemaphoreThreadPool.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
				mSemaphoreLoopThreadHandler.release();
				Looper.loop();
			}
		};

		mLoopThread.start();

		//初始化LruCache
		int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取应用的最大使用内存
		int cacheMemory = maxMemory/8;
		mLruCache = new LruCache<String, Bitmap>(cacheMemory){
			@Override
			protected int sizeOf(String key, Bitmap value) {//测量每个bitmap的大小
				return value.getRowBytes()*value.getHeight();
			}
		};

		//初始化线程池
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mTaskQueue = new LinkedList<Runnable>();
		mType = type;

		mSemaphoreThreadPool = new Semaphore(threadCount);//运行同时多个线程并行
	}

	/**
	 * 从任务队列取出一个方法
	 * 根据这里图片的加载策略的选择：FIFO或LIFO
	 * */
	private Runnable getTask() {
		if (mType == Type.FIFO) {
			return mTaskQueue.removeFirst();
		}else if (mType == Type.LIFO) {
			return mTaskQueue.removeLast();
		}
		return null;
	}

	public static ImageLoader getInstance(int threadCount,Type type){
		if (mInstance == null) {//先不做同步处理，在初始化之后可以过滤大量代码
			synchronized (ImageLoader.class) {//如果刚刚开始两个线程进入会进图同步
				if (mInstance == null) {//多个线程进入第一个if后，因为同步阻塞，得到锁之后也要在判断一次，保证不出错
					mInstance = new ImageLoader(threadCount, type);
				}
			}
		}
		return mInstance;
	}

	
	
	/**
	 * 根据path为imageView设置图片
	 * */
	public void loadImage(final String path, final ImageView imageView) {
		imageView.setTag(path);

		if (mUIHandler == null) {
			mUIHandler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					//获取得到的图片，为imageView回调设置图片
					ImageBeanHolder holder = (ImageBeanHolder) msg.obj;
					Bitmap bm = holder.bitmap;
					ImageView imageView = holder.imageView;
					String path = holder.path;

					if (imageView.getTag().toString().equals(path)) {//将path与imageView tag中的进行比较
						imageView.setImageBitmap(bm);
					}
				}
			};
		}

		Bitmap bitmap = getBitmapFromLruCache(path);
		if (bitmap != null) {
			refreshBitmap(path, imageView, bitmap);
		}else {//缓存中没有当前图片的情况
			addTasks(new Runnable() {

				@Override
				public void run() {
					//加载图片 图片压缩 
					//1.获得图片需要显示的大小
					ImageSize imageSize = getImageViewSize(imageView);
					//2.压缩图片
					Bitmap bm = decodeSampledBitmapFromPath(path,imageSize.width,imageSize.height);
					//3.把图片加入到缓存
					addBitmapToLruCache(path,bm);

					refreshBitmap(path, imageView, bm);
					mSemaphoreThreadPool.release();
				}


			});
		}
	}

	private void refreshBitmap(final String path,final ImageView imageView, Bitmap bm) {
		Message message = Message.obtain();
		ImageBeanHolder imageBeanHolder = new ImageBeanHolder();
		imageBeanHolder.bitmap = bm;
		imageBeanHolder.path = path;
		imageBeanHolder.imageView = imageView;
		message.obj = imageBeanHolder;
		mUIHandler.sendMessage(message);
	}

	/**
	 * 将图片加入缓冲区
	 * */
	protected void addBitmapToLruCache(String path, Bitmap bm) {
		if (getBitmapFromLruCache(path) == null) {
			if (bm != null) {
				mLruCache.put(path, bm);
			}
		}
	}
	/**
	 * 根据图片需要显示的宽和高对图片进行压缩
	 * */
	protected Bitmap decodeSampledBitmapFromPath(String path, int width,int height) {
		//通过bitmap对象获取图片的宽和高,但是并不把图片加载到内存中
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		options.inSampleSize = caculateInSampleSize(options,width,height);
		//使用获得的压缩比，对图片进行压缩
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path,options);
		return bitmap;
	}

	/**
	 * 根据需求的宽和高和图片实际的宽和高计算SimpleSize
	 * */
	private int caculateInSampleSize(Options options, int reqWidth, int reqHeight) {
		int width = options.outWidth;
		int heigth = options.outHeight;
		int inSampleSize = 1;

		if (width > reqWidth || heigth > reqHeight) {
			//如果图片宽或高大于实际需求的宽高则进行压缩
			int widthRadio = Math.round(width*1.0f/reqWidth);
			int heightRadio = Math.round(heigth*1.0f/reqHeight);

			inSampleSize = Math.max(widthRadio, heightRadio);//取较大的压缩比例，压缩的越多越好
		}
		return inSampleSize;
	}

	private class ImageSize{
		int width;
		int height;
	}

	/**
	 *根据imageview获得图片需要显示的大小 
	 * */
	private ImageSize getImageViewSize(ImageView imageView) {
		DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();

		ImageSize imageSize = new ImageSize();
		LayoutParams lp = imageView.getLayoutParams();
		//获取imageview的实际宽度，但可能由于imageview还未添加到容器中，这边可能为0
		int width = imageView.getWidth();
		if (width <= 0) {
			width = lp.width;//获取imageview在layout中声明的宽度
		}
		if (width <= 0) {
			//width = imageView.getMaxWidth();//检查最大值
			width = getImageViewFieldValue(imageView, "mMaxWidth");
		}
		if(width <= 0){//最坏的情况无法获取宽度，使用屏幕的宽度
			width = displayMetrics.widthPixels;
		}

		int height = imageView.getHeight();//获取imageview的实际宽度，但可能由于imageview还未添加到容器中，这边可能为0
		if (height <= 0) {
			height = lp.height;//获取imageview在layout中声明的宽度
		}
		if (height <= 0) {
			height = getImageViewFieldValue(imageView, "mMaxHeight");//检查最大值
		}
		if(width <= 0){//最坏的情况无法获取高度，使用屏幕的高度
			height = displayMetrics.heightPixels;
		}
		imageSize.height = height;
		imageSize.width = width;
		return imageSize;
	}

	/**
	 * 通过反射获取imageView的某个值
	 * */
	private static int getImageViewFieldValue(Object object,String fieldName) {
		int value = 0;


		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = field.getInt(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			e.printStackTrace();}
		return value;
	}

	/**
	 *将未执行的图片加载任务添加到task队列中 
	 * */
	private synchronized void addTasks(Runnable runnable) {
		mTaskQueue.add(runnable);
		//判断此时mLoopThreadHandler是否已经被初始化了，如果有在执行下一句
		try {
			if (mLoopThreadHandler == null) {
				mSemaphoreLoopThreadHandler.acquire();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mLoopThreadHandler.sendEmptyMessage(0X110);
	}

	/**
	 * 根据path在缓存中获取bitmap
	 * */
	private Bitmap getBitmapFromLruCache(String key) {
		return mLruCache.get(key);
	}

	private class ImageBeanHolder{
		Bitmap bitmap;
		ImageView imageView;
		String path;
	}
}
