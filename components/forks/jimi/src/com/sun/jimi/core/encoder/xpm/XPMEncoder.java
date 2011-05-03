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

package com.sun.jimi.core.encoder.xpm;

import java.awt.image.*;
import java.io.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.compat.*;

/**
 * Encoder class for XPM format.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 16:30:53 $
 */
public class XPMEncoder extends JimiEncoderBase
{
	protected static final String MAGIC = "/* XPM */\n";
	protected static final String HEADER_STRING = "static char* image[]={\n";

	protected static char[] paletteStringCharacters;

	/** destination for encoded data */
	protected PrintStream output;
	/** JimiImage to get data from */
	protected AdaptiveRasterImage jimiImage;
	/** current state of encoding */
	protected int state;

	/** number of colors in palette */
	protected int paletteSize;
	/** palette mapping index to string representation */
	protected String[] paletteStrings;
	/** size of each palette entry */
	protected int entrySize;

	public XPMEncoder()
	{
		// generate palette characters if necessary
		if (paletteStringCharacters == null)
			generatePaletteStringCharacters();
	}

	public void initSpecificEncoder(OutputStream out, AdaptiveRasterImage image)
	{
		output = new PrintStream(out);
	}

	/**
	 * Drive the encoder through the image encoding process.
	 **/
	public boolean driveEncoder() throws JimiException
	{
		try {
			jimiImage = getJimiImage();
			doImageEncode();
		}
		// if an exception is raised, set ERROR state and re-throw
		catch (Exception e)
		{
			state = ERROR;
			e.printStackTrace();
			throw new JimiException(e.toString());
		}
		// done!
		state = DONE;

		return false;
	}

	/**
	 * Return the state of the encoder.
	 **/
	public int getState()
	{
		return state;
	}

	/**
	 * Workhorse method for actually encoding the image
	 */
	protected void doImageEncode() throws JimiException, IOException
	{
		// make sure it's a palette image
		if (!(jimiImage.getColorModel() instanceof IndexColorModel)) {
			throw new JimiException("XPM only encodes palette-based images.");
		}
		IndexColorModel cModel = (IndexColorModel)jimiImage.getColorModel();

		createPaletteStrings(cModel);
		writeHeader();
		writePalette(cModel);
		writeImageData();
		writeTrailer();
	}

	/**
	 * Generate the strings which are used as color values.
	 */
	protected void createPaletteStrings(IndexColorModel cm)
	{
		int size = cm.getMapSize();

		// figure out how many characters are needed to represent a palette entry
		entrySize = 0;
		int remainingEntries = size;
		do {
			entrySize++;
			remainingEntries /= paletteStringCharacters.length;
		} while (remainingEntries != 0);

		// number of strings
		paletteStrings = new String[size];
		// create a palette string for each index
		for (int index = 0; index < size; index++) {
			int entryNumber = index;
			String paletteString = "";
			// make all necessary 'digits'
			for (int i = 0; i < entrySize; i++) {
				paletteString = String.valueOf((char)paletteStringCharacters[entryNumber % paletteStringCharacters.length])
					+ paletteString;
				entryNumber /= paletteStringCharacters.length;
			}
			paletteStrings[index] = paletteString;
		}
	}

	/**
	 * Write the XPM header to the output stream.
	 */
	protected void writeHeader() throws IOException
	{
		output.print(MAGIC);
		output.print(HEADER_STRING);
		int width = jimiImage.getWidth();
		int height = jimiImage.getHeight();
		// write header info: "<width> <height> <palette length> <characters per entry>",
		output.println("\""+ width + " " + height + " " + paletteStrings.length + " " + entrySize + "\",");
	}

	/**
	 * Write palette information to the output stream.
	 */
	protected void writePalette(IndexColorModel cm) throws IOException
	{
		for (int index = 0; index < paletteStrings.length; index++) {
			// write a palette entry, like: "aa c #ffffff",
			output.println("\"" + paletteStrings[index] + " c #" + toPaddedHexString((cm.getRGB(index)) & 0xffffff) + "\",");
		}
	}

	/**
	 * Write the image data to the output stream.
	 */
	protected void writeImageData() throws IOException, JimiException
	{
		int[] rowBuffer = new int[jimiImage.getWidth()];
		int height = jimiImage.getHeight();
		// loop through all rows writing image data
		for (int row = 0; row < height; row++) {
			jimiImage.getChannel(row, rowBuffer, 0);
			output.print("\"");
			for (int x = 0; x < rowBuffer.length; x++) {
				output.print(paletteStrings[rowBuffer[x]]);
			}
			if (row < height - 1) {
				output.println("\",");
			}
		}
	}

	/**
	 * Write the end of image marker.
	 */
	protected void writeTrailer() throws IOException
	{
		output.println("\"};");
	}

	/** Make a hex string out of an int, padding to 6 digits.
	 * @return a result of the form "0affb1".
	 */
	protected String toPaddedHexString(int value)
	{
		String valueString = Integer.toHexString(value);
		while (valueString.length() < 6) {
			valueString = "0" + valueString;
		}
		return valueString;
	}

	/**
	 * Generate the characters which can legally be used to form palette strings.
	 */
	protected void generatePaletteStringCharacters()
	{
		// legal characters in use are: '0'-'9' 'a'-'z' 'A'-'Z'
		int numberOfCharacters = 10 + 26 + 26;
		paletteStringCharacters = new char[numberOfCharacters];
		char value;
		int index = 0;

		for (value = '0'; value <= '9'; value++) {
			paletteStringCharacters[index++] = value;
		}

		for (value = 'a'; value <= 'z'; value++) {
			paletteStringCharacters[index++] = value;
		}

		for (value = 'A'; value <= 'Z'; value++) {
			paletteStringCharacters[index++] = value;
		}
	}

}

/*
$Log: XPMEncoder.java,v $
Revision 1.2  1999/04/07 16:30:53  luke
*** empty log message ***

Revision 1.1.1.1  1998/12/01 12:21:57  luke
imported

Revision 1.1.1.1  1998/09/01 02:48:50  luke
Imported from jimi_1_0_Release codebase

Revision 1.3  1998/07/01 22:14:27  chris
Removed an exception.printStackTrace() that spammed unnecessarily

Revision 1.2  1998/07/01 19:51:52  luke
Minor change taking off a slightly untidy looking ,"" at the end of
the output.

Revision 1.1  1998/06/26 19:07:09  luke
Created.

*/

