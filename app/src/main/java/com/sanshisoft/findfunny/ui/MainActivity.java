package com.sanshisoft.findfunny.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sanshisoft.findfunny.R;
import com.sanshisoft.findfunny.adapter.DrawerAdapter;
import com.sanshisoft.findfunny.model.Category;
import com.sanshisoft.findfunny.ui.fragment.AboutFragment;
import com.sanshisoft.findfunny.ui.fragment.HighdotFragment;
import com.sanshisoft.findfunny.ui.fragment.SettingsFragment;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mToggle;
	private ListView mDrawerList;
	private CharSequence mDrawerTitle;
	private List<Category> mCategoryList;
	private DrawerAdapter mAdapter;
	private int mDrawerIndex = 0;
	private String[] mDrawerTitles;
	private TypedArray mDrawerIcons; 

	private OnMyRefreshListener mMyListener;

	public interface OnMyRefreshListener {
		void onRefreshData();
	}

	public void setOnMyRefreshListener(OnMyRefreshListener listener) {
		this.mMyListener = listener;
	}

	public static final String ARG_DRAWER_NUMBER = "arg_drawer_number";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDrawerTitle = getTitle();

		// 初始化
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mCategoryList = new ArrayList<Category>();
		// 初始化侧边栏数据
		mDrawerTitles = getResources().getStringArray(R.array.drawer_array);
		mDrawerIcons = getResources().obtainTypedArray(R.array.drawer_icons);
		for(int i=0;i<mDrawerTitles.length;i++){
			mCategoryList.add(new Category(mDrawerTitles[i], mDrawerIcons.getResourceId(i, -(i+1))));
		}
		mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				setTitle(mDrawerTitle);
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				int i = mDrawerList.getCheckedItemPosition();
				setTitle(getResources().getStringArray(R.array.drawer_array)[i]);
			}
		};
		mDrawerLayout.setDrawerListener(mToggle);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mAdapter = new DrawerAdapter(mCategoryList);
		mDrawerList.setAdapter(mAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		if (savedInstanceState == null) {
			selectItem(0);
		}
		
		MobclickAgent.openActivityDurationTrack(false);
		
		UmengUpdateAgent.update(this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (mToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.action_refresh:
			mMyListener.onRefreshData();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int i,
				long l) {
			selectItem(i);
			mDrawerIndex = i;
			mAdapter.setPos(i);
		}

	}

	private void selectItem(int position) {
		Fragment f = null;
		switch (position) {
		case 0:
			f = new HighdotFragment();
			break;
		case 1:
			f = new SettingsFragment();
			break;
		case 2:
			f = new AboutFragment();
			break;
		default:
			break;
		}
		FragmentManager fm = getFragmentManager();
		Bundle args = new Bundle();
		args.putInt(ARG_DRAWER_NUMBER, position);
		f.setArguments(args);
		fm.beginTransaction().replace(R.id.content_frame, f).commit();
		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);
		invalidateOptionsMenu();
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("是否退出找亮点？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setNegativeButton("返回", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();
		// super.onBackPressed();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("MainActivity"); 
	    MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("MainActivity"); 
	    MobclickAgent.onPause(this);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem item = menu.findItem(R.id.action_refresh);
		if (mDrawerIndex != 0) {
			item.setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}
}
