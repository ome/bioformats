/*
 * ome.xml.model.Objective
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

public class Objective extends ManufacturerSpec
{
	// Base: ManufacturerSpec -- Name: Objective -- Type: Objective -- javaBase: ManufacturerSpec -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Objective.class);

	// -- Instance variables --


	// Property
	private Boolean iris;

	// Property
	private Double workingDistance;

	// Property
	private Immersion immersion;

	// Property
	private Correction correction;

	// Property
	private Double lensNA;

	// Property
	private PositiveInteger nominalMagnification;

	// Property
	private Double calibratedMagnification;

	// Property
	private String id;

	// Back reference Instrument_BackReference
	private Instrument instrument;

	// -- Constructors --

	/** Default constructor. */
	public Objective()
	{
		super();
	}

	/** 
	 * Constructs Objective recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Objective(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Objective specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Objective recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Objective".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Objective got {}", tagName);
		}
		if (element.hasAttribute("Iris"))
		{
			// Attribute property Iris
			setIris(Boolean.valueOf(
					element.getAttribute("Iris")));
		}
		if (element.hasAttribute("WorkingDistance"))
		{
			// Attribute property WorkingDistance
			setWorkingDistance(Double.valueOf(
					element.getAttribute("WorkingDistance")));
		}
		if (element.hasAttribute("Immersion"))
		{
			// Attribute property which is an enumeration Immersion
			setImmersion(Immersion.fromString(
					element.getAttribute("Immersion")));
		}
		if (element.hasAttribute("Correction"))
		{
			// Attribute property which is an enumeration Correction
			setCorrection(Correction.fromString(
					element.getAttribute("Correction")));
		}
		if (element.hasAttribute("LensNA"))
		{
			// Attribute property LensNA
			setLensNA(Double.valueOf(
					element.getAttribute("LensNA")));
		}
		if (element.hasAttribute("NominalMagnification"))
		{
			// Attribute property NominalMagnification
			setNominalMagnification(PositiveInteger.valueOf(
					element.getAttribute("NominalMagnification")));
		}
		if (element.hasAttribute("CalibratedMagnification"))
		{
			// Attribute property CalibratedMagnification
			setCalibratedMagnification(Double.valueOf(
					element.getAttribute("CalibratedMagnification")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Objective missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		// *** IGNORING *** Skipped back reference Instrument_BackReference
	}

	// -- Objective API methods --

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
	public Boolean getIris()
	{
		return iris;
	}

	public void setIris(Boolean iris)
	{
		this.iris = iris;
	}

	// Property
	public Double getWorkingDistance()
	{
		return workingDistance;
	}

	public void setWorkingDistance(Double workingDistance)
	{
		this.workingDistance = workingDistance;
	}

	// Property
	public Immersion getImmersion()
	{
		return immersion;
	}

	public void setImmersion(Immersion immersion)
	{
		this.immersion = immersion;
	}

	// Property
	public Correction getCorrection()
	{
		return correction;
	}

	public void setCorrection(Correction correction)
	{
		this.correction = correction;
	}

	// Property
	public Double getLensNA()
	{
		return lensNA;
	}

	public void setLensNA(Double lensNA)
	{
		this.lensNA = lensNA;
	}

	// Property
	public PositiveInteger getNominalMagnification()
	{
		return nominalMagnification;
	}

	public void setNominalMagnification(PositiveInteger nominalMagnification)
	{
		this.nominalMagnification = nominalMagnification;
	}

	// Property
	public Double getCalibratedMagnification()
	{
		return calibratedMagnification;
	}

	public void setCalibratedMagnification(Double calibratedMagnification)
	{
		this.calibratedMagnification = calibratedMagnification;
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
	public Instrument getInstrument()
	{
		return instrument;
	}

	public void setInstrument(Instrument instrument_BackReference)
	{
		this.instrument = instrument_BackReference;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Objective_element)
	{
		// Creating XML block for Objective

		if (Objective_element == null)
		{
			Objective_element =
					document.createElementNS(NAMESPACE, "Objective");
		}

		if (iris != null)
		{
			// Attribute property Iris
			Objective_element.setAttribute("Iris", iris.toString());
		}
		if (workingDistance != null)
		{
			// Attribute property WorkingDistance
			Objective_element.setAttribute("WorkingDistance", workingDistance.toString());
		}
		if (immersion != null)
		{
			// Attribute property Immersion
			Objective_element.setAttribute("Immersion", immersion.toString());
		}
		if (correction != null)
		{
			// Attribute property Correction
			Objective_element.setAttribute("Correction", correction.toString());
		}
		if (lensNA != null)
		{
			// Attribute property LensNA
			Objective_element.setAttribute("LensNA", lensNA.toString());
		}
		if (nominalMagnification != null)
		{
			// Attribute property NominalMagnification
			Objective_element.setAttribute("NominalMagnification", nominalMagnification.toString());
		}
		if (calibratedMagnification != null)
		{
			// Attribute property CalibratedMagnification
			Objective_element.setAttribute("CalibratedMagnification", calibratedMagnification.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Objective_element.setAttribute("ID", id.toString());
		}
		if (instrument != null)
		{
			// *** IGNORING *** Skipped back reference Instrument_BackReference
		}
		return super.asXMLElement(document, Objective_element);
	}
}
