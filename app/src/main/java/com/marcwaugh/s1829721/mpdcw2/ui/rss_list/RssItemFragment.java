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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
public class RssItemFragment extends Fragment {

	// TODO: Customize parameter argument names
	private static final String ARG_COLUMN_COUNT = "column-count";
	private static final String ARG_URL_ENDPOINT = "url-endpoint";

	// TODO: Customize parameters
	private int mColumnCount = 1;
	private String mUrlEndpoint = null;
	private OnListFragmentInteractionListener mListener;
	private RssItemRecyclerViewAdapter rva;

	private List<RssItem> mRssItems;
	private boolean mXmlLoading = false;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RssItemFragment() {
		Log.i("FragmentActivityMap", "Constructor");
	}

	public static RssItemFragment newInstance(String url) {
		return newInstance(url, 1);
	}

	public static RssItemFragment newInstance(String url, int columnCount) {
		RssItemFragment fragment = new RssItemFragment();

		Bundle args = new Bundle();
		args.putInt(ARG_COLUMN_COUNT, columnCount);
		args.putString(ARG_URL_ENDPOINT, url);

		fragment.setArguments(args);
		return fragment;
	}

	public List<RssItem> getRssItems() {
		return mRssItems;
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

					if (rva != null)
						rva.setRssItemsList(rssParser.getRssItems());

					mRssItems = rssParser.getRssItems();
					mXmlLoading = false;
				}),

				// Exception handling
				(rssParser, exception) -> {
					// Handle any exceptions during rss processing
					Log.e("RssFragment", "Failed to parse xml document (" + mUrlEndpoint + ").", exception);
					mXmlLoading = false;
				})
		).start();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			mUrlEndpoint = getArguments().getString(ARG_URL_ENDPOINT);
			mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
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

			if (mColumnCount <= 1)
				recyclerView.setLayoutManager(new LinearLayoutManager(context));
			else
				recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));

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

	public RssItemFragment setListener(OnListFragmentInteractionListener listener) {
		boolean wasNull = mListener == null;
		this.mListener = listener;

		// Setup the adapter if after onCreateView
		if (wasNull && listener != null && getView() != null && rva != null) {
			rva.setListener(listener);
		}

		return this;
	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		if (context instanceof OnListFragmentInteractionListener)
			mListener = (OnListFragmentInteractionListener) context;
		else if (context instanceof MainActivity && (((MainActivity) context).getMainActivityHelper().getFragmentRss() != null))
			mListener = ((MainActivity) context).getMainActivityHelper().getFragmentRss();
//		else
//			throw new RuntimeException(context.toString()
//					+ " must implement OnListFragmentInteractionListener");

		if (mListener != null && mUrlEndpoint != null) {
			// Reload dataset
			refreshDataset();
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
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
