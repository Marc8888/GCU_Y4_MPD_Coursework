/*
 * Copyright (c)  2020-2020, Marc Waugh
 *
 * File:		RssItem.java
 * Module:		Mobile Platform Development
 * Date:		2020
 * Student Id:	S1829721
 *
 * Please note that this copyright header might appear on CC assets such as
 * SVG vector images/icons from 3rd parties. For files such as these their
 * respective copyright notices can be found inside /LICENSES.txt
 */

package com.marcwaugh.s1829721.mpdcw2.xml;

public class RssItem
{
	//<title>M90 J1 to J3</title>
	//<description>...</description>
	//<link>http://tscot.org/03cFB2019704</link>
	//<georss:point>56.0847247150839 -3.40105048324989</georss:point>
	//<author/>
	//<comments/>
	//<pubDate>Mon, 06 Jan 2020 00:00:00 GMT</pubDate>

	private String title;
	private String description;
	private String link;
	private String georssPoint;
	private String pubDate;

	private double georssLat;
	private double georssLng;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public String getGeorssPoint()
	{
		return georssPoint;
	}

	public void setGeorssPoint(String georssPoint)
	{
		this.georssPoint = georssPoint;
	}

	public String getPubDate()
	{
		return pubDate;
	}

	public void setPubDate(String pubDate)
	{
		this.pubDate = pubDate;
	}

	public double getGeorssLat()
	{
		return georssLat;
	}

	public void setGeorssLat(double georssLat)
	{
		this.georssLat = georssLat;
	}

	public double getGeorssLng()
	{
		return georssLng;
	}

	public void setGeorssLng(double georssLng)
	{
		this.georssLng = georssLng;
	}
}
