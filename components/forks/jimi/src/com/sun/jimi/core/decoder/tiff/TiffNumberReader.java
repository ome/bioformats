
package com.sun.jimi.core.decoder.tiff;

//
//
// TiffNumberReader
//
// Modified version do not require endianess handling as input streams
// in TIFF decoder does so. This only needs to handle bytes as an
// initial version of the decompressor
//
//

class TiffNumberReader 
{

	private	  int	bytePtr ;
	protected byte	data[] ;

	TiffNumberReader( byte d[] )
	{
		bytePtr = 0 ;
		data = d ;
	}

	final public byte readByte()
	{
		return data[bytePtr++];
	}

	public String toString()
	{
		return "data len " + data.length + " index " + bytePtr;
	}
}

