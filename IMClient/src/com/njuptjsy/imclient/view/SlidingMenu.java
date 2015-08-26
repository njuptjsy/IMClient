package com.njuptjsy.imclient.view;

import com.njuptjsy.imclient.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * @author  JSY
 * @version：2015年8月26日 下午10:53:15
 * 类说明：最简单实现侧滑菜单
 */
public class SlidingMenu extends HorizontalScrollView {
	private LinearLayout mWapper;
	private ViewGroup mMenu;
	private ViewGroup mContent;
	private int mScreenWidth;
	private int mMenuRightPadding = 50;//单位dp
	private boolean once = false;
	private int mMenuWidth;
	private boolean isOpen = false;

	/**
	 * 若在布局文件中定义控件属性，切属性中无自定义属性时调用
	 * */
	public SlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}


	/**
	 * 当使用了自定义属性时调用
	 * */
	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//获取屏幕的宽度
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);

		mScreenWidth = outMetrics.widthPixels;
		//把dp转换成px
		mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());

		//获取自定义属性，并赋值给相应的控制视图显示的成员变量
		TypedArray array  = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingMenu, defStyle, 0);
		int n = array.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = array.getIndex(i);
			switch (attr) {
			case R.styleable.SlidingMenu_rightPadding:
				mMenuRightPadding = array.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()));//第二个参数是默认值
				break;
				//如果有很多个属性就在这边添加不同的case分支
			}
		}
		array.recycle();//TypedArray用完一定要释放
	}


	/**
	 * 在代码中new这个view
	 * */
	public SlidingMenu(Context context) {
		this(context, null);
	}



	/**
	 * 测量其内部子View的宽高，最后确定自己的宽高
	 * */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!once) {
			mWapper = (LinearLayout) getChildAt(0);
			mMenu = (ViewGroup) mWapper.getChildAt(0);
			mContent = (ViewGroup) mWapper.getChildAt(1);

			mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
			mContent.getLayoutParams().width = mScreenWidth;
			once = true;
		}


		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	/**
	 * 决定子View摆放的位置
	 * 通过设置偏移量将meun隐藏
	 * */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			this.scrollTo(mMenuWidth, 0);//将menu向左滑动mMenuWidth距离，进行隐藏
		}
	}

	/**
	 * 在手指抬起时判断，当前左侧隐藏宽度是否大于菜单1/2
	 * 若是则消失，若不是菜单完全显示
	 * */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_UP://因为HorizontalScrollView本身对按下和移动操作都有了定义，实现了侧滑菜单效果
			int scrollX = getScrollX();//屏幕左侧未显示的区域的宽度
			if (scrollX >= mMenuWidth/2) {
				this.smoothScrollTo(mMenuWidth, 0);//使用一个动画效果隐藏
				isOpen = false;
			}else {
				this.smoothScrollTo(0, 0);//屏幕左侧不隐藏任何宽度
				isOpen = true;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}
	
	/**
	 *显示侧滑菜单
	 * */
	public void openMenu(){
		if (isOpen) {
			return;
		}
		this.smoothScrollTo(0, 0);
		isOpen = true;
	}
	
	/**
	 * 关闭菜单
	 * */
	public void closeMenu() {
		if (!isOpen) {
			return;
		}
		this.smoothScrollTo(mMenuWidth, 0);
		isOpen = true;
	}
	
	/**
	 * 切换菜单
	 * */
	public void toggle() {
		if (isOpen) {
			closeMenu();
		}else {
			openMenu();
		}
	}
}
