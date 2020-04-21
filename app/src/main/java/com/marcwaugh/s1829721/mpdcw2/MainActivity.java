/*
 * Copyright (c)  2020-2020, Marc Waugh
 *
 * File:		MainActivity.java
 * Module:		Mobile Platform Development
 * Date:		2020
 * Student Id:	S1829721
 *
 * Please note that this copyright header might appear on CC assets such as
 * SVG vector images/icons from 3rd parties. For files such as these their
 * respective copyright notices can be found inside /LICENSES.txt
 */

package com.marcwaugh.s1829721.mpdcw2;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IApplicationFabListener;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IApplicationNavbarListener;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IApplicationPermissionResultListener;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IVisibilityChangedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	private FragmentActivityRss fragRssItems;
	private FragmentActivityMap fragGMap;
	private FragmentCredits fragCredits;
	private FragmentSearch fragSearch;

	private SearchManager mSearchManager;

	private int mCurrentView = 0;
	private final int VIEW_LIST = 0;
	private final int VIEW_MAP = 1;
	private final int VIEW_CREDITS = 2;

	private MainActivityHelper appMainApp = null;

	public MainActivity() {
		appMainApp = new MainActivityHelper(this);
		mSearchManager = new SearchManager();
	}

	public MainActivityHelper getMainActivityHelper() {
		return appMainApp;
	}

	// Menu icons are inflated just as they were with actionbar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mainactivity_toolbar_menu, menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState != null) {
			mCurrentView = savedInstanceState.getInt("currentView", VIEW_LIST);
			mSearchManager = (SearchManager) savedInstanceState.getSerializable("searchManager");

			BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view_mainactivity);
			if (bottomNavigationView != null)
				bottomNavigationView.setSelectedItemId(savedInstanceState.getInt("currentNav", R.id.navigation_roadworks));
		}

		// Find the toolbar view inside the activity layout
		Toolbar toolbar = (Toolbar) findViewById(R.id.tb_mainactivity);

		// Sets the Toolbar to act as the ActionBar for this Activity window.
		// Make sure the toolbar exists in the activity and is not null
		setSupportActionBar(toolbar);

		setupMenus();
		setupFragments();
	}

	private void setupMenus() {
		BottomNavigationView navView = findViewById(R.id.nav_view_mainactivity);

		// Pass along the navbar event to the appropriate listener
		navView.setOnNavigationItemSelectedListener((@NonNull MenuItem item) -> {
			Log.i("NavBarItemChanged", "Selected: " + item.getItemId());
			return appMainApp.invokeNavbarListener(item);
		});
	}

	private void setupFab() {
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_ma_MapToggle);

		fab.setOnClickListener((v) -> {
			appMainApp.invokeFabListener();

//			boolean isGone = findViewById(R.id.nav_view_mainactivity).getVisibility() == View.GONE;
//			appMainApp.setNavbarVisibility(isGone);
//			if (isGone) appMainApp.setFabIcon(R.drawable.ic_format_list_bulleted_white_64dp);
//			else appMainApp.setFabIcon(R.drawable.ic_map_white_64dp);
		});
	}

	private Boolean fabRequiresSetup = true;
	private void setupFragments() {
		// Setup fragments
		if (fragRssItems == null)
			fragRssItems = new FragmentActivityRss();
		if (fragGMap == null)
			fragGMap = new FragmentActivityMap();
		if (fragCredits == null)
			fragCredits = new FragmentCredits();
		if (fragSearch == null)
			fragSearch = new FragmentSearch();


		// Setup the map
		if (fabRequiresSetup) {
			setupFab();
			fabRequiresSetup = false;
		}

		// In landscape we want to display the map and the list
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			transitionFragment(fragRssItems, R.id.frag_mainactivity);
			transitionFragment(fragGMap, R.id.frag_mainactivity_map_landscape);
		}

		// If not landscape just show a single fragment
		else {
			// Switch to the default view
			switch (mCurrentView) {
				case VIEW_LIST:
					transitionFragment(fragRssItems);
					break;
				case VIEW_MAP:
					transitionFragment(fragGMap);
					break;
				case VIEW_CREDITS:
					transitionFragment(fragCredits);
					break;
			}
		}
	}

	private void transitionFragment(Fragment newFragment) {
		transitionFragment(newFragment, R.id.frag_mainactivity);
	}

	@Override
	public void onBackPressed() {
		// Disable back button
		//      this is not needed as the application uses
		//      very few activity changes
		// This also causes issues the way we handle fragments
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		this.appMainApp.invokePermissionListeners(requestCode, permissions, grantResults);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		FragmentManager fm = getSupportFragmentManager();
		Fragment existingFragment = fm.findFragmentById(R.id.frag_mainactivity);

		switch (item.getItemId()) {
			case R.id.toolbarCredits:
				// Check if we are still credits
				if (existingFragment != null && existingFragment == fragCredits) {
					appMainApp.setNavbarVisibility(true);
					transitionFragment(fragRssItems);
				}
				else {
					transitionFragment(fragCredits);
				}

				return true;

			case R.id.toolbarSearch:
				// Check if we are still credits
				if (existingFragment != null && existingFragment == fragSearch) {
					appMainApp.setNavbarVisibility(true);
					transitionFragment(fragRssItems);
				}
				else {
					transitionFragment(fragSearch);
				}
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void transitionFragment(Fragment newFragment, @IdRes int fragmentContainer) {
		View targetHost = findViewById(fragmentContainer);
		if (targetHost == null)
			return;

		FragmentManager fm = getSupportFragmentManager();

		// Get the existing fragment and check if we are changing to the same fragment
		Fragment existingFragment = fm.findFragmentById(fragmentContainer);

		// If we have an existing fragment and we are swapping to the same one
		if (existingFragment != null && existingFragment == newFragment)
			// Due to an issue of swapping to the map and back if we try to set the fragment to
			//      the same as before it will cause an issue of it not being rendered
			// The only fix to this I have found is to remove it first then re-add it.
			fm.beginTransaction().replace(fragmentContainer, new Fragment()).commit();

			// Notify the existing fragment that we are removing it
		else if (existingFragment instanceof IVisibilityChangedListener)
			((IVisibilityChangedListener) existingFragment).onVisibilityChanged(false);

		FragmentTransaction ft = fm.beginTransaction();
		ft.setCustomAnimations(R.animator.fragment_slide_left_enter,
				R.animator.fragment_slide_left_exit,
				R.animator.fragment_slide_right_enter,
				R.animator.fragment_slide_right_exit);

		// Add the fragment to the activity, pushing this transaction on to the back stack.
		ft.replace(fragmentContainer, newFragment);

		if (!(existingFragment != null && existingFragment == newFragment))
			ft.addToBackStack(null);

		ft.commit();

		if (newFragment instanceof IVisibilityChangedListener)
			((IVisibilityChangedListener) newFragment).onVisibilityChanged(true);

		// Save current view
		if (fragmentContainer == R.id.frag_mainactivity) {
			if (newFragment instanceof FragmentActivityMap) {
				mCurrentView = VIEW_MAP;
			}
			else if (newFragment instanceof FragmentCredits) {
				mCurrentView = VIEW_CREDITS;
			}
			else {
				mCurrentView = VIEW_LIST;
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);

		int currentNav = appMainApp.getNavbarItem().getItemId();

		state.putInt("currentView", mCurrentView);
		state.putInt("currentNav", currentNav);
		state.putSerializable("searchManager", mSearchManager);
	}

	public static class MainActivityHelper {
		private MainActivity app;
		private List<IApplicationNavbarListener> navbarListeners = null;
		private List<IApplicationFabListener> fabListeners = null;
		private List<IApplicationPermissionResultListener> permissionListeners = null;

		MainActivityHelper(MainActivity app) {
			this.app = app;

			navbarListeners = new ArrayList<>();
			fabListeners = new ArrayList<>();
			permissionListeners = new ArrayList<>();
		}

		public void setTitle(String text) {
			app.setTitle(text);
		}

		public void setTitle(int textId) {
			app.setTitle(textId);
		}

		public SearchManager getSearchManager() {
			return app.mSearchManager;
		}


		public void setNavbarVisibility(boolean visibility) {
			if (app.findViewById(R.id.nav_view_mainactivity) == null)
				return;

			app.findViewById(R.id.nav_view_mainactivity).setVisibility(visibility ? View.VISIBLE : View.GONE);
		}

		@Nullable
		public MenuItem getNavbarItem() {
			BottomNavigationView bottomNavigationView = (BottomNavigationView) app.findViewById(R.id.nav_view_mainactivity);
			if (bottomNavigationView == null) return null;
			return bottomNavigationView.getMenu().findItem(bottomNavigationView.getSelectedItemId());
		}

		public void addNavbarListener(IApplicationNavbarListener navbarListener) {
			if (navbarListeners.contains(navbarListener)) return;
			navbarListeners.add(navbarListener);
		}

		public void removeNavbarListener(IApplicationNavbarListener navbarListener) {
			navbarListeners.remove(navbarListener);
		}

		private boolean invokeNavbarListener(MenuItem mi) {
			boolean result = false;

			navbarListeners.removeAll(Collections.singleton(null));
			for (IApplicationNavbarListener listener : navbarListeners) {
				if (listener != null && listener.applicationNavbarClicked(mi))
					result = true;
			}

			return result;
		}

		public void setFabVisibility(boolean visibility) {
			if (app.findViewById(R.id.fab_ma_MapToggle) == null)
				return;

			app.findViewById(R.id.fab_ma_MapToggle).setVisibility(visibility ? View.VISIBLE : View.GONE);
		}

		public void setFabIcon(int resId) {
			FloatingActionButton fab = (FloatingActionButton) app.findViewById(R.id.fab_ma_MapToggle);
			if (fab == null) return;

			fab.setImageResource(resId);
		}

		public void addFabListener(IApplicationFabListener fabListener) {
			if (fabListeners.contains(fabListener)) return;
			fabListeners.add(fabListener);
		}

		public void removeFabListener(IApplicationFabListener fabListener) {
			fabListeners.remove(fabListener);
		}

		private void invokeFabListener() {
			fabListeners.removeAll(Collections.singleton(null));
			ArrayList<IApplicationFabListener> list = new ArrayList<>(fabListeners);

			Iterator<IApplicationFabListener> fabIterator = list.iterator();
			while (fabIterator.hasNext()) {
				IApplicationFabListener listener = fabIterator.next();
				if (listener != null)
					listener.applicationFabClicked();
			}
		}

		public void transitionFragment(Fragment fragment) {
			// Switch to rss items
			app.transitionFragment(fragment);
		}

		public void transitionFragment(Fragment fragment, @IdRes int fragmentContainer) {
			// Switch to rss items
			app.transitionFragment(fragment, fragmentContainer);
		}

		public FragmentActivityMap getFragmentGMap() {
			return app.fragGMap;
		}

		public FragmentActivityRss getFragmentRss() {
			return app.fragRssItems;
		}

		public MainActivity getMainActivity() {
			return app;
		}

		public void addPermissionListener(IApplicationPermissionResultListener resultListener) {
			if (permissionListeners.contains(resultListener)) return;
			permissionListeners.add(resultListener);
		}

		public void removePermissionListener(IApplicationPermissionResultListener resultListener) {
			if (resultListener != null)
				permissionListeners.remove(resultListener);
		}

		private void invokePermissionListeners(int requestCode, String[] permissions, int[] grantResults) {
			permissionListeners.removeAll(Collections.singleton(null));
			for (IApplicationPermissionResultListener listener : permissionListeners) {
				if (listener != null)
					listener.onRequestPermissionResult(requestCode, permissions, grantResults);
			}
		}
	}
}
