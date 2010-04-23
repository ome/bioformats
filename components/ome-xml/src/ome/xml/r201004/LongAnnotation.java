/*
 * ome.xml.r201004.LongAnnotation
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
 * Created by callan via xsd-fu on 2010-04-23 13:18:06+0100
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

public class LongAnnotation extends Annotation
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SA/2010-04";

	// -- Instance variables --

	// Property
	private String value;

	// -- Constructors --

	/** Default constructor. */
	public LongAnnotation()
	{
		super();
	}

	/** 
	 * Constructs LongAnnotation recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public LongAnnotation(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** 
	 * Updates LongAnnotation recursively from an XML DOM tree. <b>NOTE:</b> No
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
		super.update(element);
		String tagName = element.getTagName();
		if (!"LongAnnotation".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of LongAnnotation got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of LongAnnotation got %s",
			//		tagName));
		}
		NodeList Value_nodeList = element.getElementsByTagName("Value");
		if (Value_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Value node list size %d != 1",
					Value_nodeList.getLength()));
		}
		else if (Value_nodeList.getLength() != 0)
		{
			// Element property Value which is not complex (has no
			// sub-elements)
			setValue(Value_nodeList.item(0).getTextContent());
		}
	}

	// -- LongAnnotation API methods --

	public void link(Reference reference, OMEModelObject o)
	{
		// TODO: Should be its own Exception
		throw new RuntimeException(
				"Unable to handle reference of type: " + reference.getClass());
	}


	// Property
	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element LongAnnotation_element)
	{
		// Creating XML block for LongAnnotation
		if (LongAnnotation_element == null)
		{
			LongAnnotation_element =
					document.createElementNS(NAMESPACE, "LongAnnotation");
		}

		if (value != null)
		{
			// Element property Value which is not complex (has no
			// sub-elements)
			Element value_element = 
					document.createElementNS(NAMESPACE, "Value");
			value_element.setTextContent(value);
			LongAnnotation_element.appendChild(value_element);
		}
		return super.asXMLElement(document, LongAnnotation_element);
	}
}
