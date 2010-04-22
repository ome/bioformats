/*
 * ome.xml.r201004.OTF
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
 * Created by callan via xsd-fu on 2010-04-22 12:27:38+0100
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

public class OTF extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2010-04";

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

	// Back reference FilterSetRef
	private List<FilterSet> filterSet = new ArrayList<FilterSet>();

	// Property
	private String binaryFile;

	// Back reference Channel_BackReference
	private List<Channel> channel_BackReferenceList = new ArrayList<Channel>();

	// Back reference ChannelProfile_BackReference
	private List<ChannelProfile> channelProfile_BackReferenceList = new ArrayList<ChannelProfile>();

	// -- Constructors --

	/** Default constructor. */
	public OTF()
	{
		super();
	}

	/** 
	 * Constructs OTF recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public OTF(Element element) throws EnumerationException
	{
		update(element);
	}

	/** 
	 * Updates OTF recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"OTF".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of OTF got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of OTF got %s",
			//		tagName));
		}
		if (element.hasAttribute("SizeX"))
		{
			// Attribute property SizeX
			setSizeX(Integer.valueOf(
					element.getAttribute("SizeX")));
		}
		if (element.hasAttribute("SizeY"))
		{
			// Attribute property SizeY
			setSizeY(Integer.valueOf(
					element.getAttribute("SizeY")));
		}
		if (element.hasAttribute("Type"))
		{
			// Attribute property which is an enumeration Type
			setType(PixelType.fromString(
					element.getAttribute("Type")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
		if (element.hasAttribute("OpticalAxisAveraged"))
		{
			// Attribute property OpticalAxisAveraged
			setOpticalAxisAveraged(Boolean.valueOf(
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
			setObjectiveSettings(new ObjectiveSettings(
					(Element) ObjectiveSettings_nodeList.item(0)));
		}
		// *** IGNORING *** Skipped back reference FilterSetRef
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
			setBinaryFile(BinaryFile_nodeList.item(0).getTextContent());
		}
		// *** IGNORING *** Skipped back reference Channel_BackReference
		// *** IGNORING *** Skipped back reference ChannelProfile_BackReference
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

	// Reference FilterSetRef
	public int sizeOfLinkedFilterSetList()
	{
		return filterSet.size();
	}

	public List<FilterSet> copyLinkedFilterSetList()
	{
		return new ArrayList<FilterSet>(filterSet);
	}

	public FilterSet getLinkedFilterSet(int index)
	{
		return filterSet.get(index);
	}

	public FilterSet setLinkedFilterSet(int index, FilterSet o)
	{
		return filterSet.set(index, o);
	}

	public void linkFilterSet(FilterSet o)
	{
		this.filterSet.add(o);
	}

	public void unlinkFilterSet(FilterSet o)
	{
		this.filterSet.add(o);
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

	protected Element asXMLElement(Document document, Element OTF_element)
	{
		// Creating XML block for OTF
		if (OTF_element == null)
		{
			OTF_element =
					document.createElementNS(NAMESPACE, "OTF");
		}

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
			// *** IGNORING *** Skipped back reference FilterSetRef
		}
		if (binaryFile != null)
		{
			// Element property BinaryFile which is not complex (has no
			// sub-elements)
			Element binaryFile_element = 
					document.createElementNS(NAMESPACE, "BinaryFile");
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
		return super.asXMLElement(document, OTF_element);
	}
}
