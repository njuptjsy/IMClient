package com.njuptjsy.imclient;import java.lang.reflect.Field;import java.lang.reflect.Method;import java.util.ArrayList;import java.util.List;import android.os.Bundle;import android.support.v4.app.Fragment;import android.support.v4.app.FragmentActivity;import android.support.v4.app.FragmentPagerAdapter;import android.support.v4.view.ViewPager;import android.support.v4.view.ViewPager.OnPageChangeListener;import android.util.Log;import android.view.Gravity;import android.view.KeyEvent;import android.view.Menu;import android.view.View;import android.view.View.OnClickListener;import android.view.ViewConfiguration;import android.view.Window;import android.widget.Toast;public class MainActivity extends FragmentActivity implements OnClickListener,OnPageChangeListener{	private ViewPager mViewPager;	private List<Fragment> mTabs = new ArrayList<Fragment>();	private String[] mTitles = new String[]{"Third Fragment !","Fourth Fragment !"};	private FragmentPagerAdapter mAdapter;	private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();	private ChatFragment chatFragment;	private View MainView,chatView;	private View currentView;	private long firstTime;	@Override	protected void onCreate(Bundle savedInstanceState)	{		super.onCreate(savedInstanceState);		setContentView(R.layout.activity_main);		setOverflowButtonAlways();		getActionBar().setDisplayShowHomeEnabled(false);		initView();		initDatas();		mViewPager.setAdapter(mAdapter);		initEvent();		MainView = getLayoutInflater().inflate(R.layout.activity_main, null);		Log.i("MainActivity:onCreate", "currentView is chatView " + chatView);		currentView = MainView;	}		/**	 * 初始化所有事件	 */	private void initEvent()	{		mViewPager.setOnPageChangeListener(this);	}	private void initDatas()	{		SessionFragment sessionFragment = new SessionFragment();		mTabs.add(sessionFragment);		chatFragment = new ChatFragment();		mTabs.add(chatFragment);		for (String title : mTitles)		{			TabFragment tabFragment = new TabFragment();			Bundle bundle = new Bundle();			bundle.putString(TabFragment.TITLE, title);			tabFragment.setArguments(bundle);			mTabs.add(tabFragment);		}		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())		{			@Override			public int getCount()			{				return mTabs.size();			}			@Override			public Fragment getItem(int position)			{				return mTabs.get(position);			}		};	}	private void initView()	{		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);		ChangeColorIconWithText one = (ChangeColorIconWithText) findViewById(R.id.id_indicator_one);		mTabIndicators.add(one);		ChangeColorIconWithText two = (ChangeColorIconWithText) findViewById(R.id.id_indicator_two);		mTabIndicators.add(two);		ChangeColorIconWithText three = (ChangeColorIconWithText) findViewById(R.id.id_indicator_three);		mTabIndicators.add(three);		ChangeColorIconWithText four = (ChangeColorIconWithText) findViewById(R.id.id_indicator_four);		mTabIndicators.add(four);		one.setOnClickListener(this);		two.setOnClickListener(this);		three.setOnClickListener(this);		four.setOnClickListener(this);		one.setIconAlpha(1.0f);	}	@Override	public boolean onCreateOptionsMenu(Menu menu)	{		getMenuInflater().inflate(R.menu.main, menu);		return true;	}	private void setOverflowButtonAlways()	{		try		{			ViewConfiguration config = ViewConfiguration.get(this);			Field menuKey = ViewConfiguration.class					.getDeclaredField("sHasPermanentMenuKey");			menuKey.setAccessible(true);			menuKey.setBoolean(config, false);		} catch (Exception e)		{			e.printStackTrace();		}	}	/**	 * 设置menu显示icon	 */	@Override	public boolean onMenuOpened(int featureId, Menu menu)	{		if (featureId == Window.FEATURE_ACTION_BAR && menu != null)		{			if (menu.getClass().getSimpleName().equals("MenuBuilder"))			{				try				{					Method m = menu.getClass().getDeclaredMethod(							"setOptionalIconsVisible", Boolean.TYPE);					m.setAccessible(true);					m.invoke(menu, true);				} catch (Exception e)				{					e.printStackTrace();				}			}		}		return super.onMenuOpened(featureId, menu);	}	@Override	public void onClick(View v)	{		clickTab(v);	}	/**	 * 点击Tab按钮	 * 	 * @param v	 */	private void clickTab(View v)	{		resetOtherTabs();		switch (v.getId())		{		case R.id.id_indicator_one:			mTabIndicators.get(0).setIconAlpha(1.0f);			mViewPager.setCurrentItem(0, false);			break;		case R.id.id_indicator_two:			mTabIndicators.get(1).setIconAlpha(1.0f);			mViewPager.setCurrentItem(1, false);			break;		case R.id.id_indicator_three:			mTabIndicators.get(2).setIconAlpha(1.0f);			mViewPager.setCurrentItem(2, false);			break;		case R.id.id_indicator_four:			mTabIndicators.get(3).setIconAlpha(1.0f);			mViewPager.setCurrentItem(3, false);			break;		}	}	/**	 * 重置其他的TabIndicator的颜色	 */	private void resetOtherTabs()	{		for (int i = 0; i < mTabIndicators.size(); i++)		{			mTabIndicators.get(i).setIconAlpha(0);		}	}	@Override	public void onPageScrolled(int position, float positionOffset,int positionOffsetPixels)	{		// Log.e("TAG", "position = " + position + " ,positionOffset =  "		// + positionOffset);		if (positionOffset > 0)		{			ChangeColorIconWithText left = mTabIndicators.get(position);			ChangeColorIconWithText right = mTabIndicators.get(position + 1);			left.setIconAlpha(1 - positionOffset);			right.setIconAlpha(positionOffset);		}	}	@Override	public void onPageSelected(int position)	{		// TODO Auto-generated method stub	}	@Override	public void onPageScrollStateChanged(int state)	{		// TODO Auto-generated method stub	}	public void toChatFragmet(Fragment oldFragment,int which){		oldFragment.getView().getParent();		setActiveView(chatFragment.getView());		Log.i("MainActivity:toChatFragmet", "currentView is chatView " + chatFragment.getView());		//TODO 使用fragment实现界面切换和回退		//		FragmentManager fragmentManager = getSupportFragmentManager();		//		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();		//		chatFragment = new ChatFragment();		//		fragmentTransaction.replace(R.id.id_viewpager, chatFragment);		//		fragmentTransaction.addToBackStack(null);		//		fragmentTransaction.commit();	}	@Override	public boolean onKeyDown(int keyCode,KeyEvent event){		switch (keyCode) {		case KeyEvent.KEYCODE_BACK:			if (currentView == chatView) {				Log.i("MainActivity:onKeyDown", "currentView is chatView");				setActiveView(MainView);				return true;			}			if (currentView == MainView) {				long secondTime = System.currentTimeMillis();   				if (secondTime - firstTime > 2000) {//If two keys interval greater than 2 seconds,then not quit program					Toast toast = Toast.makeText(this, MainActivity.this.getString(R.string.exit_progress), Toast.LENGTH_SHORT);  					toast.setGravity(Gravity.CENTER, 0, 0);					toast.show();					firstTime = secondTime;//update firstTime  					return true;   				} else {//If two keys interval less  than 2 seconds,then quit program					System.exit(0);  				}   			}		}		return super.onKeyDown(keyCode, event);	}	private void setActiveView(View view) {		setContentView(view);		currentView = view;	}		public void setChatView(View chatView){		this.chatView = chatView;	}}