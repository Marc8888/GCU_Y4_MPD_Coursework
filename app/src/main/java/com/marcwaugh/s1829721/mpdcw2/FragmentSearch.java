/*
 * Copyright (c)  2020-2020, Marc Waugh
 *
 * File:		FragmentSearch.java
 * Module:		Mobile Platform Development
 * Date:		2020
 * Student Id:	S1829721
 *
 * Please note that this copyright header might appear on CC assets such as
 * SVG vector images/icons from 3rd parties. For files such as these their
 * respective copyright notices can be found inside /LICENSES.txt
 */

package com.marcwaugh.s1829721.mpdcw2;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IApplicationFabListener;
import com.marcwaugh.s1829721.mpdcw2.listenerinterfaces.IVisibilityChangedListener;

import java.util.Calendar;
import java.util.Date;

public class FragmentSearch extends Fragment implements IVisibilityChangedListener, IApplicationFabListener {
	private SearchManager mSearchManager = null;
	private MainActivity.MainActivityHelper mMainActivityHelper;
	private Date lastTransitionDate = new Date();

	private Button mBtnFilter;
	private Button mBtnClear;
	private TextView mTxtCalendarBefore;
	private TextView mTxtCalendarAfter;
	private Button mBtnDateBeforeFilter;
	private Button mBtnDateBeforeFilterClear;
	private Button mBtnDateAfterFilter;
	private Button mBtnDateAfterFilterClear;
	private EditText mEditTextFilterTitle;


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof MainActivity) {
			Log.i("FragmentSearch", "onAttach -> MainActivity");
			MainActivity activity = (MainActivity) context;
			initializeController(activity.getMainActivityHelper());
			onVisibilityChanged(true);
		}
	}

	private void initializeController(MainActivity.MainActivityHelper mainActivityHelper) {
		this.mSearchManager = mainActivityHelper.getSearchManager();
		this.mMainActivityHelper = mainActivityHelper;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search, container, false);

		if (mMainActivityHelper == null && ((MainActivity) getActivity()) != null) {
			mMainActivityHelper = ((MainActivity) getActivity()).getMainActivityHelper();
		}
		else if (mMainActivityHelper == null && ((MainActivity) getActivity()) == null) {
			return view;
		}

		mBtnFilter = view.findViewById(R.id.btn_ApplyFilter);
		mBtnClear = view.findViewById(R.id.btn_ClearFilter);

		mEditTextFilterTitle = view.findViewById(R.id.editText_FilterbyTitle);

		mBtnDateBeforeFilter = view.findViewById(R.id.btn_FilterBeforeDate_Set);
		mBtnDateBeforeFilterClear = view.findViewById(R.id.btn_FilterBeforeDate_Clear);
		mBtnDateAfterFilter = view.findViewById(R.id.btn_FilterAfterDate_Set);
		mBtnDateAfterFilterClear = view.findViewById(R.id.btn_FilterAfterDate_Clear);

		mTxtCalendarBefore = view.findViewById(R.id.lblFilterAfterDate_Display);
		mTxtCalendarAfter = view.findViewById(R.id.lblFilterBeforeDate_Display);

		mBtnFilter.setOnClickListener((v) -> {
			String x = mEditTextFilterTitle.getText().toString();
			mMainActivityHelper.getSearchManager().setFilterTitle(x);
		});

		mBtnClear.setOnClickListener((x) -> mMainActivityHelper.getSearchManager().setFilterTitle(null));

		mBtnDateBeforeFilter.setOnClickListener((v) -> {
			DialogFragment newFragment = new DatePickerFragment((v1, year, month, dayOfMonth) -> {
				mTxtCalendarBefore.setText("Filtered to DMY: " + dayOfMonth + "/" + month + "/" + year);
				Date d = constructDate(year, month, dayOfMonth);
				mMainActivityHelper.getSearchManager().setFilterDateBefore(d);
			});
			newFragment.show(mMainActivityHelper.getMainActivity().getSupportFragmentManager(), "datePickerBefore");
		});

		mBtnDateBeforeFilterClear.setOnClickListener((v) -> {
			mMainActivityHelper.getSearchManager().setFilterDateBefore(null);
		});

		mBtnDateAfterFilter.setOnClickListener((v) -> {
			DialogFragment newFragment = new DatePickerFragment((v1, year, month, dayOfMonth) -> {
				mTxtCalendarAfter.setText("Filtered to DMY: " + dayOfMonth + "/" + month + "/" + year);

				Date d = constructDate(year, month, dayOfMonth);
				mMainActivityHelper.getSearchManager().setFilterDateAfter(d);
			});
			newFragment.show(mMainActivityHelper.getMainActivity().getSupportFragmentManager(), "datePickerAfter");
		});

		mBtnDateAfterFilterClear.setOnClickListener((v) -> {
			mTxtCalendarAfter.setText("Not filtering");
			mMainActivityHelper.getSearchManager().setFilterDateAfter(null);
		});

		return view;
	}

	public Date constructDate(int year, int month, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR, 0);
		return calendar.getTime();
	}

	@Override
	public void onVisibilityChanged(boolean isVisibleToUser) {
		if (mMainActivityHelper == null) return;
		Log.i("FragmentSearch", "onVisibilityChanged: " + isVisibleToUser);

		if (isVisibleToUser) {
			mMainActivityHelper.setFabIcon(R.drawable.ic_format_list_bulleted_white_64dp);
			mMainActivityHelper.addFabListener(this);
			mMainActivityHelper.setTitle("Search");

			if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
				mMainActivityHelper.setNavbarVisibility(false);
			else
				mMainActivityHelper.setFabVisibility(true);
		}
		else {
			// Cleanup
			mMainActivityHelper.removeFabListener(this);

			if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
				mMainActivityHelper.setNavbarVisibility(true);
			else
				mMainActivityHelper.setFabVisibility(false);
		}
	}

	@Override
	public void applicationFabClicked() {
		// Limit the fragment changing to every few second(s), this will stop any potential crashes
		Date now = new Date();
		long milliseconds = (now.getTime() - lastTransitionDate.getTime());// / 1000 % 60;
		if (milliseconds > 1000) {
			lastTransitionDate = new Date();
			mMainActivityHelper.transitionFragment(mMainActivityHelper.getFragmentRss());
		}
	}
}