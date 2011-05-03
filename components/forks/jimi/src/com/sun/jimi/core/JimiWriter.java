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

import java.io.*;
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.util.*;

import com.sun.jimi.core.util.*;
import com.sun.jimi.core.raster.JimiRasterImage;
import com.sun.jimi.core.options.*;

/**
 * JimiWriter provides more fine-grained control of the encoding process, and allows writing
 * a series of images to a single image file.
 *
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 14:52:00 $
 */
public class JimiWriter
{
	protected JimiEncoder encoder;
	protected JimiImage sourceImage;
	protected JimiImageEnumeration sourceImageEnumeration;
	protected FormatOptionSet options = new BasicFormatOptionSet();
	protected FormatOptionSet overrideOptions;

	protected ProgressListener listener;

	/**
	 * Create a JimiWriter.
	 * @param filename the filename to write to
	 * @param mimeType the mimetype of the format to write in
	 */
	protected JimiWriter(String filename, String mimeType)
		throws JimiException
	{
		JimiEncoderFactory factory = JimiControl.getEncoderByType(mimeType);
		if (factory == null) {
			throw new JimiException("Cannot find encoder for type: " + mimeType);
		}
		initJimiWriter(factory);
	}

	/**
	 * Create a JimiWriter.
	 * @param filename the filename to write to
	 */
	protected JimiWriter(String filename)
		throws JimiException
	{
		JimiEncoderFactory factory = JimiControl.getEncoderByFileExtension(filename);
		if (factory == null) {
			throw new JimiException("Cannot find encoder for " + filename);
		}
		initJimiWriter(factory);
	}

	/**
	 * Create a JimiWriter.
	 * @param output the stream to write to
	 * @param mimeType the mimetype of the format to write in
	 */
	protected JimiWriter(OutputStream output, String mimeType)
		throws JimiException
	{
		JimiEncoderFactory factory = JimiControl.getEncoderByType(mimeType);
		if (factory == null) {
			throw new JimiException("Cannot find encoder for type: " + mimeType);
		}
		initJimiWriter(factory);
	}

	/**
	 * Create an uninitialized JimiWriter.
	 */
	protected JimiWriter()
	{
	}

	/**
	 * Choose the mimetype of the format to save in.
	 */
	protected void setMimeType(String typeID)
		throws JimiException
	{
		JimiEncoderFactory factory = JimiControl.getEncoderByType(typeID);
		if (factory == null) {
			throw new JimiException("Cannot find encoder for type: " + typeID);
		}
		initJimiWriter(factory);
	}

	/**
	 * Initialize the writer.
	 */
	protected void initJimiWriter(JimiEncoderFactory factory)
		throws JimiException
	{
		if (Jimi.crippled) {
			throw new JimiException("Keyless operations does not permit saving.");
		}
		if (Jimi.limited && (!(factory instanceof com.sun.jimi.core.util.FreeFormat))) {
			throw new JimiException("This format requires a JIMI Pro license.");
		}
		this.encoder = factory.createEncoder();
		if (listener != null) {
			encoder.setProgressListener(listener);
		}
	}

	/**
	 * Set the options to use for encoding.
	 */
	public void setOptions(FormatOptionSet options)
	{
		this.options = options;
		this.overrideOptions = options;
	}

	/**
	 * Register a ProgressListener to be informed of encoding progress.
	 */
	public void setProgressListener(ProgressListener listener)
	{
		this.listener = listener;
		if (encoder != null) {
			encoder.setProgressListener(listener);
		}
	}

	/**
	 * Set the source image to be written.
	 * @param ji the JimiImage to save
	 */
	public void setSource(JimiImage ji)
		throws JimiException
	{
		sourceImage = JimiUtil.asJimiRasterImage(ji);
		if (overrideOptions != null) {
			((MutableJimiImage)sourceImage).setOptions(overrideOptions);
		}
	}

	/**
	 * Set the source image to be written.
	 * @param ji the JimiImage to save
	 */
	public void setSource(Image image)
		throws JimiException
	{
		setSource(image.getSource());
	}

	/**
	 * Set the source image to be written.
	 * @param ji the JimiImage to save
	 */
	public void setSource(ImageProducer producer)
		throws JimiException
	{
		try {
			sourceImage = Jimi.createRasterImage(producer);
		}
		// invalid source
		catch (JimiException e) {
		}
	}

	/**
	 * Set a series of images as the source to be written.
	 */
	public void setSource(JimiImage[] images)
		throws JimiException
	{
		sourceImageEnumeration = new JimiImageEnumeration(images);
	}

	/**
	 * Set a series of images as the source to be written.
	 */
	public void setSource(ImageProducer[] producers)
		throws JimiException
	{
		sourceImageEnumeration = new JimiImageEnumeration(producers);
	}

	/**
	 * Set a series of images as the source to be written.
	 */
	public void setSource(Image[] images)
		throws JimiException
	{
		sourceImageEnumeration = new JimiImageEnumeration(images);
	}

	/**
	 * Set a series of images as the source to be written.
	 * @param images a JimiImage[], ImageProducer[], or Image[]
	 */
	public void setSource(Object[] images)
		throws JimiException
	{
		if (images instanceof JimiImage[]) {
			setSource((JimiImage[])images);
		}
		else if (images instanceof ImageProducer[]) {
			setSource((ImageProducer[])images);
		}
		else if (images instanceof Image[]) {
			setSource((Image[])images);
		}
		else {
			throw new JimiException("Invalid source.");
		}
	}

	/**
	 * Write the source to a file.
	 */
	public void putImage(String filename)
		throws JimiException
	{
		try {
			OutputStream output = new FileOutputStream(filename);
			output = new BufferedOutputStream(output);
			putImage(output);
			output.close();
		}
		catch (IOException e) {
			throw new JimiException();
		}
	}

	/**
	 * Write the source to a stream.
	 */
	public void putImage(OutputStream output)
		throws JimiException
	{
		// single-image mode
		if (sourceImage != null) {
			sourceImage.setOptions(options);
			encoder.encodeImages(new JimiImageEnumeration(sourceImage), output);
		}
		// multi-image mode
		else if (sourceImageEnumeration != null) {
			sourceImageEnumeration.setOptions(options);
			encoder.encodeImages(sourceImageEnumeration, output);
		}
		else {
			throw new JimiException("No source image set.");
		}
	}

	/*
	 * Legacy options support.
	 */

	/**
	 * @deprecated Use getOptions()
	 */
	public Object getPossibleValuesForProperty(String name)
		throws InvalidOptionException
	{
		try {
			return options.getOption(name).getPossibleValues();
		}
		catch (OptionException e) {
			throw new InvalidOptionException(e.getMessage());
		}
	}

	/**
	 * @deprecated Use getOptions()
	 */
	public String getPropertyDescription(String name)
		throws InvalidOptionException
	{
		try {
			return options.getOption(name).getDescription();
		}
		catch (OptionException e) {
			throw new InvalidOptionException(e.getMessage());
		}
	}

	/**
	 * @deprecated Use getOptions()
	 */
	public void clearProperties()
	{
		options = new BasicFormatOptionSet();
	}

	/**
	 * @deprecated Use getOptions()
	 */
	public Object getProperty(String name)
	{
		try {
			return options.getOption(name).getValue();
		}
		catch (OptionException e) {
			return null;
		}
	}

	/**
	 * @deprecated Use getOptions()
	 */
	public Enumeration getPropertyNames()
	{
		final FormatOption[] formatOptions = options.getOptions();
		return new Enumeration() {
			int index = 0;
			public boolean hasMoreElements() {
				return index < formatOptions.length;
			}
			public Object nextElement() {
				return formatOptions[index++];
			}
		};
	}

	/**
	 * @deprecated Use getOptions()
	 */
	public void setProperty(String name, Object value) throws InvalidOptionException
	{
		try {
			options.getOption(name).setValue(value);
		}
		catch (OptionException e) {
			throw new InvalidOptionException(e.getMessage());
		}
	}
}

