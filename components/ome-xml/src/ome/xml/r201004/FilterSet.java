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
 * Created by callan via xsd-fu on 2010-04-21 15:20:31+0100
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
	// -- Instance variables --

	// Property
	private String id;

	// Back reference ExcitationFilterRef
	private List<Filter> excitationFilterList = new ArrayList<Filter>();

	// Back reference DichroicRef
	private List<Dichroic> dichroic = new ArrayList<Dichroic>();

	// Back reference EmissionFilterRef
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

	// Reference ExcitationFilterRef
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

	public void linkExcitationFilter(Filter o)
	{
		this.excitationFilterList.add(o);
	}

	public void unlinkExcitationFilter(Filter o)
	{
		this.excitationFilterList.add(o);
	}

	// Reference DichroicRef
	public int sizeOfLinkedDichroicList()
	{
		return dichroic.size();
	}

	public List<Dichroic> copyLinkedDichroicList()
	{
		return new ArrayList<Dichroic>(dichroic);
	}

	public Dichroic getLinkedDichroic(int index)
	{
		return dichroic.get(index);
	}

	public Dichroic setLinkedDichroic(int index, Dichroic o)
	{
		return dichroic.set(index, o);
	}

	public void linkDichroic(Dichroic o)
	{
		this.dichroic.add(o);
	}

	public void unlinkDichroic(Dichroic o)
	{
		this.dichroic.add(o);
	}

	// Reference EmissionFilterRef
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

	public void linkEmissionFilter(Filter o)
	{
		this.emissionFilterList.add(o);
	}

	public void unlinkEmissionFilter(Filter o)
	{
		this.emissionFilterList.add(o);
	}

	// Property which occurs more than once
	public int sizeOfChannelList()
	{
		return channel_BackReferenceList.size();
	}

	public List<Channel> copyChannelList()
	{
		return new ArrayList<Channel>(channel_BackReferenceList);
	}

	public Channel getChannel(int index)
	{
		return channel_BackReferenceList.get(index);
	}

	public Channel setChannel(int index, Channel channel_BackReference)
	{
		return channel_BackReferenceList.set(index, channel_BackReference);
	}

	public void addChannel(Channel channel_BackReference)
	{
		channel_BackReferenceList.add(channel_BackReference);
	}

	public void removeChannel(Channel channel_BackReference)
	{
		channel_BackReferenceList.remove(channel_BackReference);
	}

	// Property which occurs more than once
	public int sizeOfOTFList()
	{
		return otf_backReferenceList.size();
	}

	public List<OTF> copyOTFList()
	{
		return new ArrayList<OTF>(otf_backReferenceList);
	}

	public OTF getOTF(int index)
	{
		return otf_backReferenceList.get(index);
	}

	public OTF setOTF(int index, OTF otf_backReference)
	{
		return otf_backReferenceList.set(index, otf_backReference);
	}

	public void addOTF(OTF otf_backReference)
	{
		otf_backReferenceList.add(otf_backReference);
	}

	public void removeOTF(OTF otf_backReference)
	{
		otf_backReferenceList.remove(otf_backReference);
	}

	// Property which occurs more than once
	public int sizeOfChannelProfileList()
	{
		return channelProfile_BackReferenceList.size();
	}

	public List<ChannelProfile> copyChannelProfileList()
	{
		return new ArrayList<ChannelProfile>(channelProfile_BackReferenceList);
	}

	public ChannelProfile getChannelProfile(int index)
	{
		return channelProfile_BackReferenceList.get(index);
	}

	public ChannelProfile setChannelProfile(int index, ChannelProfile channelProfile_BackReference)
	{
		return channelProfile_BackReferenceList.set(index, channelProfile_BackReference);
	}

	public void addChannelProfile(ChannelProfile channelProfile_BackReference)
	{
		channelProfile_BackReferenceList.add(channelProfile_BackReference);
	}

	public void removeChannelProfile(ChannelProfile channelProfile_BackReference)
	{
		channelProfile_BackReferenceList.remove(channelProfile_BackReference);
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
			FilterSet_element = document.createElement("FilterSet");
		}

		if (id != null)
		{
			// Attribute property ID
			FilterSet_element.setAttribute("ID", id.toString());
		}
		if (excitationFilterList != null)
		{
			// *** IGNORING *** Skipped back reference ExcitationFilterRef
		}
		if (dichroic != null)
		{
			// *** IGNORING *** Skipped back reference DichroicRef
		}
		if (emissionFilterList != null)
		{
			// *** IGNORING *** Skipped back reference EmissionFilterRef
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
