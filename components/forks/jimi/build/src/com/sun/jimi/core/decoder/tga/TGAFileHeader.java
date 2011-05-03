package com.sun.jimi.core.decoder.tga;

import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.util.Enumeration;

import com.sun.jimi.core.decoder.tga.TGAColorMap;
import com.sun.jimi.core.util.LEDataInputStream;

/*
	How to recognise a TGA file.
		[ there is no easy signature - this may do though ]

	@1	byte value in 0,1
		// @1 colour map type
	@2  byte value in 1,2,3,9,10,11
		// @2 image type
	@3  LE short value <=  @5 LE short value	
		// @3 short is first color map index to use
		// @5 short is size of color map
	@16 byte value in 8,15,16,24,32
		// @16 byte is pixel depth
	@17 byte value & 0xF ==  0 or 8
		// @17 image descriptor
*/

/**
 * Support structure for file footer of new tga format
 * This is not a seperate class as it really only makes
 * sense in context of the FileHeader therefore it is only
 * locale. However this may never be used due to the
 * InputStream environment and the difficulties of reading
 * this data at the end of the file.
 *
 * @author	Robin Luiten
 * @date	03/Sep/1997
 * @version	1.1
 */ /*
class TGAFileFooter
{
	final static String TGA_SIGNATURE = "TRUEVISION-XFILE";
	final static int sigLen = TGA_SIGNATURE.length();

	//** offset to extension area * /
	int extensionArea;

	//** offset to developer directory in file * /
	int developerDir;

	byte[] sigBuf = new byte[16];
	String signature;

	byte fill1;
	byte fill2;

	TGAFileFooter(LEDataInputStream in) throws IOException
	{
		extensionArea = in.readInt();
		developerDir = in.readInt();
		in.read(sigBuf, 0, sigLen);
		signature = new String(sigBuf);
		fill1 = in.readByte();
		fill2 = in.readByte();
	}
} */


/**
 * This class reads in all of the TGA image header in addition it also
 * reads in the imageID field as it is convenient to handle that here.
 *
 * @author	Robin Luiten
 * @date	03/Sep/1997
 * @version	1.1
 */
public class TGAFileHeader
{
	/** Type of this TGA file format */
	int tgaType;

	/** initial TGA image data fields */
	int idLength;		// byte value
	int colorMapType;	// byte value
	int imageType;		// byte value

	/** TGA image colour map fields */
	int firstEntryIndex;
	int colorMapLength;
	byte colorMapEntrySize;

	/** TGA image specification fields */
	int xOrigin;
	int yOrigin;
	int width;
	int height;
	byte pixelDepth;
	byte imageDescriptor;

	/** bitfields in imageDescriptor */
	byte attribPerPixel;	// how many attribute bits per pixel
	boolean leftToRight;	// order of data on scan line
	boolean topToBottom;	// order scan lines stored
	byte interleave;		// how rows are stored in image data

	byte[] imageIDbuf;
	String imageID;

	/** Set of possible file format TGA types */
	final static int TYPE_NEW = 0;
	final static int TYPE_OLD = 1;
	final static int TYPE_UNK = 2;	// cant rewind stream so unknown for now.

	/**  Set of possible image types in TGA file */
	final static int NO_IMAGE = 0;		// no image data
	final static int UCOLORMAPPED = 1;	// uncompressed color mapped image
	final static int UTRUECOLOR = 2;		// uncompressed true color image
	final static int UBLACKWHITE = 3;		// uncompressed black and white image
	final static int COLORMAPPED = 9;		// compressed color mapped image
	final static int TRUECOLOR = 10;		// compressed true color image
	final static int BLACKWHITE = 11;		// compressed black and white image

	/** Field image descriptor bitfield values definitions */
	final static int ID_ATTRIBPERPIXEL = 0xF;
	final static int ID_LEFTTORIGHT = 0x10;
	final static int ID_TOPTOBOTTOM = 0x20;
	final static int ID_INTERLEAVE  = 0xC0;

	/** Field image descriptor / interleave values */
	final static int I_NOTINTERLEAVED = 0;
	final static int I_TWOWAY = 1;
	final static int I_FOURWAY = 2;

	public TGAFileHeader(LEDataInputStream in) throws IOException
	{
		int ret;

		tgaType = TYPE_OLD; // dont try and get footer.

		// initial header fields
		idLength = in.readUnsignedByte();	
		colorMapType = in.readUnsignedByte();	
		imageType = in.readUnsignedByte();	

		// color map header fields
		firstEntryIndex = in.readUnsignedShort();
		colorMapLength = in.readUnsignedShort();
		colorMapEntrySize = in.readByte();

		// TGA image specification fields
		xOrigin = in.readUnsignedShort();
		yOrigin = in.readUnsignedShort();
		width = in.readUnsignedShort();
		height = in.readUnsignedShort();
		pixelDepth = in.readByte();
		imageDescriptor = in.readByte();

		attribPerPixel = (byte)(imageDescriptor & ID_ATTRIBPERPIXEL);
		leftToRight = (imageDescriptor & ID_LEFTTORIGHT) != 0;	// not used ?
		topToBottom = (imageDescriptor & ID_TOPTOBOTTOM) != 0;
		interleave  = (byte)((imageDescriptor & ID_INTERLEAVE) >> 6);

		if (idLength > 0)
		{
			imageIDbuf = new byte[idLength];
			in.read(imageIDbuf, 0, idLength);
			imageID = new String(imageIDbuf, 0);
		}
	}

	public String toString()
	{
		return "TGA Header " +
		" id length: " + idLength +
		" color map type: "+ colorMapType +
		" image type: "+ imageType +
		" first entry index: " + firstEntryIndex +
		" color map length: " + colorMapLength +
		" color map entry size: " + colorMapEntrySize +
		" x Origin: " + xOrigin +
		" y Origin: " + yOrigin +
		" width: "+ width +
		" height: "+ height +
		" pixel depth: "+ pixelDepth +
		" image descriptor: "+ imageDescriptor +
		(imageIDbuf == null ? "" : (" ID String: " + imageID));
	}
}
