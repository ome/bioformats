package com.sun.jimi.util;

import java.io.*;

/**
 * No-frills adapter for using ObjectOutput as an OutputStream
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $
 **/
public class ObjectOutputToStreamAdapter extends OutputStream
{

	protected ObjectOutput output_;

	public ObjectOutputToStreamAdapter(ObjectOutput Output)
	{
		output_ = Output;
	}

	public void write(int b) throws IOException
	{
		output_.write(b);
	}

	public void write(byte[] b) throws IOException
	{
		output_.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException
	{
		output_.write(b, off, len);
	}

	public void close() throws IOException
	{
		output_.close();
	}

}

