/*
 * Copyright (c)  2020-2020, Marc Waugh
 *
 * File:		IXmlConnectionFailedEventListener.java
 * Module:		Mobile Platform Development
 * Date:		2020
 * Student Id:	S1829721
 *
 * Please note that this copyright header might appear on CC assets such as
 * SVG vector images/icons from 3rd parties. For files such as these their
 * respective copyright notices can be found inside /LICENSES.txt
 */

package com.marcwaugh.s1829721.mpdcw2.xml;

import org.xmlpull.v1.XmlPullParser;

public interface IXmlConnectionFailedEventListener {
	void XmlConnectionFailed(XmlPullParser parser, Exception ex);
}
