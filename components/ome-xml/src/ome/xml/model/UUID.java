/*
 * ome.xml.model.UUID
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

public class UUID extends AbstractOMEModelObject
{
	// Base: UniversallyUniqueIdentifier -- Name: UUID -- Type: UUID -- javaBase: AbstractOMEModelObject -- javaType: String

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(UUID.class);

	// -- Instance variables --

	// Element's text data
	private String UUID_value;

	// Property
	private String fileName;

	// -- Constructors --

	/** Default constructor. */
	public UUID()
	{
		super();
	}

	/** 
	 * Constructs UUID recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public UUID(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from UUID specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates UUID recursively from an XML DOM tree. <b>NOTE:</b> No
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
		// Element's text data
		String UUID_value_textContent = element.getTextContent();
		if (UUID_value_textContent.trim().length() > 0) {
			UUID_value = String.valueOf(UUID_value_textContent);
		}
		String tagName = element.getTagName();
		if (!"UUID".equals(tagName))
		{
			LOGGER.debug("Expecting node name of UUID got {}", tagName);
		}
		if (element.hasAttribute("FileName"))
		{
			// Attribute property FileName
			setFileName(String.valueOf(
					element.getAttribute("FileName")));
		}
	}

	// -- UUID API methods --

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

	// Element's text data getter
	public String getValue()
	{
		return UUID_value;
	}

	// Element's text data setter
	public void setValue(String UUID_value)
	{
		this.UUID_value = UUID_value;
	}

	// Property
	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element UUID_element)
	{
		// Creating XML block for UUID

		if (UUID_element == null)
		{
			UUID_element =
					document.createElementNS(NAMESPACE, "UUID");
		}

		// Element's text data
		if (UUID_value != null) {
			UUID_element.setTextContent(UUID_value.toString());
		}

		if (fileName != null)
		{
			// Attribute property FileName
			UUID_element.setAttribute("FileName", fileName.toString());
		}
		return super.asXMLElement(document, UUID_element);
	}
}
