package com.sun.jimi.core.encoder.sunraster;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.*;

import java.io.OutputStream;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.util.Enumeration;
import com.sun.jimi.util.ArrayEnumeration;
import com.sun.jimi.util.PropertyOwner;
import com.sun.jimi.core.options.SunRasterOptions;

/**
 * Encoder class for SunRaster (.RAS) images.  Supports two methods
 * of encoding:  8BPP palette-images (with or without RLE compression),
 * and 24-bit no-palette images.<p>
 * Having decided which type of encoding to use, it delegates to
 * a specialised encoding class.
 * @see RGBSunRasterEncoder
 * @see PaletteSunRasterEncoder
 * @author Luke Gorrie
 * @version $Revision: 1.2 $ $Date: 1999/01/20 11:03:23 $
 **/
public class SunRasterEncoder extends JimiEncoderBase implements OptionsObject
{

	/** State of encoder. **/
	protected int state_;

	/** Stream to write RAS output to. **/
	protected OutputStream output_;

	/** use RLE compression? **/
	static final boolean DEFAULT_COMPRESSION = true;
	protected boolean useRLE_ = DEFAULT_COMPRESSION;

	/**
	 * Initialize the encoder.  Should not be called directly.
	 * @see com.sun.jimi.core.JimiEncoderBase
	 * @param out The OutputStream to write the RAS encoded image to.
	 * @param ji The JimiImage to draw information from.
	 **/
	public void initSpecificEncoder(OutputStream out, AdaptiveRasterImage ji)
	{
		output_ = out;
		if (ji.getOptions() instanceof SunRasterOptions) {
			setUseRLE(((SunRasterOptions)ji.getOptions()).isUsingRLE());
		}
	}

	/**
	 * Drive the encoder through the image encoding process.
	 **/
	public boolean driveEncoder() throws JimiException
	{
		try {
			// create an appropriate encoder
			SpecificEncoder encoder = createEncoder();
			// encode the image, sending it to the output stream
			encoder.doImageEncode();
		}
		// if an exception is raised, set ERROR state and re-throw
		catch (JimiException e)
		{
			state_ = ERROR;
			throw e;
		}
		// done!
		state_ = DONE;

		return false;
	}

	/**
	 * Return the state of the encoder.
	 **/
	public int getState()
	{
		return state_;
	}

	/**
	 * Factory method for creating an appropriate encoder based on the type of
	 * JimiImage.
	 * @return An encoder appropriate for the JimiImage to be encoded.
	 **/
	protected SpecificEncoder createEncoder() throws JimiException
	{
		// get the JimiImage
		AdaptiveRasterImage ji = getJimiImage();

		// create a header
		SunRasterHeader header = new SunRasterHeader(ji);
		ColorModel cm = ji.getColorModel();
		SpecificEncoder encoder;
		// if it's an 8-bit palette image
		if ((cm instanceof IndexColorModel) && (cm.getPixelSize() == 8))
		{
			encoder = new PaletteSunRasterEncoder();

			((PaletteSunRasterEncoder)encoder).setUseRLE(useRLE_);
		}
		// otherwise encode as RGB
		else
			encoder = new RGBSunRasterEncoder();

		// initialize encoder
		encoder.initEncoder(header, output_, ji);

		return encoder;
	}

	/*
	 * Options
	 */

	// possible bug in RLE code?  Disable RLE option
	public OptionsObject getOptionsObject() {
		return (OptionsObject)this;
	}

	void setUseRLE(boolean useRLE)
	{
		useRLE_ = useRLE;
	}

	/*
	 * Expose properties.
	 */
	static final String OPTION_COMPRESSION = "compression";
	static final Boolean[] POSSIBLE_COMPRESSION_VALUES = PropertyOwner.BOOLEAN_ARRAY;
	static final String[] OPTION_NAMES = { OPTION_COMPRESSION };
	
	public Enumeration getPropertyNames()
	{
		return new ArrayEnumeration(OPTION_NAMES);
	}

	public Object getPossibleValuesForProperty(String name)
	{
		if (name.equalsIgnoreCase(OPTION_COMPRESSION))
			return POSSIBLE_COMPRESSION_VALUES;
		else return null;
	}

	public void setProperty(String name, Object value) throws InvalidOptionException
	{
		if (name.equalsIgnoreCase(OPTION_COMPRESSION)) {
			try {
				setUseRLE(((Boolean)value).booleanValue());
			} catch(ClassCastException cce) {
				throw new InvalidOptionException("Must specify a java.lang.Boolean value");
			}
		}
		
		throw new InvalidOptionException("No such option");

	}

	public void clearProperties() {
		setUseRLE(DEFAULT_COMPRESSION);
	}
	
	public String getPropertyDescription(String name) throws InvalidOptionException {
	
		if (name.equalsIgnoreCase(OPTION_COMPRESSION)) {
		
			return "RLE compression tends to be smaller than not using compression";
			
		}
		
		throw new InvalidOptionException("No such option");
		
	}

	public Object getProperty(String name)
	{
		return null;
	}
	
}
