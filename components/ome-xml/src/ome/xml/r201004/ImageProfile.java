/*
 * ome.xml.r201004.ImageProfile
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

public class ImageProfile extends Object
{
	// -- Instance variables --

	// Property
	private ProfileSource origin;

	// Property
	private String name;

	// Property
	private String description;

	// Property
	private Instrument instrument;

	// Property
	private ObjectiveSettings objectiveSettings;

	// -- Constructors --

	/** Constructs a ImageProfile. */
	public ImageProfile()
	{
	}

	// -- ImageProfile API methods --

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
	public Instrument getInstrument()
	{
		return instrument;
	}

	public void setInstrument(Instrument instrument)
	{
		this.instrument = instrument;
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

	public Element asXMLElement(Document document)
	{
		// Creating XML block for ImageProfile
		Element ImageProfile_element = document.createElement("ImageProfile");
		if (origin != null)
		{
			// Attribute property origin
			ImageProfile_element.setAttribute("origin", origin.toString());
		}
		if (name != null)
		{
			// Element property Name which is not complex (has no
			// sub-elements)
			Element name_element = document.createElement("Name");
			name_element.setTextContent(name);
			ImageProfile_element.appendChild(name_element);
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = document.createElement("Description");
			description_element.setTextContent(description);
			ImageProfile_element.appendChild(description_element);
		}
		if (instrument != null)
		{
			// Element property InstrumentRef which is complex (has
			// sub-elements)
			ImageProfile_element.appendChild(instrument.asXMLElement(document));
		}
		if (objectiveSettings != null)
		{
			// Element property ObjectiveSettings which is complex (has
			// sub-elements)
			ImageProfile_element.appendChild(objectiveSettings.asXMLElement(document));
		}
		return ImageProfile_element;
	}

	public static ImageProfile fromXMLElement(Element element)
		throws EnumerationException
	{
		String tagName = element.getTagName();
		if (!"ImageProfile".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of ImageProfile got %s",
					tagName));
		}
		ImageProfile instance = new ImageProfile();
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
		NodeList InstrumentRef_nodeList = element.getElementsByTagName("InstrumentRef");
		if (InstrumentRef_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"InstrumentRef node list size %d != 1",
					InstrumentRef_nodeList.getLength()));
		}
		else if (InstrumentRef_nodeList.getLength() != 0)
		{
			// Element property InstrumentRef which is complex (has
			// sub-elements)
			instance.setInstrument(Instrument.fromXMLElement(
					(Element) InstrumentRef_nodeList.item(0)));
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
		return instance;
	}
}
