/*
 * ome.xml.model.LightSource
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

public abstract class LightSource extends ManufacturerSpec
{
	// Base: ManufacturerSpec -- Name: LightSource -- Type: LightSource -- javaBase: ManufacturerSpec -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(LightSource.class);

	// -- Instance variables --


	// Property
	private String id;

	// Property
	private Double power;

	// *** WARNING *** Unhandled or skipped property Laser

	// *** WARNING *** Unhandled or skipped property Filament

	// *** WARNING *** Unhandled or skipped property Arc

	// *** WARNING *** Unhandled or skipped property LightEmittingDiode

	// Back reference Instrument_BackReference
	private Instrument instrument;

	// -- Constructors --

	/** Default constructor. */
	public LightSource()
	{
		super();
	}

	/** 
	 * Constructs LightSource recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public LightSource(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from LightSource specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates LightSource recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"LightSource missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		if (element.hasAttribute("Power"))
		{
			// Attribute property Power
			setPower(Double.valueOf(
					element.getAttribute("Power")));
		}
		List<Element> Laser_nodeList =
				getChildrenByTagName(element, "Laser");
		if (Laser_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Laser node list size %d != 1",
					Laser_nodeList.size()));
		}
		else if (Laser_nodeList.size() != 0)
		{
		}
		List<Element> Filament_nodeList =
				getChildrenByTagName(element, "Filament");
		if (Filament_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Filament node list size %d != 1",
					Filament_nodeList.size()));
		}
		else if (Filament_nodeList.size() != 0)
		{
		}
		List<Element> Arc_nodeList =
				getChildrenByTagName(element, "Arc");
		if (Arc_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Arc node list size %d != 1",
					Arc_nodeList.size()));
		}
		else if (Arc_nodeList.size() != 0)
		{
		}
		List<Element> LightEmittingDiode_nodeList =
				getChildrenByTagName(element, "LightEmittingDiode");
		if (LightEmittingDiode_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"LightEmittingDiode node list size %d != 1",
					LightEmittingDiode_nodeList.size()));
		}
		else if (LightEmittingDiode_nodeList.size() != 0)
		{
		}
		// *** IGNORING *** Skipped back reference Instrument_BackReference
	}

	// -- LightSource API methods --

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

	// Property
	public Double getPower()
	{
		return power;
	}

	public void setPower(Double power)
	{
		this.power = power;
	}

	// *** WARNING *** Unhandled or skipped property Laser

	// *** WARNING *** Unhandled or skipped property Filament

	// *** WARNING *** Unhandled or skipped property Arc

	// *** WARNING *** Unhandled or skipped property LightEmittingDiode

	// Property
	public Instrument getInstrument()
	{
		return instrument;
	}

	public void setInstrument(Instrument instrument_BackReference)
	{
		this.instrument = instrument_BackReference;
	}

	protected Element asXMLElement(Document document, Element LightSource_element)
	{
		// Creating XML block for LightSource

		// Class is abstract so we may need to create its "container" element
		if (!"LightSource".equals(LightSource_element.getTagName()))
		{
			Element abstractElement =
					document.createElementNS(NAMESPACE, "LightSource");
			abstractElement.appendChild(LightSource_element);
			LightSource_element = abstractElement;
		}
		if (LightSource_element == null)
		{
			LightSource_element =
					document.createElementNS(NAMESPACE, "LightSource");
		}

		if (id != null)
		{
			// Attribute property ID
			LightSource_element.setAttribute("ID", id.toString());
		}
		if (power != null)
		{
			// Attribute property Power
			LightSource_element.setAttribute("Power", power.toString());
		}
		return super.asXMLElement(document, LightSource_element);
	}
}
