/*
 * ome.xml.model.DetectorSettings
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

public class DetectorSettings extends Settings
{
	// Base: Settings -- Name: DetectorSettings -- Type: DetectorSettings -- javaBase: Settings -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(DetectorSettings.class);

	// -- Instance variables --


	// Property
	private Binning binning;

	// Property
	private Double readOutRate;

	// Property
	private Double gain;

	// Property
	private Double offset;

	// Property
	private String id;

	// Property
	private Double voltage;

	// Back reference DetectorRef
	private Detector detector;

	// -- Constructors --

	/** Default constructor. */
	public DetectorSettings()
	{
		super();
	}

	/** 
	 * Constructs DetectorSettings recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public DetectorSettings(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from DetectorSettings specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates DetectorSettings recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"DetectorSettings".equals(tagName))
		{
			LOGGER.debug("Expecting node name of DetectorSettings got {}", tagName);
		}
		if (element.hasAttribute("Binning"))
		{
			// Attribute property which is an enumeration Binning
			setBinning(Binning.fromString(
					element.getAttribute("Binning")));
		}
		if (element.hasAttribute("ReadOutRate"))
		{
			// Attribute property ReadOutRate
			setReadOutRate(Double.valueOf(
					element.getAttribute("ReadOutRate")));
		}
		if (element.hasAttribute("Gain"))
		{
			// Attribute property Gain
			setGain(Double.valueOf(
					element.getAttribute("Gain")));
		}
		if (element.hasAttribute("Offset"))
		{
			// Attribute property Offset
			setOffset(Double.valueOf(
					element.getAttribute("Offset")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"DetectorSettings missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		if (element.hasAttribute("Voltage"))
		{
			// Attribute property Voltage
			setVoltage(Double.valueOf(
					element.getAttribute("Voltage")));
		}
		// *** IGNORING *** Skipped back reference DetectorRef
	}

	// -- DetectorSettings API methods --

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
	public Binning getBinning()
	{
		return binning;
	}

	public void setBinning(Binning binning)
	{
		this.binning = binning;
	}

	// Property
	public Double getReadOutRate()
	{
		return readOutRate;
	}

	public void setReadOutRate(Double readOutRate)
	{
		this.readOutRate = readOutRate;
	}

	// Property
	public Double getGain()
	{
		return gain;
	}

	public void setGain(Double gain)
	{
		this.gain = gain;
	}

	// Property
	public Double getOffset()
	{
		return offset;
	}

	public void setOffset(Double offset)
	{
		this.offset = offset;
	}

	// Property
	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	// Property
	public Double getVoltage()
	{
		return voltage;
	}

	public void setVoltage(Double voltage)
	{
		this.voltage = voltage;
	}

	// Property
	public Detector getDetector()
	{
		return detector;
	}

	public void setDetector(Detector detector)
	{
		this.detector = detector;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element DetectorSettings_element)
	{
		// Creating XML block for DetectorSettings

		if (DetectorSettings_element == null)
		{
			DetectorSettings_element =
					document.createElementNS(NAMESPACE, "DetectorSettings");
		}

		if (binning != null)
		{
			// Attribute property Binning
			DetectorSettings_element.setAttribute("Binning", binning.toString());
		}
		if (readOutRate != null)
		{
			// Attribute property ReadOutRate
			DetectorSettings_element.setAttribute("ReadOutRate", readOutRate.toString());
		}
		if (gain != null)
		{
			// Attribute property Gain
			DetectorSettings_element.setAttribute("Gain", gain.toString());
		}
		if (offset != null)
		{
			// Attribute property Offset
			DetectorSettings_element.setAttribute("Offset", offset.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			DetectorSettings_element.setAttribute("ID", id.toString());
		}
		if (voltage != null)
		{
			// Attribute property Voltage
			DetectorSettings_element.setAttribute("Voltage", voltage.toString());
		}
		if (detector != null)
		{
			// *** IGNORING *** Skipped back reference DetectorRef
		}
		return super.asXMLElement(document, DetectorSettings_element);
	}
}
