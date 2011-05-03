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
import java.util.*;

/**
 * Central control for managing mappings for encoders, decoders, mimetypes and
 * filetype-detection.
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 12:32:24 $
 */
public class JimiControl
{
	protected static Hashtable mimeToDecoderMap = new Hashtable();
	protected static Hashtable mimeToEncoderMap = new Hashtable();

	protected static Hashtable fileExtensionToDecoderMap = new Hashtable();
	protected static Hashtable fileExtensionToEncoderMap = new Hashtable();

	protected static Vector decoderFactories = new Vector();
	protected static Vector encoderFactories = new Vector();

	protected static Vector extensions = new Vector();

	protected static boolean extensionsAllowed = true;

	/**
	 * Add an extension to Jimi.
	 * @param extension the extension
	 */
	public static synchronized void addExtension(JimiExtension extension)
	{
		if (!extensionsAllowed) {
			throw new RuntimeException("This JIMI license does not permit extensions.");
		}
		// add to list of extensions
		extensions.addElement(extension);
		// add decoders
		JimiDecoderFactory[] decoders = extension.getDecoders();
		for (int i = 0; i < decoders.length; i++) {
			addDecoder(decoders[i]);
		}
		// add encoders
		JimiEncoderFactory[] encoders = extension.getEncoders();
		for (int i = 0; i < encoders.length; i++) {
			addEncoder(encoders[i]);
		}
	}

	protected static JimiDecoderFactory getDecoderByType(String mimeType)
	{
		return (JimiDecoderFactory)mimeToDecoderMap.get(mimeType);
	}

	protected static JimiDecoderFactory getDecoderByFileExtension(String filename)
	{
		String extension = filename;
		if (extension.lastIndexOf(".") != -1) {
			extension = extension.substring(extension.lastIndexOf(".") + 1);
		}
		return (JimiDecoderFactory)fileExtensionToDecoderMap.get(extension.toLowerCase());
	}

	protected static synchronized JimiDecoderFactory getDecoderForInputStream(PushbackInputStream input)
	{
		// only use 16 bytes of signature as a maximum
		byte[] inputData = new byte[16];
		try {
			DataInputStream dis = new DataInputStream(input);
			dis.readFully(inputData);
			input.unread(inputData);
			int numberOfDecoders = decoderFactories.size();
		signature_loop:
			for (int formatIndex = 0; formatIndex < numberOfDecoders; formatIndex++) {
				JimiDecoderFactory decoder = (JimiDecoderFactory)decoderFactories.elementAt(formatIndex);
				byte[][] formatSignatures = decoder.getFormatSignatures();
				if (formatSignatures == null) {
					continue signature_loop;
				}
				for (int sig = 0; sig < formatSignatures.length; sig++) {
					int signatureLength = Math.min(formatSignatures[sig].length, inputData.length);
					for (int i = 0; i < signatureLength; i++) {
						if (formatSignatures[sig][i] != inputData[i]) {
							break;
							//continue signature_loop;
						}
						return decoder;
					}
				}
			}
			return null;
		}
		catch (IOException e) {
			return null;
		}
	}

	protected static JimiEncoderFactory getEncoderByType(String mimeType)
	{
		return (JimiEncoderFactory)mimeToEncoderMap.get(mimeType);
	}

	protected static JimiEncoderFactory getEncoderByFileExtension(String filename)
	{
		String extension = filename;
		if (extension.lastIndexOf(".") != -1) {
			extension = extension.substring(extension.lastIndexOf(".") + 1);
		}
		return (JimiEncoderFactory)fileExtensionToEncoderMap.get(extension.toLowerCase());
	}

	protected static void addDecoder(JimiDecoderFactory decoder)
	{
		decoderFactories.addElement(decoder);

		// add associations between mimetypes and the decoder factory
		String[] mimeTypes = decoder.getMimeTypes();
		for (int i = 0; i < mimeTypes.length; i++) {
			mimeToDecoderMap.put(mimeTypes[i], decoder);
			if (mimeTypes[i].indexOf('/') > 0) {
				String xifiedType = mimeTypes[i].substring(0, mimeTypes[i].indexOf('/') + 1) +
					"x-" +
					mimeTypes[i].substring(mimeTypes[i].indexOf('/') + 1);
				mimeToDecoderMap.put(xifiedType, decoder);
			}
		}
		// and associations for the filename extensions
		String[] fileExtensions = decoder.getFilenameExtensions();
		for (int i = 0; i < fileExtensions.length; i++) {
			fileExtensionToDecoderMap.put(fileExtensions[i], decoder);
		}
	}

	protected static void addEncoder(JimiEncoderFactory encoder)
	{
		encoderFactories.addElement(encoder);

		// add associations between mimetypes and the encoder factory
		String[] mimeTypes = encoder.getMimeTypes();
		for (int i = 0; i < mimeTypes.length; i++) {
			mimeToEncoderMap.put(mimeTypes[i], encoder);
			if (mimeTypes[i].indexOf('/') > 0) {
				String xifiedType = mimeTypes[i].substring(0, mimeTypes[i].indexOf('/') + 1) +
					"x-" +
					mimeTypes[i].substring(mimeTypes[i].indexOf('/') + 1);
				mimeToEncoderMap.put(xifiedType, encoder);
			}
		}
		// and associations for the filename extensions
		String[] fileExtensions = encoder.getFilenameExtensions();
		for (int i = 0; i < fileExtensions.length; i++) {
			fileExtensionToEncoderMap.put(fileExtensions[i], encoder);
		}
	}

	protected static void disableExtensions()
	{
		extensionsAllowed = false;
	}

}

