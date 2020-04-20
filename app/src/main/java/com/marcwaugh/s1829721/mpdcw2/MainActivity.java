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

import android.os.Bundle;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {
	private FragmentActivityRss fragRssItems;
	private FragmentActivityMap fragGMap;
	private FragmentCredits fragCredits;

	private MainActivityHelper appMainApp = null;

	public MainActivity() {
		appMainApp = new MainActivityHelper(this);
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

	private void setupFragments() {
		// Setup fragments
		if (fragRssItems == null)
			fragRssItems = new FragmentActivityRss();
		if (fragGMap == null)
			fragGMap = new FragmentActivityMap();
		if (fragCredits == null)
			fragCredits = new FragmentCredits();

		// Setup the map
		setupFab();

		// Switch to the default view
		transitionFragment(fragRssItems);
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
		switch (item.getItemId()) {
			case R.id.toolbarCredits:
				transitionFragment(fragCredits);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void transitionFragment(Fragment newFragment, @IdRes int fragmentContainer) {
		FragmentManager fm = getSupportFragmentManager();

		// Get the existing fragment and check if we are changing to the same fragment
		Fragment existingFragment = fm.findFragmentById(fragmentContainer);

		// If we have an existing fragment and we are swapping to the same one
		if (existingFragment != null && existingFragment == newFragment)
			// Due to an issue of swapping to the map and back if we try to set the fragment to
			//      the same as before it will cause an issue of it not being rendered
			// The only fix to this I have found is to remove it first then re-add it.
			//fm.beginTransaction().remove(existingFragment).commit();
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
	}

	public static class MainActivityHelper {
		private MainActivity app;
		private IApplicationNavbarListener navbarListener = null;
		private IApplicationFabListener fabListener = null;
		private List<IApplicationPermissionResultListener> permissionListeners = null;

		MainActivityHelper(MainActivity app) {
			this.app = app;

			permissionListeners = new ArrayList<IApplicationPermissionResultListener>();
		}

		public void setTitle(String text) {
			app.setTitle(text);
		}

		public void setTitle(int textId) {
			app.setTitle(textId);
		}

		public void setNavbarVisibility(boolean visibility) {
			app.findViewById(R.id.nav_view_mainactivity).setVisibility(visibility ? View.VISIBLE : View.GONE);
		}

		@Nullable
		public MenuItem getNavbarItem() {
			BottomNavigationView bottomNavigationView = (BottomNavigationView) app.findViewById(R.id.nav_view_mainactivity);
			if (bottomNavigationView == null) return null;
			return bottomNavigationView.getMenu().findItem(bottomNavigationView.getSelectedItemId());
		}

		public void setNavbarListener(IApplicationNavbarListener navbarListener) {
			this.navbarListener = navbarListener;
		}

		private boolean invokeNavbarListener(MenuItem mi) {
			if (this.navbarListener != null)
				return this.navbarListener.applicationNavbarClicked(mi);
			return true;
		}

		public void setFabIcon(int resId) {
			FloatingActionButton fab = (FloatingActionButton) app.findViewById(R.id.fab_ma_MapToggle);
			if (fab == null) return;

			fab.setImageResource(resId);
		}

		public void setFabListener(IApplicationFabListener fabListener) {
			this.fabListener = fabListener;
		}

		private void invokeFabListener() {
			if (fabListener != null)
				fabListener.applicationFabClicked();
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
