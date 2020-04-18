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

public class MainActivity extends AppCompatActivity
{
	private FragmentActivityRss fragRssItems;
	private FragmentActivityMap fragGMap;

	private ApplicationMainActivity appMainApp = null;

	public MainActivity()
	{
		appMainApp = new ApplicationMainActivity(this);
	}

	// Menu icons are inflated just as they were with actionbar
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mainactivity_toolbar_menu, menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Find the toolbar view inside the activity layout
		Toolbar toolbar = (Toolbar) findViewById(R.id.tb_mainactivity);

		// Sets the Toolbar to act as the ActionBar for this Activity window.
		// Make sure the toolbar exists in the activity and is not null
		setSupportActionBar(toolbar);

		setupNavView();
		setupFragments();
	}


	private void setupNavView()
	{
		BottomNavigationView navView = findViewById(R.id.nav_view_mainactivity);

		// Pass along the navbar event to the appropriate listener
		navView.setOnNavigationItemSelectedListener((@NonNull MenuItem item) ->
		{
			Log.i("NavBarItemChanged", "Selected: " + item.getItemId());
			return appMainApp.invokeNavbarListener(item);
		});
	}

	private void setupFab()
	{
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_ma_MapToggle);

		fab.setOnClickListener((v) ->
		{
			appMainApp.invokeFabListener();
//			boolean isGone = findViewById(R.id.nav_view_mainactivity).getVisibility() == View.GONE;
//			appMainApp.setNavbarVisibility(isGone);
//			if (isGone) appMainApp.setFabIcon(R.drawable.ic_format_list_bulleted_white_64dp);
//			else appMainApp.setFabIcon(R.drawable.ic_map_white_64dp);
		});
	}

	private void setupFragments()
	{
		// Setup fragments
		fragRssItems = new FragmentActivityRss(appMainApp);
		fragGMap = new FragmentActivityMap(appMainApp);

		// Setup the map
		setupFab();

		// Switch to the default view
		transitionFragment(fragRssItems);
	}

//	private void transitionFragment(Fragment newFragment) {
//		// Get the existing fragment and check if we are changing to the same fragment
//		Fragment existingFragment = getSupportFragmentManager().findFragmentById(R.id.frag_mainactivity);
//		if (existingFragment != null && existingFragment == newFragment) {
//			// Notify the fragment we are visible
//			existingFragment.setUserVisibleHint(true);
//			return;
//		}
//
//		// Add the fragment to the activity, pushing this transaction
//		// on to the back stack.
//		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//		ft.setCustomAnimations(R.animator.fragment_slide_left_enter,
//				R.animator.fragment_slide_left_exit,
//				R.animator.fragment_slide_right_enter,
//				R.animator.fragment_slide_right_exit);
//
//		if (existingFragment != null)
//			existingFragment.setUserVisibleHint(false);
//
//		ft.replace(R.id.frag_mainactivity, newFragment);
//		ft.addToBackStack(null);
//		ft.commit();
//
//		newFragment.setUserVisibleHint(true);
//	}

	private void transitionFragment(Fragment newFragment)
	{
		transitionFragment(newFragment, R.id.frag_mainactivity);
	}

	private void transitionFragment(Fragment newFragment, @IdRes int fragmentContainer)
	{
		FragmentManager fm = getSupportFragmentManager();

		// If we don't have a fragment manager
		if (getSupportFragmentManager() == null)
		{
			// Error checking / handling.
			Log.e("MainActivity", "transitionFragment: getSupportFragmentManager() == null");
			throw new RuntimeException("MainActivity: getSupportFragmentManager() == null! Cannot continue operation");
		}

		// Get the existing fragment and check if we are changing to the same fragment
		Fragment existingFragment = fm.findFragmentById(fragmentContainer);

		// If we have an existing fragment and we are swapping to the same one
		if (existingFragment != null && existingFragment == newFragment)
		{
			// Due to an issue of swapping to the map and back if we try to set the fragment to
			//      the same as before it will cause an issue of it not being rendered
			// The only fix to this I have found is to remove it first then re-add it.
			fm.beginTransaction().remove(existingFragment).commit();
		}

		// Notify the existing fragment that we are removing it
		else if (existingFragment != null)
		{
			existingFragment.setUserVisibleHint(false);
		}

		FragmentTransaction ft = fm.beginTransaction();
		ft.setCustomAnimations(R.animator.fragment_slide_left_enter,
				R.animator.fragment_slide_left_exit,
				R.animator.fragment_slide_right_enter,
				R.animator.fragment_slide_right_exit);

		// Add the fragment to the activity, pushing this transaction on to the back stack.
		ft.replace(fragmentContainer, newFragment);
		ft.addToBackStack(null); // TODO: Add to the back stack
		ft.commit();

		newFragment.setUserVisibleHint(true);
	}

	public static class ApplicationMainActivity
	{
		private MainActivity app;
		private IApplicationNavbarListener navbarListener = null;
		private IApplicationFabListener fabListener = null;

		ApplicationMainActivity(MainActivity app)
		{
			this.app = app;
		}

		public void setTitle(String text)
		{
			app.setTitle(text);
		}

		public void setTitle(int textId)
		{
			app.setTitle(textId);
		}

		public void setNavbarVisibility(boolean visibility)
		{
			app.findViewById(R.id.nav_view_mainactivity).setVisibility(visibility ? View.VISIBLE : View.GONE);
		}

		@Nullable
		public MenuItem getNavbarItem()
		{
			BottomNavigationView bottomNavigationView = (BottomNavigationView) app.findViewById(R.id.nav_view_mainactivity);
			if (bottomNavigationView == null) return null;
			return bottomNavigationView.getMenu().findItem(bottomNavigationView.getSelectedItemId());
		}

		public void setNavbarListener(IApplicationNavbarListener navbarListener)
		{
			this.navbarListener = navbarListener;
		}

		private boolean invokeNavbarListener(MenuItem mi)
		{
			if (this.navbarListener != null)
				return this.navbarListener.applicationNavbarClicked(mi);
			return true;
		}

		public void setFabIcon(int resId)
		{
			FloatingActionButton fab = (FloatingActionButton) app.findViewById(R.id.fab_ma_MapToggle);
			if (fab == null) return;

			fab.setImageResource(resId);
		}

		public void setFabListener(IApplicationFabListener fabListener)
		{
			this.fabListener = fabListener;
		}

		private void invokeFabListener()
		{
			if (fabListener != null)
				fabListener.applicationFabClicked();
		}

		public void transitionFragment(Fragment fragment)
		{
			// Switch to rss items
			app.transitionFragment(fragment);
		}

		public void transitionFragment(Fragment fragment, @IdRes int fragmentContainer)
		{
			// Switch to rss items
			app.transitionFragment(fragment, fragmentContainer);
		}

		public FragmentActivityMap getFragmentGMap()
		{
			return app.fragGMap;
		}

		public FragmentActivityRss getFragmentRss()
		{
			return app.fragRssItems;
		}
	}
}