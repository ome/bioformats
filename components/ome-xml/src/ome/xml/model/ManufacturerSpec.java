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

public abstract class ManufacturerSpec extends AbstractOMEModelObject
{
	// Base:  -- Name: ManufacturerSpec -- Type: ManufacturerSpec -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(ManufacturerSpec.class);

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
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public ManufacturerSpec(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from ManufacturerSpec specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates ManufacturerSpec recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"ManufacturerSpec".equals(tagName))
		{
			LOGGER.debug("Expecting node name of ManufacturerSpec got {}", tagName);
		}
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

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element ManufacturerSpec_element)
	{
		// Creating XML block for ManufacturerSpec

		if (ManufacturerSpec_element == null)
		{
			ManufacturerSpec_element =
					document.createElementNS(NAMESPACE, "ManufacturerSpec");
		}

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
		return super.asXMLElement(document, ManufacturerSpec_element);
	}
}
