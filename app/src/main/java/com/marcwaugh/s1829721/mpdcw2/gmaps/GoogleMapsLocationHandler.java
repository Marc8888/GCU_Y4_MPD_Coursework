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

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class GoogleMapsLocationHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
	IFragmentLocationWantingMap mMap;
	GoogleApiClient mGoogleApiClient;

	public GoogleMapsLocationHandler(IFragmentLocationWantingMap fragmentMap)
	{
		this.mMap = fragmentMap;
	}


	public void onMapLoad()
	{

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

	}

	@Override
	public void onConnectionSuspended(int i)
	{

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
	{

	}
}
