/*
 * Copyright (c)  2020-2020, Marc Waugh
 *
 * File:		FragmentActivityRss.java
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IApplicationFabListener;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IApplicationNavbarListener;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IVisibilityChangedListener;
import com.marcwaugh.s1829721.mpdcw2.ui.rss_list.RssItemFragment;
import com.marcwaugh.s1829721.mpdcw2.xml.RssItem;

public class FragmentActivityRss extends Fragment
		implements RssItemFragment.OnListFragmentInteractionListener, IApplicationNavbarListener, IApplicationFabListener, IVisibilityChangedListener
{
	private static final String urlTSRoadWorks = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
	private static final String urlTSRoadWorksPlanned = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
	private static final String urlTSCurrentIncidents = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";


	private RssItemFragment fragRssRoadworks = null;
	private RssItemFragment fragRssRoadworksPlanned = null;
	private RssItemFragment fragRssCIncidents = null;
	private MainActivity.ApplicationMainActivity mainActivity;
	private boolean isVisible = false;
	private int targetLoadFragment = -1;

	public FragmentActivityRss(MainActivity.ApplicationMainActivity appMainApp)
	{
		this.mainActivity = appMainApp;

		fragRssRoadworks = RssItemFragment.newInstance(this, urlTSRoadWorks);
		fragRssCIncidents = RssItemFragment.newInstance(this, urlTSCurrentIncidents);
		fragRssRoadworksPlanned = RssItemFragment.newInstance(this, urlTSRoadWorksPlanned);
	}

	public RssItemFragment getFragRssRoadworks()
	{
		return fragRssRoadworks;
	}

	public RssItemFragment getFragRssRoadworksPlanned()
	{
		return fragRssRoadworksPlanned;
	}

	public RssItemFragment getFragRssCIncidents()
	{
		return fragRssCIncidents;
	}

	@Override
	public void onListFragmentInteraction(RssItem item)
	{
		// TODO: View on map split with information
	}

	@Override
	public void onVisibilityChanged(boolean isVisibleToUser)
	{
		Log.i("FragmentActivityRss", "onVisibilityChanged: " + isVisibleToUser);

		if (isVisibleToUser)
		{
			// Set the icon and listener
			mainActivity.setFabIcon(R.drawable.ic_map_white_64dp);
			mainActivity.setNavbarListener(this);
			mainActivity.setFabListener(this);

			// Invoke the event to load the correct items
			MenuItem mi = mainActivity.getNavbarItem();
			if (mi != null)
			{
				// Set the loading fragment
				targetLoadFragment = mi.getItemId();
				applicationNavbarClicked(mi);
			}
		}
		else
		{
			// Cleanup
			mainActivity.setFabListener(null);
			mainActivity.setNavbarListener(null);

			// Notify the child fragment that it is not visible
			if (getActivity().getSupportFragmentManager() != null)
			{
				Fragment existingFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.frag_rss_nav_host);
				if (existingFragment != null)
				{
					existingFragment.setUserVisibleHint(false);
				}
			}
		}

		isVisible = isVisibleToUser;
	}

	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_activity_rss, container, false);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// Setup fragments
		setupFragments();
	}

	private void setupFragments()
	{
		// Switch to the default view
		if (targetLoadFragment == -1)
			switchFragmentTo(R.id.navigation_roadworks);
		else switchFragmentTo(targetLoadFragment);
	}

	private void switchFragmentTo(int id)
	{
		switch (id)
		{
			case R.id.navigation_roadworks:
				Log.i("RssNavBarItemChanged", "Selected: Roadworks, Id = " + id);
				mainActivity.setTitle("Roadworks");
				mainActivity.transitionFragment(fragRssRoadworks, R.id.frag_rss_nav_host);
				break;

			case R.id.navigation_planned_roadworks:
				Log.i("RssNavBarItemChanged", "Selected: Planned Roadworks, Id = " + id);
				mainActivity.setTitle("Planned Roadworks");
				mainActivity.transitionFragment(fragRssRoadworksPlanned, R.id.frag_rss_nav_host);
				break;

			case R.id.navigation_current_incidents:
				Log.i("RssNavBarItemChanged", "Selected: Current Incidents, Id = " + id);
				mainActivity.setTitle("Current Incidents");
				mainActivity.transitionFragment(fragRssCIncidents, R.id.frag_rss_nav_host);
				break;

			default:
				Log.e("RssNavBarItemChanged", "Unknown selected item Id = " + id + ", Name = " + getResources().getResourceEntryName(id));
				throw new RuntimeException("FragmentActivityRss: Unknown target load fragment id: " + id);
		}
	}

	@Override
	public boolean applicationNavbarClicked(MenuItem menuItem)
	{
		// Switch to the menu item
		switchFragmentTo(menuItem.getItemId());
		return true;
	}

	@Override
	public void applicationFabClicked()
	{
		// Swap to map view
		mainActivity.transitionFragment(mainActivity.getFragmentGMap());
	}
}
