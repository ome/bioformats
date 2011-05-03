package com.sun.jimi.core.util;

/**
 * This is a general debug aid class and may not acutally be
 * shipped with Jimi however it lives here till then
 *
 * @author  Robin Luiten
 * @version 1.1	14/Dec/1997
 */
public class P
{
	public static boolean debug = true;

	public static void rt(String s)
	{
		System.out.println(s);
	}

	public static void rtN(String s)
	{
		System.out.print(s);
	}

	public static void rtd(String s)
	{
		if (debug)
			System.out.println(s);
	}

	public static void rtdN(String s)
	{
		if (debug)
			System.out.print(s);
	}

	public static void dump(byte[] b)
	{
		int i;
		for (i = 0; i < b.length; ++i)
		{
			P.rtN("x" +Integer.toHexString((int)b[i] & 0xFF)+" ");
			if ((i & 0xF) == 0xF)
				P.rt("");
		}
	}

	public static void dump(int[] b)
	{
		int i;
		for (i = 0; i < b.length; ++i)
		{
			P.rtN("x" +Integer.toHexString((int)b[i])+" ");
			if ((i & 0xF) == 0xF)
				P.rt("");
		}
	}

	public static void dumpd(byte[] b)
	{
		if  (debug)
			dump(b);
	}

	static void pause(int time)
	{
		try {Thread.sleep(time);}
		catch (InterruptedException e){}
	}
}

