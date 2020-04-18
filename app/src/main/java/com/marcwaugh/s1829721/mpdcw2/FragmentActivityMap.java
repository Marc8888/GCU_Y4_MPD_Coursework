package com.marcwaugh.s1829721.mpdcw2;

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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IApplicationFabListener;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IApplicationNavbarListener;
import com.marcwaugh.s1829721.mpdcw2.ui.rss_list.RssItemFragment;

public class FragmentActivityMap extends Fragment implements OnMapReadyCallback, IApplicationFabListener, IApplicationNavbarListener
{
	private View fragmentView = null;
	private boolean isVisible = false;
	private GoogleMap mMap;
	private MainActivity.ApplicationMainActivity mainActivity;


	public FragmentActivityMap(MainActivity.ApplicationMainActivity appMainApp)
	{
		this.mainActivity = appMainApp;
	}

	public View onCreateView(@NonNull LayoutInflater inflater,
	                         @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState)
	{
		fragmentView = inflater.inflate(R.layout.fragment_activity_map, container, false);

		SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag_map_gmap);

		assert mapFragment != null;
		mapFragment.getMapAsync(this);

		return fragmentView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		super.setUserVisibleHint(isVisibleToUser);
		Log.i("FragmentActivityRss", "setUserVisibleHint: " + isVisibleToUser);

		if (isVisibleToUser)
		{
			mainActivity.setFabIcon(R.drawable.ic_format_list_bulleted_white_64dp);
			mainActivity.setFabListener(this);
			mainActivity.setNavbarListener(this);

			// Invoke the event to load the correct items
			MenuItem mi = mainActivity.getNavbarItem();
			if (mi != null) applicationNavbarClicked(mi);
		}
		else
		{
			// Cleanup
			mainActivity.setFabListener(null);
			mainActivity.setNavbarListener(null);
		}

		isVisible = isVisibleToUser;
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
	public void onMapReady(GoogleMap googleMap)
	{
		mMap = googleMap;

		// Add a marker in Sydney and move the camera
		LatLng sydney = new LatLng(-34, 151);
		mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
		mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
	}

	@Override
	public void applicationFabClicked()
	{
		// Swap to the list view
		mainActivity.transitionFragment(mainActivity.getFragmentRss());
	}

	@Override
	public boolean applicationNavbarClicked(MenuItem menuItem)
	{
		int id = menuItem.getItemId();
		switch (id)
		{
			case R.id.navigation_roadworks:
				Log.i("RssNavBarItemChanged", "Selected: Roadworks, Id = " + id);
				mainActivity.setTitle("Roadworks");
				break;

			case R.id.navigation_planned_roadworks:
				Log.i("RssNavBarItemChanged", "Selected: Planned Roadworks, Id = " + id);
				mainActivity.setTitle("Planned Roadworks");
				break;

			case R.id.navigation_current_incidents:
				Log.i("RssNavBarItemChanged", "Selected: Current Incidents, Id = " + id);
				mainActivity.setTitle("Current Incidents");
				break;

			default:
				Log.e("RssNavBarItemChanged", "Unknown selected item Id = " + id + ", Name = " + getResources().getResourceEntryName(id));
				throw new RuntimeException("FragmentActivityRss: Unknown target load fragment id: " + id);
		}

		return true;
	}

	private void loadMarkersFromRss(RssItemFragment rssItems, @DrawableRes int id)
	{

	}
}
