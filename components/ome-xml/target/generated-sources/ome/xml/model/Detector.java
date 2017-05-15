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

public class Detector extends ManufacturerSpec
{
	// Base: ManufacturerSpec -- Name: Detector -- Type: Detector -- modelBaseType: ManufacturerSpec -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Detector.class);

	// -- Instance variables --

	// Zoom property
	private Double zoom;

	// AmplificationGain property
	private Double amplificationGain;

	// Gain property
	private Double gain;

	// Offset property
	private Double offset;

	// Type property
	private DetectorType type;

	// ID property
	private String id;

	// Voltage property
	private ElectricPotential voltage;

	// AnnotationRef reference (occurs more than once)
	private List<Annotation> annotationLinks = new ReferenceList<Annotation>();

	// Instrument_BackReference back reference
	private Instrument instrument;

	// -- Constructors --

	/** Default constructor. */
	public Detector()
	{
		super();
	}

	/**
	 * Constructs Detector recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Detector(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public Detector(Detector orig)
	{
		super(orig);
		zoom = orig.zoom;
		amplificationGain = orig.amplificationGain;
		gain = orig.gain;
		offset = orig.offset;
		type = orig.type;
		id = orig.id;
		voltage = orig.voltage;
		annotationLinks = orig.annotationLinks;
		instrument = orig.instrument;
	}

	// -- Custom content from Detector specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates Detector recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Detector".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Detector got {}", tagName);
		}
		if (element.hasAttribute("Zoom"))
		{
			// Attribute property Zoom
			setZoom(Double.valueOf(
					element.getAttribute("Zoom")));
		}
		if (element.hasAttribute("AmplificationGain"))
		{
			// Attribute property AmplificationGain
			setAmplificationGain(Double.valueOf(
					element.getAttribute("AmplificationGain")));
		}
		if (element.hasAttribute("Gain"))
		{
			// Attribute property Gain
			setGain(Double.valueOf(
					element.getAttribute("Gain")));
		}
		if (element.hasAttribute("Offset"))
		{
			// Attribute property Offset
			setOffset(Double.valueOf(
					element.getAttribute("Offset")));
		}
		if (element.hasAttribute("Type"))
		{
			// Attribute property which is an enumeration Type
			setType(DetectorType.fromString(
					element.getAttribute("Type")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Detector missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		if (element.hasAttribute("Voltage"))
		{
			// Attribute property Voltage with unit companion VoltageUnit
			String unitSymbol = element.getAttribute("VoltageUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getVoltageUnitXsdDefault();
			}
			UnitsElectricPotential modelUnit = 
				UnitsElectricPotential.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("Voltage"));
			if (baseValue != null) 
			{
				setVoltage(UnitsElectricPotentialEnumHandler.getQuantity(baseValue, modelUnit));
			}
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

	// -- Detector API methods --

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
			o_casted.linkDetector(this);
			annotationLinks.add(o_casted);
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property VoltageUnit is a unit companion
	public static String getVoltageUnitXsdDefault()
	{
		return "V";
	}

	// Property Zoom
	public Double getZoom()
	{
		return zoom;
	}

	public void setZoom(Double zoom)
	{
		this.zoom = zoom;
	}

	// Property AmplificationGain
	public Double getAmplificationGain()
	{
		return amplificationGain;
	}

	public void setAmplificationGain(Double amplificationGain)
	{
		this.amplificationGain = amplificationGain;
	}

	// Property Gain
	public Double getGain()
	{
		return gain;
	}

	public void setGain(Double gain)
	{
		this.gain = gain;
	}

	// Property Offset
	public Double getOffset()
	{
		return offset;
	}

	public void setOffset(Double offset)
	{
		this.offset = offset;
	}

	// Property Type
	public DetectorType getType()
	{
		return type;
	}

	public void setType(DetectorType type)
	{
		this.type = type;
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

	// Property Voltage with unit companion VoltageUnit
	public ElectricPotential getVoltage()
	{
		return voltage;
	}

	public void setVoltage(ElectricPotential voltage)
	{
		this.voltage = voltage;
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

			o.linkDetector(this);
		return annotationLinks.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkDetector(this);
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

	protected Element asXMLElement(Document document, Element Detector_element)
	{
		// Creating XML block for Detector

		if (Detector_element == null)
		{
			Detector_element =
					document.createElementNS(NAMESPACE, "Detector");
		}


		if (zoom != null)
		{
			// Attribute property Zoom
			Detector_element.setAttribute("Zoom", zoom.toString());
		}
		if (amplificationGain != null)
		{
			// Attribute property AmplificationGain
			Detector_element.setAttribute("AmplificationGain", amplificationGain.toString());
		}
		if (gain != null)
		{
			// Attribute property Gain
			Detector_element.setAttribute("Gain", gain.toString());
		}
		if (offset != null)
		{
			// Attribute property Offset
			Detector_element.setAttribute("Offset", offset.toString());
		}
		if (type != null)
		{
			// Attribute property Type
			Detector_element.setAttribute("Type", type.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Detector_element.setAttribute("ID", id.toString());
		}
		if (voltage != null)
		{
			// Attribute property Voltage with units companion prop.unitsCompanion.name
			if (voltage.value() != null)
			{
				Detector_element.setAttribute("Voltage", voltage.value().toString());
			}
			if (voltage.unit() != null)
			{
				try
				{
					UnitsElectricPotential enumUnits = UnitsElectricPotential.fromString(voltage.unit().getSymbol());
					Detector_element.setAttribute("VoltageUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for Detector:VoltageUnit: {}", e.toString());
				}
			}
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Detector_element.appendChild(o.asXMLElement(document));
			}
		}
		if (instrument != null)
		{
			// *** IGNORING *** Skipped back reference Instrument_BackReference
		}

		return super.asXMLElement(document, Detector_element);
	}
}
