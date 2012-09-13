/*
 * ome.xml.model.TransmittanceRange
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

public class TransmittanceRange extends AbstractOMEModelObject
{
	// Base:  -- Name: TransmittanceRange -- Type: TransmittanceRange -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(TransmittanceRange.class);

	// -- Instance variables --


	// Property
	private PositiveInteger cutIn;

	// Property
	private PercentFraction transmittance;

	// Property
	private PositiveInteger cutOut;

	// Property
	private NonNegativeInteger cutInTolerance;

	// Property
	private NonNegativeInteger cutOutTolerance;

	// -- Constructors --

	/** Default constructor. */
	public TransmittanceRange()
	{
		super();
	}

	/** 
	 * Constructs TransmittanceRange recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public TransmittanceRange(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from TransmittanceRange specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates TransmittanceRange recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"TransmittanceRange".equals(tagName))
		{
			LOGGER.debug("Expecting node name of TransmittanceRange got {}", tagName);
		}
		if (element.hasAttribute("CutIn"))
		{
			// Attribute property CutIn
			setCutIn(PositiveInteger.valueOf(
					element.getAttribute("CutIn")));
		}
		if (element.hasAttribute("Transmittance"))
		{
			// Attribute property Transmittance
			setTransmittance(PercentFraction.valueOf(
					element.getAttribute("Transmittance")));
		}
		if (element.hasAttribute("CutOut"))
		{
			// Attribute property CutOut
			setCutOut(PositiveInteger.valueOf(
					element.getAttribute("CutOut")));
		}
		if (element.hasAttribute("CutInTolerance"))
		{
			// Attribute property CutInTolerance
			setCutInTolerance(NonNegativeInteger.valueOf(
					element.getAttribute("CutInTolerance")));
		}
		if (element.hasAttribute("CutOutTolerance"))
		{
			// Attribute property CutOutTolerance
			setCutOutTolerance(NonNegativeInteger.valueOf(
					element.getAttribute("CutOutTolerance")));
		}
	}

	// -- TransmittanceRange API methods --

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
	public PositiveInteger getCutIn()
	{
		return cutIn;
	}

	public void setCutIn(PositiveInteger cutIn)
	{
		this.cutIn = cutIn;
	}

	// Property
	public PercentFraction getTransmittance()
	{
		return transmittance;
	}

	public void setTransmittance(PercentFraction transmittance)
	{
		this.transmittance = transmittance;
	}

	// Property
	public PositiveInteger getCutOut()
	{
		return cutOut;
	}

	public void setCutOut(PositiveInteger cutOut)
	{
		this.cutOut = cutOut;
	}

	// Property
	public NonNegativeInteger getCutInTolerance()
	{
		return cutInTolerance;
	}

	public void setCutInTolerance(NonNegativeInteger cutInTolerance)
	{
		this.cutInTolerance = cutInTolerance;
	}

	// Property
	public NonNegativeInteger getCutOutTolerance()
	{
		return cutOutTolerance;
	}

	public void setCutOutTolerance(NonNegativeInteger cutOutTolerance)
	{
		this.cutOutTolerance = cutOutTolerance;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element TransmittanceRange_element)
	{
		// Creating XML block for TransmittanceRange

		if (TransmittanceRange_element == null)
		{
			TransmittanceRange_element =
					document.createElementNS(NAMESPACE, "TransmittanceRange");
		}

		if (cutIn != null)
		{
			// Attribute property CutIn
			TransmittanceRange_element.setAttribute("CutIn", cutIn.toString());
		}
		if (transmittance != null)
		{
			// Attribute property Transmittance
			TransmittanceRange_element.setAttribute("Transmittance", transmittance.toString());
		}
		if (cutOut != null)
		{
			// Attribute property CutOut
			TransmittanceRange_element.setAttribute("CutOut", cutOut.toString());
		}
		if (cutInTolerance != null)
		{
			// Attribute property CutInTolerance
			TransmittanceRange_element.setAttribute("CutInTolerance", cutInTolerance.toString());
		}
		if (cutOutTolerance != null)
		{
			// Attribute property CutOutTolerance
			TransmittanceRange_element.setAttribute("CutOutTolerance", cutOutTolerance.toString());
		}
		return super.asXMLElement(document, TransmittanceRange_element);
	}
}
