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

	/**
	 * Set the description and create a cleaned version
	 *
	 * @param description
	 */
	public void setDescription(String description)
	{
		// Set the description
		this.description = description;
		if (description == null) return;

		// Clean and format the description
		String descFixed = description
				.trim() // Remove any trailing whitespace

				// Replace <br> with a new line character
				.replace("<br />", "\n")
				.replace("<br>", "\n")
				.replace("<br/>", "\n");

		String[] arrLines = descFixed.split("\n");
		StringBuilder descriptionOutput = new StringBuilder();

		// Date formats used by rss (without the day), they are no longer needed but
		//   was kept in the case that i wanted to add back in that functionality
		// SimpleDateFormat dateOutput = new SimpleDateFormat("dd MMMMM yyyy", Locale.UK);
		// SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMMM yyyy", Locale.UK);

		// Do various text changes to make the description text look more user friendly.
		//
		for (String txtLine : arrLines)
		{
			String line = txtLine.trim();

			// Replace the minutes from the date
			if (line.startsWith("Start Date: ")
					|| line.startsWith("End Date: "))
			{
				String type = line.split(":")[0]; // "Start Date:" / "End Date:"
				String text = line
						.replace("Start Date: ", "")
						.replace("End Date: ", "")
						.split(" -")[0]  // Remove " - 00:00"
						.split(", ")[1]; // Remove "Monday, "

				// Append to the output
				descriptionOutput.append(type).append(": ").append(text).append("\n");

//				try
//				{
//					Date d = dateFormat.parse(text);
//				}
//				catch (ParseException e)
//				{
//					e.printStackTrace();
//				}
				continue;
			}

			// Replace "Works:" with "Works: "
			if (line.contains("Works:"))
				line = line.replace("Works:", "Works: ");

			// Fix cases where "Traffic Management:" is not on its own line
			if (line.contains("Traffic Management:") && !line.startsWith("Traffic Management"))
			{
				// This is a quick and dirty solution, its very jank but gets the job done

				// We are fixing up the text formatting by splitting it into new lines.
				line = line.replace("Traffic Management:", "/TM//TRAFFIC/");

				// Split by our line identifier $TM
				String[] tm = line.split("/TM/");
				for (String x : tm)
				{
					// Replace our variable /TRAFFIC/ with the traffic management text
					x = x.replace("/TRAFFIC/", "Traffic Management: ");

					// Add each line to the output with a new line character
					descriptionOutput.append(x).append("\n");
				}

				continue;
			}

			descriptionOutput.append(line).append("\n");
		}

		String descriptionText = descriptionOutput.toString();
		description_cleaned = descriptionText.substring(0, descriptionText.length() - 1);
	}
}
