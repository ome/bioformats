package com.sun.jimi.util;

import java.io.*;

/**
 * No-frills adapter for using ObjectInput as an InputStream
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $
 **/
public class ObjectInputToStreamAdapter extends InputStream
{

	protected ObjectInput input_;

	public ObjectInputToStreamAdapter(ObjectInput input)
	{
		input_ = input;
	}

	public int read() throws IOException
	{
		return input_.read();
	}

	public int read(byte[] b) throws IOException
	{
		return input_.read(b);
	}

	public int read(byte[] b, int off, int len) throws IOException
	{
		return input_.read(b, off, len);
	}

	public long skip(long n) throws IOException
	{
		return input_.skip(n);
	}

	public int available() throws IOException
	{
		return input_.available();
	}

	public void close() throws IOException
	{
		input_.close();
	}

	public void mark(int readlimit)
	{
	}

	public boolean markSupported()
	{
		return false;
	}

}

