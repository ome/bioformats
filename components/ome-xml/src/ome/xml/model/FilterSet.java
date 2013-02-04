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

public class FilterSet extends ManufacturerSpec
{
	// Base: ManufacturerSpec -- Name: FilterSet -- Type: FilterSet -- javaBase: ManufacturerSpec -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(FilterSet.class);

	// -- Instance variables --


	// Property
	private String id;

	// Reference ExcitationFilterRef
	private List<Filter> excitationFilterLinks = new ArrayList<Filter>();

	// Property
	private Dichroic dichroic;

	// Reference EmissionFilterRef
	private List<Filter> emissionFilterLinks = new ArrayList<Filter>();

	// Back reference Channel_BackReference
	private List<Channel> channels = new ArrayList<Channel>();

	// Back reference Instrument_BackReference
	private Instrument instrument;

	// -- Constructors --

	/** Default constructor. */
	public FilterSet()
	{
		super();
	}

	/** 
	 * Constructs FilterSet recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public FilterSet(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from FilterSet specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates FilterSet recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"FilterSet".equals(tagName))
		{
			LOGGER.debug("Expecting node name of FilterSet got {}", tagName);
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"FilterSet missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		// Element reference ExcitationFilterRef
		List<Element> ExcitationFilterRef_nodeList =
				getChildrenByTagName(element, "ExcitationFilterRef");
		for (Element ExcitationFilterRef_element : ExcitationFilterRef_nodeList)
		{
			ExcitationFilterRef excitationFilterLinks_reference = new ExcitationFilterRef();
			excitationFilterLinks_reference.setID(ExcitationFilterRef_element.getAttribute("ID"));
			model.addReference(this, excitationFilterLinks_reference);
		}
		// Element reference DichroicRef
		List<Element> DichroicRef_nodeList =
				getChildrenByTagName(element, "DichroicRef");
		for (Element DichroicRef_element : DichroicRef_nodeList)
		{
			DichroicRef dichroic_reference = new DichroicRef();
			dichroic_reference.setID(DichroicRef_element.getAttribute("ID"));
			model.addReference(this, dichroic_reference);
		}
		// Element reference EmissionFilterRef
		List<Element> EmissionFilterRef_nodeList =
				getChildrenByTagName(element, "EmissionFilterRef");
		for (Element EmissionFilterRef_element : EmissionFilterRef_nodeList)
		{
			EmissionFilterRef emissionFilterLinks_reference = new EmissionFilterRef();
			emissionFilterLinks_reference.setID(EmissionFilterRef_element.getAttribute("ID"));
			model.addReference(this, emissionFilterLinks_reference);
		}
		// *** IGNORING *** Skipped back reference Channel_BackReference
		// *** IGNORING *** Skipped back reference Instrument_BackReference
	}

	// -- FilterSet API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof ExcitationFilterRef)
		{
			Filter o_casted = (Filter) o;
      o_casted.linkFilterSetExcitationFilter(this);
			if (!excitationFilterLinks.contains(o_casted)) {
				excitationFilterLinks.add(o_casted);
			}
			return true;
		}
		if (reference instanceof DichroicRef)
		{
			Dichroic o_casted = (Dichroic) o;
			o_casted.linkFilterSet(this);
			dichroic = o_casted;
			return true;
		}
		if (reference instanceof EmissionFilterRef)
		{
			Filter o_casted = (Filter) o;
      o_casted.linkFilterSetEmissionFilter(this);
			if (!emissionFilterLinks.contains(o_casted)) {
				emissionFilterLinks.add(o_casted);
			}
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
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

	// Reference which occurs more than once
	public int sizeOfLinkedExcitationFilterList()
	{
		return excitationFilterLinks.size();
	}

	public List<Filter> copyLinkedExcitationFilterList()
	{
		return new ArrayList<Filter>(excitationFilterLinks);
	}

	public Filter getLinkedExcitationFilter(int index)
	{
		return excitationFilterLinks.get(index);
	}

	public Filter setLinkedExcitationFilter(int index, Filter o)
	{
		return excitationFilterLinks.set(index, o);
	}

	public boolean linkExcitationFilter(Filter o)
	{

      o.linkFilterSetExcitationFilter(this);
		if (!excitationFilterLinks.contains(o)) {
			return excitationFilterLinks.add(o);
		}
		return false;
	}

	public boolean unlinkExcitationFilter(Filter o)
	{

      o.unlinkFilterSetExcitationFilter(this);
		return excitationFilterLinks.remove(o);
	}

	// Reference
	public Dichroic getLinkedDichroic()
	{
		return dichroic;
	}

	public void linkDichroic(Dichroic o)
	{
		dichroic = o;
	}

	public void unlinkDichroic(Dichroic o)
	{
		if (dichroic == o)
		{
			dichroic = null;
		}
	}

	// Reference which occurs more than once
	public int sizeOfLinkedEmissionFilterList()
	{
		return emissionFilterLinks.size();
	}

	public List<Filter> copyLinkedEmissionFilterList()
	{
		return new ArrayList<Filter>(emissionFilterLinks);
	}

	public Filter getLinkedEmissionFilter(int index)
	{
		return emissionFilterLinks.get(index);
	}

	public Filter setLinkedEmissionFilter(int index, Filter o)
	{
		return emissionFilterLinks.set(index, o);
	}

	public boolean linkEmissionFilter(Filter o)
	{

      o.linkFilterSetEmissionFilter(this);
		if (!emissionFilterLinks.contains(o)) {
			return emissionFilterLinks.add(o);
		}
		return false;
	}

	public boolean unlinkEmissionFilter(Filter o)
	{

      o.unlinkFilterSetEmissionFilter(this);
		return emissionFilterLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedChannelList()
	{
		return channels.size();
	}

	public List<Channel> copyLinkedChannelList()
	{
		return new ArrayList<Channel>(channels);
	}

	public Channel getLinkedChannel(int index)
	{
		return channels.get(index);
	}

	public Channel setLinkedChannel(int index, Channel o)
	{
		return channels.set(index, o);
	}

	public boolean linkChannel(Channel o)
	{
		if (!channels.contains(o)) {
			return channels.add(o);
		}
		return false;
	}

	public boolean unlinkChannel(Channel o)
	{
		return channels.remove(o);
	}

	// Property
	public Instrument getInstrument()
	{
		return instrument;
	}

	public void setInstrument(Instrument instrument_BackReference)
	{
		this.instrument = instrument_BackReference;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element FilterSet_element)
	{
		// Creating XML block for FilterSet

		if (FilterSet_element == null)
		{
			FilterSet_element =
					document.createElementNS(NAMESPACE, "FilterSet");
		}

		if (id != null)
		{
			// Attribute property ID
			FilterSet_element.setAttribute("ID", id.toString());
		}
		if (excitationFilterLinks != null)
		{
			// Reference property ExcitationFilterRef which occurs more than once
			for (Filter excitationFilterLinks_value : excitationFilterLinks)
			{
				ExcitationFilterRef o = new ExcitationFilterRef();
				o.setID(excitationFilterLinks_value.getID());
				FilterSet_element.appendChild(o.asXMLElement(document));
			}
		}
		if (dichroic != null)
		{
			// Reference property DichroicRef
			DichroicRef o = new DichroicRef();
			o.setID(dichroic.getID());
			FilterSet_element.appendChild(o.asXMLElement(document));
		}
		if (emissionFilterLinks != null)
		{
			// Reference property EmissionFilterRef which occurs more than once
			for (Filter emissionFilterLinks_value : emissionFilterLinks)
			{
				EmissionFilterRef o = new EmissionFilterRef();
				o.setID(emissionFilterLinks_value.getID());
				FilterSet_element.appendChild(o.asXMLElement(document));
			}
		}
		if (channels != null)
		{
			// *** IGNORING *** Skipped back reference Channel_BackReference
		}
		if (instrument != null)
		{
			// *** IGNORING *** Skipped back reference Instrument_BackReference
		}
		return super.asXMLElement(document, FilterSet_element);
	}
}
