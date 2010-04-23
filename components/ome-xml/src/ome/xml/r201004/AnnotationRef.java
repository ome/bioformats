/*
 * ome.xml.r201004.AnnotationRef
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
 * Created by callan via xsd-fu on 2010-04-23 17:06:57+0100
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

public class AnnotationRef extends Reference
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SA/2010-04";

	// -- Instance variables --

	// Property
	private String id;

	// -- Constructors --

	/** Default constructor. */
	public AnnotationRef()
	{
		super();
	}

	/** 
	 * Constructs AnnotationRef recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public AnnotationRef(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}
	
	// -- Custom content from AnnotationRef specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates AnnotationRef recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"AnnotationRef".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of AnnotationRef got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of AnnotationRef got %s",
			//		tagName));
		}
		if (!element.hasAttribute("ID"))
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"AnnotationRef missing required ID property."));
		}
		// ID property
		setID(String.valueOf(
					element.getAttribute("ID")));
		// Adding this model object to the model handler
	    	model.addModelObject(getID(), this);
	}

	// -- AnnotationRef API methods --

	public void link(Reference reference, OMEModelObject o)
	{
		// TODO: Should be its own Exception
		throw new RuntimeException(
				"Unable to handle reference of type: " + reference.getClass());
	}


	// Property
	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element AnnotationRef_element)
	{
		// Creating XML block for AnnotationRef
		if (AnnotationRef_element == null)
		{
			AnnotationRef_element =
					document.createElementNS(NAMESPACE, "AnnotationRef");
		}

		if (id != null)
		{
			// Attribute property ID
			AnnotationRef_element.setAttribute("ID", id.toString());
		}
		return super.asXMLElement(document, AnnotationRef_element);
	}
}
