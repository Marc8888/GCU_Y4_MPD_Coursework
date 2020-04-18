package com.marcwaugh.s1829721.mpdcw2.xml;

import org.xmlpull.v1.XmlPullParser;

public interface IXmlConnectionFailedEventListener
{
	void XmlConnectionFailed(XmlPullParser parser, Exception ex);
}
