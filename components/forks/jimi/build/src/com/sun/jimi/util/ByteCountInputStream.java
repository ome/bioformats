package	com.sun.jimi.util;

import java.io.*;

/**
 * This	class provides a way to	count the bytes	of data	that have
 * been	read from the underlying input stream at any given time.
 * This class has to be used carefully, if it is used underneath
 * a buffering class then it just reports how far the buffering
 * has gotten.
 *
 * @author Robin Luiten
 * @version	1.0	09/Dec/1997
 **/
public class ByteCountInputStream extends InputStream
{
	protected InputStream in;
	protected long count;

	public ByteCountInputStream(InputStream in)
	{
		this.in	= in;
		count = 0;
	}

	/**
	 * @return the number of bytes which have been read through
	 * this class from the underlying input stream
	 **/
	public long getCount()
	{
		return count;
	}

	/**
	 * To be complete a way to allow from which point the couting takes
	 * place.
	 **/
	public synchronized void resetCount()
	{
		count = 0;
	}

	public int read() throws IOException
	{
		++count;
		return in.read();
	}

	public int read(byte b[]) throws IOException
	{
		int retLen = read(b, 0, b.length);
		count += retLen;
		return retLen;
	}

	public int read(byte b[], int off, int len)	throws IOException
	{
		int retLen = in.read(b, off, len);
		count += retLen;
		return retLen;
	}

	public long	skip(long n) throws	IOException
	{
		long retLen = in.skip(n);
		count += retLen;
		return retLen;
	}

	public int available() throws IOException
	{
		return in.available();
	}

	public void	close()	throws IOException
	{
		in.close();
	}

	public synchronized	void mark(int readlimit)
	{
		in.mark(readlimit);
	}

	public synchronized	void reset() throws	IOException
	{
		in.reset();
	}

	public boolean markSupported()
	{
		return in.markSupported();
	}
}
