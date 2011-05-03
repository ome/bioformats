/*
 * Copyright (c) 1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.sun.jimi.util;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * This class provides a data type identification in the form of a string.
 * Clients of this class are responsible for mapping that identifier
 * string to something useful.<p>
 *
 * If the getID(InputStream is) method is used then the client is
 * responsible for passing an input stream which can be reset to
 * restore the data consumed by this class in an attempt to identify
 * the specified source.<p>
 *
 * Currently this class currently only supports type identification via
 * filename extension.<p>
 *			  
 * PROJECTED OPERATION IS BELOW
 *
 * This class retreives data from the designated source and checks
 * for matches against expressions stored in a configuration file to
 * locate the file ID. See external specification for format of the file
 * and field contents etc.<p>
 *
 * The configuration file is an ascii text file which is currently maintained
 * by editing the file with a text editor. This will change in the future to
 * allow applications to register there own fileTypes and expressions for
 * specific clients.<p>
 *
 * @author	Robin Luiten
 * @version	1.3 02/Jan/1998
 */
public class DataType
{
	/*
		04/Sep/1997

		This class is currently implemented purely through examining
		the filename extension of the String or URL passed to it.

	 */

	/**
	 * the default filename extensions recognised by this class
	 * these entries must be specified here in uppercase
	 **/
	final static String[] extension =
	{
		"BMP",
		"DIB",
		"RLE",
		"GIF",
		"JPG",
		"JPE",
		"JPEG",
		"PIC",
		"PCT",
		"PICT",
		"PNG",
		"PSD",
		"TGA",
		"TIF",
		"TIFF",
		"RAS",
		"ICO",
		"CUR",
		"XBM",
		"BM",
		"XPM",
		"PM",
		"PCX",
		"DCX",
		
	};

	/**
	 * the default file type id's based on the filename extensions
	 **/
	final static String[] typeID =
	{
		"image/x-bmp",
		"image/x-bmp",
		"image/x-bmp",
		"image/gif",
		"image/jpeg",
		"image/jpeg",
		"image/jpeg",
		"image/x-pict",
		"image/x-pict",
		"image/x-pict",
		"image/png",
		"image/x-psd",
		"image/x-tga",
		"image/tiff",
		"image/tiff",
		"image/x-cmu-raster",
		"image/x-ico",
		"image/x-cur",
		"image/x-xbitmap",
		"image/x-xbitmap",
		"image/x-xpixmap",
		"image/x-xpixmap",
		"image/x-pcx",
		"image/x-dcx",
	};

	/**
	 * This is the maximum number of bytes that this class will
	 * take to identify file in any situation. Therefore this constant
	 * could be used by clients to wrap input Streams with buffered
	 * input streams sufficient to reset to the beggining of input
	 * data after identification.
	 */
	public static int MAX_REWIND = 2500;

	private String client;

	private int version;

	static Properties extMap;

	static
	{
		int i;
		extMap = new Properties();
		for (i = 0; i < extension.length; ++i)
		{
			putMapping(extension[i], typeID[i]);
		}
	}

	/**
	 * @param client ID of the client for which file ID is required.
	 * @param version the version of the client for which the ID is required.
	 */
	public DataType(String client, int version)
	{
		this();
		this.client = client;
		this.version = version;
	}

	/**
	 * This constructor will find the highes version ID for the client.
	 * @param client ID of the client for which file ID is required.
	 */
	public DataType(String client)	// any version
	{
		this();
		this.client = client;
	}

	public DataType()	// the null client
	{
		this.client = "";	// not sure if want this or null yet...
		this.version = -1;
	}

	/**
	 * Add or Overide a mapping of filename extension to String type.
	 * @param extension	filename extension
	 * @param typeID id string of the type of the file to be returned for
	 * filenames with the assocciatted extension.
	 **/
	public static void putMapping(String extension, String typeID)
	{
		extMap.put(extension, typeID);
	}

	/**
	 * @return the string following the last "." in the
	 * input string. If no "." in string the return null string.
	 */
	static protected String getExtension(String str)
	{
		int lastDot = str.lastIndexOf('.');
		if (lastDot != -1 || lastDot == str.length())
			return str.substring(lastDot + 1);
		else
			return "";
	}

	/**
	 * @return a string ID of the file based only on the filename extension.
	 * This returns an empty string if no match is found.
	 */
	public static String getIDe(String fileName)
	{
		return extMap.getProperty(getExtension(fileName).toUpperCase());
	}

	/** @see getIDe */
	public static String getIDe(URL url)
	{
		return getIDe(url.getFile());
	}

	/** currently just calls getIDe() to ID files by filename extension */
	public static String getID(String fileName) throws IOException
	{
		return getIDe(fileName);
	}

	/** currently just calls getIDe() to ID files by filename extension */
	public static String getID(URL url) throws IOException
	{
		return getIDe(url.getFile());
	}

	public static String getID(InputStream is) throws IOException
	{
		throw new IOException("FileType getID() InputStream Identifying not finished");
	}

}

