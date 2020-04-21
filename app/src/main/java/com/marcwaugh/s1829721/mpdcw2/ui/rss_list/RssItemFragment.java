/*
 * Copyright (c)  2020-2020, Marc Waugh
 *
 * File:		RssItemFragment.java
 * Module:		Mobile Platform Development
 * Date:		2020
 * Student Id:	S1829721
 *
 * Please note that this copyright header might appear on CC assets such as
 * SVG vector images/icons from 3rd parties. For files such as these their
 * respective copyright notices can be found inside /LICENSES.txt
 */

package com.marcwaugh.s1829721.mpdcw2.ui.rss_list;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marcwaugh.s1829721.mpdcw2.MainActivity;
import com.marcwaugh.s1829721.mpdcw2.R;
import com.marcwaugh.s1829721.mpdcw2.SearchManager;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.ISearchChanged;
import com.marcwaugh.s1829721.mpdcw2.xml.RssItem;
import com.marcwaugh.s1829721.mpdcw2.xml.RssXmlPullParser;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RssItemFragment extends Fragment implements ISearchChanged {

	// TODO: Customize parameter argument names
	private static final String ARG_COLUMN_COUNT = "column-count";
	private static final String ARG_URL_ENDPOINT = "url-endpoint";

	// TODO: Customize parameters
	private String mUrlEndpoint = null;
	private OnListFragmentInteractionListener mListener;
	private RssItemRecyclerViewAdapter rva;

	private ArrayList<IRssListener> rssListeners;

	private List<RssItem> mRssItems;
	private List<RssItem> mRssItems_Filtered;
	private boolean mXmlLoading = false;

	private SearchManager mSearchManager = null;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RssItemFragment() {
		Log.i("FragmentActivityMap", "Constructor");
		rssListeners = new ArrayList<>();
	}

	public static RssItemFragment newInstance(String url) {
		return newInstance(url, 1);
	}

	public static RssItemFragment newInstance(String url, int columnCount) {
		RssItemFragment fragment = new RssItemFragment();

		// Set local instanced values (When initialized through map)
		fragment.mUrlEndpoint = url;

		Bundle args = new Bundle();
		args.putInt(ARG_COLUMN_COUNT, columnCount);
		args.putString(ARG_URL_ENDPOINT, url);

		fragment.setArguments(args);
		return fragment;
	}

	public List<RssItem> getRssItems() {
		return mRssItems;
	}

	public List<RssItem> getRssItemsFiltered() {
		return mRssItems_Filtered;
	}

	private void setRssItems(List<RssItem> rssItems) {
		this.mRssItems = rssItems;

		if (mSearchManager != null) {
			SearchManager.FilterState filterState = mSearchManager.getFilterState();

			if (filterState.hasFilter()) {
				ArrayList<RssItem> filteredRssItems = new ArrayList<>();
				for (RssItem item : filteredRssItems) {
					if (filterState.rssMatchesFilter(item))
						filteredRssItems.add(item);
				}

				mRssItems_Filtered = filteredRssItems;
			}
			else {
				mRssItems_Filtered = mRssItems;
			}
		}
		else {
			mRssItems_Filtered = mRssItems;
		}

		if (rva != null)
			rva.setRssItemsList(mRssItems_Filtered, true);

		if (rssListeners != null) {
			for (IRssListener listener : rssListeners) {
				if (listener != null)
					listener.rssItemsLoaded(this, mRssItems_Filtered);
			}
		}
	}

	public void refreshDataset() {
		// Only allow one instance to run!
		if (mXmlLoading || mUrlEndpoint == null)
			return;

		mXmlLoading = true;

		// Reload dataset
		new Thread(new RssXmlPullParser(mUrlEndpoint,
				// Success, parse and display data
				(rssParser) -> new Handler(Looper.getMainLooper()).post(() -> {
					// Update the rss items
					Log.d("RssItemFragment", "Setting Rss Items");

					setRssItems(rssParser.getRssItems());
					mXmlLoading = false;
				}),
				// Exception handling
				(rssParser, exception) -> {
					// Handle any exceptions during rss processing
					Log.e("RssFragment", "Failed to parse xml document (" + mUrlEndpoint + ").", exception);
					mRssItems = mRssItems_Filtered = new ArrayList<>();
					mXmlLoading = false;

					if (rssListeners != null) {
						for (IRssListener listener : rssListeners) {
							if (listener != null)
								listener.rssItemsFailed(this);
						}
					}

					rva.setRssItemsList(new ArrayList<>(), true);
				})
		).start();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			mUrlEndpoint = savedInstanceState.getString(ARG_URL_ENDPOINT);
		}
		else if (getArguments() != null) {
			mUrlEndpoint = getArguments().getString(ARG_URL_ENDPOINT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_rss_item_list, container, false);

		// Set the adapter
		if (view instanceof RecyclerView) {
			Context context = view.getContext();
			RecyclerView recyclerView = (RecyclerView) view;

			// Get display metrics
			DisplayMetrics metrics = getResources().getDisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

			// Calculate how many rows can be seen by display size (inches)
			int rowCount = 0;
			float yInches = metrics.heightPixels / metrics.ydpi;
			float xInches = metrics.widthPixels / metrics.xdpi;
			double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
			rowCount = (int) diagonalInches - 5;
			rowCount = rowCount < 0 ? 1 : Math.min(rowCount, 4);

			// In landscape show half of the usual objects
			//  as we will be displaying a side by side map view.
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
				rowCount = Math.max(1, rowCount / 2);

			// int orientation = ;
			if (rowCount > 1) {
				recyclerView.setLayoutManager(new GridLayoutManager(context, rowCount));
			}
			else {
				// In portrait
				recyclerView.setLayoutManager(new LinearLayoutManager(context));
			}

			// Initialize the list with an empty array list as the items will be loaded in the background
			// asynchronously.
			recyclerView.setAdapter(rva = new RssItemRecyclerViewAdapter(new ArrayList<RssItem>(), mListener));

			// Load items from the cache to stop reloading the information being reloaded on each event.
			if (mRssItems == null)
				refreshDataset();
			else
				rva.setRssItemsList(mRssItems, true);
		}

		return view;
	}

	public RssItemFragment setInteractionListener(OnListFragmentInteractionListener listener) {
		// Add a interaction listener
		boolean wasNull = mListener == null;
		this.mListener = listener;

		// Setup the adapter if after onCreateView
		if (wasNull && listener != null && getView() != null && rva != null) {
			rva.setListener(listener);
		}

		return this;
	}

	public RssItemFragment addRssListener(IRssListener listener) {
		// Add a new rss listener
		if (rssListeners == null) return this;
		if (rssListeners.contains(listener)) return this;

		rssListeners.add(listener);
		return this;
	}

	public RssItemFragment removeRssListener(IRssListener listener) {
		// Remove rss listener
		if (rssListeners == null) return this;
		rssListeners.remove(listener);
		return this;
	}

	private void setSearchManager(SearchManager searchManager) {
		// If our existing listener has us hooked
		if (this.mSearchManager != null)
			mSearchManager.removeListener(this);

		// Set the new listener
		mSearchManager = searchManager;

		// If our search manager is not null
		if (searchManager != null) {
			mSearchManager.addListener(this);

			// Update our search
			if (mRssItems != null)
				setRssItems(mRssItems);
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		if (context instanceof MainActivity) {
			MainActivity activity = (MainActivity) context;
			setSearchManager(activity.getMainActivityHelper().getSearchManager());

			if (mRssItems != null)
				setRssItems(mRssItems);
		}

		if (context instanceof OnListFragmentInteractionListener)
			mListener = (OnListFragmentInteractionListener) context;
		else if (context instanceof MainActivity && (((MainActivity) context).getMainActivityHelper().getFragmentRss() != null))
			mListener = ((MainActivity) context).getMainActivityHelper().getFragmentRss();

		if (mUrlEndpoint != null) {
			// Reload dataset
			refreshDataset();
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		state.putString(ARG_URL_ENDPOINT, mUrlEndpoint);
	}

	@Override
	public void applicationSearchChanged(SearchManager searchManager) {
		setSearchManager(searchManager);
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnListFragmentInteractionListener {
		// TODO: Update argument type and name
		void onListFragmentInteraction(RssItem item);
	}
}
