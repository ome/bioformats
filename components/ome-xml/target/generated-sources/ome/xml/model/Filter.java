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

public class Filter extends ManufacturerSpec
{
	// Base: ManufacturerSpec -- Name: Filter -- Type: Filter -- modelBaseType: ManufacturerSpec -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Filter.class);

	// -- Instance variables --

	// FilterWheel property
	private String filterWheel;

	// Type property
	private FilterType type;

	// ID property
	private String id;

	// TransmittanceRange property
	private TransmittanceRange transmittanceRange;

	// AnnotationRef reference (occurs more than once)
	private List<Annotation> annotationLinks = new ReferenceList<Annotation>();

	// Instrument_BackReference back reference
	private Instrument instrument;

	// FilterSet_BackReference back reference (occurs more than once)
	private List<FilterSet> filterSetExcitationFilterLinks = new ReferenceList<FilterSet>();

	// FilterSet_BackReference back reference (occurs more than once)
	private List<FilterSet> filterSetEmissionFilterLinks = new ReferenceList<FilterSet>();

	// LightPath_BackReference back reference (occurs more than once)
	private List<LightPath> lightPathExcitationFilterLinks = new ReferenceList<LightPath>();

	// LightPath_BackReference back reference (occurs more than once)
	private List<LightPath> lightPathEmissionFilterLinks = new ReferenceList<LightPath>();

	// -- Constructors --

	/** Default constructor. */
	public Filter()
	{
		super();
	}

	/**
	 * Constructs Filter recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Filter(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public Filter(Filter orig)
	{
		super(orig);
		filterWheel = orig.filterWheel;
		type = orig.type;
		id = orig.id;
		transmittanceRange = orig.transmittanceRange;
		annotationLinks = orig.annotationLinks;
		instrument = orig.instrument;
		filterSetExcitationFilterLinks = orig.filterSetExcitationFilterLinks;
		filterSetEmissionFilterLinks = orig.filterSetEmissionFilterLinks;
		lightPathExcitationFilterLinks = orig.lightPathExcitationFilterLinks;
		lightPathEmissionFilterLinks = orig.lightPathEmissionFilterLinks;
	}

	// -- Custom content from Filter specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates Filter recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Filter".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Filter got {}", tagName);
		}
		if (element.hasAttribute("FilterWheel"))
		{
			// Attribute property FilterWheel
			setFilterWheel(String.valueOf(
					element.getAttribute("FilterWheel")));
		}
		if (element.hasAttribute("Type"))
		{
			// Attribute property which is an enumeration Type
			setType(FilterType.fromString(
					element.getAttribute("Type")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Filter missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		List<Element> TransmittanceRange_nodeList =
				getChildrenByTagName(element, "TransmittanceRange");
		if (TransmittanceRange_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"TransmittanceRange node list size %d != 1",
					TransmittanceRange_nodeList.size()));
		}
		else if (TransmittanceRange_nodeList.size() != 0)
		{
			// Element property TransmittanceRange which is complex (has
			// sub-elements)
			setTransmittanceRange(new TransmittanceRange(
					(Element) TransmittanceRange_nodeList.get(0), model));
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

	// -- Filter API methods --

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
			o_casted.linkFilter(this);
			annotationLinks.add(o_casted);
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property FilterWheel
	public String getFilterWheel()
	{
		return filterWheel;
	}

	public void setFilterWheel(String filterWheel)
	{
		this.filterWheel = filterWheel;
	}

	// Property Type
	public FilterType getType()
	{
		return type;
	}

	public void setType(FilterType type)
	{
		this.type = type;
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

	// Property TransmittanceRange
	public TransmittanceRange getTransmittanceRange()
	{
		return transmittanceRange;
	}

	public void setTransmittanceRange(TransmittanceRange transmittanceRange)
	{
		this.transmittanceRange = transmittanceRange;
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

			o.linkFilter(this);
		return annotationLinks.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkFilter(this);
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
	public int sizeOfLinkedFilterSetExcitationFilterList()
	{
		return filterSetExcitationFilterLinks.size();
	}

	public List<FilterSet> copyLinkedFilterSetExcitationFilterList()
	{
		return new ArrayList<FilterSet>(filterSetExcitationFilterLinks);
	}

	public FilterSet getLinkedFilterSetExcitationFilter(int index)
	{
		return filterSetExcitationFilterLinks.get(index);
	}

	public FilterSet setLinkedFilterSetExcitationFilter(int index, FilterSet o)
	{
		return filterSetExcitationFilterLinks.set(index, o);
	}

	public boolean linkFilterSetExcitationFilter(FilterSet o)
	{
		return filterSetExcitationFilterLinks.add(o);
	}

	public boolean unlinkFilterSetExcitationFilter(FilterSet o)
	{
		return filterSetExcitationFilterLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedFilterSetEmissionFilterList()
	{
		return filterSetEmissionFilterLinks.size();
	}

	public List<FilterSet> copyLinkedFilterSetEmissionFilterList()
	{
		return new ArrayList<FilterSet>(filterSetEmissionFilterLinks);
	}

	public FilterSet getLinkedFilterSetEmissionFilter(int index)
	{
		return filterSetEmissionFilterLinks.get(index);
	}

	public FilterSet setLinkedFilterSetEmissionFilter(int index, FilterSet o)
	{
		return filterSetEmissionFilterLinks.set(index, o);
	}

	public boolean linkFilterSetEmissionFilter(FilterSet o)
	{
		return filterSetEmissionFilterLinks.add(o);
	}

	public boolean unlinkFilterSetEmissionFilter(FilterSet o)
	{
		return filterSetEmissionFilterLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedLightPathExcitationFilterList()
	{
		return lightPathExcitationFilterLinks.size();
	}

	public List<LightPath> copyLinkedLightPathExcitationFilterList()
	{
		return new ArrayList<LightPath>(lightPathExcitationFilterLinks);
	}

	public LightPath getLinkedLightPathExcitationFilter(int index)
	{
		return lightPathExcitationFilterLinks.get(index);
	}

	public LightPath setLinkedLightPathExcitationFilter(int index, LightPath o)
	{
		return lightPathExcitationFilterLinks.set(index, o);
	}

	public boolean linkLightPathExcitationFilter(LightPath o)
	{
		return lightPathExcitationFilterLinks.add(o);
	}

	public boolean unlinkLightPathExcitationFilter(LightPath o)
	{
		return lightPathExcitationFilterLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedLightPathEmissionFilterList()
	{
		return lightPathEmissionFilterLinks.size();
	}

	public List<LightPath> copyLinkedLightPathEmissionFilterList()
	{
		return new ArrayList<LightPath>(lightPathEmissionFilterLinks);
	}

	public LightPath getLinkedLightPathEmissionFilter(int index)
	{
		return lightPathEmissionFilterLinks.get(index);
	}

	public LightPath setLinkedLightPathEmissionFilter(int index, LightPath o)
	{
		return lightPathEmissionFilterLinks.set(index, o);
	}

	public boolean linkLightPathEmissionFilter(LightPath o)
	{
		return lightPathEmissionFilterLinks.add(o);
	}

	public boolean unlinkLightPathEmissionFilter(LightPath o)
	{
		return lightPathEmissionFilterLinks.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Filter_element)
	{
		// Creating XML block for Filter

		if (Filter_element == null)
		{
			Filter_element =
					document.createElementNS(NAMESPACE, "Filter");
		}


		if (filterWheel != null)
		{
			// Attribute property FilterWheel
			Filter_element.setAttribute("FilterWheel", filterWheel.toString());
		}
		if (type != null)
		{
			// Attribute property Type
			Filter_element.setAttribute("Type", type.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Filter_element.setAttribute("ID", id.toString());
		}
		if (transmittanceRange != null)
		{
			// Element property TransmittanceRange which is complex (has
			// sub-elements)
			Filter_element.appendChild(transmittanceRange.asXMLElement(document));
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Filter_element.appendChild(o.asXMLElement(document));
			}
		}
		if (instrument != null)
		{
			// *** IGNORING *** Skipped back reference Instrument_BackReference
		}
		if (filterSetExcitationFilterLinks != null)
		{
			// *** IGNORING *** Skipped back reference FilterSet_BackReference
		}
		if (filterSetEmissionFilterLinks != null)
		{
			// *** IGNORING *** Skipped back reference FilterSet_BackReference
		}
		if (lightPathExcitationFilterLinks != null)
		{
			// *** IGNORING *** Skipped back reference LightPath_BackReference
		}
		if (lightPathEmissionFilterLinks != null)
		{
			// *** IGNORING *** Skipped back reference LightPath_BackReference
		}

		return super.asXMLElement(document, Filter_element);
	}
}
