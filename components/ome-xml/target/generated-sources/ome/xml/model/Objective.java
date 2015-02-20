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

public class Objective extends ManufacturerSpec
{
	// Base: ManufacturerSpec -- Name: Objective -- Type: Objective -- modelBaseType: ManufacturerSpec -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Objective.class);

	// -- Instance variables --

	// Iris property
	private Boolean iris;

	// WorkingDistance property
	private Length workingDistance;

	// Immersion property
	private Immersion immersion;

	// Correction property
	private Correction correction;

	// LensNA property
	private Double lensNA;

	// NominalMagnification property
	private Double nominalMagnification;

	// CalibratedMagnification property
	private Double calibratedMagnification;

	// ID property
	private String id;

	// AnnotationRef reference (occurs more than once)
	private List<Annotation> annotationLinks = new ReferenceList<Annotation>();

	// Instrument_BackReference back reference
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

	/** Copy constructor. */
	public Objective(Objective orig)
	{
		super(orig);
		iris = orig.iris;
		workingDistance = orig.workingDistance;
		immersion = orig.immersion;
		correction = orig.correction;
		lensNA = orig.lensNA;
		nominalMagnification = orig.nominalMagnification;
		calibratedMagnification = orig.calibratedMagnification;
		id = orig.id;
		annotationLinks = orig.annotationLinks;
		instrument = orig.instrument;
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
			// Attribute property WorkingDistance with unit companion WorkingDistanceUnit
			String unitSymbol = element.getAttribute("WorkingDistanceUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getWorkingDistanceUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("WorkingDistance"));
			if (baseValue != null) 
			{
				setWorkingDistance(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
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
			setNominalMagnification(Double.valueOf(
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
		// Element reference AnnotationRef
		List<Element> AnnotationRef_nodeList =
				getChildrenByTagName(element, "AnnotationRef");
		for (Element AnnotationRef_element : AnnotationRef_nodeList)
		{
			AnnotationRef annotationLinks_reference = new AnnotationRef();
			annotationLinks_reference.setID(AnnotationRef_element.getAttribute("ID"));
			model.addReference(this, annotationLinks_reference);
		}
	}

	// -- Objective API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkObjective(this);
			annotationLinks.add(o_casted);
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property Iris
	public Boolean getIris()
	{
		return iris;
	}

	public void setIris(Boolean iris)
	{
		this.iris = iris;
	}

	// Property WorkingDistance with unit companion WorkingDistanceUnit
	public Length getWorkingDistance()
	{
		return workingDistance;
	}

	public void setWorkingDistance(Length workingDistance)
	{
		this.workingDistance = workingDistance;
	}

	// Property WorkingDistanceUnit is a unit companion
	public static String getWorkingDistanceUnitXsdDefault()
	{
		return "µm";
	}

	// Property Immersion
	public Immersion getImmersion()
	{
		return immersion;
	}

	public void setImmersion(Immersion immersion)
	{
		this.immersion = immersion;
	}

	// Property Correction
	public Correction getCorrection()
	{
		return correction;
	}

	public void setCorrection(Correction correction)
	{
		this.correction = correction;
	}

	// Property LensNA
	public Double getLensNA()
	{
		return lensNA;
	}

	public void setLensNA(Double lensNA)
	{
		this.lensNA = lensNA;
	}

	// Property NominalMagnification
	public Double getNominalMagnification()
	{
		return nominalMagnification;
	}

	public void setNominalMagnification(Double nominalMagnification)
	{
		this.nominalMagnification = nominalMagnification;
	}

	// Property CalibratedMagnification
	public Double getCalibratedMagnification()
	{
		return calibratedMagnification;
	}

	public void setCalibratedMagnification(Double calibratedMagnification)
	{
		this.calibratedMagnification = calibratedMagnification;
	}

	// Property ID
	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	// Reference which occurs more than once
	public int sizeOfLinkedAnnotationList()
	{
		return annotationLinks.size();
	}

	public List<Annotation> copyLinkedAnnotationList()
	{
		return new ArrayList<Annotation>(annotationLinks);
	}

	public Annotation getLinkedAnnotation(int index)
	{
		return annotationLinks.get(index);
	}

	public Annotation setLinkedAnnotation(int index, Annotation o)
	{
		return annotationLinks.set(index, o);
	}

	public boolean linkAnnotation(Annotation o)
	{

			o.linkObjective(this);
		return annotationLinks.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkObjective(this);
		return annotationLinks.remove(o);
	}

	// Property Instrument_BackReference
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
			// Attribute property WorkingDistance with units companion prop.unitsCompanion.name
			if (workingDistance.value() != null)
			{
				Objective_element.setAttribute("WorkingDistance", workingDistance.value().toString());
			}
			if (workingDistance.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(workingDistance.unit().getSymbol());
					Objective_element.setAttribute("WorkingDistanceUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for Objective:WorkingDistanceUnit: {}", e.toString());
				}
			}
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
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Objective_element.appendChild(o.asXMLElement(document));
			}
		}
		if (instrument != null)
		{
			// *** IGNORING *** Skipped back reference Instrument_BackReference
		}

		return super.asXMLElement(document, Objective_element);
	}
}
