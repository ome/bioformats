/*
 * ome.xml.r201004.OTF
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
 * Created by callan via xsd-fu on 2010-04-20 12:31:20+0100
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

public class OTF extends Object
{
	// -- Instance variables --

	// Property
	private Integer sizeX;

	// Property
	private Integer sizeY;

	// Property
	private PixelType type;

	// Property
	private String id;

	// Property
	private Boolean opticalAxisAveraged;

	// Property
	private ObjectiveSettings objectiveSettings;

	// Property
	private FilterSet filterSet;

	// Property
	private String binaryFile;

	// Back reference Channel_BackReference
	private List<Channel> channel_BackReferenceList = new ArrayList<Channel>();

	// Back reference ChannelProfile_BackReference
	private List<ChannelProfile> channelProfile_BackReferenceList = new ArrayList<ChannelProfile>();

	// -- Constructors --

	/** Constructs a OTF. */
	public OTF()
	{
	}

	// -- OTF API methods --

	// Property
	public Integer getSizeX()
	{
		return sizeX;
	}

	public void setSizeX(Integer sizeX)
	{
		this.sizeX = sizeX;
	}

	// Property
	public Integer getSizeY()
	{
		return sizeY;
	}

	public void setSizeY(Integer sizeY)
	{
		this.sizeY = sizeY;
	}

	// Property
	public PixelType getType()
	{
		return type;
	}

	public void setType(PixelType type)
	{
		this.type = type;
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
	public Boolean getOpticalAxisAveraged()
	{
		return opticalAxisAveraged;
	}

	public void setOpticalAxisAveraged(Boolean opticalAxisAveraged)
	{
		this.opticalAxisAveraged = opticalAxisAveraged;
	}

	// Property
	public ObjectiveSettings getObjectiveSettings()
	{
		return objectiveSettings;
	}

	public void setObjectiveSettings(ObjectiveSettings objectiveSettings)
	{
		this.objectiveSettings = objectiveSettings;
	}

	// Property
	public FilterSet getFilterSet()
	{
		return filterSet;
	}

	public void setFilterSet(FilterSet filterSet)
	{
		this.filterSet = filterSet;
	}

	// Property
	public String getBinaryFile()
	{
		return binaryFile;
	}

	public void setBinaryFile(String binaryFile)
	{
		this.binaryFile = binaryFile;
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
		// Creating XML block for OTF
		Element OTF_element = document.createElement("OTF");
		if (sizeX != null)
		{
			// Attribute property SizeX
			OTF_element.setAttribute("SizeX", sizeX.toString());
		}
		if (sizeY != null)
		{
			// Attribute property SizeY
			OTF_element.setAttribute("SizeY", sizeY.toString());
		}
		if (type != null)
		{
			// Attribute property Type
			OTF_element.setAttribute("Type", type.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			OTF_element.setAttribute("ID", id.toString());
		}
		if (opticalAxisAveraged != null)
		{
			// Attribute property OpticalAxisAveraged
			OTF_element.setAttribute("OpticalAxisAveraged", opticalAxisAveraged.toString());
		}
		if (objectiveSettings != null)
		{
			// Element property ObjectiveSettings which is complex (has
			// sub-elements)
			OTF_element.appendChild(objectiveSettings.asXMLElement(document));
		}
		if (filterSet != null)
		{
			// Element property FilterSetRef which is complex (has
			// sub-elements)
			OTF_element.appendChild(filterSet.asXMLElement(document));
		}
		if (binaryFile != null)
		{
			// Element property BinaryFile which is not complex (has no
			// sub-elements)
			Element binaryFile_element = document.createElement("BinaryFile");
			binaryFile_element.setTextContent(binaryFile);
			OTF_element.appendChild(binaryFile_element);
		}
		if (channel_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Channel_BackReference
		}
		if (channelProfile_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference ChannelProfile_BackReference
		}
		return OTF_element;
	}

	public static OTF fromXMLElement(Element element)
		throws EnumerationException
	{
		String tagName = element.getTagName();
		if (!"OTF".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of OTF got %s",
					tagName));
		}
		OTF instance = new OTF();
		if (element.hasAttribute("SizeX"))
		{
			// Attribute property SizeX
			instance.setSizeX(Integer.valueOf(
					element.getAttribute("SizeX")));
		}
		if (element.hasAttribute("SizeY"))
		{
			// Attribute property SizeY
			instance.setSizeY(Integer.valueOf(
					element.getAttribute("SizeY")));
		}
		if (element.hasAttribute("Type"))
		{
			// Attribute property which is an enumeration Type
			instance.setType(PixelType.fromString(
					element.getAttribute("Type")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			instance.setID(String.valueOf(
					element.getAttribute("ID")));
		}
		if (element.hasAttribute("OpticalAxisAveraged"))
		{
			// Attribute property OpticalAxisAveraged
			instance.setOpticalAxisAveraged(Boolean.valueOf(
					element.getAttribute("OpticalAxisAveraged")));
		}
		NodeList ObjectiveSettings_nodeList = element.getElementsByTagName("ObjectiveSettings");
		if (ObjectiveSettings_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"ObjectiveSettings node list size %d != 1",
					ObjectiveSettings_nodeList.getLength()));
		}
		else if (ObjectiveSettings_nodeList.getLength() != 0)
		{
			// Element property ObjectiveSettings which is complex (has
			// sub-elements)
			instance.setObjectiveSettings(ObjectiveSettings.fromXMLElement(
					(Element) ObjectiveSettings_nodeList.item(0)));
		}
		NodeList FilterSetRef_nodeList = element.getElementsByTagName("FilterSetRef");
		if (FilterSetRef_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"FilterSetRef node list size %d != 1",
					FilterSetRef_nodeList.getLength()));
		}
		else if (FilterSetRef_nodeList.getLength() != 0)
		{
			// Element property FilterSetRef which is complex (has
			// sub-elements)
			instance.setFilterSet(FilterSet.fromXMLElement(
					(Element) FilterSetRef_nodeList.item(0)));
		}
		NodeList BinaryFile_nodeList = element.getElementsByTagName("BinaryFile");
		if (BinaryFile_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"BinaryFile node list size %d != 1",
					BinaryFile_nodeList.getLength()));
		}
		else if (BinaryFile_nodeList.getLength() != 0)
		{
			// Element property BinaryFile which is not complex (has no
			// sub-elements)
			instance.setBinaryFile(BinaryFile_nodeList.item(0).getTextContent());
		}
		// *** IGNORING *** Skipped back reference Channel_BackReference
		// *** IGNORING *** Skipped back reference ChannelProfile_BackReference
		return instance;
	}
}
