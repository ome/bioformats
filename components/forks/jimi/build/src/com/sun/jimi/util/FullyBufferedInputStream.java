package com.sun.jimi.util;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * This class implements a buffered input stream which
 * sits on top of any existing InputStream.
 *
 * This class buffers all data read through the underlying
 * stream and therefore provides a markable input stream for
 * any underlying stream.
 * At initialisation a mark() is made at the start of the
 * input stream to enable return to start of stream using reset();
 *
 * [eventually] The currently used portion of the buffer can be 
 * dropped if required via dropBuf().
 *
 * This class could be extended to keep the buffered input
 * data stream in a local file cache in the future. This
 * would involved checking for write capabilities and the
 * definition [probably in environment] for where to write
 * the files too.
 *
 *
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * @@ can modify this so that it only returns data it has
 * buffered, then use ReadFully() method which will drive
 * read() to get all the data. [ simplifies this class a lot ]
 *
 * Cant do this, because if avialable() returns a number then logically
 * read() must be able to return the number in available() in one call.
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 *
 *
 * @author	Robin Luiten
 * @version	1.0	21/Aug/1997
 */
public class FullyBufferedInputStream extends FilterInputStream
{
	/**
	 * The underlying input stream. 
	 */
	protected InputStream in;

	/**
	 * maintains the sequential set of allocated memory
	 * buffers which in total hold all the buffered data.
	 */
	protected Vector buffers;

	/**
	 * index of next location in buffer that is free to use
	 */
	protected int count;

	/**
	 * index of next character to deliver
	 */
	protected int pos;

	/**
	 * size of each allocated buffer stored in buffers
	 */
	protected int bufSize;

	/**
	 * Marked location in input stream.
	 * This is -1 if not set or if its been invalidated
	 * If set will always be to a location within the available data. 
	 * ie markpos <= count. It is possible to mark location 0 when 
	 * no data buffered and it is possible to have a mark at the very
	 * end of the data stream.
	 */
	protected int markpos;

	/**
	 * default size of chunks of input stream to store and
	 * read in at a time.
	 */
	protected final static int BUFSIZE = 8096;

	/**
	 * Constructor
	 */
	public FullyBufferedInputStream(InputStream in)
	{
		this(in, BUFSIZE);			// file buffered in 8k chunks
	}

	/**
	 * Allows control of the bufSize for buffering
	 */
	public FullyBufferedInputStream(InputStream in, int bufSize)
	{
		super(in);
		this.in = in;
		this.bufSize = bufSize;
		buffers = new Vector();
		dropBuf();				// initialise state
	}

	/**
	 * Release all buffered data and memory.
	 * This is equivalent to intialising the buffer system

	 - Rob fiX
	 - this is stuffed - what if i call this when i havnt
	 - passed all the data to client yet... should only 
	 - clear data that has allready been sent to client.
	 - move up unsent dat to begining of buffer area then
	 - nuke the rest.

	 */
	public synchronized void dropBuf()
	{
		buffers.removeAllElements();
		count = 0;
		pos = 0;
		markpos = -1;
	}

	/**
	 * This method is only called when there is not sufficient
	 * data buffered to supply current needs. This method allocates
	 * another data buffer if required and reads more data in.
	 * This method() is not synchronized as it is assumed it is
	 * only called from synchronized methods().
	 */
	private void fillBuffer() throws IOException
	{
		int numBuffers;
		
		// which buffer in vector contains space to read data into
		int countBIndex = count / bufSize;
		numBuffers = buffers.size();

		if (countBIndex >= numBuffers)
		{
			// need to allocate extra buffer to read data
			buffers.addElement(new byte[bufSize]);
			numBuffers = buffers.size();
		}

		// the buffer to read data into will always be the 
		// last buffer in the vector
		byte[] buf = (byte[]) buffers.lastElement();

		// offset to available space in buffer
		int offsetCount = count - ((numBuffers-1) * bufSize);

		// remaining space in buffer to read data into
		int remainCount = bufSize - offsetCount;
		int bytesRead = in.read(buf, offsetCount, remainCount);

		if (bytesRead != -1)
			count += bytesRead;
	}

	public synchronized int read() throws IOException
	{
		if (pos >= count)	// should never be ">" - but just in case
			fillBuffer();

		// if no more data read then must be at end of file.
		if (pos >= count)
			return -1;

		// which buffer contains the current position
		int posBIndex = pos / bufSize;
		byte[] buf = (byte[])buffers.elementAt(posBIndex);

		int offsetPos = pos - (posBIndex * bufSize);
		++pos;											// next position
		return buf[offsetPos] & 0xFF;
	}

	// not necessary to overide this method.
	// public int read(byte b[]) throws IOException

	public synchronized int read(byte b[], int off, int len) throws IOException
	{
		int origLen = len;

		while (len > 0)				// while desired data left to read
		{
			if (pos >= count)	// should never be ">" - but just in case
				fillBuffer();

			// if no more data read then must be at end of file.
			if (pos >= count)
			{
				if (origLen == len)			// if no data read yet
					return -1;
				else
					return (origLen - len);	// return what we do have
			}
			else
			{
				int posBIndex = pos / bufSize;
				int countBIndex = count / bufSize;
				int numBuffers = buffers.size();
				byte[] buf = (byte[])buffers.elementAt(posBIndex);

				int offsetCount = 0;
				int offsetPos   = pos - (posBIndex * bufSize);
				int actualBufSize = bufSize;

				// position is in the same block as count 
				// make sure stay within loaded data boundaries
				if (posBIndex == countBIndex)
					actualBufSize =  count - (countBIndex * bufSize);

				// remaining bytes
				int remainPos = Math.min(actualBufSize - offsetPos, 
										 actualBufSize - offsetCount);
				int readLen = Math.min(remainPos, len);

				System.arraycopy(buf, offsetPos, b, off, readLen);
				pos += readLen;	// update input location
				off += readLen;	// update output location
				len -= readLen;	// update remaining to read
			}
		}
		return origLen - len;
	}

	public synchronized long skip(long n) throws IOException
	{
		return -1;
	}

	public synchronized void close() throws IOException
	{
		in.close();
	}

	public synchronized int available() throws IOException
	{
		return count - pos + in.available();
	}

	/**
	 * Mark a location in the input stream, note that the
	 * readlimit is totally ingored.
	 * Any call to this will override any previous mark.
	 * @input readlimit this is ignored for this input stream class
	 */
	public synchronized void mark(int readlimit)
	{
		markpos = pos;
	}

	/**
	 * Reset position to the mark.
	 * This does not invalidate the mark.
	 */
	public synchronized void reset() throws IOException
	{
		if (markpos < 0)
			throw new IOException("Resetting to invalid mark");
		pos = markpos;
	}

	/**
	 * Marks are always supported by this input stream.
	 */
	public boolean markSupported()
	{
		return true;
	}
}
