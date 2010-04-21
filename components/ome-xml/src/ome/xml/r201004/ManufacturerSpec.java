/*
 * ome.xml.r201004.ManufacturerSpec
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
 * Created by callan via xsd-fu on 2010-04-21 11:45:19+0100
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

public abstract class ManufacturerSpec extends AbstractOMEModelObject
{
	// -- Instance variables --

	// Property
	private String lotNumber;

	// Property
	private String model;

	// Property
	private String serialNumber;

	// Property
	private String manufacturer;

	// -- Constructors --

	/** Default constructor. */
	public ManufacturerSpec()
	{
		super();
	}

	/** 
	 * Constructs ManufacturerSpec recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public ManufacturerSpec(Element element) throws EnumerationException
	{
		super(element);
		if (element.hasAttribute("LotNumber"))
		{
			// Attribute property LotNumber
			setLotNumber(String.valueOf(
					element.getAttribute("LotNumber")));
		}
		if (element.hasAttribute("Model"))
		{
			// Attribute property Model
			setModel(String.valueOf(
					element.getAttribute("Model")));
		}
		if (element.hasAttribute("SerialNumber"))
		{
			// Attribute property SerialNumber
			setSerialNumber(String.valueOf(
					element.getAttribute("SerialNumber")));
		}
		if (element.hasAttribute("Manufacturer"))
		{
			// Attribute property Manufacturer
			setManufacturer(String.valueOf(
					element.getAttribute("Manufacturer")));
		}
	}

	// -- ManufacturerSpec API methods --

	// Property
	public String getLotNumber()
	{
		return lotNumber;
	}

	public void setLotNumber(String lotNumber)
	{
		this.lotNumber = lotNumber;
	}

	// Property
	public String getModel()
	{
		return model;
	}

	public void setModel(String model)
	{
		this.model = model;
	}

	// Property
	public String getSerialNumber()
	{
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber)
	{
		this.serialNumber = serialNumber;
	}

	// Property
	public String getManufacturer()
	{
		return manufacturer;
	}

	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	protected Element asXMLElement(Document document, Element ManufacturerSpec_element)
	{
		// Creating XML block for ManufacturerSpec
		if (ManufacturerSpec_element == null)
		{
			ManufacturerSpec_element = document.createElement("ManufacturerSpec");
		}
		ManufacturerSpec_element = super.asXMLElement(document, ManufacturerSpec_element);

		if (lotNumber != null)
		{
			// Attribute property LotNumber
			ManufacturerSpec_element.setAttribute("LotNumber", lotNumber.toString());
		}
		if (model != null)
		{
			// Attribute property Model
			ManufacturerSpec_element.setAttribute("Model", model.toString());
		}
		if (serialNumber != null)
		{
			// Attribute property SerialNumber
			ManufacturerSpec_element.setAttribute("SerialNumber", serialNumber.toString());
		}
		if (manufacturer != null)
		{
			// Attribute property Manufacturer
			ManufacturerSpec_element.setAttribute("Manufacturer", manufacturer.toString());
		}
		return ManufacturerSpec_element;
	}
}
