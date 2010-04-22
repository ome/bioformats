/*
 * ome.xml.r201004.LightSourceSettings
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
 * Created by callan via xsd-fu on 2010-04-22 17:27:24+0100
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

public class LightSourceSettings extends Settings
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2010-04";

	// -- Instance variables --

	// Property
	private Integer wavelength;

	// Property
	private Double attenuation;

	// Property
	private String id;

	// -- Constructors --

	/** Default constructor. */
	public LightSourceSettings()
	{
		super();
	}

	/** 
	 * Constructs LightSourceSettings recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public LightSourceSettings(Element element) throws EnumerationException
	{
		update(element);
	}

	/** 
	 * Updates LightSourceSettings recursively from an XML DOM tree. <b>NOTE:</b> No
	 * properties are removed, only added or updated.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public void update(Element element) throws EnumerationException
	{	
		super.update(element);
		String tagName = element.getTagName();
		if (!"LightSourceSettings".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of LightSourceSettings got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of LightSourceSettings got %s",
			//		tagName));
		}
		if (element.hasAttribute("Wavelength"))
		{
			// Attribute property Wavelength
			setWavelength(Integer.valueOf(
					element.getAttribute("Wavelength")));
		}
		if (element.hasAttribute("Attenuation"))
		{
			// Attribute property Attenuation
			setAttenuation(Double.valueOf(
					element.getAttribute("Attenuation")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
	}

	// -- LightSourceSettings API methods --


	// Property
	public Integer getWavelength()
	{
		return wavelength;
	}

	public void setWavelength(Integer wavelength)
	{
		this.wavelength = wavelength;
	}

	// Property
	public Double getAttenuation()
	{
		return attenuation;
	}

	public void setAttenuation(Double attenuation)
	{
		this.attenuation = attenuation;
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

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element LightSourceSettings_element)
	{
		// Creating XML block for LightSourceSettings
		if (LightSourceSettings_element == null)
		{
			LightSourceSettings_element =
					document.createElementNS(NAMESPACE, "LightSourceSettings");
		}

		if (wavelength != null)
		{
			// Attribute property Wavelength
			LightSourceSettings_element.setAttribute("Wavelength", wavelength.toString());
		}
		if (attenuation != null)
		{
			// Attribute property Attenuation
			LightSourceSettings_element.setAttribute("Attenuation", attenuation.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			LightSourceSettings_element.setAttribute("ID", id.toString());
		}
		return super.asXMLElement(document, LightSourceSettings_element);
	}
}
