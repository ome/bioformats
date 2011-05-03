package com.sun.jimi.core.encoder.jpg;

import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.InvalidOptionException;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.OptionsObject;
import com.sun.jimi.util.ArrayEnumeration;
import com.sun.jimi.util.IntegerRange;
import com.sun.jimi.core.options.JPGOptions;


/**
 * An integrating front end to the JPG Baseline image encoder
 * purchased. This is reasonable the whole lot could be improved
 * and tightened up.
 *
 * Should make a thorough check of the error condition handling
 * throughout the jpg encoder as I believe currently it ignores
 * many errors.
 *
 * @author	Robin Luiten
 * @date	29/Oct/1997
 * @version	1.0
 */
public class JPGEncoder extends JimiEncoderBase implements OptionsObject
{
	/** decoder return state */
	private int state;

	CompressInfo cinfo;

	OutputStream out;

	short [][][] sample_buf;		// buffer to hold 1 MCU row for processing
	short [][][] sampled_img=null;	// image after downsampling
	int	fullwidth, rows_in_mcu, rows_to_get, cur_pixel_row, ci;

	// defines color spaces
	public static final short CS_UNKNOWN=0;	// error/unspecified 
	public static final short CS_GRAYSCALE=1;// monochrome (only 1 component)
	public static final short CS_RGB=2;		// red/green/blue 
	public static final short CS_YCbCr=3;	// Y/Cb/Cr  

	static final short	DCTSIZE = 8;

	static final int DEFAULT_QUALITY = 75;
    int quality_ = DEFAULT_QUALITY;

	// once-were-statics
	public Shared shared = new Shared();

	/** support method for use of jpg encoder */
	void input_init(CompressInfo cinfo)
	{
		ColorModel cm = cinfo.ji.getColorModel();

        cinfo.in_color_space = -1;		// unset

/* Disabled until have time to figure out whay CS_GRAYSCALE is failing
	if (cm instanceof DirectColorModel)
		{
			int pixelSize = cm.getPixelSize();
			DirectColorModel dcm = (DirectColorModel)cm;
			int redMask = dcm.getRedMask();
			int greenMask = dcm.getGreenMask();
			int blueMask = dcm.getBlueMask();

			if (pixelSize == 8 && redMask == greenMask && greenMask == blueMask)
			{
		        cinfo.input_components = 1;
		        cinfo.in_color_space = CS_GRAYSCALE;
			}
		}
*/
		if (cinfo.in_color_space == -1)		// still unset set to default
		{
			// force R/G/B color space input
	        cinfo.input_components = 3;
	        cinfo.in_color_space = CS_RGB;
			cinfo.ji.setRGBDefault(true); // force to RGB mode
		}

		cinfo.image_width = cinfo.ji.getWidth();
		cinfo.image_height = cinfo.ji.getHeight();
	}

	/**
	 * This initialiser actually takes a fair amount of time because it has
	 * to retrieve image details to initialise the JPG code. The initialisation
	 * of JPG code could be re-arranged to move it to driveDecoder() but it is
	 * not important now and will occur when JPG Encoder is modified to allow
	 * for quality setting JPG saving.
	 **/
	public void initSpecificEncoder(OutputStream out, AdaptiveRasterImage ji) throws JimiException
	{
		this.out = out;
		this.state = 0;

		if (ji.getOptions() instanceof JPGOptions) {
			quality_ = ((JPGOptions)ji.getOptions()).getQuality();
		}
	}
	
	void initCInfo() throws JimiException
	{
		this.cinfo = new CompressInfo(quality_);
		cinfo.output_file = new DataOutputStream(new BufferedOutputStream(out));

		// must retrieve JimiImage here as JPG setup needs the width/height
		// information of rits buffers etc.
		cinfo.ji = getJimiImage();

		input_init(cinfo);

		CompressSetup.setCDefaults(cinfo);

		// and if it's grayscale, set some fields back
		if (cinfo.in_color_space == CS_GRAYSCALE)
			CompressSetup.setMDefaults(cinfo);
		
		// setup sampling factors and downsampled width/height for each component
		CompressSetup.initial_setup(cinfo);

		// setup sampling factors, image dimensions, etc. for a scan
		// for future use, because we have only one scan
		if (cinfo.interleave)
			CompressSetup.interleaved_scan_setup(cinfo);
		else
			CompressSetup.noninterleaved_scan_setup(cinfo);

		// this is the width after edge expansion
		fullwidth = util.roundUp(cinfo.image_width, cinfo.max_h_samp_factor * DCTSIZE);

		// and we have this many pixel rows in an MCU
		rows_in_mcu = cinfo.max_v_samp_factor * DCTSIZE;

		// allocate buffer for raw image
		sample_buf  = new short[cinfo.num_components][rows_in_mcu][fullwidth];

		// allocate buffer for downsampled image
		sampled_img = new short[cinfo.num_components][rows_in_mcu][fullwidth];

		// initialize the color conversion table
		shared.convertColor.rgb_ycc_init(cinfo);

		// init huffman coding tables
		shared.huffEncode.huff_init(cinfo);
	}

	public boolean driveEncoder() throws JimiException
	{
		initCInfo();

		// write headers
		Write.write_file_header(cinfo);
		Write.write_scan_header(cinfo);
  
		for (cur_pixel_row = 0; cur_pixel_row < cinfo.image_height;
				cur_pixel_row += rows_in_mcu)
		{
			rows_to_get = Math.min(rows_in_mcu, cinfo.image_height - cur_pixel_row);

			// get some rows and convert color space, if necessary
			if (cinfo.num_components == 1)
			{	// ie this is Grayscale
				shared.convertColor.get_grayscale_rows(cinfo, rows_to_get, sample_buf);
			}
			else if (cinfo.num_components == 3)
			{	// ie this is RGB
				shared.convertColor.get_rgb_ycc_rows(cinfo, rows_to_get, sample_buf);
			}

			// expand edge
			Sample.edge_expand(cinfo, cinfo.image_width, rows_to_get,
								fullwidth, rows_in_mcu, sample_buf);

			// downsample Cb Cr
			for (ci=0; ci < cinfo.num_components; ci++)
			{
				Sample.downsample(cinfo, ci, cinfo.comp_info[ci].true_comp_width,
						cinfo.comp_info[ci].v_samp_factor * DCTSIZE,  
					    sample_buf[ci], sampled_img[ci]);
			} 
			shared.mcu.extract_MCUs(cinfo, sampled_img, 1);
		}

		// finishing up, write the last few bits, if any
		shared.huffEncode.huff_term(cinfo);

		// and write EOI
		Write.write_file_trailer(cinfo);

		state |= DONE;
		return false;	// state change - Completed operation in acutality.
	}

	public void freeEncoder() throws JimiException
	{
		sample_buf = null;
		sampled_img = null;
		cinfo.ji = null;
		cinfo = null;
		super.freeEncoder();
	}

	public int getState()
	{
		return this.state;
	}


	/* PROPERTIES HANDLING */

	public OptionsObject getOptionsObject()
	{
		return (OptionsObject)this;
	}
	
	static final String QUALITY_OPTION_NAME = "quality";
	static final IntegerRange POSSIBLE_QUALITY_VALUES = new IntegerRange(0, 100);

	static final String[] PROPERTY_NAMES = { QUALITY_OPTION_NAME };

	public Enumeration getPropertyNames()
	{
		return new ArrayEnumeration(PROPERTY_NAMES);
	}
	
	public Object getProperty(String key)
	{
		if(key.equalsIgnoreCase(QUALITY_OPTION_NAME))
			return new Integer(quality_);
		return null;
	}

	public void setProperty(String key, Object val) throws InvalidOptionException
	{
		if (key.equalsIgnoreCase(QUALITY_OPTION_NAME))
		{
			int value;
			try
			{
				value = ((Integer)val).intValue();
			}
			catch(ClassCastException cce)
			{
				throw new InvalidOptionException("Value must be a java.lang.Integer");
			}
			
			if (!POSSIBLE_QUALITY_VALUES.isInRange(value))
			{
				throw new InvalidOptionException("Value must be between " +
					POSSIBLE_QUALITY_VALUES.getLeastValue() + " and " + 
					POSSIBLE_QUALITY_VALUES.getGreatestValue());
			}
			
			quality_ = value;
			
		}
		else
		{
			throw new InvalidOptionException("No such option");
		}
	}

	public Object getPossibleValuesForProperty(String name) throws InvalidOptionException
	{
		if(name.equalsIgnoreCase(QUALITY_OPTION_NAME))
			return POSSIBLE_QUALITY_VALUES;
		
		throw new InvalidOptionException("No such option");
	}

	public String getPropertyDescription(String name) throws InvalidOptionException
	{
		if (name.equalsIgnoreCase(QUALITY_OPTION_NAME))
			return "A lower quality image is smaller, but has more information loss";
		
		throw new InvalidOptionException("No such option");
	}
	
	public void clearProperties()
	{
		quality_ = DEFAULT_QUALITY; //default
	}

}
