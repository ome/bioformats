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

public class StageLabel extends AbstractOMEModelObject
{
	// Base:  -- Name: StageLabel -- Type: StageLabel -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(StageLabel.class);

	// -- Instance variables --

	// Name property
	private String name;

	// Y property
	private Length y;

	// X property
	private Length x;

	// Z property
	private Length z;

	// -- Constructors --

	/** Default constructor. */
	public StageLabel()
	{
	}

	/**
	 * Constructs StageLabel recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public StageLabel(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public StageLabel(StageLabel orig)
	{
		name = orig.name;
		y = orig.y;
		x = orig.x;
		z = orig.z;
	}

	// -- Custom content from StageLabel specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates StageLabel recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"StageLabel".equals(tagName))
		{
			LOGGER.debug("Expecting node name of StageLabel got {}", tagName);
		}
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			setName(String.valueOf(
					element.getAttribute("Name")));
		}
		if (element.hasAttribute("Y"))
		{
			// Attribute property Y with unit companion YUnit
			String unitSymbol = element.getAttribute("YUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getYUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("Y"));
			if (baseValue != null) 
			{
				setY(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("X"))
		{
			// Attribute property X with unit companion XUnit
			String unitSymbol = element.getAttribute("XUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getXUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("X"));
			if (baseValue != null) 
			{
				setX(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("Z"))
		{
			// Attribute property Z with unit companion ZUnit
			String unitSymbol = element.getAttribute("ZUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getZUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("Z"));
			if (baseValue != null) 
			{
				setZ(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
	}

	// -- StageLabel API methods --

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


	// Property Name
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	// Property ZUnit is a unit companion
	public static String getZUnitXsdDefault()
	{
		return "reference frame";
	}

	// Property YUnit is a unit companion
	public static String getYUnitXsdDefault()
	{
		return "reference frame";
	}

	// Property XUnit is a unit companion
	public static String getXUnitXsdDefault()
	{
		return "reference frame";
	}

	// Property Y with unit companion YUnit
	public Length getY()
	{
		return y;
	}

	public void setY(Length y)
	{
		this.y = y;
	}

	// Property X with unit companion XUnit
	public Length getX()
	{
		return x;
	}

	public void setX(Length x)
	{
		this.x = x;
	}

	// Property Z with unit companion ZUnit
	public Length getZ()
	{
		return z;
	}

	public void setZ(Length z)
	{
		this.z = z;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element StageLabel_element)
	{
		// Creating XML block for StageLabel

		if (StageLabel_element == null)
		{
			StageLabel_element =
					document.createElementNS(NAMESPACE, "StageLabel");
		}


		if (name != null)
		{
			// Attribute property Name
			StageLabel_element.setAttribute("Name", name.toString());
		}
		if (y != null)
		{
			// Attribute property Y with units companion prop.unitsCompanion.name
			if (y.value() != null)
			{
				StageLabel_element.setAttribute("Y", y.value().toString());
			}
			if (y.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(y.unit().getSymbol());
					StageLabel_element.setAttribute("YUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for StageLabel:YUnit: {}", e.toString());
				}
			}
		}
		if (x != null)
		{
			// Attribute property X with units companion prop.unitsCompanion.name
			if (x.value() != null)
			{
				StageLabel_element.setAttribute("X", x.value().toString());
			}
			if (x.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(x.unit().getSymbol());
					StageLabel_element.setAttribute("XUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for StageLabel:XUnit: {}", e.toString());
				}
			}
		}
		if (z != null)
		{
			// Attribute property Z with units companion prop.unitsCompanion.name
			if (z.value() != null)
			{
				StageLabel_element.setAttribute("Z", z.value().toString());
			}
			if (z.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(z.unit().getSymbol());
					StageLabel_element.setAttribute("ZUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for StageLabel:ZUnit: {}", e.toString());
				}
			}
		}

		return super.asXMLElement(document, StageLabel_element);
	}
}
