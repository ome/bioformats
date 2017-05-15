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

public class DetectorSettings extends Settings
{
	// Base: Settings -- Name: DetectorSettings -- Type: DetectorSettings -- modelBaseType: Settings -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(DetectorSettings.class);

	// -- Instance variables --

	// Zoom property
	private Double zoom;

	// Binning property
	private Binning binning;

	// Integration property
	private PositiveInteger integration;

	// ReadOutRate property
	private Frequency readOutRate;

	// Gain property
	private Double gain;

	// Offset property
	private Double offset;

	// ID property
	private String id;

	// Voltage property
	private ElectricPotential voltage;

	// DetectorRef back reference
	private Detector detector;

	// -- Constructors --

	/** Default constructor. */
	public DetectorSettings()
	{
		super();
	}

	/**
	 * Constructs DetectorSettings recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public DetectorSettings(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public DetectorSettings(DetectorSettings orig)
	{
		super(orig);
		zoom = orig.zoom;
		binning = orig.binning;
		integration = orig.integration;
		readOutRate = orig.readOutRate;
		gain = orig.gain;
		offset = orig.offset;
		id = orig.id;
		voltage = orig.voltage;
		detector = orig.detector;
	}

	// -- Custom content from DetectorSettings specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates DetectorSettings recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"DetectorSettings".equals(tagName))
		{
			LOGGER.debug("Expecting node name of DetectorSettings got {}", tagName);
		}
		if (element.hasAttribute("Zoom"))
		{
			// Attribute property Zoom
			setZoom(Double.valueOf(
					element.getAttribute("Zoom")));
		}
		if (element.hasAttribute("Binning"))
		{
			// Attribute property which is an enumeration Binning
			setBinning(Binning.fromString(
					element.getAttribute("Binning")));
		}
		if (element.hasAttribute("Integration"))
		{
			// Attribute property Integration
			setIntegration(PositiveInteger.valueOf(
					element.getAttribute("Integration")));
		}
		if (element.hasAttribute("ReadOutRate"))
		{
			// Attribute property ReadOutRate with unit companion ReadOutRateUnit
			String unitSymbol = element.getAttribute("ReadOutRateUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getReadOutRateUnitXsdDefault();
			}
			UnitsFrequency modelUnit = 
				UnitsFrequency.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("ReadOutRate"));
			if (baseValue != null) 
			{
				setReadOutRate(UnitsFrequencyEnumHandler.getQuantity(baseValue, modelUnit));
			}
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
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"DetectorSettings missing required ID property."));
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
	}

	// -- DetectorSettings API methods --

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

	// Property Binning
	public Binning getBinning()
	{
		return binning;
	}

	public void setBinning(Binning binning)
	{
		this.binning = binning;
	}

	// Property Integration
	public PositiveInteger getIntegration()
	{
		return integration;
	}

	public void setIntegration(PositiveInteger integration)
	{
		this.integration = integration;
	}

	// Property ReadOutRate with unit companion ReadOutRateUnit
	public Frequency getReadOutRate()
	{
		return readOutRate;
	}

	public void setReadOutRate(Frequency readOutRate)
	{
		this.readOutRate = readOutRate;
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

	// Property ReadOutRateUnit is a unit companion
	public static String getReadOutRateUnitXsdDefault()
	{
		return "MHz";
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

	// Property DetectorRef
	public Detector getDetector()
	{
		return detector;
	}

	public void setDetector(Detector detector)
	{
		this.detector = detector;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element DetectorSettings_element)
	{
		// Creating XML block for DetectorSettings

		if (DetectorSettings_element == null)
		{
			DetectorSettings_element =
					document.createElementNS(NAMESPACE, "DetectorSettings");
		}


		if (zoom != null)
		{
			// Attribute property Zoom
			DetectorSettings_element.setAttribute("Zoom", zoom.toString());
		}
		if (binning != null)
		{
			// Attribute property Binning
			DetectorSettings_element.setAttribute("Binning", binning.toString());
		}
		if (integration != null)
		{
			// Attribute property Integration
			DetectorSettings_element.setAttribute("Integration", integration.toString());
		}
		if (readOutRate != null)
		{
			// Attribute property ReadOutRate with units companion prop.unitsCompanion.name
			if (readOutRate.value() != null)
			{
				DetectorSettings_element.setAttribute("ReadOutRate", readOutRate.value().toString());
			}
			if (readOutRate.unit() != null)
			{
				try
				{
					UnitsFrequency enumUnits = UnitsFrequency.fromString(readOutRate.unit().getSymbol());
					DetectorSettings_element.setAttribute("ReadOutRateUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for DetectorSettings:ReadOutRateUnit: {}", e.toString());
				}
			}
		}
		if (gain != null)
		{
			// Attribute property Gain
			DetectorSettings_element.setAttribute("Gain", gain.toString());
		}
		if (offset != null)
		{
			// Attribute property Offset
			DetectorSettings_element.setAttribute("Offset", offset.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			DetectorSettings_element.setAttribute("ID", id.toString());
		}
		if (voltage != null)
		{
			// Attribute property Voltage with units companion prop.unitsCompanion.name
			if (voltage.value() != null)
			{
				DetectorSettings_element.setAttribute("Voltage", voltage.value().toString());
			}
			if (voltage.unit() != null)
			{
				try
				{
					UnitsElectricPotential enumUnits = UnitsElectricPotential.fromString(voltage.unit().getSymbol());
					DetectorSettings_element.setAttribute("VoltageUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for DetectorSettings:VoltageUnit: {}", e.toString());
				}
			}
		}
		if (detector != null)
		{
			// *** IGNORING *** Skipped back reference DetectorRef
		}

		return super.asXMLElement(document, DetectorSettings_element);
	}
}
