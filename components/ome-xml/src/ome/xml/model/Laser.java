/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
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
 * Created by callan via xsd-fu on 2012-05-18 10:08:16+0100
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

public class Laser extends LightSource
{
	// Base:  -- Name: Laser -- Type: Laser -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Laser.class);

	// -- Instance variables --


	// Property
	private Boolean pockelCell;

	// Property
	private Pulse pulse;

	// Property
	private LaserMedium laserMedium;

	// Property
	private Boolean tuneable;

	// Property
	private PositiveInteger wavelength;

	// Property
	private PositiveInteger frequencyMultiplication;

	// Property
	private LaserType type;

	// Property
	private Double repetitionRate;

	// Property
	private LightSource pump;

	// -- Constructors --

	/** Default constructor. */
	public Laser()
	{
		super();
	}

	/** 
	 * Constructs Laser recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Laser(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Laser specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Laser recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Laser".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Laser got {}", tagName);
		}
		if (element.hasAttribute("PockelCell"))
		{
			// Attribute property PockelCell
			setPockelCell(Boolean.valueOf(
					element.getAttribute("PockelCell")));
		}
		if (element.hasAttribute("Pulse"))
		{
			// Attribute property which is an enumeration Pulse
			setPulse(Pulse.fromString(
					element.getAttribute("Pulse")));
		}
		if (element.hasAttribute("LaserMedium"))
		{
			// Attribute property which is an enumeration LaserMedium
			setLaserMedium(LaserMedium.fromString(
					element.getAttribute("LaserMedium")));
		}
		if (element.hasAttribute("Tuneable"))
		{
			// Attribute property Tuneable
			setTuneable(Boolean.valueOf(
					element.getAttribute("Tuneable")));
		}
		if (element.hasAttribute("Wavelength"))
		{
			// Attribute property Wavelength
			setWavelength(PositiveInteger.valueOf(
					element.getAttribute("Wavelength")));
		}
		if (element.hasAttribute("FrequencyMultiplication"))
		{
			// Attribute property FrequencyMultiplication
			setFrequencyMultiplication(PositiveInteger.valueOf(
					element.getAttribute("FrequencyMultiplication")));
		}
		if (element.hasAttribute("Type"))
		{
			// Attribute property which is an enumeration Type
			setType(LaserType.fromString(
					element.getAttribute("Type")));
		}
		if (element.hasAttribute("RepetitionRate"))
		{
			// Attribute property RepetitionRate
			setRepetitionRate(Double.valueOf(
					element.getAttribute("RepetitionRate")));
		}
		// Element reference Pump
		List<Element> Pump_nodeList =
				getChildrenByTagName(element, "Pump");
		for (Element Pump_element : Pump_nodeList)
		{
			Pump pump_reference = new Pump();
			pump_reference.setID(Pump_element.getAttribute("ID"));
			model.addReference(this, pump_reference);
		}
	}

	// -- Laser API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof Pump)
		{
			LightSource o_casted = (LightSource) o;
			pump = o_casted;
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property
	public Boolean getPockelCell()
	{
		return pockelCell;
	}

	public void setPockelCell(Boolean pockelCell)
	{
		this.pockelCell = pockelCell;
	}

	// Property
	public Pulse getPulse()
	{
		return pulse;
	}

	public void setPulse(Pulse pulse)
	{
		this.pulse = pulse;
	}

	// Property
	public LaserMedium getLaserMedium()
	{
		return laserMedium;
	}

	public void setLaserMedium(LaserMedium laserMedium)
	{
		this.laserMedium = laserMedium;
	}

	// Property
	public Boolean getTuneable()
	{
		return tuneable;
	}

	public void setTuneable(Boolean tuneable)
	{
		this.tuneable = tuneable;
	}

	// Property
	public PositiveInteger getWavelength()
	{
		return wavelength;
	}

	public void setWavelength(PositiveInteger wavelength)
	{
		this.wavelength = wavelength;
	}

	// Property
	public PositiveInteger getFrequencyMultiplication()
	{
		return frequencyMultiplication;
	}

	public void setFrequencyMultiplication(PositiveInteger frequencyMultiplication)
	{
		this.frequencyMultiplication = frequencyMultiplication;
	}

	// Property
	public LaserType getType()
	{
		return type;
	}

	public void setType(LaserType type)
	{
		this.type = type;
	}

	// Property
	public Double getRepetitionRate()
	{
		return repetitionRate;
	}

	public void setRepetitionRate(Double repetitionRate)
	{
		this.repetitionRate = repetitionRate;
	}

	// Reference
	public LightSource getLinkedPump()
	{
		return pump;
	}

	public void linkPump(LightSource o)
	{
		pump = o;
	}

	public void unlinkPump(LightSource o)
	{
		if (pump == o)
		{
			pump = null;
		}
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Laser_element)
	{
		// Creating XML block for Laser

		if (Laser_element == null)
		{
			Laser_element =
					document.createElementNS(NAMESPACE, "Laser");
		}

		if (pockelCell != null)
		{
			// Attribute property PockelCell
			Laser_element.setAttribute("PockelCell", pockelCell.toString());
		}
		if (pulse != null)
		{
			// Attribute property Pulse
			Laser_element.setAttribute("Pulse", pulse.toString());
		}
		if (laserMedium != null)
		{
			// Attribute property LaserMedium
			Laser_element.setAttribute("LaserMedium", laserMedium.toString());
		}
		if (tuneable != null)
		{
			// Attribute property Tuneable
			Laser_element.setAttribute("Tuneable", tuneable.toString());
		}
		if (wavelength != null)
		{
			// Attribute property Wavelength
			Laser_element.setAttribute("Wavelength", wavelength.toString());
		}
		if (frequencyMultiplication != null)
		{
			// Attribute property FrequencyMultiplication
			Laser_element.setAttribute("FrequencyMultiplication", frequencyMultiplication.toString());
		}
		if (type != null)
		{
			// Attribute property Type
			Laser_element.setAttribute("Type", type.toString());
		}
		if (repetitionRate != null)
		{
			// Attribute property RepetitionRate
			Laser_element.setAttribute("RepetitionRate", repetitionRate.toString());
		}
		if (pump != null)
		{
			// Reference property Pump
			Pump o = new Pump();
			o.setID(pump.getID());
			Laser_element.appendChild(o.asXMLElement(document));
		}
		return super.asXMLElement(document, Laser_element);
	}
}
