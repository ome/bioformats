/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2014 Open Microscopy Environment:
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

import ome.units.quantity.Angle;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.unit.Unit;

import ome.xml.model.enums.*;
import ome.xml.model.enums.handlers.*;
import ome.xml.model.primitives.*;

public class Rights extends AbstractOMEModelObject
{
	// Base:  -- Name: Rights -- Type: Rights -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Rights.class);

	// -- Instance variables --

	// RightsHolder property
	private String rightsHolder;

	// RightsHeld property
	private String rightsHeld;

	// -- Constructors --

	/** Default constructor. */
	public Rights()
	{
	}

	/**
	 * Constructs Rights recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Rights(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public Rights(Rights orig)
	{
		rightsHolder = orig.rightsHolder;
		rightsHeld = orig.rightsHeld;
	}

	// -- Custom content from Rights specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates Rights recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Rights".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Rights got {}", tagName);
		}
		List<Element> RightsHolder_nodeList =
				getChildrenByTagName(element, "RightsHolder");
		if (RightsHolder_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"RightsHolder node list size %d != 1",
					RightsHolder_nodeList.size()));
		}
		else if (RightsHolder_nodeList.size() != 0)
		{
			// Element property RightsHolder which is not complex (has no
			// sub-elements)
			setRightsHolder(
					String.valueOf(RightsHolder_nodeList.get(0).getTextContent()));
		}
		List<Element> RightsHeld_nodeList =
				getChildrenByTagName(element, "RightsHeld");
		if (RightsHeld_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"RightsHeld node list size %d != 1",
					RightsHeld_nodeList.size()));
		}
		else if (RightsHeld_nodeList.size() != 0)
		{
			// Element property RightsHeld which is not complex (has no
			// sub-elements)
			setRightsHeld(
					String.valueOf(RightsHeld_nodeList.get(0).getTextContent()));
		}
	}

	// -- Rights API methods --

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


	// Property RightsHolder
	public String getRightsHolder()
	{
		return rightsHolder;
	}

	public void setRightsHolder(String rightsHolder)
	{
		this.rightsHolder = rightsHolder;
	}

	// Property RightsHeld
	public String getRightsHeld()
	{
		return rightsHeld;
	}

	public void setRightsHeld(String rightsHeld)
	{
		this.rightsHeld = rightsHeld;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Rights_element)
	{
		// Creating XML block for Rights

		if (Rights_element == null)
		{
			Rights_element =
					document.createElementNS(NAMESPACE, "Rights");
		}


		if (rightsHolder != null)
		{
			// Element property RightsHolder which is not complex (has no
			// sub-elements)
			Element rightsHolder_element =
					document.createElementNS(NAMESPACE, "RightsHolder");
			rightsHolder_element.setTextContent(rightsHolder.toString());
			Rights_element.appendChild(rightsHolder_element);
		}
		if (rightsHeld != null)
		{
			// Element property RightsHeld which is not complex (has no
			// sub-elements)
			Element rightsHeld_element =
					document.createElementNS(NAMESPACE, "RightsHeld");
			rightsHeld_element.setTextContent(rightsHeld.toString());
			Rights_element.appendChild(rightsHeld_element);
		}

		return super.asXMLElement(document, Rights_element);
	}
}
