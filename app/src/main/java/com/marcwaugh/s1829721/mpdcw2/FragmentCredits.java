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
import android.graphics.Typeface;
import android.os.Bundle;
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

	private final MainActivity.ApplicationMainActivity mApp;
	private TextView mTextViewCredits;
	private Date lastTransitionDate;

	FragmentCredits(MainActivity.ApplicationMainActivity application) {
		this.mApp = application;
		this.lastTransitionDate = new Date();
	}

	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_credits, container, false);
		mTextViewCredits = view.findViewById(R.id.textViewCredits);

		mTextViewCredits.setHorizontallyScrolling(true);
		mTextViewCredits.setTextSize(14);
		mTextViewCredits.setTypeface(Typeface.MONOSPACE);    //all characters the same width

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
					new InputStreamReader(mApp.getMainActivity().getAssets().open("Credits.txt")));

			// Throw away the first 2 header lines before parsing
			while ((line = bufferedReader.readLine()) != null)
				credits.append(line);

			bufferedReader.close();
			mTextViewCredits.setText(credits.toString());
		}
		catch (IOException e) {
			mTextViewCredits.setText("Failed to open credits file...\n"
					+ e.toString());
		}
	}

	@Override
	public void onVisibilityChanged(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			mApp.setFabListener(this);
			mApp.setNavbarVisibility(false); // Hide the navbar
			mApp.setFabIcon(R.drawable.ic_format_list_bulleted_white_64dp);
		}
		else {
			mApp.setFabListener(null);
			mApp.setNavbarVisibility(true); // Show the navbar
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
			mApp.transitionFragment(mApp.getFragmentRss());
		}
	}
}
