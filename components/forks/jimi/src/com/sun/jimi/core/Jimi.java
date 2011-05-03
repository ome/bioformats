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
import java.awt.event.*;
import java.awt.image.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;

import com.sun.jimi.core.raster.JimiRasterImage;
import com.sun.jimi.core.raster.JimiRasterImageImporter;
import com.sun.jimi.core.util.QueuedImageProducerProxy;
import com.sun.jimi.core.util.JimiUtil;
import com.sun.jimi.core.util.JimiImageFactoryProxy;
import com.sun.jimi.core.filters.AspectReplicateScaleFilter;

/**
 * <h3>Jimi - Java Image Management Interface</h3>
 * <strong>(c) 1997-1999 Sun Microsystems, Inc.</strong><p>
 *
 * <h4>Introduction</h4>
 *
 * The <strong>Java Image Management Interface</strong> provides facilities for loading
 * and saving images in many different file formats. It provides a simple interface
 * to loading and saving of images which is consistent with the way the built-in AWT Toolkit
 * image load mechanism for jpeg and gif files operates.<p>
 * 
 * This class "Jimi" presents the highest and simplest level access to the image
 * loading and saving functionality.<p>
 *
 * <h4>Image Data Sources and Destinations</h4>
 *
 * The <code>getImageProducer</code> methods are provided for those wishing to
 * avoid the overhead of having an Image object created for the decoded
 * image.<p>
 *
 * Image data may be loaded from the following image data sources.<p>
 * <ul>
 * <li> File
 * <li> URL
 * <li> InputStream
 * </ul>
 * <p>
 * The encoded image data can be output to the following destinations.<p>
 * <ul>
 * <li> File
 * <li> OutputStream
 * </ul>
 * <p>
 *
 * <h4>Loading Images</h4>
 *
 * The methods to use for loading images have the method name <code>getImage</code>.
 *
 * Images can be loaded in several ways, including using custom Virtual Memory Management,
 * and also with an enhanced interface which provides more fined-grained access to images -
 * JimiRasterImage.  To be able to decode images, all you need is a filename, URL, or InputStream
 * to read them from.  Some of the basic <code>getImage</code> methods available are:
 * <ul>
 * <li><code><A HREF="#getImage(java.lang.String, java.lang.String)">getImage(Filename)</A></code>
 * <li><code><A HREF="#getImage(java.io.InputStream, java.lang.String)">getImage(InputStream)</A></code>
 * <li><code><A HREF="#getImage(java.net.URL, java.lang.String)">getImage(URL)</A></code>
 * <li><code><A HREF="#getImage(java.io.InputStream)">getImage(InputStream)</A></code>
 * </ul>
 *
 * <p>
 * When loading images, there are several flags available:
 * <ul>
 * <li><strong>ASYNCHRONOUS</strong>: This flag requests that images be loaded asynchronously, so the
 * method will return immediately and the image will be populated with pixel data with a separate thread.
 * <br>
 * This is a default flag.
 * </li>
 * <li><strong>SYNCHRONOUS</strong>: This flag requests that the image be fully loaded before the
 * getImage call returns.
 * </li>
 * <p>
 * <li><strong>IN_MEMORY</strong>: This flag requests that the image be loaded into an in-memory buffer for
 * optimal speed of pixel access.
 * <br>
 * This is a default flag.
 * </li>
 * <li><strong>VIRTUAL_MEMORY</strong>: This flag requests that the image be loaded using Jimi's own Virtual
 * Memory Management system to create a page file.  This option gives full access to pixel data using
 * only minimal amounts of memory for buffering.
 * </li>
 * <li><strong>ONE_SHOT</strong>: This flag requests that the image be decoded directly to the first
 * ImageConsumer with no intermediate storage.  This allows the fastest and most memory-efficient form of
 * loading, but the image generated cannot be saved and its pixel data cannot be access directly.
 * </li>
 * </ul>
 * <p>
 * For example, to create an ImageProducer for reading the file "myFile.png", you would write:
 * <p>
 * <code>ImageProducer myProducer = Jimi.getImageProducer("myFile.png", Jimi.VIRTUAL_MEMORY);</code>
 * <h4>Saving Images</h4>
 *
 * The methods to use for saving images have the method name <code>putImage</code>.
 * <ul>
 * <li><code><A HREF="#putImage(java.lang.String, java.awt.Image, java.lang.String)">putImage(typeID, Image, filename)</A></code>
 * <li><code><A HREF="#putImage(java.lang.String, java.awt.Image, java.io.OutputStream)">putImage(typeID, Image, OutputStream)</A></code>
 * <li><code><A HREF="#putImage(java.lang.String, java.awt.ImageProducer, java.lang.String)">putImage(typeID, ImageProducer, filename)</A></code>
 * <li><code><A HREF="#putImage(java.lang.String, java.awt.ImageProducer, java.io.OutputStream)">putImage(typls
eID, ImageProducer, OutputStream)</A></code>
 * </ul>
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.5 $
 */
public class Jimi
{
	static {
		if (System.getSecurityManager() == null) {
			System.runFinalizersOnExit(true);
		}
	}

	public static final int 
	/** Flag indicating that image loading should be asynchronous */
	ASYNCHRONOUS = 1,
		/** Flag indicating that image loading should be synchronous */
		SYNCHRONOUS = 1 << 1,
		/** Flag indicating that image data should be loaded into in-memory buffers */
		IN_MEMORY = 1 << 2,
		/** Flag indicating that image data should be loaded into virtual-memory buffers */
		VIRTUAL_MEMORY = 1 << 3,
		/** Flag indicating that "one-shot" image production should be used */
		ONE_SHOT = 1 << 4;


	protected static int defaultFlags = ASYNCHRONOUS | IN_MEMORY;

	protected static JimiImageFactory memoryFactory;
	protected static JimiImageFactory vmemFactory;
	protected static JimiImageFactory oneshotFactory;

	private Jimi()
	{
	}

	/**
	 * Get an ImageProducer for an image.
	 * @param filename the file to read the image from
	 * @return the ImageProducer
	 */
	public static ImageProducer getImageProducer(String filename)
	{
		return getImageProducer(filename, defaultFlags);
	}

	/**
	 * Get an ImageProducer for an image.
	 * @param filename the file to read the image from
	 * @param flags flags indicating the mode of image loading
	 * @return the ImageProducer
	 */
	public static ImageProducer getImageProducer(String filename, int flags)
	{
		try {
			JimiReader reader = new JimiReader(getFactory(flags), filename);
			reader.setBlocking(JimiUtil.flagSet(flags, SYNCHRONOUS));
			return reader.getImageProducer();
		}
		catch (JimiException e) {
			return JimiUtil.getErrorImageProducer();
		}
	}

	/**
	 * Load an Image.
	 * @param filename the file to load the image from
	 * @return the Image
	 */
	public static Image getImage(String filename)
	{
		return getImage(filename, defaultFlags);
	}

	/**
	 * Load an Image.
	 * @param filename the file to load the image from
	 * @param flags flags indicating the mode of image loading
	 * @return the Image
	 */
	public static Image getImage(String filename, int flags)
	{
		return Toolkit.getDefaultToolkit().createImage(getImageProducer(filename, flags));
	}

	/**
	 * Load an image into a JimiRasterImage for easier access to image information.
	 * @param filename the file to load the image from
	 */
	public static JimiRasterImage getRasterImage(String filename)
		throws JimiException
	{
		return getRasterImage(filename, defaultFlags);
	}

	/**
	 * Load an image into a JimiRasterImage for easier access to image information.
	 * @param filename the file to load the image from
	 * @param flags flags indicating the mode of image loading
	 * @return a JimiRasterImage representing the image
	 */
	public static JimiRasterImage getRasterImage(String filename, int flags)
		throws JimiException
	{
		JimiReader reader = new JimiReader(getFactory(flags), filename);
		reader.setBlocking(JimiUtil.flagSet(flags, SYNCHRONOUS));
		return reader.getRasterImage();
	}

	/**
	 * Get an ImageProducer for an image.
	 * @param in the stream to read the image from
	 * @return the ImageProducer
	 */
	public static ImageProducer getImageProducer(InputStream in)
	{
		return getImageProducer(in, defaultFlags);
	}

	/**
	 * Get an ImageProducer for an image.
	 * @param in the stream to read the image from
	 * @param flags flags indicating the mode of image loading
	 * @return the ImageProducer
	 */
	public static ImageProducer getImageProducer(InputStream in, int flags)
	{
		try {
			JimiReader reader = new JimiReader(getFactory(flags), in);
			reader.setBlocking(JimiUtil.flagSet(flags, SYNCHRONOUS));
			return reader.getImageProducer();
		}
		catch (JimiException e) {
			return JimiUtil.getErrorImageProducer();
		}
	}

	/**
	 * Get an ImageProducer for an image.
	 * @param in the stream to read the image from
	 * @param typeID the mime-type of the image data format
	 * @return the ImageProducer
	 */
	public static ImageProducer getImageProducer(InputStream in, String typeID)
	{
		return getImageProducer(in, typeID, defaultFlags);
	}

	/**
	 * Get an ImageProducer for an image.
	 * @param in the stream to read the image from
	 * @param typeID the mime-type of the image data format
	 * @param flags flags indicating the mode of image loading
	 * @return the ImageProducer
	 */
	public static ImageProducer getImageProducer(InputStream in, String typeID, int flags)
	{
		try {
			JimiReader reader = new JimiReader(getFactory(flags), in, typeID);
			reader.setBlocking(JimiUtil.flagSet(flags, SYNCHRONOUS));
			return reader.getImageProducer();
		}
		catch (JimiException e) {
			return JimiUtil.getErrorImageProducer();
		}
	}

	/**
	 * Get an ImageProducer for an image.
	 * @param filename the file to read the image from
	 * @param typeID the mime-type of the image data format
	 * @return the ImageProducer
	 */
	public static ImageProducer getImageProducer(String filename, String typeID)
	{
		return getImageProducer(filename, typeID, defaultFlags);
	}

	/**
	 * Get an ImageProducer for an image.
	 * @param filename the file to read the image from
	 * @param typeID the mime-type of the image data format
	 * @param flags flags indicating the mode of image loading
	 * @return the ImageProducer
	 */
	public static ImageProducer getImageProducer(String filename, String typeID, int flags)
	{
		try {
			JimiReader reader = new JimiReader(getFactory(flags), filename, typeID);
			reader.setBlocking(JimiUtil.flagSet(flags, SYNCHRONOUS));
			return reader.getImageProducer();
		}
		catch (JimiException e) {
			return JimiUtil.getErrorImageProducer();
		}
	}

	/**
	 * Load an Image.
	 * @param in the stream to load the image from
	 * @return the Image
	 */
	public static Image getImage(InputStream in)
	{
		return getImage(in, defaultFlags);
	}

	/**
	 * Load an Image.
	 * @param in the stream to load the image from
	 * @param flags flags indicating the mode of image loading
	 * @return the Image
	 */
	public static Image getImage(InputStream in, int flags)
	{
		return Toolkit.getDefaultToolkit().createImage(getImageProducer(in, flags));
	}

	/**
	 * Load an Image.
	 * @param in the stream to load the image from
	 * @return the Image
	 */
	public static Image getImage(InputStream in, String typeID)
	{
		return getImage(in, typeID, defaultFlags);
	}

	/**
	 * Load an Image.
	 * @param in the stream to load the image from
	 * @param flags flags indicating the mode of image loading
	 * @return the Image
	 */
	public static Image getImage(InputStream in, String typeID, int flags)
	{
		return Toolkit.getDefaultToolkit().createImage(getImageProducer(in, typeID, flags));
	}

	/**
	 * Load an Image.
	 * @param filename the file to read the image from
	 * @param flags flags indicating the mode of image loading
	 * @return the Image
	 */
	public static Image getImage(String filename, String typeID, int flags)
	{
		return Toolkit.getDefaultToolkit().createImage(getImageProducer(filename, typeID, flags));
	}

	/**
	 * Load an Image.
	 * @param filename the file to read the image from
	 * @param flags flags indicating the mode of image loading
	 * @return the Image
	 */
	public static Image getImage(String filename, String typeID)
	{
		return getImage(filename, typeID, defaultFlags);
	}

	/**
	 * Get an ImageProducer for an image.
	 * @param location the location to read the image from
	 * @return the ImageProducer
	 */
	public static ImageProducer getImageProducer(URL location)
	{
		return getImageProducer(location, defaultFlags);
	}

	/**
	 * Get an ImageProducer for an image.
	 * @param location the location to read the image from
	 * @param flags flags indicating the mode of image loading
	 * @return the ImageProducer
	 */
	public static ImageProducer getImageProducer(URL location, int flags)
	{
		try {
			JimiReader reader = new JimiReader(getFactory(flags), location);
			return reader.getImageProducer();
		}
		catch (JimiException e) {
			return JimiUtil.getErrorImageProducer();
		}
	}

	/**
	 * Get an ImageProducer for an image.
	 * @param location the location to read the image from
	 * @param typeID mime-type of the image format
	 * @param flags flags indicating the mode of image loading
	 * @return the ImageProducer
	 */
	public static ImageProducer getImageProducer(URL location, String typeID, int flags)
	{
		try {
			JimiReader reader = new JimiReader(getFactory(flags), location, typeID);
			return reader.getImageProducer();
		}
		catch (JimiException e) {
			return JimiUtil.getErrorImageProducer();
		}
	}

	/**
	 * Get an ImageProducer for an image.
	 * @param location the location to read the image from
	 * @param typeID mime-type of the image format
	 * @return the ImageProducer
	 */
	public static ImageProducer getImageProducer(URL location, String typeID)
	{
		return getImageProducer(location, typeID, defaultFlags);
	}

	/**
	 * Load an Image.
	 * @param location the location to load the image from
	 * @param typeID mime-type of the image format
	 * @param flags flags indicating the mode of image loading
	 * @return the Image
	 */
	public static Image getImage(URL location, String typeID, int flags)
	{
		return Toolkit.getDefaultToolkit().createImage(getImageProducer(location, typeID, flags));
	}

	/**
	 * Load an Image.
	 * @param location the location to load the image from
	 * @param typeID mime-type of the image format
	 * @return the Image
	 */
	public static Image getImage(URL location, String typeID)
	{
		return getImage(location, typeID, defaultFlags);
	}

	/**
	 * Load an Image.
	 * @param loaction the location to load the image from
	 * @return the Image
	 */
	public static Image getImage(URL location)
	{
		return getImage(location, defaultFlags);
	}

	/**
	 * Load an Image.
	 * @param location the location to load the image from
	 * @param flags flags indicating the mode of image loading
	 * @return the Image
	 */
	public static Image getImage(URL location, int flags)
	{
		return Toolkit.getDefaultToolkit().createImage(getImageProducer(location, flags));
	}

	/**
	 * Load an image into a JimiRasterImage for easier access to image information.
	 * @param location the location to load the image from
	 */
	public static JimiRasterImage getRasterImage(URL location)
		throws JimiException
	{
		return getRasterImage(location, defaultFlags);
	}

	/**
	 * Load an image into a JimiRasterImage for easier access to image information.
	 * @param location the file to load the image from
	 * @param flags flags indicating the mode of image loading
	 * @return a JimiRasterImage representing the image
	 */
	public static JimiRasterImage getRasterImage(URL location, int flags)
		throws JimiException
	{
		JimiReader reader = new JimiReader(getFactory(flags), location);
		return reader.getRasterImage();
	}

	/**
	 * Load an image into a JimiRasterImage for easier access to image information.
	 * @param location the location to load the image from
	 * @param typeID the mime-type of the image
	 */
	public static JimiRasterImage getRasterImage(URL location, String typeID)
		throws JimiException
	{
		return getRasterImage(location, typeID, defaultFlags);
	}

	/**
	 * Load an image into a JimiRasterImage for easier access to image information.
	 * @param location the location to load the image from
	 * @param typeID the mime-type of the image
	 * @param flags flags indicating the mode of image loading
	 */
	public static JimiRasterImage getRasterImage(URL location, String typeID, int flags)
		throws JimiException
	{
		JimiReader reader = new JimiReader(getFactory(flags), location, typeID);
		return reader.getRasterImage();
	}

	/**
	 * Load an image into a JimiRasterImage for easier access to image information.
	 * @param input the stream to read image data from
	 * @param typeID the mimetype of the image data format
	 * @return a JimiRasterImage representing the image
	 */
	public static JimiRasterImage getRasterImage(InputStream input, String typeID)
		throws JimiException
	{
		return getRasterImage(input, typeID, defaultFlags);
	}

	/**
	 * Load an image into a JimiRasterImage for easier access to image information.
	 * @param input the stream to read image data from
	 * @param typeID the mimetype of the image data format
	 * @param flags flags indicating the mode of image loading
	 * @return a JimiRasterImage representing the image
	 */
	public static JimiRasterImage getRasterImage(InputStream input, String typeID, int flags)
		throws JimiException
	{
		JimiReader reader = new JimiReader(getFactory(flags), input, typeID);
		return reader.getRasterImage();
	}

	public static JimiRasterImage getRasterImage(InputStream input)
		throws JimiException
	{
		return getRasterImage(input, defaultFlags);
	}

	public static JimiRasterImage getRasterImage(InputStream input, int flags)
		throws JimiException
	{
		JimiReader reader = new JimiReader(getFactory(flags), input);
		return reader.getRasterImage();
	}

	/**
	 * Save an image.
	 * @param image the image to save
	 * @param filename the file to write the image into
	 * @exception JimiException if an error prevents image encoding
	 */
	public static void putImage(Image image, String filename)
		throws JimiException
	{
		putImage(image.getSource(), filename);
	}

	/**
	 * Save an image.
	 * @param producer the image to save
	 * @param filename the file to write the image into
	 * @exception JimiException if an error prevents image encoding
	 */
	public static void putImage(ImageProducer producer, String filename)
		throws JimiException
	{
		putImage(createRasterImage(producer), filename);
	}

	/**
	 * Save an image.
	 * @param ji the image to save
	 * @param filename the file to write the image into
	 * @exception JimiException if an error prevents image encoding
	 */
	public static void putImage(JimiImage ji, String filename)
		throws JimiException
	{
		JimiWriter jw = new JimiWriter(filename);
		jw.setSource(ji);
		jw.putImage(filename);
	}

	/**
	 * Save an image.
	 * @param image the image to save
	 * @param output the stream to write the image into
	 * @param id the mime-type of the format to save the image as
	 * @exception JimiException if an error prevents image encoding
	 */
	public static void putImage(String id, Image image, OutputStream output)
		throws JimiException
	{
		putImage(id, image.getSource(), output);
	}

	/**
	 * Save an image.
	 * @param producer the image to save
	 * @param output the stream to write the image into
	 * @param id the mime-type of the format to save the image as
	 * @exception JimiException if an error prevents image encoding
	 */
	public static void putImage(String id, ImageProducer producer, OutputStream output)
		throws JimiException
	{
		putImage(id, createRasterImage(producer), output);
	}

	/**
	 * Save an image.
	 * @param ji the image to save
	 * @param output the stream to write the image into
	 * @param id the mime-type of the format to save the image as
	 * @exception JimiException if an error prevents image encoding
	 */
	public static void putImage(String id, JimiImage ji, OutputStream output)
		throws JimiException
	{
		JimiWriter jw = new JimiWriter(output, id);
		jw.setSource(ji);
		jw.putImage(output);
	}

	/**
	 * Save an image.
	 * @param image the image to save
	 * @param filename the file to write to
	 * @param id the mime-type of the format to save the image as
	 * @exception JimiException if an error prevents image encoding
	 */
	public static void putImage(String id, JimiImage image, String filename)
		throws JimiException
	{
		JimiWriter jw = new JimiWriter(filename, id);
		jw.setSource(image);
		jw.putImage(filename);
	}

	/**
	 * Save an image.
	 * @param image the image to save
	 * @param filename the file to write to
	 * @param id the mime-type of the format to save the image as
	 * @exception JimiException if an error prevents image encoding
	 */
	public static void putImage(String id, Image image, String filename)
		throws JimiException
	{
		JimiWriter jw = new JimiWriter(filename, id);
		jw.setSource(image);
		jw.putImage(filename);
	}

	/**
	 * Save an image.
	 * @param image the image to save
	 * @param filename the file to write to
	 * @param id the mime-type of the format to save the image as
	 * @exception JimiException if an error prevents image encoding
	 */
	public static void putImage(String id, ImageProducer image, String filename)
		throws JimiException
	{
		JimiWriter jw = new JimiWriter(filename, id);
		jw.setSource(image);
		jw.putImage(filename);
	}

	/**
	 * Create a JimiRasterImage using pixel data collected from an ImageProducer.
	 * @param producer the ImageProducer to collect data from
	 * @return the resulting JimiRasterImage
	 * @exception JimiException if an error prevents the image from being created
	 */
	public static JimiRasterImage createRasterImage(ImageProducer producer)
		throws JimiException
	{
		return createRasterImage(producer, IN_MEMORY);
	}

	/**
	 * Create a JimiRasterImage using pixel data collected from an ImageProducer.
	 * @param producer the ImageProducer to collect data from
	 * @param flags the flags indicating which type of image to create, either
	 * VIRUTAL_MEMORY or IN_MEMORY.
	 * @return the resulting JimiRasterImage
	 * @exception JimiException if an error prevents the image from being created
	 */
	public static JimiRasterImage createRasterImage(ImageProducer producer, int flags)
		throws JimiException
	{
		if (producer instanceof QueuedImageProducerProxy) {
			producer = ((QueuedImageProducerProxy)producer).getImageProducer();
		}
		if (producer instanceof JimiRasterImage) {
			return (JimiRasterImage)producer;
		}
		else {
			return createRasterImage(producer, getFactory(flags));
		}
	}

	/**
	 * Create a JimiReader without setting the decoding source.
	 * The reader must later be initialized with <code>setSource</code>.
	 * @param typeID the mime-type for the decoding.
	 * @return the JimiReader
	 */
	public static JimiReader createTypedJimiReader(String typeID)
		throws JimiException
	{
		return createTypedJimiReader(typeID, defaultFlags);
	}

	/**
	 * Create a JimiReader without setting the decoding source.
	 * The reader must later be initialized with <code>setSource</code>.
	 * @param typeID the mime-type for the decoding.
	 * @param flags flags indicating the mode of image loading
	 * @return the JimiReader
	 */
	public static JimiReader createTypedJimiReader(String typeID, int flags)
		throws JimiException
	{
		JimiReader jr = new JimiReader(getFactory(flags));
		jr.setMimeType(typeID);

		return jr;
	}

	/**
	 * Create a JimiReader to control decoding of an image or series of images.
	 * @param filename the file to read the image from
	 * @return the JimiReader
	 * @exception JimiException if an error prevents the JimiReader from being initialized
	 */
	public static JimiReader createJimiReader(String filename)
		throws JimiException
	{
		return createJimiReader(filename, defaultFlags);
	}

	/**
	 * Create a JimiReader to control decoding of an image or series of images.
	 * @param filename the file to read the image from
	 * @return the JimiReader
	 * @exception JimiException if an error prevents the JimiReader from being initialized
	 */
	public static JimiReader createJimiReader(InputStream input)
		throws JimiException
	{
		return createJimiReader(input, defaultFlags);
	}

	/**
	 * Create a JimiReader to control decoding of an image or series of images.
	 * @param input the stream to read image data from
	 * @param typeID the mimetype of the image file format
	 * @param flags the flags indicating which type of image to create, either
	 * @return a JimiReader to read an image from the stream
	 */
	public static JimiReader createTypedJimiReader(InputStream input, String typeID)
		throws JimiException
	{
		return createTypedJimiReader(input, typeID);
	}

	/**
	 * Create a JimiReader to control decoding of an image or series of images.
	 * @param input the stream to read image data from
	 * @param typeID the mimetype of the image file format
	 * @param flags the flags indicating which type of image to create, either
	 * @return a JimiReader to read an image from the stream
	 */
	public static JimiReader createTypedJimiReader(InputStream input, String typeID, int flags)
		throws JimiException
	{
		return new JimiReader(getFactory(flags), input, typeID);
	}

	/**
	 * Create a JimiWriter for a specified Mime-type to control image encoding with.
	 * @param typeID the mime-type of the format to write with
	 */
	public static JimiWriter createTypedJimiWriter(String typeID)
		throws JimiException
	{
		return new JimiWriter((OutputStream)null, typeID);
	}

	/**
	 * Create a JimiReader to control decoding of an image or series of images.
	 * @param filename the file to read the image from
	 * @return the JimiReader
	 * @exception JimiException if an error prevents the JimiReader from being initialized
	 */
	public static JimiReader createJimiReader(URL location)
		throws JimiException
	{
		return createJimiReader(location, defaultFlags);
	}

	/**
	 * Create a JimiReader to control decoding of an image or series of images.
	 * @param filename the file to read the image from
	 * @return the JimiReader
	 * @exception JimiException if an error prevents the JimiReader from being initialized
	 */
	public static JimiReader createJimiReader(String filename, int flags)
		throws JimiException
	{
		return new JimiReader(getFactory(flags), filename);
	}

	/**
	 * Create a JimiReader to control decoding of an image or series of images.
	 * @param filename the file to read the image from
	 * @return the JimiReader
	 * @exception JimiException if an error prevents the JimiReader from being initialized
	 */
	public static JimiReader createJimiReader(InputStream input, int flags)
		throws JimiException
	{
		return new JimiReader(getFactory(flags), input);
	}

	/**
	 * Create a JimiReader to control decoding of an image or series of images.
	 * @param filename the file to read the image from
	 * @return the JimiReader
	 * @exception JimiException if an error prevents the JimiReader from being initialized
	 */
	public static JimiReader createJimiReader(URL location, int flags)
		throws JimiException
	{
		return new JimiReader(getFactory(flags), location);
	}

	/**
	 * Create a JimiWriter to control encoding of an image or series of images.
	 * @param filename the file to write to
	 * @return the JimiWriter
	 * @exception JimiException if an error prevents the JimiWriter from being initialized
	 */
	public static JimiWriter createJimiWriter(String filename)
		throws JimiException
	{
		return new JimiWriter(filename);
	}

	/**
	 * Create a JimiWriter to control encoding of an image or series of images.
	 * @param id the mime-type of the format to write image data as
	 * @param output the stream to write to
	 * @return the JimiWriter
	 * @exception JimiException if an error prevents the JimiWriter from being initialized
	 */
	public static JimiWriter createJimiWriter(String id, OutputStream output)
		throws JimiException
	{
		return new JimiWriter(output, id);
	}

	/**
	 * Set the default flags to use for image decoding.  This is initially set to
	 * <code>ASYNCHRONOUS | IN_MEMORY</code>.
	 * @param flags decoding flags
	 */
	public static void setDefaultFlags(int flags)
	{
		defaultFlags = flags;
	}

	/**
	 * Get a list of all mime-types supported for image encoding.
	 * @return a String[] containing the types
	 */
	public static String[] getEncoderTypes()
	{
		String[] types = new String[JimiControl.mimeToEncoderMap.size()];
		Enumeration enum = JimiControl.mimeToEncoderMap.keys();
		for (int i = 0; (i < types.length) && (enum.hasMoreElements()); i++) {
			types[i] = (String)enum.nextElement();
		}
		return types;
	}

	/**
	 * Get a list of all mime-types supported for image encoding.
	 * @return a String[] containing the types
	 */
	public static String[] getDecoderTypes()
	{
		String[] types = new String[JimiControl.mimeToDecoderMap.size()];
		Enumeration enum = JimiControl.mimeToDecoderMap.keys();
		for (int i = 0; (i < types.length) && (enum.hasMoreElements()); i++) {
			types[i] = (String)enum.nextElement();
		}
		return types;
	}

	protected static JimiRasterImage createRasterImage(ImageProducer producer, JimiImageFactory factory)
		throws JimiException
	{
		while (factory instanceof JimiImageFactoryProxy) {
			factory = ((JimiImageFactoryProxy)factory).getProxiedFactory();
		}
		return JimiRasterImageImporter.importImage(producer, factory);
	}

	/**
	 * Get a JimiImageFactory based on flags provided.
	 * @param flags flags indicating whether to give in-memory, VMM, or One-shot.
	 */
	public static JimiImageFactory getFactory(int flags)
	{
		if ((flags & ONE_SHOT) != 0) {
			return oneshotFactory;
		}
		else if ((flags & VIRTUAL_MEMORY) != 0) {
			if (vmemFactory == null) {
				if (limited) {
					vmemFactory = memoryFactory;
				}
				try {
					Class vmemFactoryClass = Class.forName("com.sun.jimi.core.VMemJimiImageFactory");
					vmemFactory = (JimiImageFactory)vmemFactoryClass.newInstance();
					//VMMControl.setFactory((VMemJimiImageFactory)vmemFactory);
				}
				catch (Exception e)
				{
					vmemFactory = memoryFactory;
				}
			}
			return vmemFactory;
		}
		else {
			return memoryFactory;
		}
	}

	// limited operates as Jimi Standard
	static boolean limited = false;
	// crippled is keyless operation
	static boolean crippled = false;;
	// true if demo version
	static boolean demoversion = false;;
	/*
	 * Key checking
	 */
	static
	{
		JimiControl.addExtension(new JimiProExtension());
		memoryFactory = JimiFactoryManager.getMemoryFactory();
		oneshotFactory = JimiFactoryManager.getOneshotFactory();
	}


	/**
	 * Entry method:
	 *   "-version" prints Jimi version and build number.
	 *   "-encoders" lists all encoder mime-types
	 *   "-decoders" lists all decoder mime-types
	 */
	public static void main(String[] args)
	{
		if (args.length == 0) {
			System.err.println("Usage: Jimi <-version | -encoders | -decoders>");
		}
		if (args[0].equals("-version")) {
			try {
				java.util.Properties props = new java.util.Properties();
				props.load(Jimi.class.getResourceAsStream("version"));
				System.out.println("Jimi version: " + props.getProperty("jimi.version") +
								   " (build " + props.getProperty("jimi.build") + ")");
			}
			catch (Exception e) {
				System.err.println("Unable to read version information: " + e.toString());
			}
		}
		else if (args[0].equals("-encoders")) {
			System.out.println("Supported encoder mimetypes:");
			String[] types = getEncoderTypes();
			for (int i = 0; i < types.length; i++) {
				System.out.println(types[i]);
			}
		}
		else if (args[0].equals("-decoders")) {
			System.out.println("Supported decoder mimetypes:");
			String[] types = getDecoderTypes();
			for (int i = 0; i < types.length; i++) {
				System.out.println(types[i]);
			}
		}
	}

	/**
	 * Can be called to ensure that the class is loaded, and static initializers have run.
	 */
	static void init()
	{
	}

}

