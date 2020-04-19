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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

	private String description_cleaned;

	private double georssLat;
	private double georssLng;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
		if (this.title != null) this.title = this.title.trim();
	}

	public String getDescription()
	{
		return description;
	}

	public String getDescription_Cleaned()
	{
		return this.description_cleaned;
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

	public void setDescription(String description)
	{
		this.description = description;
		if (description == null) return;


		// Clean and format the description
		String descFixed = description
				.trim()
				.replace("<br />", "\n")
				.replace("<br>", "\n")
				.replace("<br/>", "\n");

		String[] lines = descFixed.split("\n");
		StringBuilder descriptionOutput = new StringBuilder();

		SimpleDateFormat dateOutput = new SimpleDateFormat("dd MMMMM yyyy", Locale.UK);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMMM yyyy", Locale.UK);

		for (String line : lines)
		{
			if (line.startsWith("Start Date: ")
					|| line.startsWith("End Date: "))
			{
				String type = line.split(":")[0]; // "Start Date:" / "End Date:"
				String text = line
						.replace("Start Date: ", "")
						.replace("End Date: ", "")
						.split(" -")[0]  // Remove " - 00:00"
						.split(", ")[1]; // Remove "Monday, "
				try
				{
					Date d = dateFormat.parse(text);
					descriptionOutput
							.append(type)
							.append(": ")
							.append(text)
							.append("\n");
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}

				continue;
			}

			descriptionOutput.append(line).append("\n");
		}

		String descriptionText = descriptionOutput.toString();
		description_cleaned = descriptionText.substring(0, descriptionText.length() - 1);
	}
}
