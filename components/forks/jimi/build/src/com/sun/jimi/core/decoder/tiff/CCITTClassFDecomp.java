
//
// 28/Jan/1998	RL
// New source version from Alfonso
// NOTE: endOfLine calls advancePointer() and in the case where
// endOfLine is called on the last line in a strip and the compression
// COMPRESSION_CCITTFAX3 - ie this decompresor then the strip array of
// data is actually access past the end of the data. Therefore the allocated
// strip byte[] array is allocated 2 bytes bigger to avoid this error.
// This is not a good fix but it seems to work fine.
//

package com.sun.jimi.core.decoder.tiff;

//
//
// CCITTClassFDecomp
//
//  Es una variante del compresor CCITT3d1 que
//  incorpora las opciones de formato de fax
//
//  Realmente no es necesaria para AED, pero la
//  hago porque me da la gana
//
class CCITTClassFDecomp extends CCITT3d1Decomp
{
	private boolean fillBits ;
	int lastRow;
	int curRow;

	public final boolean getFillBits()
	{
		return fillBits ;
	}

	CCITTClassFDecomp( TiffNumberReader r, int aBitDirection, boolean fillBitsOption )
	{
		super( r , aBitDirection );
		fillBits = fillBitsOption ;
	}
	public void setRowsPerStrip(int rows)
	{
		lastRow = rows;
	}
	public void begOfPage() {}
	public void begOfStrip()
	{

		super.begOfStrip();
		// leading byte-aligned EOL code will take up 16 bits, so throw away
		// the first 2 bytes
		readByte();
		readByte();
	}
	protected byte[] pbuf = new byte[100];
	protected int bufVals = 0;

	public byte readByte()
	{
		if (bufVals > 0) {
			return pbuf[--bufVals];
		}
		else return super.readByte();
	}

	public void pushValue(byte value)
	{
		pbuf[bufVals++] = value;
	}

	/**
	 * Move forward in the file.
	 * @param offset the number of bits to advance through
	 */
	private void advancePointer(int offset)
	{
		// if already aligned at end of byte
		if ((bitOffset != 0) && (bitOffset % 8 == 0)) {
			byteSource = readByte();
		}
		int bit_offset = (bitOffset % 8) + offset;
		int new_bytes = bit_offset / 8;
		while (new_bytes-- > 0) byteSource = readByte();
		byteSource <<= bit_offset % 8;
		bitOffset = bit_offset;
		bitOffset += offset;
	}

	// at the end of each line, we skip past the 12-bit end-of-line code, and skip
	// forward to a byte-aligned area if necessary
	public void endOfLine()
	{
		if ((++curRow) == lastRow) {
			return;
		}
		// assume byte-aligned EOLs
		bitOffset = 0;
		while (readByte() != 1);
	}

}

