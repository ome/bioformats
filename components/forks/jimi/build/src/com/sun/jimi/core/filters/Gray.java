package com.sun.jimi.core.filters;

import java.awt.image.*;

/**
 * Filter to convert a color image to gray.
 * @author Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:57 $
 **/
public class Gray extends RGBImageFilter
{

	public int filterRGB(int x, int y, int rgb)
	{
		int value = (((rgb >> 16) & 0xff) +
								 ((rgb >> 8) & 0xff) +
								 (rgb & 0xff)) / 3;

		return 0xff000000 | (value << 16) | (value << 8) | value;
	}

}

