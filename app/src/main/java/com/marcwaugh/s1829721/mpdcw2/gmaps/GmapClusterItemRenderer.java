/*
 * Copyright (c)  2020-2020, Marc Waugh
 *
 * File:		GmapClusterItemRenderer.java
 * Module:		Mobile Platform Development
 * Date:		2020
 * Student Id:	S1829721
 *
 * Please note that this copyright header might appear on CC assets such as
 * SVG vector images/icons from 3rd parties. For files such as these their
 * respective copyright notices can be found inside /LICENSES.txt
 */

package com.marcwaugh.s1829721.mpdcw2.gmaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.marcwaugh.s1829721.mpdcw2.R;

public class GmapClusterItemRenderer extends DefaultClusterRenderer<GmapClusterItem>
{
	public GmapClusterItemRenderer(Context context, GoogleMap map, ClusterManager<GmapClusterItem> clusterManager)
	{
		super(context, map, clusterManager);
		clusterManager.setRenderer(this);
	}

	@Override
	protected void onBeforeClusterItemRendered(GmapClusterItem markerItem, MarkerOptions markerOptions)
	{
		// Set our custom icon if one is attached to the marker
		if (markerItem.getIcon() != null)
		{
			markerOptions.icon(markerItem.getIcon());
		}

		markerOptions.visible(true);
	}

	@Override
	protected void onClusterItemUpdated(GmapClusterItem item, Marker marker)
	{
		super.onClusterItemUpdated(item, marker);

		// The cluster renderer has an issue inside its default logic where a marker's text is null by default
		// after searching i had found this fix posted on github.

		/// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		//
		//  Source / Credits: https://gist.github.com/CallumCarmicheal/d8dcb36b2ec394d960f4c0c93d560d9d
		//
		// Replaced logic with a fix due to a bug where the marker .getTitle is null!
		// This is the code from inside the com.google.maps.android.clustering.view.DefaultClusterRenderer class licensed under apache
		//
		// I REPEAT THIS IS NOT MY ORIGINAL WORK, THE COPYRIGHT HOLDER FOR THE FOLLOWING CODE IS GOOGLE 2013 LICENSED UNDER APACHE !!!!
		//
		//  ==== TERMS OF APACHE LICENSE ====
		//
		// Permissions:
		// - Commercial use
		// - Distribution
		// - Modification
		// - Patent use
		// - Private use
		//
		// Conditions:
		// - License and copyright notice
		// - State changes
		//
		// Limitations:
		// - Liability
		// - Trademark use
		// - Warranty
		//
		/// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		boolean changed = false;

		// Update marker text if the item text changed - same logic as adding marker in CreateMarkerTask.perform()
		if (item.getTitle() != null || item.getSnippet() != null)
		{
			// FIX: FIX for issue where marker.getTitle is null
			if (marker.getTitle() == null || !marker.getTitle().equals(item.getTitle()))
			{
				marker.setTitle(item.getTitle());
				changed = true;
			}

			// FIX: FIX for issue where marker.getSnippet is null
			if (marker.getSnippet() == null || !marker.getSnippet().equals(item.getSnippet()))
			{
				marker.setSnippet(item.getSnippet());
				changed = true;
			}
		}

		else if (item.getSnippet() != null && !item.getSnippet().equals(marker.getTitle()))
		{
			marker.setTitle(item.getSnippet());
			changed = true;
		}

		else if (item.getTitle() != null && !item.getTitle().equals(marker.getTitle()))
		{
			marker.setTitle(item.getTitle());
			changed = true;
		}

		// Update marker position if the item changed position
		if (!marker.getPosition().equals(item.getPosition()))
		{
			marker.setPosition(item.getPosition());
			changed = true;
		}

		if (changed && marker.isInfoWindowShown())
		{
			// Force a refresh of marker info window contents
			marker.showInfoWindow();
		}
	}

	// SOURCE: https://stackoverflow.com/questions/42365658/custom-marker-in-google-maps-in-android-with-vector-asset-icon
	public static BitmapDescriptor BitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId)
	{
		Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_place_blue_64dp);
		background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());

		Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
		int left = 52;
		int top = 30;
		vectorDrawable.setBounds(left, top, vectorDrawable.getIntrinsicWidth() + left, vectorDrawable.getIntrinsicHeight() + top);

		Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		background.draw(canvas);
		vectorDrawable.draw(canvas);
		return BitmapDescriptorFactory.fromBitmap(bitmap);
	}
}
