package com.sun.jimi.core.util;

import java.awt.image.ColorModel;

/**
 * ColorModel for grayscale images. Allows for pixels of a given
 * bit depth to directly represent a grayscale value.<p>
 * Possible future optimisation might include a generated internal lookup
 * table to make the individual get methods more efficient.<p>
 * This class is flexible in that it can be used for black and white
 * images and any bit depth grayscale image from 1 to 8 bits in depth.<p>
 * Examples of use:<p>
 * <ul>
 * <li> Black and White image where pixels of value 0 are white<br>
 * new GrayscaleColorModel(true, 1);
 * <li> Common usage case for 8 bit depth grayscale image<br>
 * new GrayscaleColorModel(true);
 * </ul>
 *
 * @author  Robin Luiten
 * @version 1.2	25/Dec/1997
 */
public class GrayscaleColorModel extends ColorModel
{
	/** flag indicating that 0 pixel value is white if set else is black **/
	boolean zeroIsWhite;

	/** the maximum value for each channel of RED/GREEN/BLUE **/
	int maxVal;

	/**
	 * mask to limit the pixel values to allowed range
	 * e.g	if pixelSize = 1 then pixelMask = 0x1
	 *		if pixelSize = 8 then pixelMask = 0xFF
	 **/
	int pixelMask;

    /**
     * Constructs a GrayColorModel. Pixels described by this color
     * model will all have alpha components of 255 (fully opaque).
	 * This colormodel is similar to IndexColorModel except the
	 * palette is fixed to scales of grey from black to white.
	 * This constructor set pixel size to 8 and color component 
	 * arrays of size 256.
	 * @param zeroIsWhite Flag if set means that a pixel of value of 0
	 * return the color white. Else value pixel value 0 returns black.
     */
	public GrayscaleColorModel(boolean zeroIsWhite)
	{
		super(8);
		init(zeroIsWhite);
	}

	/**
	 * @param zeroIsWhite Flag if set means that a pixel of value of 0
	 * return the color white
	 * @param pixelSize range of values 1 to 8 bits only.
	 **/
	public GrayscaleColorModel(boolean zeroIsWhite, int pixelSize)
	{
		super(pixelSize);
		init(zeroIsWhite);
	}

	/**
	 * @return the flag indicating if ZeroIsWhite for this ColorModel.
	 * If returns false then Zero is Black for this ColorModel.
	 **/
	public boolean getZeroIsWhite()
	{
		return zeroIsWhite;
	}

	/**
	 * @param zeroIsWhite Flag if set means that a pixel of value of 0
	 * return the color white
	 * transition between.	If not set then colour transition reversed.
	 **/
	protected void init(boolean zeroIsWhite)
	{
		this.zeroIsWhite = zeroIsWhite;
		maxVal = (int)Math.pow(2, getPixelSize());
		--maxVal;
		pixelMask = 0xFF >> (8 - getPixelSize());
	}

	/**
	 * @param pixel	The pixel to lookup value for. For this colour model
	 *	the pixel can only range between 0 and (2 ^ pixelDepth) - 1)
	 * @return the red color compoment for the specified pixel in the
	 * range 0-255.
	 */
	public int getRed(int pixel)
	{
		pixel &= pixelMask;
		if (zeroIsWhite)
			pixel = maxVal - pixel;

		// scale value  0  to  2^depth - 1   to  0 to FF
		pixel = (pixel * 0xFF ) / maxVal;
		return pixel;		
	}

	/**
	 * @param pixel	The pixel to lookup value for. For this colour model
	 *				the pixel can only range between 0 and 255.
	 * @return the green color compoment for the specified pixel in the
	 * range 0-255.
	 */
	public int getGreen(int pixel)
	{
		pixel &= pixelMask;
		if (zeroIsWhite)
			pixel = maxVal - pixel;
		// scale value  0  to  2^depth - 1   to  0 to FF
		pixel = (pixel * 0xFF ) / maxVal;
		return pixel;		
	}

	/**
	 * @param pixel	The pixel to lookup value for. For this colour model
	 *				the pixel can only range between 0 and 255.
	 * @return the blue color compoment for the specified pixel in the
	 * range 0-255.
	 */
	public int getBlue(int pixel)
	{
		pixel &= pixelMask;
		if (zeroIsWhite)
			pixel = maxVal - pixel;
		// scale value  0  to  2^depth - 1   to  0 to FF
		pixel = (pixel * 0xFF ) / maxVal;
		return pixel;		
	}

	/**
	 * @param pixel	The pixel to lookup value for. For this colour model
	 *				the pixel can only range between 0 and 255.
	 * @return the alpha compoment for the specified pixel in the
	 * range 0-255.
	 */
	public int getAlpha(int pixel)
	{
		return 0xFF;		// totally opaque
	}

    /**
     * Returns the color of the pixel in the default RGB color model.
     * @see ColorModel#getRGBdefault
     */
    public int getRGB(int pixel)
    {
		int val = getAlpha(pixel) << 24 | 
				getRed(pixel) << 16 | 
				getGreen(pixel) << 8 | 
				getBlue(pixel);
		return val;
    }

}

