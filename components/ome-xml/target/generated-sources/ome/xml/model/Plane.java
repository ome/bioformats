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

public class Plane extends AbstractOMEModelObject
{
	// Base:  -- Name: Plane -- Type: Plane -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Plane.class);

	// -- Instance variables --

	// ExposureTime property
	private Time exposureTime;

	// PositionZ property
	private Length positionZ;

	// PositionX property
	private Length positionX;

	// PositionY property
	private Length positionY;

	// DeltaT property
	private Time deltaT;

	// TheC property
	private NonNegativeInteger theC;

	// TheZ property
	private NonNegativeInteger theZ;

	// TheT property
	private NonNegativeInteger theT;

	// HashSHA1 property
	private String hashSHA1;

	// AnnotationRef reference (occurs more than once)
	private List<Annotation> annotationLinks = new ReferenceList<Annotation>();

	// Pixels_BackReference back reference
	private Pixels pixels;

	// -- Constructors --

	/** Default constructor. */
	public Plane()
	{
	}

	/**
	 * Constructs Plane recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Plane(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public Plane(Plane orig)
	{
		exposureTime = orig.exposureTime;
		positionZ = orig.positionZ;
		positionX = orig.positionX;
		positionY = orig.positionY;
		deltaT = orig.deltaT;
		theC = orig.theC;
		theZ = orig.theZ;
		theT = orig.theT;
		hashSHA1 = orig.hashSHA1;
		annotationLinks = orig.annotationLinks;
		pixels = orig.pixels;
	}

	// -- Custom content from Plane specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates Plane recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Plane".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Plane got {}", tagName);
		}
		if (element.hasAttribute("ExposureTime"))
		{
			// Attribute property ExposureTime with unit companion ExposureTimeUnit
			String unitSymbol = element.getAttribute("ExposureTimeUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getExposureTimeUnitXsdDefault();
			}
			UnitsTime modelUnit = 
				UnitsTime.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("ExposureTime"));
			if (baseValue != null) 
			{
				setExposureTime(UnitsTimeEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("PositionZ"))
		{
			// Attribute property PositionZ with unit companion PositionZUnit
			String unitSymbol = element.getAttribute("PositionZUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getPositionZUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("PositionZ"));
			if (baseValue != null) 
			{
				setPositionZ(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
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
		if (element.hasAttribute("DeltaT"))
		{
			// Attribute property DeltaT with unit companion DeltaTUnit
			String unitSymbol = element.getAttribute("DeltaTUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getDeltaTUnitXsdDefault();
			}
			UnitsTime modelUnit = 
				UnitsTime.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("DeltaT"));
			if (baseValue != null) 
			{
				setDeltaT(UnitsTimeEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("TheC"))
		{
			// Attribute property TheC
			setTheC(NonNegativeInteger.valueOf(
					element.getAttribute("TheC")));
		}
		if (element.hasAttribute("TheZ"))
		{
			// Attribute property TheZ
			setTheZ(NonNegativeInteger.valueOf(
					element.getAttribute("TheZ")));
		}
		if (element.hasAttribute("TheT"))
		{
			// Attribute property TheT
			setTheT(NonNegativeInteger.valueOf(
					element.getAttribute("TheT")));
		}
		List<Element> HashSHA1_nodeList =
				getChildrenByTagName(element, "HashSHA1");
		if (HashSHA1_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"HashSHA1 node list size %d != 1",
					HashSHA1_nodeList.size()));
		}
		else if (HashSHA1_nodeList.size() != 0)
		{
			// Element property HashSHA1 which is not complex (has no
			// sub-elements)
			setHashSHA1(
					String.valueOf(HashSHA1_nodeList.get(0).getTextContent()));
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

	// -- Plane API methods --

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
			o_casted.linkPlane(this);
			annotationLinks.add(o_casted);
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property ExposureTime with unit companion ExposureTimeUnit
	public Time getExposureTime()
	{
		return exposureTime;
	}

	public void setExposureTime(Time exposureTime)
	{
		this.exposureTime = exposureTime;
	}

	// Property PositionZ with unit companion PositionZUnit
	public Length getPositionZ()
	{
		return positionZ;
	}

	public void setPositionZ(Length positionZ)
	{
		this.positionZ = positionZ;
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

	// Property DeltaT with unit companion DeltaTUnit
	public Time getDeltaT()
	{
		return deltaT;
	}

	public void setDeltaT(Time deltaT)
	{
		this.deltaT = deltaT;
	}

	// Property TheC
	public NonNegativeInteger getTheC()
	{
		return theC;
	}

	public void setTheC(NonNegativeInteger theC)
	{
		this.theC = theC;
	}

	// Property TheZ
	public NonNegativeInteger getTheZ()
	{
		return theZ;
	}

	public void setTheZ(NonNegativeInteger theZ)
	{
		this.theZ = theZ;
	}

	// Property PositionZUnit is a unit companion
	public static String getPositionZUnitXsdDefault()
	{
		return "reference frame";
	}

	// Property PositionXUnit is a unit companion
	public static String getPositionXUnitXsdDefault()
	{
		return "reference frame";
	}

	// Property DeltaTUnit is a unit companion
	public static String getDeltaTUnitXsdDefault()
	{
		return "s";
	}

	// Property TheT
	public NonNegativeInteger getTheT()
	{
		return theT;
	}

	public void setTheT(NonNegativeInteger theT)
	{
		this.theT = theT;
	}

	// Property ExposureTimeUnit is a unit companion
	public static String getExposureTimeUnitXsdDefault()
	{
		return "s";
	}

	// Property PositionYUnit is a unit companion
	public static String getPositionYUnitXsdDefault()
	{
		return "reference frame";
	}

	// Property HashSHA1
	public String getHashSHA1()
	{
		return hashSHA1;
	}

	public void setHashSHA1(String hashSHA1)
	{
		this.hashSHA1 = hashSHA1;
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

			o.linkPlane(this);
		return annotationLinks.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkPlane(this);
		return annotationLinks.remove(o);
	}

	// Property Pixels_BackReference
	public Pixels getPixels()
	{
		return pixels;
	}

	public void setPixels(Pixels pixels_BackReference)
	{
		this.pixels = pixels_BackReference;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Plane_element)
	{
		// Creating XML block for Plane

		if (Plane_element == null)
		{
			Plane_element =
					document.createElementNS(NAMESPACE, "Plane");
		}


		if (exposureTime != null)
		{
			// Attribute property ExposureTime with units companion prop.unitsCompanion.name
			if (exposureTime.value() != null)
			{
				Plane_element.setAttribute("ExposureTime", exposureTime.value().toString());
			}
			if (exposureTime.unit() != null)
			{
				try
				{
					UnitsTime enumUnits = UnitsTime.fromString(exposureTime.unit().getSymbol());
					Plane_element.setAttribute("ExposureTimeUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for Plane:ExposureTimeUnit: {}", e.toString());
				}
			}
		}
		if (positionZ != null)
		{
			// Attribute property PositionZ with units companion prop.unitsCompanion.name
			if (positionZ.value() != null)
			{
				Plane_element.setAttribute("PositionZ", positionZ.value().toString());
			}
			if (positionZ.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(positionZ.unit().getSymbol());
					Plane_element.setAttribute("PositionZUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for Plane:PositionZUnit: {}", e.toString());
				}
			}
		}
		if (positionX != null)
		{
			// Attribute property PositionX with units companion prop.unitsCompanion.name
			if (positionX.value() != null)
			{
				Plane_element.setAttribute("PositionX", positionX.value().toString());
			}
			if (positionX.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(positionX.unit().getSymbol());
					Plane_element.setAttribute("PositionXUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for Plane:PositionXUnit: {}", e.toString());
				}
			}
		}
		if (positionY != null)
		{
			// Attribute property PositionY with units companion prop.unitsCompanion.name
			if (positionY.value() != null)
			{
				Plane_element.setAttribute("PositionY", positionY.value().toString());
			}
			if (positionY.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(positionY.unit().getSymbol());
					Plane_element.setAttribute("PositionYUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for Plane:PositionYUnit: {}", e.toString());
				}
			}
		}
		if (deltaT != null)
		{
			// Attribute property DeltaT with units companion prop.unitsCompanion.name
			if (deltaT.value() != null)
			{
				Plane_element.setAttribute("DeltaT", deltaT.value().toString());
			}
			if (deltaT.unit() != null)
			{
				try
				{
					UnitsTime enumUnits = UnitsTime.fromString(deltaT.unit().getSymbol());
					Plane_element.setAttribute("DeltaTUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for Plane:DeltaTUnit: {}", e.toString());
				}
			}
		}
		if (theC != null)
		{
			// Attribute property TheC
			Plane_element.setAttribute("TheC", theC.toString());
		}
		if (theZ != null)
		{
			// Attribute property TheZ
			Plane_element.setAttribute("TheZ", theZ.toString());
		}
		if (theT != null)
		{
			// Attribute property TheT
			Plane_element.setAttribute("TheT", theT.toString());
		}
		if (hashSHA1 != null)
		{
			// Element property HashSHA1 which is not complex (has no
			// sub-elements)
			Element hashSHA1_element =
					document.createElementNS(NAMESPACE, "HashSHA1");
			hashSHA1_element.setTextContent(hashSHA1.toString());
			Plane_element.appendChild(hashSHA1_element);
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Plane_element.appendChild(o.asXMLElement(document));
			}
		}
		if (pixels != null)
		{
			// *** IGNORING *** Skipped back reference Pixels_BackReference
		}

		return super.asXMLElement(document, Plane_element);
	}
}
