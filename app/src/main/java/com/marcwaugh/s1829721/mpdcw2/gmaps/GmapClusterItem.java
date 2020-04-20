/*
 * Copyright (c)  2020-2020, Marc Waugh
 *
 * File:		GmapClusterItem.java
 * Module:		Mobile Platform Development
 * Date:		2020
 * Student Id:	S1829721
 *
 * Please note that this copyright header might appear on CC assets such as
 * SVG vector images/icons from 3rd parties. For files such as these their
 * respective copyright notices can be found inside /LICENSES.txt
 */

package com.marcwaugh.s1829721.mpdcw2.gmaps;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

public class GmapClusterItem implements ClusterItem {
	private final LatLng mPosition;
	private final String mTitle;
	private final String mSnippet;
	private final BitmapDescriptor mIcon;


	public GmapClusterItem(double lat, double lng) {
		mPosition = new LatLng(lat, lng);
		mTitle = "";
		mSnippet = "";
		mIcon = null;
	}

	public GmapClusterItem(double lat, double lng, String title, String snippet) {
		mPosition = new LatLng(lat, lng);
		mTitle = title;
		mSnippet = snippet;
		mIcon = null;
	}

	public GmapClusterItem(double lat, double lng, String title, String snippet, BitmapDescriptor icon) {
		mPosition = new LatLng(lat, lng);
		mTitle = title;
		mSnippet = snippet;
		mIcon = icon;
	}

	public GmapClusterItem(LatLng position, String title, String snippet) {
		mPosition = position;
		mTitle = title;
		mSnippet = snippet;
		mIcon = null;
	}

	public GmapClusterItem(LatLng position, String title, String snippet, BitmapDescriptor icon) {
		mPosition = position;
		mTitle = title;
		mSnippet = snippet;
		mIcon = icon;
	}

	public GmapClusterItem(MarkerOptions markerOptions) {
		this.mPosition = markerOptions.getPosition();
		this.mTitle = markerOptions.getTitle();
		this.mSnippet = markerOptions.getSnippet();
		this.mIcon = markerOptions.getIcon();
	}

	@Override
	public LatLng getPosition() {
		return mPosition;
	}

	@Override
	public String getTitle() {
		return mTitle;
	}

	@Override
	public String getSnippet() {
		return mSnippet;
	}

	public BitmapDescriptor getIcon() {
		return mIcon;
	}
}
