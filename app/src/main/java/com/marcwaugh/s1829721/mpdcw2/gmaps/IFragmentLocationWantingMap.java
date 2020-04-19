/*
 * Copyright (c)  2020-2020, Marc Waugh
 *
 * File:		IFragmentLocationWantingMap.java
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

import com.google.android.gms.maps.GoogleMap;

public interface IFragmentLocationWantingMap
{
	Context getContext();

	GoogleMap getGMapInstance();
}
