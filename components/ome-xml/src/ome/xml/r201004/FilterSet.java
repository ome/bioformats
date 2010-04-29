/*
 * ome.xml.r201004.FilterSet
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
 * Created by callan via xsd-fu on 2010-04-29 09:45:43+0100
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
import ome.xml.r201004.primitives.*;

public class FilterSet extends ManufacturerSpec
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2010-04";

	// -- Instance variables --

	// Property
	private String id;

	// Reference ExcitationFilterRef
	private List<Filter> excitationFilterList = new ArrayList<Filter>();

	// Property
	private Dichroic dichroic;

	// Reference EmissionFilterRef
	private List<Filter> emissionFilterList = new ArrayList<Filter>();

	// Back reference Channel_BackReference
	private List<Channel> channel_BackReferenceList = new ArrayList<Channel>();

	// Back reference OTF_BackReference
	private List<OTF> otf_backReferenceList = new ArrayList<OTF>();

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
			System.err.println(String.format(
					"WARNING: Expecting node name of FilterSet got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of FilterSet got %s",
			//		tagName));
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
			ExcitationFilterRef excitationFilterList_reference = new ExcitationFilterRef();
			excitationFilterList_reference.setID(ExcitationFilterRef_element.getAttribute("ID"));
			model.addReference(this, excitationFilterList_reference);
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
			EmissionFilterRef emissionFilterList_reference = new EmissionFilterRef();
			emissionFilterList_reference.setID(EmissionFilterRef_element.getAttribute("ID"));
			model.addReference(this, emissionFilterList_reference);
		}
		// *** IGNORING *** Skipped back reference Channel_BackReference
		// *** IGNORING *** Skipped back reference OTF_BackReference
	}

	// -- FilterSet API methods --

	public void link(Reference reference, OMEModelObject o)
	{
		if (reference instanceof ExcitationFilterRef)
		{
			Filter o_casted = (Filter) o;
			o_casted.linkFilterSet(this);
			excitationFilterList.add(o_casted);
			return;
		}
		if (reference instanceof DichroicRef)
		{
			Dichroic o_casted = (Dichroic) o;
			o_casted.linkFilterSet(this);
			dichroic = o_casted;
			return;
		}
		if (reference instanceof EmissionFilterRef)
		{
			Filter o_casted = (Filter) o;
			o_casted.linkFilterSet(this);
			emissionFilterList.add(o_casted);
			return;
		}
		// TODO: Should be its own Exception
		throw new RuntimeException(
				"Unable to handle reference of type: " + reference.getClass());
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
		return excitationFilterList.size();
	}

	public List<Filter> copyLinkedExcitationFilterList()
	{
		return new ArrayList<Filter>(excitationFilterList);
	}

	public Filter getLinkedExcitationFilter(int index)
	{
		return excitationFilterList.get(index);
	}

	public Filter setLinkedExcitationFilter(int index, Filter o)
	{
		return excitationFilterList.set(index, o);
	}

	public boolean linkExcitationFilter(Filter o)
	{
		o.linkFilterSet(this);
		return excitationFilterList.add(o);
	}

	public boolean unlinkExcitationFilter(Filter o)
	{
		o.unlinkFilterSet(this);
		return excitationFilterList.remove(o);
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
		return emissionFilterList.size();
	}

	public List<Filter> copyLinkedEmissionFilterList()
	{
		return new ArrayList<Filter>(emissionFilterList);
	}

	public Filter getLinkedEmissionFilter(int index)
	{
		return emissionFilterList.get(index);
	}

	public Filter setLinkedEmissionFilter(int index, Filter o)
	{
		return emissionFilterList.set(index, o);
	}

	public boolean linkEmissionFilter(Filter o)
	{
		o.linkFilterSet(this);
		return emissionFilterList.add(o);
	}

	public boolean unlinkEmissionFilter(Filter o)
	{
		o.unlinkFilterSet(this);
		return emissionFilterList.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedChannelList()
	{
		return channel_BackReferenceList.size();
	}

	public List<Channel> copyLinkedChannelList()
	{
		return new ArrayList<Channel>(channel_BackReferenceList);
	}

	public Channel getLinkedChannel(int index)
	{
		return channel_BackReferenceList.get(index);
	}

	public Channel setLinkedChannel(int index, Channel o)
	{
		return channel_BackReferenceList.set(index, o);
	}

	public boolean linkChannel(Channel o)
	{
		return channel_BackReferenceList.add(o);
	}

	public boolean unlinkChannel(Channel o)
	{
		return channel_BackReferenceList.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedOTFList()
	{
		return otf_backReferenceList.size();
	}

	public List<OTF> copyLinkedOTFList()
	{
		return new ArrayList<OTF>(otf_backReferenceList);
	}

	public OTF getLinkedOTF(int index)
	{
		return otf_backReferenceList.get(index);
	}

	public OTF setLinkedOTF(int index, OTF o)
	{
		return otf_backReferenceList.set(index, o);
	}

	public boolean linkOTF(OTF o)
	{
		return otf_backReferenceList.add(o);
	}

	public boolean unlinkOTF(OTF o)
	{
		return otf_backReferenceList.remove(o);
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
		if (excitationFilterList != null)
		{
			// Reference property ExcitationFilterRef which occurs more than once
			for (Filter excitationFilterList_value : excitationFilterList)
			{
				ExcitationFilterRef o = new ExcitationFilterRef();
				o.setID(excitationFilterList_value.getID());
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
		if (emissionFilterList != null)
		{
			// Reference property EmissionFilterRef which occurs more than once
			for (Filter emissionFilterList_value : emissionFilterList)
			{
				EmissionFilterRef o = new EmissionFilterRef();
				o.setID(emissionFilterList_value.getID());
				FilterSet_element.appendChild(o.asXMLElement(document));
			}
		}
		if (channel_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Channel_BackReference
		}
		if (otf_backReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference OTF_BackReference
		}
		return super.asXMLElement(document, FilterSet_element);
	}
}
