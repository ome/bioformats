/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
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
 * Created by rleigh via xsd-fu on 2013-02-04 15:52:35.744474
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
