/*
 * Copyright (c)  2020-2020, Marc Waugh
 *
 * File:		FragmentActivityMap.java
 * Module:		Mobile Platform Development
 * Date:		2020
 * Student Id:	S1829721
 *
 * Please note that this copyright header might appear on CC assets such as
 * SVG vector images/icons from 3rd parties. For files such as these their
 * respective copyright notices can be found inside /LICENSES.txt
 */

package com.marcwaugh.s1829721.mpdcw2;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.marcwaugh.s1829721.mpdcw2.gmaps.GmapClusterItem;
import com.marcwaugh.s1829721.mpdcw2.gmaps.GmapClusterItemRenderer;
import com.marcwaugh.s1829721.mpdcw2.gmaps.GoogleMapsLocationHandler;
import com.marcwaugh.s1829721.mpdcw2.gmaps.IFragmentLocationWantingMap;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IApplicationFabListener;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IApplicationNavbarListener;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IVisibilityChangedListener;
import com.marcwaugh.s1829721.mpdcw2.ui.rss_list.RssItemFragment;
import com.marcwaugh.s1829721.mpdcw2.xml.RssItem;

import java.util.Date;
import java.util.List;

public class FragmentActivityMap extends Fragment
		implements OnMapReadyCallback, IApplicationFabListener, IApplicationNavbarListener, IVisibilityChangedListener, IFragmentLocationWantingMap {
	private Date lastTransitionDate;
	private boolean mapReady = false;
	private GoogleMap mMap;
	private GoogleMapsLocationHandler locationHandler;
	private ClusterManager<GmapClusterItem> mClusterManager;

	private MainActivity.MainActivityHelper mainActivity;
	private RssItemFragment rssItemFragment = null;

	@DrawableRes
	private int rssItemIcon = 0;

	public FragmentActivityMap() {
		this.lastTransitionDate = new Date();
		Log.i("FragmentActivityMap", "Constructor");
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof MainActivity) {
			MainActivity activity = (MainActivity) context;

			initializeController(activity.getMainActivityHelper());
			Log.i("FragmentActivityRss", "onAttach -> MainActivity");
			onVisibilityChanged(true);
		}
	}

	private void initializeController(MainActivity.MainActivityHelper helper) {
		this.mainActivity = helper;
		this.locationHandler = new GoogleMapsLocationHandler(this);
	}

	public View onCreateView(@NonNull LayoutInflater inflater,
	                         @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_activity_map, container, false);

		SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag_map_gmap);

		assert mapFragment != null;
		mapFragment.getMapAsync(this);

		return fragmentView;
	}

	@Override
	public void onVisibilityChanged(boolean isVisibleToUser) {
		if (mainActivity == null) return;
		Log.i("FragmentActivityRss", "setUserVisibleHint: " + isVisibleToUser);

		if (isVisibleToUser) {
			mainActivity.setFabIcon(R.drawable.ic_format_list_bulleted_white_64dp);
			mainActivity.setFabListener(this);
			mainActivity.setNavbarListener(this);

			// Invoke the event to load the correct items
			MenuItem mi = mainActivity.getNavbarItem();
			if (mi != null) applicationNavbarClicked(mi);
		}
		else {
			// Cleanup
			mainActivity.setFabListener(null);
			mainActivity.setNavbarListener(null);

			rssItemFragment = null;
			rssItemIcon = 0;

			mapReady = false;
		}
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.clear();

		if (getContext() == null)
			throw new RuntimeException("FragmentActivityMap: Context is null!");

		this.locationHandler.onMapReady();

		// Create a cluster manager and it's renderer
		mClusterManager = new ClusterManager<>(getContext(), mMap);
		ClusterRenderer clusterRenderer = new GmapClusterItemRenderer(getContext(), googleMap, mClusterManager);

		// Point the map's listeners at the listeners implemented by the cluster
		// manager.
		mMap.setOnCameraIdleListener(mClusterManager);
		mMap.setOnMarkerClickListener(mClusterManager);

		loadMarkersFromRss(rssItemFragment, rssItemIcon);
		mapReady = true;
	}

	@Override
	public void applicationFabClicked() {
		// Limit the list / map changing to every few second(s), this will stop any potential crashes
		Date now = new Date();
		long milliseconds = (now.getTime() - lastTransitionDate.getTime());// / 1000 % 60;
		if (milliseconds > 1000) {
			lastTransitionDate = new Date();

			// Swap to the list view
			mainActivity.transitionFragment(mainActivity.getFragmentRss());
		}
	}

	@Override
	public boolean applicationNavbarClicked(MenuItem menuItem) {
		int id = menuItem.getItemId();

		switch (id) {
			case R.id.navigation_roadworks:
				Log.i("RssNavBarItemChanged", "Selected: Roadworks, Id = " + id);
				mainActivity.setTitle("Roadworks");
				rssItemFragment = mainActivity.getFragmentRss().getFragRssRoadworks();
				rssItemIcon = R.drawable.ic_svg_noun_under_construction;
				break;

			case R.id.navigation_planned_roadworks:
				Log.i("RssNavBarItemChanged", "Selected: Planned Roadworks, Id = " + id);
				mainActivity.setTitle("Planned Roadworks");
				rssItemFragment = mainActivity.getFragmentRss().getFragRssRoadworksPlanned();
				rssItemIcon = R.drawable.ic_svg_noun_calendar;
				break;

			case R.id.navigation_current_incidents:
				Log.i("RssNavBarItemChanged", "Selected: Current Incidents, Id = " + id);
				mainActivity.setTitle("Current Incidents");
				rssItemFragment = mainActivity.getFragmentRss().getFragRssCIncidents();
				rssItemIcon = R.drawable.ic_svg_noun_road;
				break;

			default:
				Log.e("RssNavBarItemChanged", "Unknown selected item Id = " + id + ", Name = " + getResources().getResourceEntryName(id));
				throw new RuntimeException("FragmentActivityRss: Unknown target load fragment id: " + id);
		}

		// If the map has already loaded load new markers
		if (mapReady)
			loadMarkersFromRss(rssItemFragment, rssItemIcon);

		return true;
	}

	@Override
	public void onStart() {
		super.onStart();

		// If the map has already loaded load new markers
		if (mapReady)
			loadMarkersFromRss(rssItemFragment, rssItemIcon);
	}

	private void loadMarkersFromRss(RssItemFragment rssItems, @DrawableRes int drawableResourceId) {
		if (rssItems == null || drawableResourceId == -1 || drawableResourceId == 0)
			//throw new RuntimeException("FragmentActivityMap: RssItems or Drawable id is null!");
			return;

		// Get the items
		List<RssItem> listRssItems = rssItems.getRssItems();
		if (listRssItems == null) return;

		// Clear markers from cluster
		mClusterManager.clearItems();

		// Check if we have no items, if none then just return
		if (listRssItems.size() == 0) {
			mClusterManager.cluster();
			return;
		}

		// Calculate bounds to autozoom the map
		LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

		// Display markers
		BitmapDescriptor bdf = GmapClusterItemRenderer.BitmapDescriptorFromVector(getContext(), drawableResourceId);

		for (RssItem rss : listRssItems) {
			LatLng location = new LatLng(rss.getGeorssLat(), rss.getGeorssLng());
			GmapClusterItem clusterItem = new GmapClusterItem(
					location,
					rss.getTitle(),
					rss.getDescription_Cleaned(),
					bdf);

			// Add the marker to the cluster manager
			mClusterManager.addItem(clusterItem);

			// Add the latlng to the builder to create the bounds
			boundsBuilder.include(location);
		}

		// Render the cluster items
		mClusterManager.cluster();

		// Get the bounds
		LatLngBounds bounds = boundsBuilder.build();

		// Animate the camera zoom
		int padding = 100; // Offset from edges of the map in pixels
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
		mMap.animateCamera(cameraUpdate);
	}

	@Override
	public GoogleMap getGMapInstance() {
		return mMap;
	}

	@Override
	public MainActivity.MainActivityHelper getApplication() {
		return mainActivity;
	}
}
