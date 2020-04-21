/*
 * Copyright (c)  2020-2020, Marc Waugh
 *
 * File:		SearchManager.java
 * Module:		Mobile Platform Development
 * Date:		2020
 * Student Id:	S1829721
 *
 * Please note that this copyright header might appear on CC assets such as
 * SVG vector images/icons from 3rd parties. For files such as these their
 * respective copyright notices can be found inside /LICENSES.txt
 */

package com.marcwaugh.s1829721.mpdcw2;

import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.ISearchChanged;
import com.marcwaugh.s1829721.mpdcw2.xml.RssItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class SearchManager implements Serializable {
	private ArrayList<ISearchChanged> listeners = new ArrayList<>();

	private String mFilterTitle = "";
	private Date mFilterDateBefore = null;
	private Date mFilterDateAfter = null;

	public void setFilterTitle(String searchValue) {
		this.mFilterTitle = searchValue;
		invokeListeners();
	}

	public void setFilterDateBefore(Date d) {
		mFilterDateBefore = d;
		invokeListeners();
	}

	public void setFilterDateAfter(Date d) {
		mFilterDateAfter = d;
		invokeListeners();
	}

	public FilterState getFilterState() {
		FilterState state = new FilterState(this);

		state.hasDateFilter = mFilterDateBefore != null || mFilterDateAfter != null;
		state.hasDateStartFilter = mFilterDateBefore != null || mFilterDateAfter != null;
		state.hasDateEndFilter = mFilterDateAfter != null;
		state.hasTitleFilter = !isNullOrWhitespace(mFilterTitle);

		return state;
	}

	private static boolean isNullOrEmpty(String s) {
		return s == null || s.length() == 0;
	}

	private static boolean isNullOrWhitespace(String s) {
		return isNullOrEmpty(s) || isWhitespace(s);
	}

	private static boolean isWhitespace(String s) {
		int length = s.length();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				if (!Character.isWhitespace(s.charAt(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public void addListener(ISearchChanged resultListener) {
		if (listeners.contains(resultListener)) return;
		listeners.add(resultListener);
	}

	public void removeListener(ISearchChanged resultListener) {
		if (resultListener != null)
			listeners.remove(resultListener);
	}

	private void invokeListeners() {
		listeners.removeAll(Collections.singleton(null));
		for (ISearchChanged listener : listeners) {
			if (listener != null)
				listener.applicationSearchChanged(this);
		}
	}

	public class FilterLatLng {
		public double lat;
		public double lng;

		public FilterLatLng(double lat, double lng) {
			this.lat = lat;
			this.lng = lng;
		}
	}

	public class FilterState implements Serializable {
		private final SearchManager manager;

		boolean hasDateFilter = false;
		boolean hasDateStartFilter = false;
		boolean hasDateEndFilter = false;

		boolean hasTitleFilter = false;

		public boolean hasFilter() {
			// Set the filter state
			return hasDateFilter || hasDateStartFilter || hasDateEndFilter
					|| hasTitleFilter;
		}

		public FilterState(SearchManager manager) {
			this.manager = manager;
		}

		public boolean rssMatchesFilter(RssItem item) {
			if (hasTitleFilter && item.getTitle().toLowerCase().contains(manager.mFilterTitle.toLowerCase()))
				return true;

			// Todo: more filters
			return false;
		}
	}
}
