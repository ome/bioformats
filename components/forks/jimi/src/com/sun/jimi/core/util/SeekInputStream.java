package com.sun.jimi.core.util;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;

import com.sun.jimi.core.util.LEDataInputStream;
import com.sun.jimi.util.ByteCountInputStream;
import com.sun.jimi.util.ExpandableArray;

/**
 * Minimimum Buffering Seekable Linear processed Data Input Stream
 *

Problem
 TIFF file format
 IFD can be anywhere and so can image data no gaurantee on order
 This is annoying as could have to seek around back/forward.

Solution
 So create class which returns correct stream - [file header class]
 This class reads in and buffers chunks of file as necessary if it
 needs to skip forward through file - then when user requests input
 stream at location 'X' it can either hand back underlying stream or
 returns inputstream made from byte[] array buffered.

 It assumes that any file section will only ever be read once so that
 if the underlying stream returned it is not buffered and that does
 not matter in the slightest.

 This design to minimise the amount of extra memory needed to buffer the
 file to allow proper decoding. In most cases this class should not
 be buffering anything I expect for TIFF as generally IFD will preceed
 image data blocks.

 *
 *
 * Could do all this as a transparent InputStream class which implements
 * DataInput and provides one extra methods seek(long pos). Then this class
 * just acts as a proxy and selects the appropriatte input stream internally
 * whenever seek() is called and the individual methods do appropriatte
 * things.
 *
 *
 * This buffers as little as possible and will release any buffered pieces
 * the minute those pieces of data have been pumped out the InputStream.
 * As it assumes that anything is read only once.
 *		- currently doesnt release anything till whole object gone -
 *
 *
 * As a rule if skip() is used to go to a buffered memory area then the
 * data will not be exhausted in the buffered area by client because of the
 * way the data is only buffered on a skip to an area that wants to be read
 * and it is assumed that each byte in file is only passed back to client once.
 *
 *
 * As i need to have DataInput() interface to this stream and skip() only
 * exists on this object stream this has to be the implementer of DataInput.
 * This class makes use of DataInputStream and LEDataInputStream to provide
 * the functionality dependent on the constructor parameter be.
 *
 * Example of a key assumption this class makes.
 *
 * Start reading file at locatin 0 read 20 bytes.then seek to location 200
 * and read 20 bytes then seek to location 20 and read 180 bytes. This all
 * causes no problem but it is totall requiered that a seek() occur after
 * reading the 180 bytes as it is invalid to read any more data.
 *
 * @author	Robin Luiten
 * @version 1.0	17/Dec/1997
 **/

public class SeekInputStream extends FilterInputStream implements DataInput
{
	/** underlying input stream source of data **/
	InputStream in;

	/** current location relative start of file in bytes in underlying input stream **/
	int curOffset;

	/** 
	 * data from underlying input stream which had to be buffered because
	 * a seek caused data to be skipped over in the underlying input stream
	 **/
	ExpandableArray bufs;

	/** the input stream to use to read data **/
	DataInput setD;	// DataInputStream or LEDataInputStream on set stream
	InputStream setDIS;	// DataInputStream or LEDataInputStream on set stream

	/** flag indicating that reading data from input stream in big endian format **/
	boolean be;

	/** flag indicating using underlying stream directly in setD **/
	boolean underlying;

	/**
	 * @param be flag set indicates big endian input stream
	 * @param in input stream to read data from. This input stream is
	 * assumed to allready have been Buffered.
	 * @param consumed the number of bytes of data that has allready
	 * been consumed from the input stream so that any requests for
	 * an input stream at a location 'X' in the file relative to the
	 * start of the file can be catered for appropriately.
	 **/
	public SeekInputStream(boolean be, InputStream in, int consumed) throws IOException
	{
		super(in);
		this.be = be;
		this.in = in;
		this.bufs = new ExpandableArray();
		this.curOffset = consumed;
		setInputStream(curOffset);
	}

	/**
	 * Additional method to allow seeking to byte location in file.
	 * @param pos the byte offset in the file that is desired to seek to
	 * @excetion IOException thrown if seeking to a section of file not
	 * available because it was read elsewhere or it has allready been read.
	 **/
	public synchronized void seek(int pos) throws IOException
	{
		setInputStream(pos);
	}

	/**
	 * @param location the absolute byte location relative to the start
	 * of the file that the input stream returned should be set at.
	 * @return InputStream which could be the underling input stream or
	 * maybe a ByteArrayInputStream based on a previously buffered file 
	 * section.
	 * @exception IOException thrown if error encountered either buffering
	 * data when getting to location or cannot find buffered data
	 **/
	protected void setInputStream(int location) throws IOException
	{
		InputStream stream;
		underlying = false;
		if (location == curOffset)
		{
			stream = in;
			underlying = true;
		}
		else if (location > curOffset)
		{
			// buffer data that we need to move past
			int buflen = location - curOffset;
			ByteBuf bb = new ByteBuf(curOffset, buflen);
			int n = readFully(in, bb.buf, 0, buflen);
			if (n < 0)	// encountered EOF
			{
				curOffset += -n;
				throw new IOException();
			}
			else
				curOffset += n;

			bufs.addElement(bb);
			stream = in;				// now at correct location
			underlying = true;
		}
		else
		{
			stream = getBufIS(location);	// return if buffered
		}

		if (be)
			setD  =	new DataInputStream(stream);
		else
			setD  =	new LEDataInputStream(stream);
		setDIS = (InputStream)setD;
	}

	/**
	 * @return ByteArrayInputStream based on buffered input data if its available.
	 * @exception IOException cannot find buffered data for this location
	 **/
	protected InputStream getBufIS(int location) throws IOException
	{
		// check if we have this buffered
		int i;
		ByteBuf bb;

		for (i = 0; i < bufs.size(); ++i)
		{
			bb = (ByteBuf)bufs.elementAt(i);
			if (location >= bb.offset && location < bb.offset + bb.buf.length)
			{
				// found buffer
				int relative = location - bb.offset;
				return new ByteArrayInputStream(bb.buf, relative, bb.buf.length - relative);
			}
		}
		throw new IOException("not buffered");
	}

	/**
	 * read as much as possible from inputstream as possible
	 * @return how much data read from input stream. value returned
	 * is negative if end of file encountered. the value should be
	 * negated to figure out how much data was read upto end of file.
	 **/
	protected static int readFully(InputStream is, byte[] b, int off, int len) throws IOException
	{
		int n;
		for (n = 0; n < len;)
		{
			int count = is.read(b, n, len - n);
			if (count < 0)
				return -n;
			n += count;
		}
		return n;
	}

	public synchronized	final int read() throws IOException
	{

		int b = setDIS.read();
		if (b >= 0 && underlying)
			++curOffset;
		return b;
	}

	public synchronized	final int read(byte	b[], int off, int len) throws IOException
	{
		int readlen = setDIS.read(b, off, len);
		if (readlen < 0)	// encountered end of file
		{
			if (!underlying)	// if not underlying input stream
			{
				// ran out of byte array buffer 
				// so set the input stream in case we have other
				// buffers avail or we are back to underlying input stream
				setInputStream(curOffset);
				return 0;			// make client to retry
			}
			else
				return readlen;
		}
		else
		{
			if (underlying)
				curOffset += readlen;
			return readlen;
		}
	}

	/**
	 * close underlying input stream and release a few handles on objects
	 **/
	public void close() throws IOException
	{
		setD = null;
		in.close();
		in = null;
		bufs = null;
		curOffset = 0;
	}

	public long skip(long n) throws IOException
	{
		long skipcount = setDIS.skip(n);
		if (skipcount < 0)
		{
			if (!underlying)	// if not underlying input stream
			{
				// ran out of byte array buffer 
				// so set the input stream in case we have other
				// buffers avail or we are back to underlying input stream
				setInputStream(curOffset);
				return 0;			// make client to retry
			}
			else
				return skipcount;
		}
		else
		{
 			if (underlying)
	 			curOffset += skipcount;
			return skipcount;
		}
	}

	public int available() throws IOException
	{
		return setDIS.available();
	}

	/** this does nothing **/
	public synchronized void mark(int readlimit)
	{
	}

	/** @return false always for this class **/
	public boolean markSupported()
	{
		return false;
	}

	/** this does nothing **/
	public synchronized void reset() throws IOException
	{
	}



	public final void readFully(byte b[]) throws IOException
	{
		setD.readFully(b,	0, b.length);
		if (underlying)
			curOffset += b.length;
	}

	public final void readFully(byte b[], int off, int len)	throws IOException
	{
		setD.readFully(b,	off, len);
		if (underlying)
			curOffset += len;
	}

	public final int skipBytes(int n) throws IOException
	{
		int skipped =  setD.skipBytes(n);
		if (underlying)
			curOffset += skipped;
		return skipped;
	}

	public final boolean readBoolean() throws IOException
	{
		if (underlying)
			++curOffset;
		return setD.readBoolean();
	}

	public final byte readByte() throws	IOException
	{
		byte b =  setD.readByte();
		if (b >= 0)
			++curOffset;
		return b;
	}

	public final int readUnsignedByte()	throws IOException
	{
		int ret = setD.readUnsignedByte();
		if (ret >= 0)
			++curOffset;
		return ret;
	}

	public final short readShort() throws IOException
	{
		short ret = setD.readShort();
		if (underlying)
			curOffset += 2;
		return ret;
	}

	public final int readUnsignedShort() throws	IOException
	{ 
		int ret = setD.readUnsignedShort();
		if (ret >= 0 && underlying)
			curOffset += 2;
		return ret;
	}

	public final char readChar() throws	IOException
	{
		char ret = setD.readChar();
		if (underlying)
			curOffset += 2;
		return ret;
	}

	public final int readInt() throws IOException
	{
		int ret = setD.readInt();
		if (underlying)
			curOffset += 4;
		return ret;
	}

	public final long readLong() throws	IOException
	{
		long ret = setD.readLong();
		if (underlying)
			curOffset += 8;
		return ret;
	}

	public final float readFloat() throws IOException
	{
		return Float.intBitsToFloat(readInt());
	}

	public final double	readDouble() throws	IOException
	{
		return Double.longBitsToDouble(readLong());
	}

	/** this method should not be called in this class **/
	public final String	readLine() throws IOException
	{
		return new String();
	}

	/** this method should not be called in this class **/
	public final String	readUTF() throws IOException
	{
		return setD.readUTF();
	}

	/** this method should not be called in this class **/
	public final String readUTF(DataInput in) throws	IOException
	{
		return DataInputStream.readUTF(in);
	}
}


/**
 * Class purely to hold buffered byte data read from file.
 * @author	Robin Luiten
 * @version	1.0 17/Dec/1997
 **/
class ByteBuf
{
	/** buffered data **/
	byte[]	buf;

	/** where in file this data came from **/
	int		offset;

	ByteBuf(int offset, int len)
	{
		this.offset = offset;
		this.buf = new byte[len];
	}
}
