/*
 * Copyright (c) 1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.sun.jimi.core;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.Enumeration;

import com.sun.jimi.core.util.*;
import com.sun.jimi.core.raster.JimiRasterImage;

/**
 * JimiReader provides more fine-grained control of the decoding process, and allows reading
 * series' of images from a single image file.
 * @author  Luke Gorrie
 * @version $Revision: 1.5 $ $Date: 1999/04/30 18:42:36 $
 */
public class JimiReader
{
	public static final int UNKNOWN = -1;

	protected static final int STREAM_BUFFER_SIZE = 10 * 1024;

	// decoder-related state.
	protected JimiDecoderFactory decoderFactory;
	protected JimiDecoder decoder;
	protected JimiImageFactory imageFactory;
	protected InputStream input;

	// cache
	protected JimiRasterImage cacheJimiImage;
	protected ImageProducer cacheImageProducer;
	protected Image cacheImage;
	protected int cacheIndex = -1;

	/** image series state */
	protected int seriesIndex = 0;
	protected ImageSeriesDecodingController series;

	/** true if images should be fully decoded before being returned */
	protected boolean synchronous;

	/** set to true if built-in JPEG decoding is in use */
	protected boolean builtinJPEG;
	protected ImageProducer jpegProducer;
	protected URL location;
	protected String filename;

	protected ProgressListener listener;

	// Command for the decoder to run when it finishes.
	protected Runnable cleanupCommand;

	/**
	 * Create and partially initialize a JimiReader.
	 *
	 * @param imageFactory factory for creating images
	 * @param decoderFactory factory for creating the decoder
	 * @param input stream to read image data from
	 */
	protected JimiReader(JimiImageFactory imageFactory, JimiDecoderFactory decoderFactory,
					  InputStream input) throws JimiException
	{
		initReader(imageFactory, decoderFactory, input);
	}

	/**
	 * Create and partially initialize a JimiReader.
	 *
	 * @param imageFactory factory for creating images
	 * @param input stream to read image data from
	 * @param typeID the mimetype of the image format
	 */
	protected JimiReader(JimiImageFactory imageFactory, InputStream input, String typeID)
		throws JimiException
	{
		JimiDecoderFactory decoderFactory = JimiControl.getDecoderByType(typeID);
		if (decoderFactory == null) {
			throw new JimiException("No decoder available for " + typeID);
		}
		initReader(imageFactory, decoderFactory, input);
	}

	/**
	 * Create and partially initialize a JimiReader.
	 *
	 * @param imageFactory factory for creating images
	 * @param filename the name of the image file
	 * @param typeID the mimetype of the image format
	 */
	protected JimiReader(JimiImageFactory factory, String filename, String typeID)
		throws JimiException
	{
		cleanupCommand = new StreamCloseCommand();
		JimiDecoderFactory decoderFactory = JimiControl.getDecoderByType(typeID);
		if (decoderFactory == null) {
			throw new JimiException("No decoder available for " + typeID);
		}
		try {
			InputStream input = new FileInputStream(filename);
			input = new BufferedInputStream(input, STREAM_BUFFER_SIZE);
			initReader(imageFactory, decoderFactory, input);
		}
		catch (IOException e) {
			throw new JimiException(e.getMessage());
		}
	}

	/**
	 * Create and partially initialize a JimiReader.
	 *
	 * @param imageFactory factory for creating images
	 * @param location the URL of the image file
	 */
	protected JimiReader(JimiImageFactory imageFactory, URL location)
		throws JimiException
	{
		cleanupCommand = new StreamCloseCommand();
		this.location = location;
		try {
			JimiDecoderFactory decoderFactory =
				JimiControl.getDecoderByFileExtension(location.toString());
			URLConnection conn = location.openConnection();
			InputStream input = location.openStream();
			if (decoderFactory == null) {
				decoderFactory = JimiControl.getDecoderByType(conn.getContentType());
			}
			if (decoderFactory == null) {
				PushbackInputStream stream = new PushbackInputStream(input, 128);
				decoderFactory = JimiControl.getDecoderForInputStream(stream);
				input = stream;
			}
			if (decoderFactory == null) {
				throw new JimiException("No decoder available for location: " + location);
			}
			input = new BufferedInputStream(input, STREAM_BUFFER_SIZE);
			initReader(imageFactory, decoderFactory, input);
		}
		catch (IOException e) {
			throw new JimiException(e.toString());
		}
	}

	/**
	 * Create and partially initialize a JimiReader.
	 *
	 * @param imageFactory factory for creating images
	 * @param location the URL of the image file
	 * @param typeID the mimetype of the image format
	 */
	protected JimiReader(JimiImageFactory imageFactory, URL location, String typeID)
		throws JimiException
	{
		cleanupCommand = new StreamCloseCommand();
		this.location = location;
		try {
			JimiDecoderFactory decoderFactory =
				JimiControl.getDecoderByType(typeID);
			if (decoderFactory == null) {
				throw new JimiException("No decoder available for file: " + location);
			}
			InputStream input = location.openStream();
			input = new BufferedInputStream(input, STREAM_BUFFER_SIZE);
			initReader(imageFactory, decoderFactory, input);
		}
		catch (IOException e) {
			throw new JimiException(e.toString());
		}
	}

	/**
	 * Create and partially initialize a JimiReader.
	 *
	 * @param imageFactory factory for creating images
	 * @param filename the name of the image file
	 */
	protected JimiReader(JimiImageFactory imageFactory, String filename)
		throws JimiException
	{
		cleanupCommand = new StreamCloseCommand();
		this.filename = filename;
		try {
			JimiDecoderFactory decoderFactory = JimiControl.getDecoderByFileExtension(filename);
			if (decoderFactory == null) {
				throw new JimiException("No decoder available for file: " + filename);
			}
			InputStream input = new FileInputStream(filename);
			input = new BufferedInputStream(input, STREAM_BUFFER_SIZE);
			initReader(imageFactory, decoderFactory, input);
		}
		catch (IOException e) {
			throw new JimiException(e.toString());
		}
	}

	/**
	 * Create and partially initialize a JimiReader.
	 *
	 * @param imageFactory factory for creating images
	 * @param input stream to read image data from
	 */
	protected JimiReader(JimiImageFactory imageFactory, InputStream input)
		throws JimiException
	{
		PushbackInputStream stream = new PushbackInputStream(input, 128);
		JimiDecoderFactory decoderFactory = JimiControl.getDecoderForInputStream(stream);
		if (decoderFactory == null) {
			throw new JimiException("Cannot find decoder for stream");
		}
		initReader(imageFactory, decoderFactory, stream);
	}

	/**
	 * Create and partially initialize a JimiReader.
	 *
	 * @param imageFactory factory for creating images
	 */
	protected JimiReader(JimiImageFactory imageFactory)
		throws JimiException
	{
		initReader(imageFactory);
	}

	/**
	 * Find out how many images are available.
	 */
	public int getNumberOfImages()
	{
		return UNKNOWN;
	}

	/**
	 * Register a ProgressListener to be informed of decoding progress.
	 */
	public void setProgressListener(ProgressListener listener)
	{
		this.listener = listener;
		if (decoder != null) {
			decoder.setProgressListener(listener);
		}
	}

	/**
	 * Set the mime-type of the format.
	 */
	public void setMimeType(String typeID)
		throws JimiException
	{
		JimiDecoderFactory decoderFactory = JimiControl.getDecoderByType(typeID);
		if (decoderFactory == null) {
			throw new JimiException("Cannot find decoder for type: " + typeID);
		}
		if (input != null) {
			initReader(imageFactory, decoderFactory, input);
		}
		else {
			initReader(imageFactory, decoderFactory);
		}
	}

	/**
	 * Initialize or re-initialize the reader.
	 */
	protected void initReader(JimiImageFactory imageFactory, JimiDecoderFactory decoderFactory,
							  InputStream input)
	{
		if (decoderFactory instanceof FreeFormat) {
			imageFactory = JimiUtil.stripStamping(imageFactory);
		}
		this.imageFactory = imageFactory;
		this.decoderFactory = decoderFactory;
		decoder = decoderFactory.createDecoder();
		if (listener != null && decoder != null) {
			decoder.setProgressListener(listener);
		}
		this.input = input;
		if (decoderFactory.getClass().getName()
			.equals("com.sun.jimi.core.decoder.builtin.BuiltinDecoderFactory")) {
			builtinJPEG = true;
		}
		else {
			if (cleanupCommand != null) {
				decoder.addCleanupCommand(cleanupCommand);
			}
			series = decoder.initDecoding(imageFactory, input);
		}
	}

	/**
	 * Initialize or re-initialize the reader.
	 */
	protected void initReader(JimiImageFactory imageFactory, JimiDecoderFactory decoderFactory)
	{
		if (decoderFactory instanceof FreeFormat) {
			imageFactory = JimiUtil.stripStamping(imageFactory);
		}
		initReader(imageFactory);
		this.decoderFactory = decoderFactory;
		decoder = decoderFactory.createDecoder();
		if (listener != null) {
			decoder.setProgressListener(listener);
		}
		if (decoderFactory.getClass().getName()
			.equals("com.sun.jimi.core.decoder.builtin.BuiltinDecoderFactory")) {
			builtinJPEG = true;
		}
		else if (cleanupCommand != null) {
			decoder.addCleanupCommand(cleanupCommand);
		}
	}

	/**
	 * Initialize or re-initialize the reader.
	 */
	protected void initReader(JimiImageFactory factory)
	{
		this.imageFactory = factory;
	}

	/**
	 * Replace or set the source for the image data.
	 * The JimiReader object already exists and the type of image file
	 * format that can be decoded is set. Setting the image source
	 * to an image file of a type different from what this JimiReader 
	 * object is set for will generate exceptions and or errors from 
	 * any methods which retrieve Image data in any form.
	 *
	 * @param in InputStream from which to read image data
	 * @exception JimiException is not currently thrown
	 **/
	public void setSource(InputStream input)
		throws JimiException
	{
		// initialize reader
		initReader(imageFactory, decoderFactory, input);
	}

	/**
	 * Set a file as the image data source.
	 *
	 * @param filename the name of the file to read from
	 */
	public void setSource(String filename)
		throws JimiException
	{
		InputStream input;
		try {
			input = new FileInputStream(filename);
			this.filename = filename;
			input = new BufferedInputStream(input, STREAM_BUFFER_SIZE);
		}
		catch (Exception e) {
			throw new JimiException("Unable to open source file.");
		}
		initReader(imageFactory, decoderFactory, input);
	}

	/**
	 * Set a URL as the image data source.
	 *
	 * @param location the location to read from
	 */
	public void setSource(URL location)
		throws JimiException
	{
		InputStream input;
		try {
			input = location.openStream();
			this.location = location;
			input = new BufferedInputStream(input, STREAM_BUFFER_SIZE);
		}
		catch (IOException e) {
			throw new JimiException("Unable to open source URL.");
		}
	}

	/**
	 * Choose whether to block on image loading, i.e. operate synchronously.
	 *
	 * @param synchronous true if getImage-methods should block until the image
	 * is fully loaded
	 */
	public void setBlocking(boolean synchronous)
	{
		this.synchronous = synchronous;
	}

	/**
	 * Get the size of the first image.
	 */
	public Dimension getSize()
		throws JimiException
	{
		JimiRasterImage image = getRasterImage();
		image.waitInfoAvailable();
		return new Dimension(image.getWidth(), image.getHeight());
	}

	/**
	 * Read a single JimiRasterImage from the source.
	 * @return the JimiRasterImage
	 * @exception JimiException if an error prevents the image from being loaded
	 */
	public JimiRasterImage getRasterImage()
		throws JimiException
	{
		if (cacheIndex == 0) {
			if (cacheJimiImage != null) {
				return cacheJimiImage;
			}
			else {
				return Jimi.createRasterImage(cacheImageProducer);
			}
		}
		else if (seriesIndex == 0) {
			JimiRasterImage img = getNextJimiImage();
			if (decoder != null) {
				decoder.setFinished();
			}
			return img;
		}
		else {
			throw new JimiException();
		}
	}

	/**
	 * Read a single image from the source and return an ImageProducer for it.
	 * @return the ImageProducer
	 * @exception JimiException if an error prevents the image from being loaded
	 */
	public ImageProducer getImageProducer()
	{
		try {
			if (cacheIndex == 0) {
				return cacheImageProducer;
			}
			else if (seriesIndex == 0) {
				ImageProducer img = getNextImageProducer();
				if (decoder != null) {
					decoder.setFinished();
				}
				return img;
			}
		}
		catch (JimiException e) {
		}
		return JimiUtil.getErrorImageProducer();
	}

	/**
	 * Reada single image from the source and return an Image representation.
	 * @return the Image
	 */
	public Image getImage()
	{
		if (cacheIndex == 0) {
			if (cacheImage != null) {
				return cacheImage;
			}
			else {
				Image img = Toolkit.getDefaultToolkit().createImage(cacheImageProducer);
				GraphicsUtils.waitForImage(img);
				cacheImage = img;
				return img;
			}
		}
		else {
			try {
				Image img = getNextImage();
				if (decoder != null) {
					decoder.setFinished();
				}
				return img;
			}
			catch (Exception e) {
				return JimiUtil.getErrorImage();
			}
		}
	}

	/**
	 * Enumerate all images stored in the file.
	 * @return the Enumeration of JimiRasterImages.
	 */
	public Enumeration getRasterImageEnumeration()
	{
		return new ImageSeriesEnumerator(this, ImageSeriesEnumerator.JIMIIMAGE);
	}

	/**
	 * Enumerate all images stored in the file.
	 * @return the Enumeration of Images.
	 */
	public Enumeration getImageEnumeration()
	{
		return new ImageSeriesEnumerator(this, ImageSeriesEnumerator.IMAGE);
	}

	/**
	 * Enumerate all images stored in the file.
	 * @return the Enumeration of ImageProducers.
	 */
	public Enumeration getImageProducerEnumeration()
	{
		return new ImageSeriesEnumerator(this, ImageSeriesEnumerator.IMAGEPRODUCER);
	}

	public void skipNextImage()
		throws JimiException
	{
		if (!series.hasMoreImages()) {
			series.skipNextImage();
			seriesIndex++;
		}
		else {
			throw new JimiException("Attemping to move beyond last image.");
		}
	}

	/*
	 * Random access methods.
	 */

	/**
	 * Get an ImageProducer for an image at a specified index in the image series.
	 * This method is not guaranteed to return corrently when referencing
	 * an image behind the reader's current index.
	 * @param n the index of the image to decode
	 * @return the ImageProducer for image number n
	 * @exception JimiException if an error prevents decoding
	 */
	public ImageProducer getImageProducer(int n)
		throws JimiException
	{
		if (n < seriesIndex) {
			throw new JimiException("Unable to access image number " + n);
		}
		while (seriesIndex < n) {
			skipNextImage();
		}
		return getNextImageProducer();
	}

	/**
	 * Get an Image at a specified index in the image series.
	 * This method is not guaranteed to return corrently when referencing
	 * an image behind the reader's current index.
	 * @param n the index of the image to decode
	 * @return the ImageProducer for image number n
	 * @exception JimiException if an error prevents decoding
	 */
	public Image getImage(int n)
		throws JimiException
	{
		if (n == cacheIndex && cacheImage != null) {
			return cacheImage;
		}
		else {
			ImageProducer prod = getImageProducer(n);
			Image i = Toolkit.getDefaultToolkit().createImage(prod);
			if (synchronous) {
				GraphicsUtils.waitForImage(i);
			}
			return i;
		}
	}

	/**
	 * Close the reader.  This can be called to indicate that no more images
	 * will be requested.  When all requested images are fully loaded,
	 * the input is closed.  This is implied by single-image getting methods.
	 */
	public void close()
	{
		if (decoder != null) {
			decoder.setFinished();
		}
	}

	protected JimiRasterImage getNextJimiImage()
		throws JimiException
	{
		return getNextJimiImage(true);
	}

	/**
	 * Get the next available image.
	 *
	 * @param allowSynchronous false if blocking should not be performed in this method
	 * @return the image
	 * @exception JimiException if an error prevents the image from being loaded
	 */
	protected JimiRasterImage getNextJimiImage(boolean allowSynchronous)
		throws JimiException
	{
		if (builtinJPEG) {
			if (cacheIndex == 0) {
				return cacheJimiImage;
			}
			else if (seriesIndex == 0) {
				Image image = getBuiltinJPEG();
				cacheImage = image;
				cacheImageProducer = null;
				cacheJimiImage = null;
				cacheIndex = 0;
				seriesIndex++;
				return Jimi.createRasterImage(image.getSource());
			}
			else {
				throw new JimiException();
			}
		}
		else {
			JimiDecodingController controller = series.getNextController();
			JimiImage ji = controller.getJimiImage();
			if (allowSynchronous && synchronous) {
				controller.requestDecoding();
				ji.waitFinished();
			}
			JimiRasterImage rasterImage = JimiUtil.asJimiRasterImage(ji);
			if (rasterImage == null) {
				throw new JimiException();
			}
			cacheJimiImage = rasterImage;
			cacheImageProducer = rasterImage.getImageProducer();
			cacheImage = null;
			cacheIndex = seriesIndex;
			seriesIndex++;

			return rasterImage;
		}
	}

	/**
	 * Get the next available ImageProducer.
	 *
	 * @exception JimiException if no more ImageProducers can be loaded
	 */
	protected ImageProducer getNextImageProducer()
		throws JimiException
	{
		if (builtinJPEG) {
			if (cacheIndex == 0) {
				return cacheImageProducer;
			}
			else if (seriesIndex == 0) {
				Image image = getBuiltinJPEG();
				cacheIndex = 0;
				cacheImage = image;
				cacheImageProducer = image.getSource();
				seriesIndex++;
				return image.getSource();
			}
			else {
				throw new JimiException();
			}
		}
		else {
			JimiRasterImage image = getNextJimiImage(false);
			cacheJimiImage = image;
			cacheImageProducer = image.getImageProducer();
			cacheIndex = seriesIndex;
			return image.getImageProducer();
		}
	}

	/**
	 * Get the next available JimiImage.
	 *
	 * @exception JimiException if no more JimiImages can be loaded
	 */
	protected Image getNextImage()
		throws JimiException
	{
		if (builtinJPEG) {
			if (cacheIndex == 0) {
				if (cacheImage != null) {
					return cacheImage;
				}
				else {
					Image image = Toolkit.getDefaultToolkit().createImage(cacheImageProducer);
					GraphicsUtils.waitForImage(image);
					return image;
				}
			}
			else if (seriesIndex == 0) {
				Image image = getBuiltinJPEG();
				cacheIndex = 0;
				cacheImage = image;
				cacheImageProducer = image.getSource();
				seriesIndex++;
				GraphicsUtils.waitForImage(image);
				return image;
			}
			else {
				throw new JimiException();
			}
		}
		ImageProducer producer = getNextImageProducer();
		Image image = Toolkit.getDefaultToolkit().createImage(producer);
		cacheImage = image;
		GraphicsUtils.waitForImage(image);
		return image;
	}

	/**
	 * Check if more images are available.
	 */
	protected boolean hasMoreElements()
	{
		if (builtinJPEG) {
			return seriesIndex == 0;
		}
		else {
			return series.hasMoreImages();
		}
	}

	/**
	 * Get the decoding controller for the next image in the series.
	 */
	protected JimiDecodingController getNextController()
	{
		JimiDecodingController controller = series.getNextController();

		return controller;
	}

	/**
	 * Use the JDKs inbuilt JPEG decoder to read an image.
	 * Ensure that the image is back-ended by a JimiRasterImage.
	 */
	protected Image getBuiltinImage()
	{
		JimiRasterImage rasterImage = null;
		try {
			rasterImage = getBuiltinJimiImage();
		}
		catch (JimiException e) {
			return JimiUtil.getErrorImage();
		}
		cacheJimiImage = rasterImage;
		cacheImageProducer = rasterImage.getImageProducer();
		Image image = Toolkit.getDefaultToolkit().createImage(rasterImage.getImageProducer());

		return image;
	}

	/**
	 * Use the JDKs inbuilt JPEG decoder to read an image, and convert it to a
	 * JimiRasterImage.
	 */
	protected JimiRasterImage getBuiltinJimiImage()
		throws JimiException
	{
		Image image = getBuiltinJPEG();
		JimiRasterImage rasterImage = Jimi.createRasterImage(image.getSource(), imageFactory);
		return rasterImage;
	}


	/**
	 * Low-level interface to builtin JPEG decoder.
	 */
	protected Image getBuiltinJPEG()
	{
		Image awtimage = null;
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		if (location != null) {
			awtimage = toolkit.getImage(location);
		}
		else if (filename != null) {
			awtimage = toolkit.getImage(filename);
		}
		else {
			try {
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				byte[] buf = new byte[STREAM_BUFFER_SIZE];

				int read_len = 0;
				while ((read_len = input.read(buf)) != -1) {
					byteOut.write(buf, 0, read_len);
				}
				input.close();

				byte[] data = byteOut.toByteArray();

				awtimage = toolkit.createImage(data);
			}
			catch (IOException e) {
				awtimage = JimiUtil.getErrorImage();
			}
		}

		if (Jimi.crippled) {
			ImageFilter filter = new StampImageFilter();
			ImageProducer prod = new FilteredImageSource(awtimage.getSource(), filter);
			awtimage = Toolkit.getDefaultToolkit().createImage(prod);
		}

		return awtimage;
	}

	/**
	 * Cleanup command for stream closing.
	 * Used to have decoders close streams, but only when the streams
	 * are created by the JimiReader itself.
	 */
	class StreamCloseCommand implements Runnable
	{
		public void run()
		{
			try {
				JimiReader.this.input.close();
			}
			catch (IOException e) {
			}
		}
	}

}

/**
 * Class for enumerating decoder results in the desired form,
 * Image, ImageProducer, or JimiImage.
 */
class ImageSeriesEnumerator implements Enumeration
{
	protected JimiReader reader;

	protected int type;

	protected static final int IMAGE = 0;
	protected static final int JIMIIMAGE = 1;
	protected static final int IMAGEPRODUCER = 2;

	protected boolean loadedFirstImage = false;

	protected boolean error = false;

	protected Object prev;

	/**
	 * Enumerate an image series using a specified type.
	 * @param contoller the controller for the series
	 * @param type the type, either ImageProducer, Image, or JimiImage
	 */
	public ImageSeriesEnumerator(JimiReader reader, int type)
	{
		this.reader = reader;
		this.type = type;
	}

	public boolean hasMoreElements()
	{
		return (!error) && ((!loadedFirstImage) || reader.hasMoreElements());
	}

	public Object nextElement()
	{
		return createNextElement();
	}

	public Object createNextElement()
	{
		loadedFirstImage = true;
		if (type == JIMIIMAGE)
		{
			try {
				return reader.getNextJimiImage();
			} catch (JimiException e) {
				error = true;
				return null;
			}
		}
		else if (type == IMAGE) {
			try {
				return reader.getNextImage();
			} catch (JimiException e) {
				error = true;
				return JimiUtil.getErrorImage();
			}
		}
		else if (type == IMAGEPRODUCER) {
			try {
				return reader.getNextImageProducer();
			} catch (JimiException e) {
				error = true;
				return JimiUtil.getErrorImageProducer();
			}
		}
		return null;
	}
}

