/*
 * Copyright 1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.sun.jimi.core.util;

/**
 * Utility methods for applying binary operations to arbitrarily large numbers,
 * represented by byte arrays.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/04/07 19:18:18 $
 */
public class BinOps
{

	/**
	 * Apply an exclusive OR.  The value being XOR'd will wrap if it
	 * is not large enough to cover the target.
	 * @param value the value to modify
	 * @param xor the value to XOR with
	 */
	public static void xor(byte[] value, byte[] xor)
	{
		xor(value, xor, 0, value.length);
	}

	/**
	 * Apply an exclusive OR.  The value being XOR'd will wrap if it
	 * is not large enough to cover the target.
	 * @param value the value to modify
	 * @param xor the value to XOR with
	 * @param start the index in value to start at
	 * @param count the number of indexes to apply to in value
	 */
	public static void xor(byte[] value, byte[] xor, int start, int count)
	{
		for (int i = 0; i < count; i++) {
			value[start + i] ^= xor[i % xor.length];
		}
	}

	public static void rotateLeft(byte[] value, int bits)
	{
		rol(value, bits, 0, value.length);
	}

	public static void rol(byte[] value, int bits, int start, int count)
	{
		int overflow = bits % 8;
		int bytes = bits / 8;
		if (overflow == 0) {
			byte[] work = (byte[])value.clone();
			System.arraycopy(work, start + bytes, value, start, count - bytes);
			System.arraycopy(work, start, value, start + count - bytes, bytes);
		}
		else {
			byte val;
			int leftoverMask = (1 << overflow) - 1;
			byte[] work = (byte[])value.clone();
			for (int i = 0; i < count; i++) {
				int lhs = start + ((i + bytes) % count);
				int rhs = start + ((i + bytes + 1) % count);
				val = (byte)(work[lhs] << overflow);
				val = (byte)(val | (byte)((work[rhs] >> (8 - overflow)) & leftoverMask));
				value[start + ((i + bytes) % count)] = val;
			}
		}
	}

	public static void ror(byte[] value, int bits)
	{
		ror(value, bits, 0, value.length);
	}

	public static void ror(byte[] value, int bits, int start, int count)
	{
		int overflow = bits % 8;
		int bytes = bits / 8;
		if (overflow == 0) {
			byte[] work = (byte[])value.clone();
			System.arraycopy(work, start, value, start + bytes, count - bytes);
			System.arraycopy(work, start + count - bytes, value, start, bytes);
		}
		else {
			byte val;
			int leftoverMask = (1 << (8 - overflow)) - 1;
			byte[] work = (byte[])value.clone();
			for (int i = 0; i < count; i++) {
				int lhs = start + ((i + bytes) % count);
				int rhs = start + ((i + bytes + 1) % count);
				val = (byte)(work[lhs] << ((8 - overflow)));
				val = (byte)(val | (byte)(((work[rhs] >>> overflow) & leftoverMask)));
				value[start + ((i + bytes + 1) % count)] = val;
			}
		}
	}

	public static void add(byte[] value, byte[] add)
	{
		add(value, add, 0, value.length);
	}

	public static void add(byte[] value, byte[] add, int start, int count)
	{
		for (int i = 0; i < count; i++) {
			value[start + i] += (byte)(add[i % add.length]);
		}
	}

	public static void sub(byte[] value, byte[] sub)
	{
		sub(value, sub, 0, value.length);
	}

	public static void sub(byte[] value, byte[] sub, int start, int count)
	{
		for (int i = 0; i < count; i++) {
			value[start + i] -= (byte)(sub[i % sub.length]);
		}
	}

	public static void main(String[] args)
	{
		byte[] testData = { (byte)0, (byte)2, (byte)3 };
		for (int i = 0; i < testData.length; i++) {
			System.out.println(Integer.toBinaryString(((int)testData[i]) & 0xff));
		}
		System.out.println(" =>");
		sub(testData, new byte[] { 1 });
		for (int i = 0; i < testData.length; i++) {
			System.out.println(Integer.toString(((int)testData[i]) & 0xff));
		}
	}

}

