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

	// Property
	private FilterSet filterSet;

	// Property
	private BinaryFile binaryFile;

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
			// Element property BinaryFile which is complex (has
			// sub-elements)
			setBinaryFile(new BinaryFile(
					(Element) BinaryFile_nodeList.item(0)));
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

	// Reference
	public FilterSet getLinkedFilterSet()
	{
		return filterSet;
	}

	public void linkFilterSet(FilterSet o)
	{
		filterSet = o;
	}

	public void unlinkFilterSet(FilterSet o)
	{
		if (filterSet == o)
		{
			filterSet = null;
		}
	}

	// Property
	public BinaryFile getBinaryFile()
	{
		return binaryFile;
	}

	public void setBinaryFile(BinaryFile binaryFile)
	{
		this.binaryFile = binaryFile;
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
			// Reference property FilterSetRef
			FilterSetRef o = new FilterSetRef();
			o.setID(filterSet.getID());
			OTF_element.appendChild(o.asXMLElement(document));
		}
		if (binaryFile != null)
		{
			// Element property BinaryFile which is complex (has
			// sub-elements)
			OTF_element.appendChild(binaryFile.asXMLElement(document));
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
