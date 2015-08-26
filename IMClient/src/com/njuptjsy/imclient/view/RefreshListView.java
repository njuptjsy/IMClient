package com.njuptjsy.imclient.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.njuptjsy.imclient.R;

import android.R.integer;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;;

public class RefreshListView extends ListView implements OnScrollListener{
	private View header;
	private int headerHeight;//顶部view的高度
	private int firstVisibleItem;//当前第一个可见item位置
	private boolean inTopOfListview;//记录当前是在listview的最顶端按下
	private int startY;//按下时的y值
	private int state;//当前listView下拉状态
	private static final int NONMAL = 0;
	private static final int PULLING = 1;//显示下拉刷新
	private static final int RELEASE = 2;//显示松开刷新
	private static final int REFLASHING = 3;//真正刷新
	private int scrollState;//当前的滚动转态
	private Context context;
	private RotateAnimation animationToDown,animationToUp;
	private IReflashListener listener;//刷新数据的接口

	public RefreshListView(Context context) {
		super(context);
		initView(context);
		this.context = context;
	}

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		this.context = context;
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		this.context = context;
	}

	/**
	 * 初始化界面添加顶部布局文件到ListView
	 * */
	private void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		header = inflater.inflate(R.layout.header_layout,null);
		measureView(header);
		headerHeight = header.getMeasuredHeight();
		Log.i("RefreshListView:initView", "header view heigth: "+ headerHeight);
		topPadding(-headerHeight);
		this.addHeaderView(header);//将自定义的view作为顶部布局文件加入
		this.setOnScrollListener(this);//设置滚动监听
	}
	/**
	 * @author JSY
	 * 设置header布局的上边距
	 * */
	private void topPadding(int topPadding){
		header.setPadding(header.getPaddingLeft(), topPadding, header.getPaddingRight(), header.getPaddingBottom());
	}

	/**
	 * 结合父布局决定自身在真正绘图时所占的宽和高
	 * 结合子视图的LayoutParams所给出的MeasureSpec信息来获取最合适的结果
	 * */
	private void measureView(View view){
		ViewGroup.LayoutParams lp = view.getLayoutParams();//首先取到子布局的布局参数
		if (lp == null) {//如果没有则新建，代码中设定
			lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int width = ViewGroup.getChildMeasureSpec(0, 0, lp.width);//1.左右边距2.内边距3.子布局宽度
		//结合从子视图的LayoutParams所给出的MeasureSpec信息来通过父布局获取最合适的结果宽或高
		int height;
		int tempHeight = lp.height;
		if (tempHeight > 0) {//MeasureSpc类封装了父View传递给子View的布局(layout)要求
			height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);//高度不为空填充父布局，根据提供的大小值和模式创建一个测量值
		}
		else {
			height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);//高度为0，不需要填充
		}
		view.measure(width, height);//最终将得到的宽和高调用View的measure方法，measure中又会调用onMeasure方法
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (firstVisibleItem == 0) {
				inTopOfListview = true;
				startY = (int) ev.getY();
			}
			Log.i("RefreshListView:onTouchEvent","MotionEvent.ACTION_DOWN");
			reflashViewByState();
			break;
		case MotionEvent.ACTION_MOVE:
			onMove(ev);
			reflashViewByState();
			break;
		case MotionEvent.ACTION_UP:
			if (state == RELEASE) {
				state = REFLASHING;
				//加载最新数据
				reflashViewByState();
				listener.onReflash();
			}else if (state == PULLING) {
				state = NONMAL;
				inTopOfListview = false;
				reflashViewByState();
			}
			break;

		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 判读移动过程中的操作
	 * */
	private void onMove(MotionEvent ev){
		if (!inTopOfListview) {
			return;
		}
		int tempY = (int) ev.getY();
		int length = tempY - startY;
		int topPadding = length - headerHeight;
		switch (state) {
		case NONMAL:
			if (length > 0) {
				Log.i("RefreshListView:onMove","length" + length);
				state = PULLING;
			}
			break;
		case PULLING:
			topPadding(topPadding);
			if (length > headerHeight + 30 && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
				state = RELEASE;
			}
			break;
		case RELEASE:
			topPadding(topPadding);
			if (length < headerHeight + 30) {
				state = PULLING;
			}else if (length <= 0) {
				state = NONMAL;
				inTopOfListview = false;
			}
			break;
		case REFLASHING:

			break;
		}
	}

	/**根据当前状态改变界面显示*/
	private void reflashViewByState(){
		TextView tip = (TextView) header.findViewById(R.id.tip);
		ImageView arrow = (ImageView) header.findViewById(R.id.arrow);
		ProgressBar progressBar = (ProgressBar) header.findViewById(R.id.progess);

		setAnim();

		switch (state) {
		case NONMAL:
			topPadding(-headerHeight);
			break;
		case PULLING:
			arrow.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tip.setText(context.getString(R.string.pull_refresh));

			arrow.clearAnimation();
			arrow.setAnimation(animationToDown);
			break;
		case RELEASE:
			arrow.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tip.setText(context.getString(R.string.release_refresh));

			arrow.clearAnimation();
			arrow.setAnimation(animationToUp);
			break;
		case REFLASHING:
			topPadding(50);
			arrow.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
			tip.setText(context.getString(R.string.refreshing));
			arrow.clearAnimation();
			break;
		}
	}
	/**
	 * 设置箭头对话
	 * */
	private void setAnim() {
		animationToDown = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f , RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animationToDown.setDuration(500);
		animationToDown.setFillAfter(true);
		animationToUp = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f , RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animationToUp.setDuration(500);
		animationToUp.setFillAfter(true);
	}

	/**
	 * 获取完数据
	 * */
	public void reflashComplete(){
		state = NONMAL;
		inTopOfListview = false;
		reflashViewByState();
		TextView lastUpdateTime = (TextView) header.findViewById(R.id.lastupdate_time);
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		String time = format.format(date);
		lastUpdateTime.setText(time);
	}
	
	public void setOnReflashListener(IReflashListener listener){
		this.listener = listener;
	}
	
	/**
	 * 刷新数据用的接口
	 * */
	public interface IReflashListener{
		public void onReflash();
	}
}
