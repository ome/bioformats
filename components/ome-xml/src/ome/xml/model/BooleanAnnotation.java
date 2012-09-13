/*
 * ome.xml.model.BooleanAnnotation
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

public class BooleanAnnotation extends BasicAnnotation
{
	// Base: BasicAnnotation -- Name: BooleanAnnotation -- Type: BooleanAnnotation -- javaBase: BasicAnnotation -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SA/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(BooleanAnnotation.class);

	// -- Instance variables --


	// Property
	private Boolean value;

	// Back reference StructuredAnnotations_BackReference
	private StructuredAnnotations structuredAnnotations;

	// -- Constructors --

	/** Default constructor. */
	public BooleanAnnotation()
	{
		super();
	}

	/** 
	 * Constructs BooleanAnnotation recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public BooleanAnnotation(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from BooleanAnnotation specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates BooleanAnnotation recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"BooleanAnnotation".equals(tagName))
		{
			LOGGER.debug("Expecting node name of BooleanAnnotation got {}", tagName);
		}
		List<Element> Value_nodeList =
				getChildrenByTagName(element, "Value");
		if (Value_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Value node list size %d != 1",
					Value_nodeList.size()));
		}
		else if (Value_nodeList.size() != 0)
		{
			// Element property Value which is not complex (has no
			// sub-elements)
			setValue(
					Boolean.valueOf(Value_nodeList.get(0).getTextContent()));
		}
		// *** IGNORING *** Skipped back reference StructuredAnnotations_BackReference
	}

	// -- BooleanAnnotation API methods --

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
	public Boolean getValue()
	{
		return value;
	}

	public void setValue(Boolean value)
	{
		this.value = value;
	}

	// Property
	public StructuredAnnotations getStructuredAnnotations()
	{
		return structuredAnnotations;
	}

	public void setStructuredAnnotations(StructuredAnnotations structuredAnnotations_BackReference)
	{
		this.structuredAnnotations = structuredAnnotations_BackReference;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element BooleanAnnotation_element)
	{
		// Creating XML block for BooleanAnnotation

		if (BooleanAnnotation_element == null)
		{
			BooleanAnnotation_element =
					document.createElementNS(NAMESPACE, "BooleanAnnotation");
		}

		if (value != null)
		{
			// Element property Value which is not complex (has no
			// sub-elements)
			Element value_element = 
					document.createElementNS(NAMESPACE, "Value");
			value_element.setTextContent(value.toString());
			BooleanAnnotation_element.appendChild(value_element);
		}
		if (structuredAnnotations != null)
		{
			// *** IGNORING *** Skipped back reference StructuredAnnotations_BackReference
		}
		return super.asXMLElement(document, BooleanAnnotation_element);
	}
}
