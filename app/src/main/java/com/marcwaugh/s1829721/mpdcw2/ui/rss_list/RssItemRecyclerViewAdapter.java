/*
 * Copyright (c)  2020-2020, Marc Waugh
 *
 * File:		RssItemRecyclerViewAdapter.java
 * Module:		Mobile Platform Development
 * Date:		2020
 * Student Id:	S1829721
 *
 * Please note that this copyright header might appear on CC assets such as
 * SVG vector images/icons from 3rd parties. For files such as these their
 * respective copyright notices can be found inside /LICENSES.txt
 */

package com.marcwaugh.s1829721.mpdcw2.ui.rss_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.marcwaugh.s1829721.mpdcw2.R;
import com.marcwaugh.s1829721.mpdcw2.ui.rss_list.RssItemFragment.OnListFragmentInteractionListener;
import com.marcwaugh.s1829721.mpdcw2.xml.RssItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link RssItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class RssItemRecyclerViewAdapter extends RecyclerView.Adapter<RssItemRecyclerViewAdapter.ViewHolder> {

	private final OnListFragmentInteractionListener mListener;
	private List<RssItem> mRssItesm;

	public RssItemRecyclerViewAdapter(List<RssItem> items, OnListFragmentInteractionListener listener) {
		mRssItesm = items;
		mListener = listener;
	}

	public void setRssItemsList(List<RssItem> rssItems, Boolean notifyChanged) {
		mRssItesm = rssItems;

		if (notifyChanged)
			notifyDataSetChanged();
	}

	public void setRssItemsList(List<RssItem> rssItems) {
		setRssItemsList(rssItems, true);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.fragment_rss_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		holder.mItem = mRssItesm.get(position);
		holder.mTxtTitle.setText(mRssItesm.get(position).getTitle());
		holder.mTxtDescription.setText(
				mRssItesm.get(position).getDescription_Cleaned());

		holder.mView.setOnClickListener((view) ->
		{
			if (null != mListener) {
				// Notify the active callbacks interface (the activity, if the
				// fragment is attached to one) that an item has been selected.
				mListener.onListFragmentInteraction(holder.mItem);
			}
		});
	}

	@Override
	public int getItemCount() {
		return mRssItesm.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public final View mView;
		public final TextView mTxtTitle;
		public final TextView mTxtDescription;
		public RssItem mItem;

		public ViewHolder(View view) {
			super(view);
			mView = view;
			mTxtTitle = (TextView) view.findViewById(R.id.txtTitle);
			mTxtDescription = (TextView) view.findViewById(R.id.txtDescription);
		}

		@Override
		public String toString() {
			return super.toString() + " '" + mTxtTitle.getText() + "'";
		}
	}
}
