/*
 * Copyright (c)  2020-2020, Marc Waugh
 *
 * File:		FragmentCredits.java
 * Module:		Mobile Platform Development
 * Date:		2020
 * Student Id:	S1829721
 *
 * Please note that this copyright header might appear on CC assets such as
 * SVG vector images/icons from 3rd parties. For files such as these their
 * respective copyright notices can be found inside /LICENSES.txt
 */

package com.marcwaugh.s1829721.mpdcw2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IApplicationFabListener;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IVisibilityChangedListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class FragmentCredits extends Fragment
		implements IVisibilityChangedListener, IApplicationFabListener {

	private MainActivity.MainActivityHelper mainActivity;
	private TextView mText;
	private Date lastTransitionDate;

	public FragmentCredits() {
		this.lastTransitionDate = new Date();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof MainActivity) {
			MainActivity activity = (MainActivity) context;

			initializeController(activity.getMainActivityHelper());
			Log.i("FragmentActivityRss", "onAttach -> MainActivity");
			onVisibilityChanged(true);
		}
	}

	private void initializeController(MainActivity.MainActivityHelper helper) {
		this.mainActivity = helper;
		lastTransitionDate = new Date();
	}

	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_credits, container, false);
		mText = view.findViewById(R.id.textViewCredits);

		// Disable user input (READ ONLY)
		//mText.setInputType(InputType.TYPE_NULL);
		mText.setTextSize(14);
		mText.setTypeface(Typeface.MONOSPACE);

		return view;
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onStart() {
		super.onStart();

		try {
			String line = "";
			StringBuilder credits = new StringBuilder();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(mainActivity.getMainActivity().getAssets().open("Credits.txt")));

			// Throw away the first 2 header lines before parsing
			while ((line = bufferedReader.readLine()) != null)
				credits.append(line).append("\n");

			bufferedReader.close();

			String text = credits.toString();
			mText.setText(text);
		}
		catch (IOException e) {
			mText.setText("Failed to open credits file...\n"
					+ e.toString());
		}
	}

	@Override
	public void onVisibilityChanged(boolean isVisibleToUser) {
		if (mainActivity == null)
			return;

		if (isVisibleToUser) {
			mainActivity.addFabListener(this);
			mainActivity.setNavbarVisibility(false); // Hide the navbar
			mainActivity.setFabIcon(R.drawable.ic_format_list_bulleted_white_64dp);
			mainActivity.setTitle("Credits");
		}
		else {
			mainActivity.removeFabListener(this);
			mainActivity.setNavbarVisibility(true); // Show the navbar
		}
	}

	@Override
	public void applicationFabClicked() {
		// Limit the list / map changing to every few second(s), this will stop any potential crashes
		Date now = new Date();
		long milliseconds = (now.getTime() - lastTransitionDate.getTime());// / 1000 % 60;
		if (milliseconds > 1000) {
			lastTransitionDate = new Date();

			// Swap to the list view
			mainActivity.transitionFragment(mainActivity.getFragmentRss());
		}
	}
}
