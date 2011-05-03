/*
Tabstops	4
Created
18/Dec/1997	RL
	Created
*/
package com.sun.jimi.core.decoder.tiff;

import java.io.*;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import com.sun.jimi.core.util.SeekInputStream;

/**
 * TIF Image File Directory
 * @author	Robin Luiten
 * @version	$Revision: 1.1.1.1 $
 **/
class IFD
{
	/** number of fields in the IFD **/
	int count;

	/** The fields for this IFD **/
	TIFField[] fields;

	/** Offset to next IFD **/
	int offset;

	/**
	 * @param offset position in the file at which the IFD entry
	 * should be read
	 **/
	public void read(int offset, SeekInputStream sis) throws IOException
	{
		int i;
		sis.seek(offset);						// go to IFD
		count = sis.readUnsignedShort();

		fields = new TIFField[count];
		for (i = 0; i < count; ++i)
		{
			fields[i] = new TIFField();
			fields[i].read(sis);
		}

		this.offset = sis.readInt();
	}

	/**
	 * @param id tif tag of desired field
	 * @return the tiff field in the IFD that matches the id. null if not found
	 **/
	TIFField getField(int id)
	{
		int i;
		for (i = 0; i < count; ++i)
		{
			if (fields[i].equals(id))
				return fields[i];
		}
		return null;
	}

	Enumeration getFields()
	{
		return new TIFFieldEnumerator(this);
	}

}


/**
 * @author	Robin Luiten
 * @version	$Revision: 1.1.1.1 $
 **/
class TIFFieldEnumerator implements Enumeration
{
	/** reference to the IFD to enumerate out **/
	IFD ifd;

	/** which TIFField we are up to **/
	int idx;

	TIFFieldEnumerator(IFD ifd)
	{
		this.ifd = ifd;
		idx = 0;
	}

	public boolean hasMoreElements()
	{
		return idx < ifd.count;
	}

	public Object nextElement()
	{
		if (idx < ifd.count)
			return ifd.fields[idx++];
		throw new NoSuchElementException("TIFFieldEnumerator");
	}
}

