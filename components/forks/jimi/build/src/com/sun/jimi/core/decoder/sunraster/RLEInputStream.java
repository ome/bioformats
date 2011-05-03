package com.sun.jimi.core.decoder.sunraster;

import java.io.*;

public class RLEInputStream extends FilterInputStream
{

	/** value of the RLE escape code **/
	protected static final int RLE_ESCAPE = 0x80;

	/** the number of values left in the current run **/
	protected int runLength_;
	/** the value of the current run **/
	protected int runValue_;

	public RLEInputStream(InputStream source)
	{
		super(source);
	}

	public int read() throws IOException
	{
		// if there is an unfinished run in progress
		if (runLength_ != 0)
		{
			runLength_--;
			return runValue_;
		}

		// read a new value
		int value = ( (int)super.read() ) & 0xff;

		// if it's an escape code
		if (value == RLE_ESCAPE)
		{
			// read the run-length, or count of how many values are in the run
			int count = ( (int)super.read() ) & 0xff;

			// if the count is 0, it is an escaped 0x80
			if (count == 0)
				return RLE_ESCAPE;

			// set the remaining run length to the count.  this is valid
			// because the actual number of values in the run is count + 1, and
			// one is returned here without decrementing the count
			runLength_ = count;
			runValue_ = ( (int)super.read() ) & 0xff;

			return runValue_;
		}

		// the value is a literal, so just return it
		return value;
	}

	public int read(byte[] buf, int off, int len)
		throws IOException
	{
		int count;
		for (count = 0; count < len; count++)
		{
			int value = read();
			// if end of stream
			if (value == -1)
				break;

			buf[off + count] = (byte)value;
		}
		return count;
	}

	public int read(byte[] buf)
		throws IOException
	{
		return read(buf, 0, buf.length);
	}

	public void skip(int n)
		throws IOException
	{
		for (int i = 0; i < n; i++)
			read();
	}

	public boolean markSupported()
	{
		return false;
	}

}

