/*
 * ome.xml.r201004.FilterSet
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2010 Open Microscopy Environment
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
 * Created by callan via xsd-fu on 2010-04-19 19:23:58+0100
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r201004;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ome.xml.r201004.enums.*;

public class FilterSet extends ManufacturerSpec
{
	// -- Instance variables --

	// Property
	private String id;

	// Property which occurs more than once
	private List<Filter> excitationFilterList = new ArrayList<Filter>();

	// Property
	private Dichroic dichroic;

	// Property which occurs more than once
	private List<Filter> emissionFilterList = new ArrayList<Filter>();

	// Back reference Channel_BackReference
	private List<Channel> channel_BackReferenceList = new ArrayList<Channel>();

	// Back reference OTF_BackReference
	private List<OTF> otf_backReferenceList = new ArrayList<OTF>();

	// Back reference ChannelProfile_BackReference
	private List<ChannelProfile> channelProfile_BackReferenceList = new ArrayList<ChannelProfile>();

	// -- Constructors --

	/** Constructs a FilterSet. */
	public FilterSet()
	{
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

	// Property which occurs more than once
	public int sizeOfExcitationFilterList()
	{
		return excitationFilterList.size();
	}

	public List<Filter> copyExcitationFilterList()
	{
		return new ArrayList<Filter>(excitationFilterList);
	}

	public Filter getExcitationFilter(int index)
	{
		return excitationFilterList.get(index);
	}

	public Filter setExcitationFilter(int index, Filter excitationFilter)
	{
		return excitationFilterList.set(index, excitationFilter);
	}

	public void addExcitationFilter(Filter excitationFilter)
	{
		excitationFilterList.add(excitationFilter);
	}

	public void removeExcitationFilter(Filter excitationFilter)
	{
		excitationFilterList.remove(excitationFilter);
	}

	// Property
	public Dichroic getDichroic()
	{
		return dichroic;
	}

	public void setDichroic(Dichroic dichroic)
	{
		this.dichroic = dichroic;
	}

	// Property which occurs more than once
	public int sizeOfEmissionFilterList()
	{
		return emissionFilterList.size();
	}

	public List<Filter> copyEmissionFilterList()
	{
		return new ArrayList<Filter>(emissionFilterList);
	}

	public Filter getEmissionFilter(int index)
	{
		return emissionFilterList.get(index);
	}

	public Filter setEmissionFilter(int index, Filter emissionFilter)
	{
		return emissionFilterList.set(index, emissionFilter);
	}

	public void addEmissionFilter(Filter emissionFilter)
	{
		emissionFilterList.add(emissionFilter);
	}

	public void removeEmissionFilter(Filter emissionFilter)
	{
		emissionFilterList.remove(emissionFilter);
	}

	// Back reference Channel_BackReference
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

	public Channel setLinkedChannel(int index, Channel channel_BackReference)
	{
		return channel_BackReferenceList.set(index, channel_BackReference);
	}

	public void linkChannel(Channel channel_BackReference)
	{
		this.channel_BackReferenceList.add(channel_BackReference);
	}

	public void unlinkChannel(Channel channel_BackReference)
	{
		this.channel_BackReferenceList.add(channel_BackReference);
	}

	// Back reference OTF_BackReference
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

	public OTF setLinkedOTF(int index, OTF otf_backReference)
	{
		return otf_backReferenceList.set(index, otf_backReference);
	}

	public void linkOTF(OTF otf_backReference)
	{
		this.otf_backReferenceList.add(otf_backReference);
	}

	public void unlinkOTF(OTF otf_backReference)
	{
		this.otf_backReferenceList.add(otf_backReference);
	}

	// Back reference ChannelProfile_BackReference
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

	public ChannelProfile setLinkedChannelProfile(int index, ChannelProfile channelProfile_BackReference)
	{
		return channelProfile_BackReferenceList.set(index, channelProfile_BackReference);
	}

	public void linkChannelProfile(ChannelProfile channelProfile_BackReference)
	{
		this.channelProfile_BackReferenceList.add(channelProfile_BackReference);
	}

	public void unlinkChannelProfile(ChannelProfile channelProfile_BackReference)
	{
		this.channelProfile_BackReferenceList.add(channelProfile_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for FilterSet
		Element FilterSet_element = document.createElement("FilterSet");
		if (id != null)
		{
			// Attribute property ID
			FilterSet_element.setAttribute("ID", id.toString());
		}
		if (excitationFilterList != null)
		{
			// Element property ExcitationFilterRef which is complex (has
			// sub-elements) and occurs more than once
			for (Filter excitationFilterList_value : excitationFilterList)
			{
				FilterSet_element.appendChild(excitationFilterList_value.asXMLElement(document));
			}
		}
		if (dichroic != null)
		{
			// Element property DichroicRef which is complex (has
			// sub-elements)
			FilterSet_element.appendChild(dichroic.asXMLElement(document));
		}
		if (emissionFilterList != null)
		{
			// Element property EmissionFilterRef which is complex (has
			// sub-elements) and occurs more than once
			for (Filter emissionFilterList_value : emissionFilterList)
			{
				FilterSet_element.appendChild(emissionFilterList_value.asXMLElement(document));
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
		return FilterSet_element;
	}
}
