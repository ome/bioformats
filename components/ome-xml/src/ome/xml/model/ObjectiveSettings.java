/*
 * ome.xml.model.ObjectiveSettings
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

public class ObjectiveSettings extends Settings
{
	// Base: Settings -- Name: ObjectiveSettings -- Type: ObjectiveSettings -- javaBase: Settings -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(ObjectiveSettings.class);

	// -- Instance variables --


	// Property
	private Double refractiveIndex;

	// Property
	private Double correctionCollar;

	// Property
	private String id;

	// Property
	private Medium medium;

	// Back reference ObjectiveRef
	private Objective objective;

	// -- Constructors --

	/** Default constructor. */
	public ObjectiveSettings()
	{
		super();
	}

	/** 
	 * Constructs ObjectiveSettings recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public ObjectiveSettings(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from ObjectiveSettings specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates ObjectiveSettings recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"ObjectiveSettings".equals(tagName))
		{
			LOGGER.debug("Expecting node name of ObjectiveSettings got {}", tagName);
		}
		if (element.hasAttribute("RefractiveIndex"))
		{
			// Attribute property RefractiveIndex
			setRefractiveIndex(Double.valueOf(
					element.getAttribute("RefractiveIndex")));
		}
		if (element.hasAttribute("CorrectionCollar"))
		{
			// Attribute property CorrectionCollar
			setCorrectionCollar(Double.valueOf(
					element.getAttribute("CorrectionCollar")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"ObjectiveSettings missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		if (element.hasAttribute("Medium"))
		{
			// Attribute property which is an enumeration Medium
			setMedium(Medium.fromString(
					element.getAttribute("Medium")));
		}
		// *** IGNORING *** Skipped back reference ObjectiveRef
	}

	// -- ObjectiveSettings API methods --

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
	public Double getRefractiveIndex()
	{
		return refractiveIndex;
	}

	public void setRefractiveIndex(Double refractiveIndex)
	{
		this.refractiveIndex = refractiveIndex;
	}

	// Property
	public Double getCorrectionCollar()
	{
		return correctionCollar;
	}

	public void setCorrectionCollar(Double correctionCollar)
	{
		this.correctionCollar = correctionCollar;
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

	// Property
	public Medium getMedium()
	{
		return medium;
	}

	public void setMedium(Medium medium)
	{
		this.medium = medium;
	}

	// Property
	public Objective getObjective()
	{
		return objective;
	}

	public void setObjective(Objective objective)
	{
		this.objective = objective;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element ObjectiveSettings_element)
	{
		// Creating XML block for ObjectiveSettings

		if (ObjectiveSettings_element == null)
		{
			ObjectiveSettings_element =
					document.createElementNS(NAMESPACE, "ObjectiveSettings");
		}

		if (refractiveIndex != null)
		{
			// Attribute property RefractiveIndex
			ObjectiveSettings_element.setAttribute("RefractiveIndex", refractiveIndex.toString());
		}
		if (correctionCollar != null)
		{
			// Attribute property CorrectionCollar
			ObjectiveSettings_element.setAttribute("CorrectionCollar", correctionCollar.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			ObjectiveSettings_element.setAttribute("ID", id.toString());
		}
		if (medium != null)
		{
			// Attribute property Medium
			ObjectiveSettings_element.setAttribute("Medium", medium.toString());
		}
		if (objective != null)
		{
			// *** IGNORING *** Skipped back reference ObjectiveRef
		}
		return super.asXMLElement(document, ObjectiveSettings_element);
	}
}
