/*
 * ome.xml.model.External
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

public class External extends AbstractOMEModelObject
{
	// Base:  -- Name: External -- Type: External -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/BinaryFile/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(External.class);

	// -- Instance variables --


	// Property
	private String href;

	// Property
	private Compression compression;

	// Property
	private String sha1;

	// -- Constructors --

	/** Default constructor. */
	public External()
	{
		super();
	}

	/** 
	 * Constructs External recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public External(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from External specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates External recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"External".equals(tagName))
		{
			LOGGER.debug("Expecting node name of External got {}", tagName);
		}
		if (element.hasAttribute("href"))
		{
			// Attribute property href
			sethref(String.valueOf(
					element.getAttribute("href")));
		}
		if (element.hasAttribute("Compression"))
		{
			// Attribute property which is an enumeration Compression
			setCompression(Compression.fromString(
					element.getAttribute("Compression")));
		}
		if (element.hasAttribute("SHA1"))
		{
			// Attribute property SHA1
			setSHA1(String.valueOf(
					element.getAttribute("SHA1")));
		}
	}

	// -- External API methods --

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
	public String gethref()
	{
		return href;
	}

	public void sethref(String href)
	{
		this.href = href;
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
	public String getSHA1()
	{
		return sha1;
	}

	public void setSHA1(String sha1)
	{
		this.sha1 = sha1;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element External_element)
	{
		// Creating XML block for External

		if (External_element == null)
		{
			External_element =
					document.createElementNS(NAMESPACE, "External");
		}

		if (href != null)
		{
			// Attribute property href
			External_element.setAttribute("href", href.toString());
		}
		if (compression != null)
		{
			// Attribute property Compression
			External_element.setAttribute("Compression", compression.toString());
		}
		if (sha1 != null)
		{
			// Attribute property SHA1
			External_element.setAttribute("SHA1", sha1.toString());
		}
		return super.asXMLElement(document, External_element);
	}
}
