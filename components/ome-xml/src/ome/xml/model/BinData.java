/*
 * ome.xml.model.BinData
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) @year@ Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee,
 *      University of Wisconsin-Madison
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *-----------------------------------------------------------------------------
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by melissa via xsd-fu on 2012-09-10 13:40:21-0400
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

public class BinData extends AbstractOMEModelObject
{
	// Base:  -- Name: BinData -- Type: BinData -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/BinaryFile/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(BinData.class);

	// -- Instance variables --


	// Property
	private Boolean bigEndian;

	// Property
	private NonNegativeLong length;

	// Property
	private Compression compression;

	// Back reference Pixels_BackReference
	private Pixels pixels;

	// Back reference Mask_BackReference
	private Mask mask;

	// -- Constructors --

	/** Default constructor. */
	public BinData()
	{
		super();
	}

	/** 
	 * Constructs BinData recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public BinData(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from BinData specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates BinData recursively from an XML DOM tree. <b>NOTE:</b> No
	 * properties are removed, only added or updated.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public void update(Element element, OMEModel model)
	    throws EnumerationException
	{
		super.update(element, model);
		String tagName = element.getTagName();
		if (!"BinData".equals(tagName))
		{
			LOGGER.debug("Expecting node name of BinData got {}", tagName);
		}
		if (element.hasAttribute("BigEndian"))
		{
			// Attribute property BigEndian
			setBigEndian(Boolean.valueOf(
					element.getAttribute("BigEndian")));
		}
		if (element.hasAttribute("Length"))
		{
			// Attribute property Length
			setLength(NonNegativeLong.valueOf(
					element.getAttribute("Length")));
		}
		if (element.hasAttribute("Compression"))
		{
			// Attribute property which is an enumeration Compression
			setCompression(Compression.fromString(
					element.getAttribute("Compression")));
		}
		// *** IGNORING *** Skipped back reference Pixels_BackReference
		// *** IGNORING *** Skipped back reference Mask_BackReference
	}

	// -- BinData API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property
	public Boolean getBigEndian()
	{
		return bigEndian;
	}

	public void setBigEndian(Boolean bigEndian)
	{
		this.bigEndian = bigEndian;
	}

	// Property
	public NonNegativeLong getLength()
	{
		return length;
	}

	public void setLength(NonNegativeLong length)
	{
		this.length = length;
	}

	// Property
	public Compression getCompression()
	{
		return compression;
	}

	public void setCompression(Compression compression)
	{
		this.compression = compression;
	}

	// Property
	public Pixels getPixels()
	{
		return pixels;
	}

	public void setPixels(Pixels pixels_BackReference)
	{
		this.pixels = pixels_BackReference;
	}

	// Property
	public Mask getMask()
	{
		return mask;
	}

	public void setMask(Mask mask_BackReference)
	{
		this.mask = mask_BackReference;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element BinData_element)
	{
		// Creating XML block for BinData

		if (BinData_element == null)
		{
			BinData_element =
					document.createElementNS(NAMESPACE, "BinData");
		}

		if (bigEndian != null)
		{
			// Attribute property BigEndian
			BinData_element.setAttribute("BigEndian", bigEndian.toString());
		}
		if (length != null)
		{
			// Attribute property Length
			BinData_element.setAttribute("Length", length.toString());
		}
		if (compression != null)
		{
			// Attribute property Compression
			BinData_element.setAttribute("Compression", compression.toString());
		}
		if (pixels != null)
		{
			// *** IGNORING *** Skipped back reference Pixels_BackReference
		}
		if (mask != null)
		{
			// *** IGNORING *** Skipped back reference Mask_BackReference
		}
		return super.asXMLElement(document, BinData_element);
	}
}
