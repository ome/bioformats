package com.sun.jimi.core.util;

import java.io.*;
import java.awt.*;
import java.awt.image.*;

import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.util.ObjectInputToStreamAdapter;
import com.sun.jimi.util.ObjectOutputToStreamAdapter;

/**
 * A Serializable wrapper for Images.  JimiSerializers hold a reference to an
 * <code>Image</code>, not necessarily created with JIMI, and acts as a Serializable
 * container for it.
 * @author  Luke Gorrie
 * @version $Revision: 1.2 $
 **/
public class JimiImageSerializer implements Externalizable
{

	private static final long serialVersionUID = -3284218653375208081L;

	protected static final String DEFAULT_ENCODING = "image/png";

	/** The encoding format of the image. **/
	private String encodingFormat_ = DEFAULT_ENCODING;
	/** The image to serialize **/
	protected transient Image image_;

	/**
	 * Constructs a JimiImageSerializer.  <code>setImage</code> must be called to
	 * set the image to serialize.
	 **/
	public JimiImageSerializer()
	{
	}

	/**
	 * Constructs a JimiImageSerializer for a given image, using a default encoding scheme.
	 * @param image the image to serialize
	 **/
	public JimiImageSerializer(Image image)
	{
		this(image, DEFAULT_ENCODING);
	}

	/**
	 * Constructs a JimiImageSerializer for a given image, with a given encoding format.
	 * @param image The image to serialize.
	 * @param encodingFormat the mime-type of the encoding format to use.  eg "image/png"
	 **/
	private JimiImageSerializer(Image image, String encodingFormat)
	{
		image_ = image;
		encodingFormat_ = encodingFormat;
	}

	/**
	 * Returns the <code>Image</code> being serialized.
	 * @return the image
	 **/
	public Image getImage()
	{
		return image_;
	}

	/**
	 * Set the source image to serialize.
	 * @param image the image to serialize
	 **/
	public void setImage(Image image)
	{
		image_ = image;
	}

	/*
	 * Externalizable implementation
	 */

	/**
	 * Extenalization support, should not be invoked directly.
	 **/
	public void writeExternal(ObjectOutput out) throws IOException
	{
		// make an OutputStream wrapper to pass to Jimi
		OutputStream out_stream = new ObjectOutputToStreamAdapter(out);

		// write encoding format
		out.writeObject(encodingFormat_);

		try {			// write image to stream
			
			Jimi.putImage(encodingFormat_, image_, out_stream);
		}
		catch (JimiException je) {
			throw new IOException("Unable to write image: " + je.getMessage());
		}

	}

	/**
	 * Extenalization support, should not be invoked directly.
	 **/
	public void readExternal(ObjectInput in) throws IOException
	{
		try {
		// read the encoding format that the image was stored with
		encodingFormat_ = (String)in.readObject();
		}
		catch (ClassNotFoundException e)
		{
			throw new IOException(e.getMessage());
		}		
		// create an InputStream wrapper to pass to jimi
		
		InputStream in_stream = new ObjectInputToStreamAdapter(in);

		// decode image
		image_ = Jimi.getImage(in_stream, encodingFormat_);
		GraphicsUtils.waitForImage(new Canvas(), image_);

	}

}
