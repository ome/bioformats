package	com.sun.jimi.core.util;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * <H3>LEDataInputStream	- Little Endian	Data Input Stream</H3>
 *
 * This	class implements an	input stream filter	to allow reading
 * of java native datatypes	from an	input stream which has those
 * native datatypes	stored in a	little endian byte order.<p>
 *
 * This	is the sister class	of the DataInputStream which allows
 * for reading of java native datatypes	from an	input stream with
 * the datatypes stored	in big endian byte order.<p>
 *
 * This	class implements the minimum required and calls	DataInputStream
 * for some	of the required	methods	for	DataInput.<p>
 *
 * Not all methods are implemented due to lack of immediatte requirement
 * for that functionality. It is not clear if it is ever going to be
 * functionally required to be able to read UTF data in a LittleEndianManner<p>
 *
 * @author	Robin Luiten
 * @version	1.1	15/Dec/1997
 */
public class LEDataInputStream extends FilterInputStream implements DataInput
{
	/**
	 * To reuse	some of	the	non	endian dependent methods from
	 * DataInputStreams	methods.
	 */
	DataInputStream	dataIn;

	public LEDataInputStream(InputStream in)
	{
		super(in);
		dataIn = new DataInputStream(in);
	}

	public void close() throws IOException
	{
		dataIn.close();		// better close as we create it.
							// this will close underlying as well.
	}

	public synchronized	final int read(byte	b[]) throws	IOException
	{
		return dataIn.read(b, 0, b.length);
	}

	public synchronized	final int read(byte	b[], int off, int len) throws IOException
	{
		int	rl = dataIn.read(b,	off, len);
		return rl;
	}

	public final void readFully(byte b[]) throws IOException
	{
		dataIn.readFully(b,	0, b.length);
	}

	public final void readFully(byte b[], int off, int len)	throws IOException
	{
		dataIn.readFully(b,	off, len);
	}

	public final int skipBytes(int n) throws IOException
	{
		return dataIn.skipBytes(n);
	}

	public final boolean readBoolean() throws IOException
	{
		int	ch = dataIn.read();
		if (ch < 0)
			throw new EOFException();
		return (ch != 0);
	}

	public final byte readByte() throws	IOException
	{
		int	ch = dataIn.read();
		if (ch < 0)
			throw new EOFException();
		return (byte)(ch);
	}

	public final int readUnsignedByte()	throws IOException
	{
		int	ch = dataIn.read();
		if (ch < 0)
			throw new EOFException();
		return ch;
	}

	public final short readShort() throws IOException
	{
		int	ch1	= dataIn.read();
		int	ch2	= dataIn.read();
		if ((ch1 | ch2)	< 0)
			 throw new EOFException();
		return (short)((ch1	<< 0) +	(ch2 <<	8));
	}

	public final int readUnsignedShort() throws	IOException
	{ 
		int	ch1	= dataIn.read();
		int	ch2	= dataIn.read();
		if ((ch1 | ch2)	< 0)
			 throw new EOFException();
		return (ch1	<< 0) +	(ch2 <<	8);
	}

	public final char readChar() throws	IOException
	{
		int	ch1	= dataIn.read();
		int	ch2	= dataIn.read();
		if ((ch1 | ch2)	< 0)
			 throw new EOFException();
		return (char)((ch1 << 0) + (ch2	<< 8));
	}

	public final int readInt() throws IOException
	{
		int	ch1	= dataIn.read();
		int	ch2	= dataIn.read();
		int	ch3	= dataIn.read();
		int	ch4	= dataIn.read();
		if ((ch1 | ch2 | ch3 | ch4)	< 0)
			 throw new EOFException();
		return ((ch1 <<	0) + (ch2 << 8)	+ (ch3 << 16) +	(ch4 <<	24));
	}

	public final long readLong() throws	IOException
	{
		int	i1 = readInt();
		int	i2 = readInt();
		return ((long)(i1) & 0xFFFFFFFFL) +	(i2	<< 32);
	}

	public final float readFloat() throws IOException
	{
		return Float.intBitsToFloat(readInt());
	}

	public final double	readDouble() throws	IOException
	{
		return Double.longBitsToDouble(readLong());
	}

	/**
	 * dont call this it is not implemented.
	 * @return empty new string 
	 **/
	public final String	readLine() throws IOException
	{
		return new String();
	}

	/**
	 * dont call this it is not implemented
	 * @return empty new string 
	 **/
	public final String	readUTF() throws IOException
	{
		return new String();
	}

	/**
	 * dont call this it is not implemented
	 * @return empty new string 
	 **/
	public final static	String readUTF(DataInput in) throws	IOException
	{
		return new String();
	}
}

