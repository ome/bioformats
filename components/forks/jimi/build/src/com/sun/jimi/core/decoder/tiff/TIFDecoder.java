package com.sun.jimi.core.decoder.tiff;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.util.ByteCountInputStream;
import com.sun.jimi.util.ExpandableArray;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.UnsupportedFormatException;
import com.sun.jimi.core.util.LEDataInputStream;
import com.sun.jimi.core.util.SeekInputStream;
import com.sun.jimi.core.util.Packbits;

/**
 * This class provides the implementation for a JimiDecoder which decodes
 * images from files stored in the TIFF file format.
 *
 * Currently  supports loading of uncompressed TIFFs of the following formats.
 *	Black & White
 *  Palette
 *  RGB
 * 	Support also for BLACK and White images compressed with the various CCITT optoins.
 *  No support for 2D encoding [this is rare] in T4 CCITT
 *  No support for the Uncompressed flag option for T4 CCITT
 *  No support for "uncompressed" optoin for T6 CCITT
 *  No support for "raw Group 3 encoded fax file"  - no TIFF header no support
 *  Support for Packbits compressed image formats.
 *  Support for LZW compressed image formats
 *
 * @author	Robin Luiten
 * @version $Revision: 1.3 $
 */
public class TIFDecoder extends JimiDecoderBase
{
	/** Signature at front of TIFF File if file written from Big Endian environment */
	public final short TIF_SIG_BE = 0x4D4D;

	/** Signature at front of TIFF File if file written from Little Endian environment */
	public final short TIF_SIG_LE = 0x4949;

	/** Version field of TIFF file format files **/	
	public final short TIF_VERSION = 0x002A;

	/** Lenght of the TIFF file format header. Signature + version + IFD offset **/
	public final short TIF_HEADER_SIZE = 8;

	/** jimi image to fill in **/
	private AdaptiveRasterImage ji;

	/** underlying input stream **/
	private InputStream in;

	/** buffered wrapper on underlying input stream **/
	private InputStream bufIn;

	/**
	 * This input stream hides the details of possible optional buffering
	 * required because TIFF files can have IFDs almost anywhere.
	 **/
	private SeekInputStream sis;

	/** decoder return state **/
	private int state;

	// next 3 memebers hold data for TIFF File Header
	/** TIFF file format header signature **/
	private short tifSignature;

	/** TIFF file format version number **/
	private short version;

	/** offset to first Image File Descriptor **/
	private int ifdOffset;

	public void initDecoder(InputStream in, AdaptiveRasterImage ji) throws JimiException
	{
		try
		{
			DataInputStream dIn;

			this.in = in;
			this.bufIn = in;
			dIn = new DataInputStream(bufIn);
			// Retrieve the TIFF File Header

			this.tifSignature = dIn.readShort();

			if ((tifSignature != TIF_SIG_LE && tifSignature != TIF_SIG_BE))
				throw new UnsupportedFormatException("not a TIFF file");

			// read size of short so far
			sis = new SeekInputStream(tifSignature == TIF_SIG_BE, bufIn, 2);

			this.version = sis.readShort();
			this.ifdOffset = sis.readInt();

			if (version != TIF_VERSION || ifdOffset < TIF_HEADER_SIZE)
				throw new UnsupportedFormatException("invalid TIFF Version or TIFF Header size");

			this.ji = ji;
			this.nextIFDoffset = ifdOffset;
			this.state = MOREIMAGES;
			IFDs = new ExpandableArray();
		}
		catch (IOException e)
		{
			throw new JimiException("IO Error reading TIFF header");
		}
	}

	/** offset to next ifd to read */
	int nextIFDoffset;

	/** collection of Image File Directories */
	ExpandableArray IFDs;

	public void skipImage()
		throws JimiException
	{
		try {
			IFD ifd = new IFD();
			ifd.read(nextIFDoffset, sis);
			nextIFDoffset = ifd.offset;
			if (nextIFDoffset == 0) {
				state &= ~MOREIMAGES;
			}
		}
		catch (IOException e) {
			throw new JimiException("Read error");
		}
	}

	public boolean driveDecoder() throws JimiException
	{
		try
		{

			if (nextIFDoffset == 0) {
				throw new JimiException("No more images.");
			}
			IFD ifd = new IFD();
			ifd.read(nextIFDoffset, sis);
			nextIFDoffset = ifd.offset;
			if (nextIFDoffset == 0) {
				state &= ~MOREIMAGES;
			}
			else {
				state |= MOREIMAGES;
			}
			IFDs.addElement(ifd);		// not sure care about keeping IFD's
			try {
				decode(ifd);
			} catch (Exception e) {}
			ji.addFullCoverage();
			state = 0;
			state |= INFOAVAIL;			// got image info
			state |= IMAGEAVAIL;		// got image
			if (nextIFDoffset != 0) {
				state |= MOREIMAGES;
			}
		}
		catch (IOException e)
		{
			throw new JimiException("TIFF IO Error");
		}
		return false;	// all done
	}

	public void freeDecoder() throws JimiException
	{
		in = null;
		bufIn = null;
		sis = null;
		ji = null;
	}

	public int getState()
	{
		return state;
	}

	public AdaptiveRasterImage getJimiImage()
	{
		return this.ji;
	}

	public void decode(IFD ifd) throws JimiException, IOException
	{
		IFDDecode ifdD  = new IFDDecode(this, ji, sis);
		ifdD.decodeTags(ifd);
		ifdD.decodeImage();
	}

	public int getCapabilities()
	{
		return MULTIIMAGE;		// no capabilities
	}

} // end of class TIFDecoder

