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

public class Dichroic extends ManufacturerSpec
{
	// Base: ManufacturerSpec -- Name: Dichroic -- Type: Dichroic -- modelBaseType: ManufacturerSpec -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Dichroic.class);

	// -- Instance variables --

	// ID property
	private String id;

	// AnnotationRef reference (occurs more than once)
	private List<Annotation> annotationLinks = new ReferenceList<Annotation>();

	// Instrument_BackReference back reference
	private Instrument instrument;

	// FilterSet_BackReference back reference (occurs more than once)
	private List<FilterSet> filterSets = new ReferenceList<FilterSet>();

	// LightPath_BackReference back reference (occurs more than once)
	private List<LightPath> lightPaths = new ReferenceList<LightPath>();

	// -- Constructors --

	/** Default constructor. */
	public Dichroic()
	{
		super();
	}

	/**
	 * Constructs Dichroic recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Dichroic(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public Dichroic(Dichroic orig)
	{
		super(orig);
		id = orig.id;
		annotationLinks = orig.annotationLinks;
		instrument = orig.instrument;
		filterSets = orig.filterSets;
		lightPaths = orig.lightPaths;
	}

	// -- Custom content from Dichroic specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates Dichroic recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Dichroic".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Dichroic got {}", tagName);
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Dichroic missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
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

	// -- Dichroic API methods --

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
			o_casted.linkDichroic(this);
			annotationLinks.add(o_casted);
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
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

			o.linkDichroic(this);
		return annotationLinks.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkDichroic(this);
		return annotationLinks.remove(o);
	}

	// Property Instrument_BackReference
	public Instrument getInstrument()
	{
		return instrument;
	}

	public void setInstrument(Instrument instrument_BackReference)
	{
		this.instrument = instrument_BackReference;
	}

	// Reference which occurs more than once
	public int sizeOfLinkedFilterSetList()
	{
		return filterSets.size();
	}

	public List<FilterSet> copyLinkedFilterSetList()
	{
		return new ArrayList<FilterSet>(filterSets);
	}

	public FilterSet getLinkedFilterSet(int index)
	{
		return filterSets.get(index);
	}

	public FilterSet setLinkedFilterSet(int index, FilterSet o)
	{
		return filterSets.set(index, o);
	}

	public boolean linkFilterSet(FilterSet o)
	{
		return filterSets.add(o);
	}

	public boolean unlinkFilterSet(FilterSet o)
	{
		return filterSets.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedLightPathList()
	{
		return lightPaths.size();
	}

	public List<LightPath> copyLinkedLightPathList()
	{
		return new ArrayList<LightPath>(lightPaths);
	}

	public LightPath getLinkedLightPath(int index)
	{
		return lightPaths.get(index);
	}

	public LightPath setLinkedLightPath(int index, LightPath o)
	{
		return lightPaths.set(index, o);
	}

	public boolean linkLightPath(LightPath o)
	{
		return lightPaths.add(o);
	}

	public boolean unlinkLightPath(LightPath o)
	{
		return lightPaths.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Dichroic_element)
	{
		// Creating XML block for Dichroic

		if (Dichroic_element == null)
		{
			Dichroic_element =
					document.createElementNS(NAMESPACE, "Dichroic");
		}


		if (id != null)
		{
			// Attribute property ID
			Dichroic_element.setAttribute("ID", id.toString());
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Dichroic_element.appendChild(o.asXMLElement(document));
			}
		}
		if (instrument != null)
		{
			// *** IGNORING *** Skipped back reference Instrument_BackReference
		}
		if (filterSets != null)
		{
			// *** IGNORING *** Skipped back reference FilterSet_BackReference
		}
		if (lightPaths != null)
		{
			// *** IGNORING *** Skipped back reference LightPath_BackReference
		}

		return super.asXMLElement(document, Dichroic_element);
	}
}
