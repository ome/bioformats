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
 * Created by callan via xsd-fu on 2010-04-22 17:37:18+0100
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

	// Back reference ChannelProfile_BackReference
	private List<ChannelProfile> channelProfile_BackReferenceList = new ArrayList<ChannelProfile>();

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
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public FilterSet(Element element) throws EnumerationException
	{
		update(element);
	}

	/** 
	 * Updates FilterSet recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
		// *** IGNORING *** Skipped back reference ExcitationFilterRef
		// *** IGNORING *** Skipped back reference DichroicRef
		// *** IGNORING *** Skipped back reference EmissionFilterRef
		// *** IGNORING *** Skipped back reference Channel_BackReference
		// *** IGNORING *** Skipped back reference OTF_BackReference
		// *** IGNORING *** Skipped back reference ChannelProfile_BackReference
	}

	// -- FilterSet API methods --


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

	// Reference which occurs more than once
	public int sizeOfLinkedChannelProfileList()
	{
		return channelProfile_BackReferenceList.size();
	}

	public List<ChannelProfile> copyLinkedChannelProfileList()
	{
		return new ArrayList<ChannelProfile>(channelProfile_BackReferenceList);
	}

	public ChannelProfile getLinkedChannelProfile(int index)
	{
		return channelProfile_BackReferenceList.get(index);
	}

	public ChannelProfile setLinkedChannelProfile(int index, ChannelProfile o)
	{
		return channelProfile_BackReferenceList.set(index, o);
	}

	public boolean linkChannelProfile(ChannelProfile o)
	{
		return channelProfile_BackReferenceList.add(o);
	}

	public boolean unlinkChannelProfile(ChannelProfile o)
	{
		return channelProfile_BackReferenceList.remove(o);
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
				FilterRef o = new FilterRef();
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
				FilterRef o = new FilterRef();
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
		if (channelProfile_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference ChannelProfile_BackReference
		}
		return super.asXMLElement(document, FilterSet_element);
	}
}
