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
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IApplicationPermissionResultListener;

public class GoogleMapsLocationHandler
		implements IApplicationPermissionResultListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
	private static final int PERM_REQUEST_LOCATION = 99;

	IFragmentLocationWantingMap mMap;
	SupportMapFragment mapFrag;
	LocationRequest mLocationRequest;
	GoogleApiClient mGoogleApiClient;
	Location mLastLocation;

	public GoogleMapsLocationHandler(IFragmentLocationWantingMap fragmentMap)
	{
		this.mMap = fragmentMap;
	}

	public void onMapReady()
	{
		GoogleMap map = mMap.getGMapInstance();

		//Initialize Google Play Services
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			if (ContextCompat.checkSelfPermission(mMap.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
					== PackageManager.PERMISSION_GRANTED)
			{
				//Location Permission already granted
				buildGoogleApiClient();
				map.setMyLocationEnabled(true);
			}
			else
			{
				//Request Location Permission
				checkLocationPermission();
			}
		}
		else
		{
			buildGoogleApiClient();
			map.setMyLocationEnabled(true);
		}
	}


	private void checkLocationPermission()
	{
		Context ctx = mMap.getContext();
		if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED)
		{
			// Should a permission explanation be displayed
			//  eg    "Let this application show your location"
			// why -> "We want to display your location on a route"
			if (ActivityCompat.shouldShowRequestPermissionRationale(mMap.getMainActivity(),
					Manifest.permission.ACCESS_FINE_LOCATION))
			{

				// Show an explanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.
				new AlertDialog.Builder(mMap.getContext())
						.setTitle("Location Permission Needed")
						.setMessage("This app needs the Location permission, please accept to use location functionality")
						.setPositiveButton("OK", (dialogInterface, i) ->
						{
							//Prompt the user once explanation has been shown
							ActivityCompat.requestPermissions(mMap.getMainActivity(),
									new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
									PERM_REQUEST_LOCATION);
						})
						.create()
						.show();
			}
			else
			{
				// No explanation needed, we can request the permission.
				ActivityCompat.requestPermissions(mMap.getMainActivity(),
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						PERM_REQUEST_LOCATION);
			}
		}
	}

	private synchronized void buildGoogleApiClient()
	{
		mGoogleApiClient = new GoogleApiClient.Builder(mMap.getContext())
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnected(@Nullable Bundle bundle)
	{
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(1000);
		mLocationRequest.setFastestInterval(1000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

		reloadLocation();
	}

	public void reloadLocation()
	{
		if (ContextCompat.checkSelfPermission(mMap.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED)
		{
			FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(mGoogleApiClient.getContext());
			fusedLocationClient.getLastLocation().addOnSuccessListener(this::onLocationSuccess);
		}
	}


	private void onLocationSuccess(Location location)
	{
		if (location != null)
		{
			mLastLocation = location;
		}
	}

	@Override
	public void onConnectionSuspended(int i)
	{
		//
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
	{
		//
	}

	@Override
	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
	{
		switch (requestCode)
		{
			case PERM_REQUEST_LOCATION:
			{
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// permission was granted, yay! Do the
					// location-related task you need to do.
					if (ContextCompat.checkSelfPermission(mMap.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
							== PackageManager.PERMISSION_GRANTED)
					{
						if (mGoogleApiClient == null)
							buildGoogleApiClient();

						mMap.getGMapInstance().setMyLocationEnabled(true);
					}

				}
				else
				{
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Toast.makeText(mMap.getContext(), "permission denied", Toast.LENGTH_LONG).show();
				}
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}
}
