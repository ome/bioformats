/*
 * ome.xml.model.Dichroic
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
 * Created by melissa via xsd-fu on 2012-01-12 20:06:01-0500
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

public class Dichroic extends ManufacturerSpec
{
	// Base: ManufacturerSpec -- Name: Dichroic -- Type: Dichroic -- javaBase: ManufacturerSpec -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2011-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Dichroic.class);

	// -- Instance variables --


	// Property
	private String id;

	// Back reference FilterSet_BackReference
	private List<FilterSet> filterSet_BackReferenceList = new ArrayList<FilterSet>();

	// Back reference LightPath_BackReference
	private List<LightPath> lightPath_BackReferenceList = new ArrayList<LightPath>();

	// -- Constructors --

	/** Default constructor. */
	public Dichroic()
	{
		super();
	}

	/** 
	 * Constructs Dichroic recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Dichroic(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Dichroic specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Dichroic recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Dichroic".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Dichroic got {}", tagName);
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Dichroic missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		// *** IGNORING *** Skipped back reference FilterSet_BackReference
		// *** IGNORING *** Skipped back reference LightPath_BackReference
	}

	// -- Dichroic API methods --

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
	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	// Reference which occurs more than once
	public int sizeOfLinkedFilterSetList()
	{
		return filterSet_BackReferenceList.size();
	}

	public List<FilterSet> copyLinkedFilterSetList()
	{
		return new ArrayList<FilterSet>(filterSet_BackReferenceList);
	}

	public FilterSet getLinkedFilterSet(int index)
	{
		return filterSet_BackReferenceList.get(index);
	}

	public FilterSet setLinkedFilterSet(int index, FilterSet o)
	{
		return filterSet_BackReferenceList.set(index, o);
	}

	public boolean linkFilterSet(FilterSet o)
	{
		if (!filterSet_BackReferenceList.contains(o)) {
			return filterSet_BackReferenceList.add(o);
		}
		return false;
	}

	public boolean unlinkFilterSet(FilterSet o)
	{
		return filterSet_BackReferenceList.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedLightPathList()
	{
		return lightPath_BackReferenceList.size();
	}

	public List<LightPath> copyLinkedLightPathList()
	{
		return new ArrayList<LightPath>(lightPath_BackReferenceList);
	}

	public LightPath getLinkedLightPath(int index)
	{
		return lightPath_BackReferenceList.get(index);
	}

	public LightPath setLinkedLightPath(int index, LightPath o)
	{
		return lightPath_BackReferenceList.set(index, o);
	}

	public boolean linkLightPath(LightPath o)
	{
		if (!lightPath_BackReferenceList.contains(o)) {
			return lightPath_BackReferenceList.add(o);
		}
		return false;
	}

	public boolean unlinkLightPath(LightPath o)
	{
		return lightPath_BackReferenceList.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Dichroic_element)
	{
		// Creating XML block for Dichroic

		if (Dichroic_element == null)
		{
			Dichroic_element =
					document.createElementNS(NAMESPACE, "Dichroic");
		}

		if (id != null)
		{
			// Attribute property ID
			Dichroic_element.setAttribute("ID", id.toString());
		}
		if (filterSet_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference FilterSet_BackReference
		}
		if (lightPath_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference LightPath_BackReference
		}
		return super.asXMLElement(document, Dichroic_element);
	}
}
