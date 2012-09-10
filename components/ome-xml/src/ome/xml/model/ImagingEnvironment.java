/*
 * ome.xml.model.ImagingEnvironment
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

public class ImagingEnvironment extends AbstractOMEModelObject
{
	// Base:  -- Name: ImagingEnvironment -- Type: ImagingEnvironment -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(ImagingEnvironment.class);

	// -- Instance variables --


	// Property
	private PercentFraction co2Percent;

	// Property
	private Double temperature;

	// Property
	private Double airPressure;

	// Property
	private PercentFraction humidity;

	// -- Constructors --

	/** Default constructor. */
	public ImagingEnvironment()
	{
		super();
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
			// Attribute property Temperature
			setTemperature(Double.valueOf(
					element.getAttribute("Temperature")));
		}
		if (element.hasAttribute("AirPressure"))
		{
			// Attribute property AirPressure
			setAirPressure(Double.valueOf(
					element.getAttribute("AirPressure")));
		}
		if (element.hasAttribute("Humidity"))
		{
			// Attribute property Humidity
			setHumidity(PercentFraction.valueOf(
					element.getAttribute("Humidity")));
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


	// Property
	public PercentFraction getCO2Percent()
	{
		return co2Percent;
	}

	public void setCO2Percent(PercentFraction co2Percent)
	{
		this.co2Percent = co2Percent;
	}

	// Property
	public Double getTemperature()
	{
		return temperature;
	}

	public void setTemperature(Double temperature)
	{
		this.temperature = temperature;
	}

	// Property
	public Double getAirPressure()
	{
		return airPressure;
	}

	public void setAirPressure(Double airPressure)
	{
		this.airPressure = airPressure;
	}

	// Property
	public PercentFraction getHumidity()
	{
		return humidity;
	}

	public void setHumidity(PercentFraction humidity)
	{
		this.humidity = humidity;
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
			// Attribute property Temperature
			ImagingEnvironment_element.setAttribute("Temperature", temperature.toString());
		}
		if (airPressure != null)
		{
			// Attribute property AirPressure
			ImagingEnvironment_element.setAttribute("AirPressure", airPressure.toString());
		}
		if (humidity != null)
		{
			// Attribute property Humidity
			ImagingEnvironment_element.setAttribute("Humidity", humidity.toString());
		}
		return super.asXMLElement(document, ImagingEnvironment_element);
	}
}
