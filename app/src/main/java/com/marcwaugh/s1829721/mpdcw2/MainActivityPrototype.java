/*
 * Copyright (c)  2020-2020, Marc Waugh
 *
 * File:		MainActivityPrototype.java
 * Module:		Mobile Platform Development
 * Date:		2020
 * Student Id:	S1829721
 *
 * Please note that this copyright header might appear on CC assets such as
 * SVG vector images/icons from 3rd parties. For files such as these their
 * respective copyright notices can be found inside /LICENSES.txt
 */

/*
 * Student:     Marc Waugh
 * Student Id:  S1829721
 *
 * Description
 *
 * This is the main activity that acts as the first page of the app
 */

package com.marcwaugh.s1829721.mpdcw2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.marcwaugh.s1829721.mpdcw2.xml.IXmlErrorEventListener;
import com.marcwaugh.s1829721.mpdcw2.xml.IXmlFinishedEventListener;
import com.marcwaugh.s1829721.mpdcw2.xml.RssItem;
import com.marcwaugh.s1829721.mpdcw2.xml.RssXmlPullParser;

import java.util.List;

public class MainActivityPrototype
		extends AppCompatActivity
		implements View.OnClickListener, IXmlFinishedEventListener, IXmlErrorEventListener {
	// Traffic Scotland URLs
	private static final String urlTSRoadWorks = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
	private static final String urlTSRoadWorksPlanned = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
	private static final String urlTSCurrentIncidents = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
	private String result;
	private TextView rawDataDisplay;
	private Button startButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_prototype);

		startButton = (Button) findViewById(R.id.startButton);
		rawDataDisplay = (TextView) findViewById(R.id.rawDataDisplay);
		startButton.setOnClickListener(this);
	}

	public void onClick(View view) {
		startProgress();
	}

	public void startProgress() {
		// Autoload the roadworks
		new Thread(new RssXmlPullParser(urlTSRoadWorks, this, this)).start();
	} //

	@Override
	public void XmlFinishedParsing(final RssXmlPullParser parser) {
		// Get a handler that can be used to post to the main thread
		Handler mainHandler = new Handler(Looper.getMainLooper());

		Runnable myRunnable = new Runnable() {
			@Override
			public void run() {
				List<RssItem> items = parser.getRssItems();

				// Update recylcer


			} // This is your code
		};

		mainHandler.post(myRunnable);
	}

	@Override
	public void XmlErrorDuringParsing(RssXmlPullParser parser, Exception ex) {

	}
} // End of MainActivity