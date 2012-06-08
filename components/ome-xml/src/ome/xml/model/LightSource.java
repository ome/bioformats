/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by callan via xsd-fu on 2012-05-18 10:08:16+0100
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
