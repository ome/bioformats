/*
 * ome.xml.r201004.ChannelProfile
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

public class ChannelProfile extends Object
{
	// -- Instance variables --

	// Property
	private ProfileSource origin;

	// Property
	private String name;

	// Property
	private String description;

	// Property
	private LightSourceSettings lightSourceSettings;

	// Property
	private OTF otf;

	// Property
	private DetectorSettings detectorSettings;

	// Property
	private FilterSet filterSet;

	// -- Constructors --

	/** Constructs a ChannelProfile. */
	public ChannelProfile()
	{
	}

	// -- ChannelProfile API methods --

	// Property
	public ProfileSource getorigin()
	{
		return origin;
	}

	public void setorigin(ProfileSource origin)
	{
		this.origin = origin;
	}

	// Property
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	// Property
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	// Property
	public LightSourceSettings getLightSourceSettings()
	{
		return lightSourceSettings;
	}

	public void setLightSourceSettings(LightSourceSettings lightSourceSettings)
	{
		this.lightSourceSettings = lightSourceSettings;
	}

	// Property
	public OTF getOTF()
	{
		return otf;
	}

	public void setOTF(OTF otf)
	{
		this.otf = otf;
	}

	// Property
	public DetectorSettings getDetectorSettings()
	{
		return detectorSettings;
	}

	public void setDetectorSettings(DetectorSettings detectorSettings)
	{
		this.detectorSettings = detectorSettings;
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

	public Element asXMLElement(Document document)
	{
		// Creating XML block for ChannelProfile
		Element ChannelProfile_element = document.createElement("ChannelProfile");
		if (origin != null)
		{
			// Attribute property origin
			ChannelProfile_element.setAttribute("origin", origin.toString());
		}
		if (name != null)
		{
			// Element property Name which is not complex (has no
			// sub-elements)
			Element name_element = document.createElement("Name");
			name_element.setTextContent(name);
			ChannelProfile_element.appendChild(name_element);
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = document.createElement("Description");
			description_element.setTextContent(description);
			ChannelProfile_element.appendChild(description_element);
		}
		if (lightSourceSettings != null)
		{
			// Element property LightSourceSettings which is complex (has
			// sub-elements)
			ChannelProfile_element.appendChild(lightSourceSettings.asXMLElement(document));
		}
		if (otf != null)
		{
			// Element property OTFRef which is complex (has
			// sub-elements)
			ChannelProfile_element.appendChild(otf.asXMLElement(document));
		}
		if (detectorSettings != null)
		{
			// Element property DetectorSettings which is complex (has
			// sub-elements)
			ChannelProfile_element.appendChild(detectorSettings.asXMLElement(document));
		}
		if (filterSet != null)
		{
			// Element property FilterSetRef which is complex (has
			// sub-elements)
			ChannelProfile_element.appendChild(filterSet.asXMLElement(document));
		}
		return ChannelProfile_element;
	}

	public static ChannelProfile fromXMLElement(Element element)
		throws EnumerationException
	{
		String tagName = element.getTagName();
		if (!"ChannelProfile".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of ChannelProfile got %s",
					tagName));
		}
		ChannelProfile instance = new ChannelProfile();
		if (element.hasAttribute("origin"))
		{
			// Attribute property which is an enumeration origin
			instance.setorigin(ProfileSource.fromString(
					element.getAttribute("origin")));
		}
		NodeList Name_nodeList = element.getElementsByTagName("Name");
		if (Name_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Name node list size %d != 1",
					Name_nodeList.getLength()));
		}
		else if (Name_nodeList.getLength() != 0)
		{
			// Element property Name which is not complex (has no
			// sub-elements)
			instance.setName(Name_nodeList.item(0).getTextContent());
		}
		NodeList Description_nodeList = element.getElementsByTagName("Description");
		if (Description_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Description node list size %d != 1",
					Description_nodeList.getLength()));
		}
		else if (Description_nodeList.getLength() != 0)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			instance.setDescription(Description_nodeList.item(0).getTextContent());
		}
		NodeList LightSourceSettings_nodeList = element.getElementsByTagName("LightSourceSettings");
		if (LightSourceSettings_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"LightSourceSettings node list size %d != 1",
					LightSourceSettings_nodeList.getLength()));
		}
		else if (LightSourceSettings_nodeList.getLength() != 0)
		{
			// Element property LightSourceSettings which is complex (has
			// sub-elements)
			instance.setLightSourceSettings(LightSourceSettings.fromXMLElement(
					(Element) LightSourceSettings_nodeList.item(0)));
		}
		NodeList OTFRef_nodeList = element.getElementsByTagName("OTFRef");
		if (OTFRef_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"OTFRef node list size %d != 1",
					OTFRef_nodeList.getLength()));
		}
		else if (OTFRef_nodeList.getLength() != 0)
		{
			// Element property OTFRef which is complex (has
			// sub-elements)
			instance.setOTF(OTF.fromXMLElement(
					(Element) OTFRef_nodeList.item(0)));
		}
		NodeList DetectorSettings_nodeList = element.getElementsByTagName("DetectorSettings");
		if (DetectorSettings_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"DetectorSettings node list size %d != 1",
					DetectorSettings_nodeList.getLength()));
		}
		else if (DetectorSettings_nodeList.getLength() != 0)
		{
			// Element property DetectorSettings which is complex (has
			// sub-elements)
			instance.setDetectorSettings(DetectorSettings.fromXMLElement(
					(Element) DetectorSettings_nodeList.item(0)));
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
		return instance;
	}
}
