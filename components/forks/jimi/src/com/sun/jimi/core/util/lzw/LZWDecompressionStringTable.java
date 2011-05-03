package com.sun.jimi.core.util.lzw;

import java.io.*;

final class LZWDecompressionStringTable
{

	// indicates that no prefix string is to be used for a table entry.
	private static final int NO_PREFIX = -1;
	// the default maximum number of entries in the table
	private static final int DEFAULT_TABLE_SIZE = 1 << 12;

	// expanded table of values
	protected byte[][] strings_;
	// current number of table entries
	protected int size_ = 0;
	// size in bits of the initial table entries.
	protected int codeSize_;
	// size of the table
	protected int tableSize_;

	/**
	 * Constructs a table and populates it with initial values.
	 * @param codesize The number of bits worth of initial values.
	 **/
	public LZWDecompressionStringTable(int codesize)
	{
		this(codesize, DEFAULT_TABLE_SIZE);
	}

	/**
	 * Constructs a table and populates it with initial values.
	 * @param codesize The number of bits worth of initial values.
	 * @param tablesize The maximum size of the table.
	 **/
	public LZWDecompressionStringTable(int codesize, int tablesize)
	{
		tableSize_ = tablesize;
		codeSize_ = codesize;
		initTable();
	}

	/**
	 * Populates the table with initial values.
	 **/
	protected void initTable()
	{
		strings_ = new byte[tableSize_][];

		int codes = (1 << codeSize_) + 2;
		for (int idx = 0; idx < codes; idx++)
		{
			addCharString(NO_PREFIX, (byte)idx);
		}
	}

	/**
	 * Clears the table of all but the initial values.
	 **/
	public void clearTable()
	{
		size_ = (1 << codeSize_) + 2;

	}

	/**
	 * Adds a new string to the table, constructed from an existing string
	 * and an additional character.
	 * @param index The index in the table of the prefix-string.
	 * @param value The value to be appended to the prefix string to create
	 * the new table entry.
	 **/
	public int addCharString(int index, byte value)
	{
		if (index == NO_PREFIX)
		{
			strings_[size_] = new byte[] { value };
		}
		else
		{
			int length = strings_[index].length + 1;
			byte[] expanded_value = new byte[length];
			System.arraycopy(strings_[index], 0,
											 expanded_value, 0, length - 1);
			expanded_value[length - 1] = value;
			strings_[size_] = expanded_value;
		}

		return size_++;
	}

	/**
	 * Expands as much of a string from the table into a buffer as possible.
	 * @param buf The buffer to expand the string into.
	 * @param offset The offset in the buffer first the first byte of
	 * the string.
	 * @param code The code to determine the string from.
	 * @param skip_head The number of bytes to skip from the front of the
	 * string.  Can be used to skip a segment of the string that was written
	 * in a previous expansion.
	 **/
	public int expandCode(byte[] buf, int offset, int code, int skip_head)
	{
		int string_length = strings_[code].length - skip_head;
		int max_length = buf.length - offset;
		int length = (max_length > string_length) ? string_length : max_length;

		System.arraycopy(strings_[code], skip_head, buf, offset, length);

		return (string_length > length) ? -(length+skip_head) : length;
	}

	/**
	 * Check if there is a string for a specific code in the table.
	 * @param code The code to check for.
	 * @return True if a match is found.
	 **/
	public final boolean contains(int code)
	{
		return code < size_;
	}

}

