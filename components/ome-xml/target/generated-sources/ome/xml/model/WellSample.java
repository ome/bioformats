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

public class WellSample extends AbstractOMEModelObject
{
	// Base:  -- Name: WellSample -- Type: WellSample -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SPW/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(WellSample.class);

	// -- Instance variables --

	// Index property
	private NonNegativeInteger index;

	// PositionX property
	private Length positionX;

	// PositionY property
	private Length positionY;

	// Timepoint property
	private Timestamp timepoint;

	// ID property
	private String id;

	// ImageRef reference
	private Image image;

	// PlateAcquisition_BackReference back reference (occurs more than once)
	private List<PlateAcquisition> plateAcquisitions = new ReferenceList<PlateAcquisition>();

	// Well_BackReference back reference
	private Well well;

	// -- Constructors --

	/** Default constructor. */
	public WellSample()
	{
	}

	/**
	 * Constructs WellSample recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public WellSample(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public WellSample(WellSample orig)
	{
		index = orig.index;
		positionX = orig.positionX;
		positionY = orig.positionY;
		timepoint = orig.timepoint;
		id = orig.id;
		image = orig.image;
		plateAcquisitions = orig.plateAcquisitions;
		well = orig.well;
	}

	// -- Custom content from WellSample specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates WellSample recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"WellSample".equals(tagName))
		{
			LOGGER.debug("Expecting node name of WellSample got {}", tagName);
		}
		if (element.hasAttribute("Index"))
		{
			// Attribute property Index
			setIndex(NonNegativeInteger.valueOf(
					element.getAttribute("Index")));
		}
		if (element.hasAttribute("PositionX"))
		{
			// Attribute property PositionX with unit companion PositionXUnit
			String unitSymbol = element.getAttribute("PositionXUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getPositionXUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("PositionX"));
			if (baseValue != null) 
			{
				setPositionX(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("PositionY"))
		{
			// Attribute property PositionY with unit companion PositionYUnit
			String unitSymbol = element.getAttribute("PositionYUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getPositionYUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("PositionY"));
			if (baseValue != null) 
			{
				setPositionY(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("Timepoint"))
		{
			// Attribute property Timepoint
			setTimepoint(Timestamp.valueOf(
					element.getAttribute("Timepoint")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"WellSample missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		// Element reference ImageRef
		List<Element> ImageRef_nodeList =
				getChildrenByTagName(element, "ImageRef");
		for (Element ImageRef_element : ImageRef_nodeList)
		{
			ImageRef image_reference = new ImageRef();
			image_reference.setID(ImageRef_element.getAttribute("ID"));
			model.addReference(this, image_reference);
		}
	}

	// -- WellSample API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof ImageRef)
		{
			Image o_casted = (Image) o;
			o_casted.linkWellSample(this);
			image = o_casted;
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property Index
	public NonNegativeInteger getIndex()
	{
		return index;
	}

	public void setIndex(NonNegativeInteger index)
	{
		this.index = index;
	}

	// Property PositionX with unit companion PositionXUnit
	public Length getPositionX()
	{
		return positionX;
	}

	public void setPositionX(Length positionX)
	{
		this.positionX = positionX;
	}

	// Property PositionY with unit companion PositionYUnit
	public Length getPositionY()
	{
		return positionY;
	}

	public void setPositionY(Length positionY)
	{
		this.positionY = positionY;
	}

	// Property PositionXUnit is a unit companion
	public static String getPositionXUnitXsdDefault()
	{
		return "reference frame";
	}

	// Property Timepoint
	public Timestamp getTimepoint()
	{
		return timepoint;
	}

	public void setTimepoint(Timestamp timepoint)
	{
		this.timepoint = timepoint;
	}

	// Property PositionYUnit is a unit companion
	public static String getPositionYUnitXsdDefault()
	{
		return "reference frame";
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

	// Reference
	public Image getLinkedImage()
	{
		return image;
	}

	public void linkImage(Image o)
	{
		image = o;
	}

	public void unlinkImage(Image o)
	{
		if (image == o)
		{
			image = null;
		}
	}

	// Reference which occurs more than once
	public int sizeOfLinkedPlateAcquisitionList()
	{
		return plateAcquisitions.size();
	}

	public List<PlateAcquisition> copyLinkedPlateAcquisitionList()
	{
		return new ArrayList<PlateAcquisition>(plateAcquisitions);
	}

	public PlateAcquisition getLinkedPlateAcquisition(int index)
	{
		return plateAcquisitions.get(index);
	}

	public PlateAcquisition setLinkedPlateAcquisition(int index, PlateAcquisition o)
	{
		return plateAcquisitions.set(index, o);
	}

	public boolean linkPlateAcquisition(PlateAcquisition o)
	{
		return plateAcquisitions.add(o);
	}

	public boolean unlinkPlateAcquisition(PlateAcquisition o)
	{
		return plateAcquisitions.remove(o);
	}

	// Property Well_BackReference
	public Well getWell()
	{
		return well;
	}

	public void setWell(Well well_BackReference)
	{
		this.well = well_BackReference;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element WellSample_element)
	{
		// Creating XML block for WellSample

		if (WellSample_element == null)
		{
			WellSample_element =
					document.createElementNS(NAMESPACE, "WellSample");
		}


		if (index != null)
		{
			// Attribute property Index
			WellSample_element.setAttribute("Index", index.toString());
		}
		if (positionX != null)
		{
			// Attribute property PositionX with units companion prop.unitsCompanion.name
			if (positionX.value() != null)
			{
				WellSample_element.setAttribute("PositionX", positionX.value().toString());
			}
			if (positionX.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(positionX.unit().getSymbol());
					WellSample_element.setAttribute("PositionXUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for WellSample:PositionXUnit: {}", e.toString());
				}
			}
		}
		if (positionY != null)
		{
			// Attribute property PositionY with units companion prop.unitsCompanion.name
			if (positionY.value() != null)
			{
				WellSample_element.setAttribute("PositionY", positionY.value().toString());
			}
			if (positionY.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(positionY.unit().getSymbol());
					WellSample_element.setAttribute("PositionYUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for WellSample:PositionYUnit: {}", e.toString());
				}
			}
		}
		if (timepoint != null)
		{
			// Attribute property Timepoint
			WellSample_element.setAttribute("Timepoint", timepoint.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			WellSample_element.setAttribute("ID", id.toString());
		}
		if (image != null)
		{
			// Reference property ImageRef
			ImageRef o = new ImageRef();
			o.setID(image.getID());
			WellSample_element.appendChild(o.asXMLElement(document));
		}
		if (plateAcquisitions != null)
		{
			// *** IGNORING *** Skipped back reference PlateAcquisition_BackReference
		}
		if (well != null)
		{
			// *** IGNORING *** Skipped back reference Well_BackReference
		}

		return super.asXMLElement(document, WellSample_element);
	}
}
