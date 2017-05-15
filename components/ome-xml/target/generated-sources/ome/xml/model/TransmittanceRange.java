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

public class TransmittanceRange extends AbstractOMEModelObject
{
	// Base:  -- Name: TransmittanceRange -- Type: TransmittanceRange -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(TransmittanceRange.class);

	// -- Instance variables --

	// Transmittance property
	private PercentFraction transmittance;

	// CutInTolerance property
	private Length cutInTolerance;

	// CutOutTolerance property
	private Length cutOutTolerance;

	// CutIn property
	private Length cutIn;

	// CutOut property
	private Length cutOut;

	// -- Constructors --

	/** Default constructor. */
	public TransmittanceRange()
	{
	}

	/**
	 * Constructs TransmittanceRange recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public TransmittanceRange(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public TransmittanceRange(TransmittanceRange orig)
	{
		transmittance = orig.transmittance;
		cutInTolerance = orig.cutInTolerance;
		cutOutTolerance = orig.cutOutTolerance;
		cutIn = orig.cutIn;
		cutOut = orig.cutOut;
	}

	// -- Custom content from TransmittanceRange specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates TransmittanceRange recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"TransmittanceRange".equals(tagName))
		{
			LOGGER.debug("Expecting node name of TransmittanceRange got {}", tagName);
		}
		if (element.hasAttribute("Transmittance"))
		{
			// Attribute property Transmittance
			setTransmittance(PercentFraction.valueOf(
					element.getAttribute("Transmittance")));
		}
		if (element.hasAttribute("CutInTolerance"))
		{
			// Attribute property CutInTolerance with unit companion CutInToleranceUnit
			String unitSymbol = element.getAttribute("CutInToleranceUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getCutInToleranceUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			NonNegativeFloat baseValue = NonNegativeFloat.valueOf(
					element.getAttribute("CutInTolerance"));
			if (baseValue != null) 
			{
				setCutInTolerance(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("CutOutTolerance"))
		{
			// Attribute property CutOutTolerance with unit companion CutOutToleranceUnit
			String unitSymbol = element.getAttribute("CutOutToleranceUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getCutOutToleranceUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			NonNegativeFloat baseValue = NonNegativeFloat.valueOf(
					element.getAttribute("CutOutTolerance"));
			if (baseValue != null) 
			{
				setCutOutTolerance(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("CutIn"))
		{
			// Attribute property CutIn with unit companion CutInUnit
			String unitSymbol = element.getAttribute("CutInUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getCutInUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			PositiveFloat baseValue = PositiveFloat.valueOf(
					element.getAttribute("CutIn"));
			if (baseValue != null) 
			{
				setCutIn(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("CutOut"))
		{
			// Attribute property CutOut with unit companion CutOutUnit
			String unitSymbol = element.getAttribute("CutOutUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getCutOutUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			PositiveFloat baseValue = PositiveFloat.valueOf(
					element.getAttribute("CutOut"));
			if (baseValue != null) 
			{
				setCutOut(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
	}

	// -- TransmittanceRange API methods --

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


	// Property Transmittance
	public PercentFraction getTransmittance()
	{
		return transmittance;
	}

	public void setTransmittance(PercentFraction transmittance)
	{
		this.transmittance = transmittance;
	}

	// Property CutInTolerance with unit companion CutInToleranceUnit
	public Length getCutInTolerance()
	{
		return cutInTolerance;
	}

	public void setCutInTolerance(Length cutInTolerance)
	{
		this.cutInTolerance = cutInTolerance;
	}

	// Property CutOutTolerance with unit companion CutOutToleranceUnit
	public Length getCutOutTolerance()
	{
		return cutOutTolerance;
	}

	public void setCutOutTolerance(Length cutOutTolerance)
	{
		this.cutOutTolerance = cutOutTolerance;
	}

	// Property CutOutToleranceUnit is a unit companion
	public static String getCutOutToleranceUnitXsdDefault()
	{
		return "nm";
	}

	// Property CutInToleranceUnit is a unit companion
	public static String getCutInToleranceUnitXsdDefault()
	{
		return "nm";
	}

	// Property CutIn with unit companion CutInUnit
	public Length getCutIn()
	{
		return cutIn;
	}

	public void setCutIn(Length cutIn)
	{
		this.cutIn = cutIn;
	}

	// Property CutOutUnit is a unit companion
	public static String getCutOutUnitXsdDefault()
	{
		return "nm";
	}

	// Property CutInUnit is a unit companion
	public static String getCutInUnitXsdDefault()
	{
		return "nm";
	}

	// Property CutOut with unit companion CutOutUnit
	public Length getCutOut()
	{
		return cutOut;
	}

	public void setCutOut(Length cutOut)
	{
		this.cutOut = cutOut;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element TransmittanceRange_element)
	{
		// Creating XML block for TransmittanceRange

		if (TransmittanceRange_element == null)
		{
			TransmittanceRange_element =
					document.createElementNS(NAMESPACE, "TransmittanceRange");
		}


		if (transmittance != null)
		{
			// Attribute property Transmittance
			TransmittanceRange_element.setAttribute("Transmittance", transmittance.toString());
		}
		if (cutInTolerance != null)
		{
			// Attribute property CutInTolerance with units companion prop.unitsCompanion.name
			if (cutInTolerance.value() != null)
			{
				TransmittanceRange_element.setAttribute("CutInTolerance", cutInTolerance.value().toString());
			}
			if (cutInTolerance.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(cutInTolerance.unit().getSymbol());
					TransmittanceRange_element.setAttribute("CutInToleranceUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for TransmittanceRange:CutInToleranceUnit: {}", e.toString());
				}
			}
		}
		if (cutOutTolerance != null)
		{
			// Attribute property CutOutTolerance with units companion prop.unitsCompanion.name
			if (cutOutTolerance.value() != null)
			{
				TransmittanceRange_element.setAttribute("CutOutTolerance", cutOutTolerance.value().toString());
			}
			if (cutOutTolerance.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(cutOutTolerance.unit().getSymbol());
					TransmittanceRange_element.setAttribute("CutOutToleranceUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for TransmittanceRange:CutOutToleranceUnit: {}", e.toString());
				}
			}
		}
		if (cutIn != null)
		{
			// Attribute property CutIn with units companion prop.unitsCompanion.name
			if (cutIn.value() != null)
			{
				TransmittanceRange_element.setAttribute("CutIn", cutIn.value().toString());
			}
			if (cutIn.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(cutIn.unit().getSymbol());
					TransmittanceRange_element.setAttribute("CutInUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for TransmittanceRange:CutInUnit: {}", e.toString());
				}
			}
		}
		if (cutOut != null)
		{
			// Attribute property CutOut with units companion prop.unitsCompanion.name
			if (cutOut.value() != null)
			{
				TransmittanceRange_element.setAttribute("CutOut", cutOut.value().toString());
			}
			if (cutOut.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(cutOut.unit().getSymbol());
					TransmittanceRange_element.setAttribute("CutOutUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for TransmittanceRange:CutOutUnit: {}", e.toString());
				}
			}
		}

		return super.asXMLElement(document, TransmittanceRange_element);
	}
}
