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

package com.sun.jimi.core.options;

/**
 * Options class for GIF images.
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/04/07 19:09:35 $
 */
public class GIFOptions extends BasicFormatOptionSet
{
	/** Constant meaning that the image has no transparency */
	public static final int NO_TRANSPARENCY = -1;
	/** Constant meaning unlimited number of loops */
	public static final int LOOP_FOREVER = 0;

	protected IntOption transparentIndex;
	protected BooleanOption interlace;
	protected IntOption frameDelay;
	protected BooleanOption localPalette;
	protected IntOption numberOfLoops;

	public GIFOptions()
	{
		transparentIndex = new IntOption("Transparent index", "The palette index of the image which " +
										 "should be treated as transparent, or -1 for no transparency.",
										 NO_TRANSPARENCY);

		interlace = new BooleanOption("Interlace", "Whether the image is interlaced.", false);

		frameDelay = new IntOption("Frame delay",
								   "For Animated GIFs, the number of hundredths of seconds to display a frame for.",
								   100, 0, 65535);

		localPalette = new BooleanOption("Local palette",
										 "For Animated GIFs, whether each frame has its own palette.",
										 true);

		numberOfLoops = new IntOption("Number of loops",
									  "For Animated GIFs, how many times to loop through the animation, " +
									  "or -1 to loop forever.", LOOP_FOREVER);

		initWithOptions(new FormatOption[] {
			transparentIndex, interlace, frameDelay, localPalette, numberOfLoops
				});
				
	}

	/**
	 * Set the index to be treated as transparent.
	 * @param index the color index
	 */
	public void setTransparentIndex(int index)
		throws OptionException
	{
		transparentIndex.setIntValue(index);
	}

	/**
	 * Get the index to be treated as transparent.
	 */
	public int getTransparentIndex()
	{
		return transparentIndex.getIntValue();
	}

	/**
	 * Set whether the image should be interlaced.
	 * @param value true for interlacing
	 */
	public void setInterlaced(boolean value)
		throws OptionException
	{
		interlace.setBooleanValue(value);
	}

	/**
	 * Check whether interlacing is used.
	 * @return true if interlacing is used
	 */
	public boolean isInterlaced()
	{
		return interlace.getBooleanValue();
	}

	/**
	 * Set the delay for a frame of animation.
	 * @param delay the delay in hundredths of a second
	 */
	public void setFrameDelay(int delay)
		throws OptionException
	{
		frameDelay.setIntValue(delay);
	}

	/**
	 * Get the delay for a frame of animation.
	 * @return the delay in hundredths of a second
	 */
	public int getFrameDelay()
	{
		return frameDelay.getIntValue();
	}

	/**
	 * Set whether frames should have their own palettes.
	 * @param value true for separate palettes, false for shared
	 */
	public void setUseLocalPalettes(boolean value)
	{
		localPalette.setBooleanValue(value);
	}
	
	/**
	 * Check whether local palettes are in use.
	 * @return true if each frame has its own palette
	 */
	public boolean isUsingLocalPalettes()
	{
		return localPalette.getBooleanValue();
	}

	/**
	 * Set the number of times to play an animation
	 * @param value the number of times to play, between 1 and 65535, or LOOP_FOREVER
	 */
	public void setNumberOfLoops(int value)
		throws OptionException
	{
		numberOfLoops.setIntValue(value);
	}

	/**
	 * Get the number of times an animation will be played.
	 * @return the number of times an animation will be played, or LOOP_FOREVER
	 */
	public int getNumberOfLoops()
	{
		return numberOfLoops.getIntValue();
	}

	public String toString()
	{
		return "[transparentIndex="+transparentIndex.getIntValue()+
			", interlace="+interlace.getBooleanValue()+
			", frameDelay="+frameDelay.getIntValue()+
			", localPalette="+localPalette.getBooleanValue()+
			", numberOfLoops="+numberOfLoops.getIntValue()+"]";
	}
}

