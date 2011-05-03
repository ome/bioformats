package	com.sun.jimi.core.util;

import java.io.DataOutput;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <H3>LEDataOutputStream - Little Endian Data Input Stream</H3>
 *
 * A data output stream lets an application write primitive Java data 
 * types to an output stream in a portable way. This particular output
 * stream is setup to write data in a format that would be written by
 * a system which uses little endian memory architecture.<p>
 *
 * This class does not implement writeUTF() as it is not required for
 * our application. In addition it is unclear if a writeUTF in little
 * endian format would ever be useful.<p>
 *
 * @author Paul Gray
 * @version 1.3	30/Dec/1997
 **/
public class LEDataOutputStream extends FilterOutputStream implements DataOutput
{
	protected int written;

	public LEDataOutputStream(OutputStream out)
	{
		super(out);
		written	= 0;
	}

	public synchronized	void write(int b) throws IOException
	{
		out.write(b);
		written++;
	}

	public synchronized	void write(byte	b[], int off, int len) throws IOException
	{
		out.write(b, off, len);
		written	+= len;
	}

	public void	flush()	throws IOException
	{
		out.flush();
	}

	public final void writeBoolean(boolean v) throws IOException
	{
		out.write(v	? 1	: 0);
		written++;
	}

	public final void writeByte(int	v) throws IOException
	{
		out.write(v);
		written++;
	}

	public final void writeShort(int v)	throws IOException
	{
		OutputStream out = this.out;
		out.write((v >>> 0)	& 0xFF);
		out.write((v >>> 8)	& 0xFF);
		written	+= 2;
	}

	public final void writeChar(int	v) throws IOException
	{
		OutputStream out = this.out;
		out.write((v >>> 0)	& 0xFF);
		out.write((v >>> 8)	& 0xFF);
		written	+= 2;
	}

	public final void writeInt(int v) throws IOException
	{
		OutputStream out = this.out;
		out.write((v >>>  0) & 0xFF);
		out.write((v >>>  8) & 0xFF);
		out.write((v >>> 16) & 0xFF);
		out.write((v >>> 24) & 0xFF);
		written	+= 4;
	}

	public final void writeLong(long v)	throws IOException
	{
		OutputStream out = this.out;
		out.write((int)(v >>>  0) &	0xFF);
		out.write((int)(v >>>  8) &	0xFF);
		out.write((int)(v >>> 16) &	0xFF);
		out.write((int)(v >>> 24) &	0xFF);
		out.write((int)(v >>> 32) &	0xFF);
		out.write((int)(v >>> 40) &	0xFF);
		out.write((int)(v >>> 48) &	0xFF);
		out.write((int)(v >>> 56) &	0xFF);
		written	+= 8;
	}

	public final void writeFloat(float v) throws IOException
	{
		writeInt(Float.floatToIntBits(v));
	}

	public final void writeDouble(double v)	throws IOException
	{
		writeLong(Double.doubleToLongBits(v));
	}

	public final void writeBytes(String	s) throws IOException
	{
		OutputStream out = this.out;
		int	len	= s.length();
		for	(int i = 0 ; i < len ; i++)
		{
			out.write((byte)s.charAt(i));
		}
		written	+= len;
	}

	public final void writeChars(String	s) throws IOException
	{
		OutputStream out = this.out;
		int	len	= s.length();
		for	(int i = 0 ; i < len ; i++)
		{
			int	v =	s.charAt(i);
			out.write((v >>> 8)	& 0xFF);
			out.write((v >>> 0)	& 0xFF);
		}
		written	+= len * 2;
	}

	/**
	 * dont call this it is not implemented.
	 **/
	public final void writeUTF(String str) throws IOException
	{
	}

	public final int size()
	{
		return written;
	}

}
