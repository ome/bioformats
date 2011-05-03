package com.sun.jimi.core.decoder.tiff;

import java.io.*;
import com.sun.jimi.core.util.SeekInputStream;

/**
 * @author	Robin Luiten
 * @version	1.0	18/Dec/1997
 **/
class TIFField
{
	// TIFF Field Tag Types
	public static final int BYTE		= 1;	//  8 bits
	public static final int ASCII		= 2;
	public static final int SHORT		= 3;	// 16 bits
	public static final int LONG		= 4;	// 32 bits
	public static final int RATIONAL	= 5;

	// TIFF 6.0 additions - really only custom extensions use the below
	public static final int SBYTE		= 6;
	public static final int UNDEFINE	= 7;
	public static final int SSHORT		= 8;
	public static final int SLONG		= 9;
	public static final int SRATIONAL	= 10;
	public static final int FLOAT		= 11;
	public static final int DOUBLE		= 12;

	// unused at the moment.
	public static final int dataWidths[] =
	{
		1,			// no value 
		1,			// BYTE
		1,			// ASCII
		2,			// SHORT
		4,			// LONG
		8,			// RATIONAL
		1,			// SBYTE
		1,			// UNDEFINED
		2,			// SSHORT
		4,			// SLONG
		8,			// SRATIONAL
		4,			// FLOAT
		8			// DOUBLE
	};

	/** field identifier - the tag **/
	short id;

	/** scalar type of identifier **/
	short type;

	/** number of items in the tiff field **/
	int count;

	/**
	 * byte offset or data value.
	 * if the data is read in as an int if it is an offset or int size
	 * if the data is read in as short(s) then the data is read in two
	 * sucessive shorts with the first short being placed in the two most
	 * significant bytes as in Big Endian Format [which is Java standard]
	 * if the data is read inas byte(s) then the data is read in as 4
	 * bytes with first byte in Most Significant byte as if Big Endian
	 **/
	int offset;

	public final void read(SeekInputStream sis) throws IOException
	{
		id   = sis.readShort();
		type = sis.readShort();
		count = sis.readInt();

		// extra fiddling to read in the data in a useful form.
		if (count * dataWidths[type] > 4)
		{
			// read in as offset
			offset = sis.readInt();
		}
		else
		{
			// handle reading in of data values that are stored in
			// the offset field.
			switch (type)
			{
			case BYTE:
			case ASCII:
			case SBYTE:
			case UNDEFINE:	// 8 bit size
				offset  = sis.readUnsignedByte() << 24;	// MSB
				offset += sis.readUnsignedByte() << 16;
				offset += sis.readUnsignedByte() << 8;
				offset += sis.readUnsignedByte();		// LSB
				break;

			case SHORT:
			case SSHORT:	// 16 bit size
				offset  = sis.readUnsignedShort() << 16;
				offset += sis.readUnsignedShort();
				break;

			default: // picks up LONG, SLONG, FLOAT types
				offset = sis.readInt();			// 4 byte data value
				break;
			}
		}
	}

	public final boolean equals(int id)
	{
		return this.id == id;
	}

	/**
	 * This method should only be called for BYTE, SHORT, LONG
	 * SBYTE, SSHORT, SLONG. And only works for count == 1
,	 * @return the value for the Field
	 **/
	public int getInt(SeekInputStream sis) throws IOException
	{
		int val = 0;
		boolean error = false;

		if (count == 1)	// invalid to call this method for this
		{
			switch (type)
			{
			case BYTE:	val =         (offset >> 24) & 0xFF; break;
			case SBYTE:	val = ((byte)((offset >> 24) & 0xFF)); break;
			case SHORT:	val =          (offset >> 16) & 0xFFFF; break;
			case SSHORT:val = ((short)((offset >> 16) & 0xFFFF)); break;
			case LONG:	val = offset; break;	// returning as signed careful
			case SLONG:	val = offset; break;
			default: error = true; break;
			}
		}
		else
			error = true;

		if (error)				// should not have called this method 
			throw new RuntimeException("wrong method " + toString()); // hack for now
		return val;
	}

	/** @returns the rational divided out in a double **/
	public float getRational(SeekInputStream sis) throws IOException
	{
		long num;
		long den;

		sis.seek(offset);
		if (type == RATIONAL)
		{
			num = sis.readInt();
			if (num < 0)
				num &= 0xFFFFFFFFL;
			den = sis.readInt();
			if (den < 0)
				den &= 0xFFFFFFFFL;
		}
		else // SRATIONAL
		{
			num = sis.readInt();
			den = sis.readInt();
		}
		return (float)(num/den);
	}

	public float[] getRationalArray(SeekInputStream sis) throws IOException
	{
		long num;
		long den;
		float[] f = new float[count];

		sis.seek(offset);
		for (int i = 0; i < count; ++i)
		{
			if (type == RATIONAL)
			{
				num = sis.readInt();
				if (num < 0)
					num &= 0xFFFFFFFFL;
				den = sis.readInt();
				if (den < 0)
					den &= 0xFFFFFFFFL;
			}
			else // SRATIONAL
			{
				num = sis.readInt();
				den = sis.readInt();
			}
			f[i] = (float)(num/den);
		}
		return f;
	}

	/** @return int[] for any array of short[] or int[] **/
	public int[] getIntArray(SeekInputStream sis) throws IOException
	{
		int[] arr = new int[count];

		if (type == BYTE || type == SBYTE || type == SHORT || type == SSHORT)
		{
			short[] s = getShortArray(sis);

			// convert to int array and return
			for (int i = 0; i < count; ++i)
				arr[i] = s[i];
		}
		else if (type == LONG || type == SLONG)
		{
			if (count == 1)
				arr[0] = offset;
			else	// go to offset and get data
			{
				sis.seek(offset);
				for (int i = 0; i < count; ++i)
					arr[i] = sis.readInt();
			}
		}
		else
			throw new RuntimeException("wrong method " + toString()); // hack for now

		return arr;
	}

	public short[] getShortArray(SeekInputStream sis) throws IOException
	{
		short[] arr = new short[count];

		if (type == BYTE || type == SBYTE)
		{
			byte[] b = getByteArray(sis);

			// convert to short array and return
			for (int i = 0; i < count; ++i)
				arr[i] = b[i];
		}
		else if (type == SHORT || type == SSHORT)
		{
			if (count <= 2)
			{
				if (count >= 1)
					arr[0] = (short)((offset >> 16) & 0xFFFF);
				if (count >= 2)
					arr[1] = (short)((offset      ) & 0xFFFF);
			}
			else	// go to offset and get data
			{
				sis.seek(offset);
				for (int i = 0; i < count; ++i)
					arr[i] = sis.readShort();
			}
		}
		else
			throw new RuntimeException("wrong method " + toString()); // hack for now

		return arr;
	}

	public byte[] getByteArray(SeekInputStream sis) throws IOException
	{
		byte[] arr = new byte[count];

		if (type == BYTE || type == SBYTE)
		{
			if (count <= 4)
			{
				if (count >= 1)
					arr[0] = (byte)((offset >> 24) & 0xFF);
				if (count >= 2)
					arr[1] = (byte)((offset >> 16) & 0xFF);
				if (count >= 3)
					arr[2] = (byte)((offset >>  8) & 0xFF);
				if (count == 4)
					arr[3] = (byte)((offset      ) & 0xFF);
			}
			else	// go to offset and get data
			{
				sis.seek(offset);
				for (int i = 0; i < count; ++i)
					arr[i] = sis.readByte();
	 		}
		}
		else
			throw new RuntimeException("wrong method " + toString()); // hack for now

		return arr;
	}

	/**
	 * @return Ascii data. The null byte is consumed and any pad byte to make
	 * and even length set of bytes.
	 **/
	String getString(SeekInputStream sis) throws IOException
	{
		byte[] b = getByteArray(sis);
	//	return new String(b, 0, b.length - 1); // exclude trailing NULL
	// ^^^^^ 1.0 incompatible
		return new String(b, 0, 0, b.length - 1); // exclude trailing NULL

	}

	public static final String typeNames[] =
	{
		"no value", "BYTE", "ASCII", "SHORT", "LONG",
		"RATIONAL",	"SBYTE","UNDEFINED", "SSHORT",
		"SLONG", "SRATIONAL", "FLOAT", "DOUBLE"
	};

	public String toString()
	{
		return " id " + id + " count " + count + " type (" + typeNames[type] + ") " + type + " offset " + offset;
	}
}
