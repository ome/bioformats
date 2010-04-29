/*
 * ome.xml.r201004.BinData
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
 * Created by callan via xsd-fu on 2010-04-29 16:39:29+0100
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r201004;

import java.util.ArrayList;
import java.util.List;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ome.xml.r201004.enums.*;
import ome.xml.r201004.primitives.*;

public class BinData extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/BinaryFile/2010-04";

	// -- Instance variables --

	// Property
	private Boolean bigEndian;

	// Property
	private NonNegativeInteger length;

	// Property
	private Compression compression;

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
			System.err.println(String.format(
					"WARNING: Expecting node name of BinData got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of BinData got %s",
			//		tagName));
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
			setLength(NonNegativeInteger.valueOf(
					element.getAttribute("Length")));
		}
		if (element.hasAttribute("Compression"))
		{
			// Attribute property which is an enumeration Compression
			setCompression(Compression.fromString(
					element.getAttribute("Compression")));
		}
	}

	// -- BinData API methods --

	public void link(Reference reference, OMEModelObject o)
	{
		// TODO: Should be its own Exception
		throw new RuntimeException(
				"Unable to handle reference of type: " + reference.getClass());
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
	public NonNegativeInteger getLength()
	{
		return length;
	}

	public void setLength(NonNegativeInteger length)
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
		return super.asXMLElement(document, BinData_element);
	}
}
