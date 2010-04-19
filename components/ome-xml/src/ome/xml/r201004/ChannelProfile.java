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
}
