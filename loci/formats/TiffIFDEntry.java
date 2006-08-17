package loci.formats;

/**
 * This class represents a single raw TIFF IFD entry. It does not retrieve or
 * store the values from the entry's specific offset and is based on the TIFF
 * 6.0 specification of an IFD entry.
 *
 * @author Chris Allan callan at blackcat.ca
 */
public class TiffIFDEntry {
	/** The <i>Tag</i> that identifies the field. */
	private int tag;
	
	/** The field <i>Type</i>. */
	private int type;
	
	/** The number of values, <i>Count</i> of the indicated <i>Type</i>. */
	private int valueCount;
	
	/**
	 * The <i>Value Offset</i>, the file offset (in bytes) of the <i>Value</i>
	 * for the field.
	 */
	private int valueOffset;
	
	public TiffIFDEntry (int tag, int type, int valueCount, int valueOffset)
	{
		this.tag = tag;
		this.type = type;
		this.valueCount = valueCount;
		this.valueOffset = valueOffset;
	}

	/**
	 * Retrieves the entry's <i>Tag<i> value.
	 * @return the entry's <i>Tag<i> value.
	 */
	public int getTag() {
		return tag;
	}

	/**
	 * Retrieves the entry's <i>Type<i> value.
	 * @return the entry's <i>Type<i> value.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Retrieves the entry's <i>ValueCount<i> value.
	 * @return the entry's <i>ValueCount<i> value.
	 */
	public int getValueCount() {
		return valueCount;
	}

	/**
	 * Retrieves the entry's <i>ValueOffset<i> value.
	 * @return the entry's <i>ValueOffset<i> value.
	 */
	public int getValueOffset() {
		return valueOffset;
	}
}
