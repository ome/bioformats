/*
 * ome.xml.model.Leader
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

public class Leader extends Reference
{
	// Base: Reference -- Name: Leader -- Type: Leader -- javaBase: Reference -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Leader.class);

	// -- Instance variables --


	// Property
	private String id;

	// Back reference ExperimenterGroup_BackReference
	private List<ExperimenterGroup> experimenterGroups = new ArrayList<ExperimenterGroup>();

	// -- Constructors --

	/** Default constructor. */
	public Leader()
	{
		super();
	}

	/** 
	 * Constructs Leader recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Leader(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Leader specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Leader recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Leader".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Leader got {}", tagName);
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Leader missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		// *** IGNORING *** Skipped back reference ExperimenterGroup_BackReference
	}

	// -- Leader API methods --

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
	public int sizeOfLinkedExperimenterGroupList()
	{
		return experimenterGroups.size();
	}

	public List<ExperimenterGroup> copyLinkedExperimenterGroupList()
	{
		return new ArrayList<ExperimenterGroup>(experimenterGroups);
	}

	public ExperimenterGroup getLinkedExperimenterGroup(int index)
	{
		return experimenterGroups.get(index);
	}

	public ExperimenterGroup setLinkedExperimenterGroup(int index, ExperimenterGroup o)
	{
		return experimenterGroups.set(index, o);
	}

	public boolean linkExperimenterGroup(ExperimenterGroup o)
	{
		if (!experimenterGroups.contains(o)) {
			return experimenterGroups.add(o);
		}
		return false;
	}

	public boolean unlinkExperimenterGroup(ExperimenterGroup o)
	{
		return experimenterGroups.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Leader_element)
	{
		// Creating XML block for Leader

		if (Leader_element == null)
		{
			Leader_element =
					document.createElementNS(NAMESPACE, "Leader");
		}

		if (id != null)
		{
			// Attribute property ID
			Leader_element.setAttribute("ID", id.toString());
		}
		if (experimenterGroups != null)
		{
			// *** IGNORING *** Skipped back reference ExperimenterGroup_BackReference
		}
		return super.asXMLElement(document, Leader_element);
	}
}
