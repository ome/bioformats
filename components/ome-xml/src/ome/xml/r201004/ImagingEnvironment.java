/*
 * ome.xml.r201004.ImagingEnvironment
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
 * Created by callan via xsd-fu on 2010-04-29 09:45:43+0100
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r201004;

import java.util.ArrayList;
import java.util.List;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ome.xml.r201004.enums.*;
import ome.xml.r201004.primitives.*;

public class ImagingEnvironment extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2010-04";

	// -- Instance variables --

	// Property
	private PercentFraction co2percent;

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
			System.err.println(String.format(
					"WARNING: Expecting node name of ImagingEnvironment got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of ImagingEnvironment got %s",
			//		tagName));
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

	public void link(Reference reference, OMEModelObject o)
	{
		// TODO: Should be its own Exception
		throw new RuntimeException(
				"Unable to handle reference of type: " + reference.getClass());
	}


	// Property
	public PercentFraction getCO2Percent()
	{
		return co2percent;
	}

	public void setCO2Percent(PercentFraction co2percent)
	{
		this.co2percent = co2percent;
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

		if (co2percent != null)
		{
			// Attribute property CO2Percent
			ImagingEnvironment_element.setAttribute("CO2Percent", co2percent.toString());
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
