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

public class ImagingEnvironment extends AbstractOMEModelObject
{
	// Base:  -- Name: ImagingEnvironment -- Type: ImagingEnvironment -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(ImagingEnvironment.class);

	// -- Instance variables --

	// CO2Percent property
	private PercentFraction co2Percent;

	// Temperature property
	private Temperature temperature;

	// AirPressure property
	private Pressure airPressure;

	// Humidity property
	private PercentFraction humidity;

	// Map property
	private Map map;

	// -- Constructors --

	/** Default constructor. */
	public ImagingEnvironment()
	{
	}

	/**
	 * Constructs ImagingEnvironment recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public ImagingEnvironment(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public ImagingEnvironment(ImagingEnvironment orig)
	{
		co2Percent = orig.co2Percent;
		temperature = orig.temperature;
		airPressure = orig.airPressure;
		humidity = orig.humidity;
		map = orig.map;
	}

	// -- Custom content from ImagingEnvironment specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates ImagingEnvironment recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"ImagingEnvironment".equals(tagName))
		{
			LOGGER.debug("Expecting node name of ImagingEnvironment got {}", tagName);
		}
		if (element.hasAttribute("CO2Percent"))
		{
			// Attribute property CO2Percent
			setCO2Percent(PercentFraction.valueOf(
					element.getAttribute("CO2Percent")));
		}
		if (element.hasAttribute("Temperature"))
		{
			// Attribute property Temperature with unit companion TemperatureUnit
			String unitSymbol = element.getAttribute("TemperatureUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getTemperatureUnitXsdDefault();
			}
			UnitsTemperature modelUnit = 
				UnitsTemperature.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("Temperature"));
			if (baseValue != null) 
			{
				setTemperature(UnitsTemperatureEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("AirPressure"))
		{
			// Attribute property AirPressure with unit companion AirPressureUnit
			String unitSymbol = element.getAttribute("AirPressureUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getAirPressureUnitXsdDefault();
			}
			UnitsPressure modelUnit = 
				UnitsPressure.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("AirPressure"));
			if (baseValue != null) 
			{
				setAirPressure(UnitsPressureEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("Humidity"))
		{
			// Attribute property Humidity
			setHumidity(PercentFraction.valueOf(
					element.getAttribute("Humidity")));
		}
		List<Element> Map_nodeList =
				getChildrenByTagName(element, "Map");
		if (Map_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Map node list size %d != 1",
					Map_nodeList.size()));
		}
		else if (Map_nodeList.size() != 0)
		{
			// Element property Map which is complex (has
			// sub-elements)
			setMap(new Map(
					(Element) Map_nodeList.get(0), model));
		}
	}

	// -- ImagingEnvironment API methods --

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


	// Property CO2Percent
	public PercentFraction getCO2Percent()
	{
		return co2Percent;
	}

	public void setCO2Percent(PercentFraction co2Percent)
	{
		this.co2Percent = co2Percent;
	}

	// Property Temperature with unit companion TemperatureUnit
	public Temperature getTemperature()
	{
		return temperature;
	}

	public void setTemperature(Temperature temperature)
	{
		this.temperature = temperature;
	}

	// Property AirPressure with unit companion AirPressureUnit
	public Pressure getAirPressure()
	{
		return airPressure;
	}

	public void setAirPressure(Pressure airPressure)
	{
		this.airPressure = airPressure;
	}

	// Property TemperatureUnit is a unit companion
	public static String getTemperatureUnitXsdDefault()
	{
		return "°C";
	}

	// Property Humidity
	public PercentFraction getHumidity()
	{
		return humidity;
	}

	public void setHumidity(PercentFraction humidity)
	{
		this.humidity = humidity;
	}

	// Property AirPressureUnit is a unit companion
	public static String getAirPressureUnitXsdDefault()
	{
		return "mbar";
	}

	// Property Map
	public Map getMap()
	{
		return map;
	}

	public void setMap(Map map)
	{
		this.map = map;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element ImagingEnvironment_element)
	{
		// Creating XML block for ImagingEnvironment

		if (ImagingEnvironment_element == null)
		{
			ImagingEnvironment_element =
					document.createElementNS(NAMESPACE, "ImagingEnvironment");
		}


		if (co2Percent != null)
		{
			// Attribute property CO2Percent
			ImagingEnvironment_element.setAttribute("CO2Percent", co2Percent.toString());
		}
		if (temperature != null)
		{
			// Attribute property Temperature with units companion prop.unitsCompanion.name
			if (temperature.value() != null)
			{
				ImagingEnvironment_element.setAttribute("Temperature", temperature.value().toString());
			}
			if (temperature.unit() != null)
			{
				try
				{
					UnitsTemperature enumUnits = UnitsTemperature.fromString(temperature.unit().getSymbol());
					ImagingEnvironment_element.setAttribute("TemperatureUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for ImagingEnvironment:TemperatureUnit: {}", e.toString());
				}
			}
		}
		if (airPressure != null)
		{
			// Attribute property AirPressure with units companion prop.unitsCompanion.name
			if (airPressure.value() != null)
			{
				ImagingEnvironment_element.setAttribute("AirPressure", airPressure.value().toString());
			}
			if (airPressure.unit() != null)
			{
				try
				{
					UnitsPressure enumUnits = UnitsPressure.fromString(airPressure.unit().getSymbol());
					ImagingEnvironment_element.setAttribute("AirPressureUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for ImagingEnvironment:AirPressureUnit: {}", e.toString());
				}
			}
		}
		if (humidity != null)
		{
			// Attribute property Humidity
			ImagingEnvironment_element.setAttribute("Humidity", humidity.toString());
		}
		if (map != null)
		{
			// Element property Map which is complex (has
			// sub-elements)
			ImagingEnvironment_element.appendChild(map.asXMLElement(document));
		}

		return super.asXMLElement(document, ImagingEnvironment_element);
	}
}
