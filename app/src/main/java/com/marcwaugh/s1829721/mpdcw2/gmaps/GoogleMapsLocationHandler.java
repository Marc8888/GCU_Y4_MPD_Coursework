/*
 * Copyright (c)  2020-2020, Marc Waugh
 *
 * File:		GoogleMapsLocationHandler.java
 * Module:		Mobile Platform Development
 * Date:		2020
 * Student Id:	S1829721
 *
 * Please note that this copyright header might appear on CC assets such as
 * SVG vector images/icons from 3rd parties. For files such as these their
 * respective copyright notices can be found inside /LICENSES.txt
 */

package com.marcwaugh.s1829721.mpdcw2.gmaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.marcwaugh.s1829721.mpdcw2.MainActivity;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IApplicationPermissionResultListener;

public class GoogleMapsLocationHandler
		implements IApplicationPermissionResultListener {
	private static final int PERM_REQUEST_LOCATION = 99;
	private IFragmentLocationWantingMap mMapFragment;

	public GoogleMapsLocationHandler(IFragmentLocationWantingMap fragmentMap) {
		this.mMapFragment = fragmentMap;

		// Register the permission listener
		mMapFragment.getApplication().addPermissionListener(this);
	}

	protected void finalize() {
		// Remove the permission listener
		mMapFragment.getApplication().removePermissionListener(this);
	}

	public void onMapReady() {
		// Get the google maps instance
		GoogleMap map = mMapFragment.getGMapInstance();

		// Check if we need to ask for permission,
		//      if not then just enable location services.
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(mMapFragment.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
					== PackageManager.PERMISSION_GRANTED) {
				// Location Permission already granted, enable services
				map.setMyLocationEnabled(true);
			}
			else {
				// Request Location Permission
				checkLocationPermission();
			}
		}
		else {
			// Enable location services
			map.setMyLocationEnabled(true);
		}
	}


	private void checkLocationPermission() {
		// Get context and activity
		Context ctx = mMapFragment.getContext();
		MainActivity activity = mMapFragment.getApplication().getMainActivity();

		if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			// Should a permission explanation be displayed
			//  eg    "Let this application show your location"
			// why -> "We want to display your location on a route"
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
					Manifest.permission.ACCESS_FINE_LOCATION)) {

				// Show an explanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.
				new AlertDialog.Builder(ctx)
						.setTitle("Location Permission Needed")
						.setMessage("This application would like to display your current location on the map, " +
								"To do this we will need to see your current location.")
						.setPositiveButton("OK", (dialogInterface, i) ->
						{
							//Prompt the user once explanation has been shown
							ActivityCompat.requestPermissions(activity,
									new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
									PERM_REQUEST_LOCATION);
						})
						.create()
						.show();
			}
			else {
				// No explanation needed, we can request the permission.
				ActivityCompat.requestPermissions(mMapFragment.getApplication().getMainActivity(),
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						PERM_REQUEST_LOCATION);
			}
		}
	}

	@Override
	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
		// If the request is our perm id.
		if (requestCode == PERM_REQUEST_LOCATION) {
			// If the results are empty then the request was denied,
			//   if not check if our permission is granted
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission was granted
				if (ContextCompat.checkSelfPermission(mMapFragment.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
						== PackageManager.PERMISSION_GRANTED)
					mMapFragment.getGMapInstance().setMyLocationEnabled(true);
			}
			else {
				// Permission has been denied, disable MyLocation.
				mMapFragment.getGMapInstance().setMyLocationEnabled(false);

				Toast.makeText(mMapFragment.getContext(), "Location permission denied", Toast.LENGTH_LONG).show();
			}
		}
	}
}
