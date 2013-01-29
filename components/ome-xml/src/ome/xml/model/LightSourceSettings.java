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

public class LightSourceSettings extends Settings
{
	// Base: Settings -- Name: LightSourceSettings -- Type: LightSourceSettings -- javaBase: Settings -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(LightSourceSettings.class);

	// -- Instance variables --


	// Property
	private PositiveInteger wavelength;

	// Property
	private PercentFraction attenuation;

	// Property
	private String id;

	// Back reference LightSourceRef
	private LightSource lightSource;

	// Back reference MicrobeamManipulation_BackReference
	private MicrobeamManipulation microbeamManipulation;

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
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public LightSourceSettings(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from LightSourceSettings specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates LightSourceSettings recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"LightSourceSettings".equals(tagName))
		{
			LOGGER.debug("Expecting node name of LightSourceSettings got {}", tagName);
		}
		if (element.hasAttribute("Wavelength"))
		{
			// Attribute property Wavelength
			setWavelength(PositiveInteger.valueOf(
					element.getAttribute("Wavelength")));
		}
		if (element.hasAttribute("Attenuation"))
		{
			// Attribute property Attenuation
			setAttenuation(PercentFraction.valueOf(
					element.getAttribute("Attenuation")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"LightSourceSettings missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		// *** IGNORING *** Skipped back reference LightSourceRef
		// *** IGNORING *** Skipped back reference MicrobeamManipulation_BackReference
	}

	// -- LightSourceSettings API methods --

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
	public PositiveInteger getWavelength()
	{
		return wavelength;
	}

	public void setWavelength(PositiveInteger wavelength)
	{
		this.wavelength = wavelength;
	}

	// Property
	public PercentFraction getAttenuation()
	{
		return attenuation;
	}

	public void setAttenuation(PercentFraction attenuation)
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

	// Property
	public LightSource getLightSource()
	{
		return lightSource;
	}

	public void setLightSource(LightSource lightSource)
	{
		this.lightSource = lightSource;
	}

	// Property
	public MicrobeamManipulation getMicrobeamManipulation()
	{
		return microbeamManipulation;
	}

	public void setMicrobeamManipulation(MicrobeamManipulation microbeamManipulation_BackReference)
	{
		this.microbeamManipulation = microbeamManipulation_BackReference;
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
		if (lightSource != null)
		{
			// *** IGNORING *** Skipped back reference LightSourceRef
		}
		if (microbeamManipulation != null)
		{
			// *** IGNORING *** Skipped back reference MicrobeamManipulation_BackReference
		}
		return super.asXMLElement(document, LightSourceSettings_element);
	}
}
