package com.marcwaugh.s1829721.mpdcw2.xml;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

// Need separate thread to access the internet resource over network
// Other neater solutions should be adopted in later iterations.
public class RssXmlPullParser implements Runnable
{
	private String mUrl;
	private List<RssItem> mRssItems;
	private IXmlFinishedEventListener mFinishedEventListener;
	private IXmlErrorEventListener mXmlErrorEventListener;

	// TODO: Implement IXmlConnectionFailedEventListener
	public RssXmlPullParser(String websiteUrl, IXmlFinishedEventListener finishedListener, IXmlErrorEventListener errorListener)
	{
		mUrl = websiteUrl;
		mFinishedEventListener = finishedListener;
		mXmlErrorEventListener = errorListener;

		mRssItems = new ArrayList<RssItem>();
	}

	public List<RssItem> getRssItems()
	{
		return mRssItems;
	}

	@Override
	public void run()
	{
		Log.i("XmlParser", "Downloading and Parsing information from " + mUrl);
		String data = loadWebData();
		ParseXML(data);
	}

	private void ParseXML(String inputXml)
	{
		// Parse the XML.
		//
		XmlPullParser parser = Xml.newPullParser();

		try
		{
			parser.setInput(new StringReader(inputXml));

			// Temporary storage for the rss items
			RssItem currentItem = null;

			// Loop xml elements
			int xmlEventType = parser.getEventType();
			while (xmlEventType != XmlPullParser.END_DOCUMENT)
			{
				String xmlTag = null;
				xmlTag = parser.getName(); // Get the xml tag name
				switch (xmlEventType)
				{
					case XmlPullParser.START_DOCUMENT:
						//Log.w("XmlParser", "Start Document!");
						break;

					// Start of the xml tag
					case XmlPullParser.START_TAG:
						//Log.i("XmlParser", "Start Tag: " + xmlTag);
						switch (xmlTag.toLowerCase())
						{
							case "item":
								currentItem = new RssItem();
								break;

							case "title":
								if (currentItem != null)
									currentItem.setTitle(parser.nextText());

								break;

							case "description":
								if (currentItem != null)
									currentItem.setDescription(parser.nextText());
								break;

							case "link":
								if (currentItem != null)
									currentItem.setLink(parser.nextText());
								break;

							case "point": // ns = georss
								if (currentItem != null)
								{
									String point = parser.nextText();
									currentItem.setGeorssPoint(point);

									if (!(point == null || point.isEmpty()))
									{
										String[] pointArr = point.split(" ");
										if (pointArr.length == 2)
										{
											currentItem.setGeorssLat(Double.parseDouble(pointArr[0]));
											currentItem.setGeorssLng(Double.parseDouble(pointArr[1]));
											//Log.i("RssParser", "Parsed GeoRSS: Lat("+currentItem.getGeorssLat()+") Lng("+currentItem.getGeorssLng()+")");
										}
									}
								}
								break;

							case "pubdate":
								if (currentItem != null)
									currentItem.setPubDate(parser.nextText());
								break;
						}

						break;

					// End of the xml tag
					case XmlPullParser.END_TAG:
						xmlTag = parser.getName();

						//
						if (xmlTag.equalsIgnoreCase("item"))
						{
							mRssItems.add(currentItem);
							currentItem = null;
						}

						break;
				}

				xmlEventType = parser.next();
			}
		}
		catch (Exception ex)
		{
			// Pass the exception to any handlers
			if (mXmlErrorEventListener != null)
			{
				mXmlErrorEventListener.XmlErrorDuringParsing(this, ex);

				// We are not returning any data for error'd parsers so just close the thread.
				return;
			}

			// TODO: Handle exceptions
			throw new RuntimeException(ex);
		}

		Log.i("RssParser", "Finished parsing: " + mUrl);

		// Invoke our listener
		if (this.mFinishedEventListener != null)
		{
			this.mFinishedEventListener.XmlFinishedParsing(this);
		}
	}

	/**
	 * Retrieve a string from a web url
	 *
	 * @return Url data
	 */
	private String loadWebData()
	{
		String data = "";
		URL url;
		URLConnection urlConnection;
		BufferedReader bufferedReader = null;
		String line = "";

		try
		{
			url = new URL(this.mUrl);
			urlConnection = url.openConnection();
			bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

			// Throw away the first 2 header lines before parsing
			while ((line = bufferedReader.readLine()) != null)
				data = data + line;

			bufferedReader.close();
			return data;
		}
		catch (IOException ex)
		{
			Log.e("Failed to load website", "IOException: " + ex.getMessage());
			return null;
		}
	}
}
