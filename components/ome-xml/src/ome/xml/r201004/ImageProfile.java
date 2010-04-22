/*
 * ome.xml.r201004.ImageProfile
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
 * Created by callan via xsd-fu on 2010-04-22 12:03:51+0100
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

public class ImageProfile extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OMERO/2010-04";

	// -- Instance variables --

	// Property
	private ProfileSource origin;

	// Property
	private String name;

	// Property
	private String description;

	// Back reference InstrumentRef
	private List<Instrument> instrument = new ArrayList<Instrument>();

	// Property
	private ObjectiveSettings objectiveSettings;

	// -- Constructors --

	/** Default constructor. */
	public ImageProfile()
	{
		super();
	}

	/** 
	 * Constructs ImageProfile recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public ImageProfile(Element element) throws EnumerationException
	{
		update(element);
	}

	/** 
	 * Updates ImageProfile recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"ImageProfile".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of ImageProfile got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of ImageProfile got %s",
			//		tagName));
		}
		if (element.hasAttribute("origin"))
		{
			// Attribute property which is an enumeration origin
			setorigin(ProfileSource.fromString(
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
			setName(Name_nodeList.item(0).getTextContent());
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
			setDescription(Description_nodeList.item(0).getTextContent());
		}
		// *** IGNORING *** Skipped back reference InstrumentRef
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

	// Reference InstrumentRef
	public int sizeOfLinkedInstrumentList()
	{
		return instrument.size();
	}

	public List<Instrument> copyLinkedInstrumentList()
	{
		return new ArrayList<Instrument>(instrument);
	}

	public Instrument getLinkedInstrument(int index)
	{
		return instrument.get(index);
	}

	public Instrument setLinkedInstrument(int index, Instrument o)
	{
		return instrument.set(index, o);
	}

	public void linkInstrument(Instrument o)
	{
		this.instrument.add(o);
	}

	public void unlinkInstrument(Instrument o)
	{
		this.instrument.add(o);
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
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element ImageProfile_element)
	{
		// Creating XML block for ImageProfile
		if (ImageProfile_element == null)
		{
			ImageProfile_element =
					document.createElementNS("http://www.openmicroscopy.org/Schemas/OMERO/2010-04", "ImageProfile");
		}

		if (origin != null)
		{
			// Attribute property origin
			ImageProfile_element.setAttribute("origin", origin.toString());
		}
		if (name != null)
		{
			// Element property Name which is not complex (has no
			// sub-elements)
			Element name_element = 
					document.createElementNS("http://www.openmicroscopy.org/Schemas/OMERO/2010-04", "Name");
			name_element.setTextContent(name);
			ImageProfile_element.appendChild(name_element);
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = 
					document.createElementNS("http://www.openmicroscopy.org/Schemas/OMERO/2010-04", "Description");
			description_element.setTextContent(description);
			ImageProfile_element.appendChild(description_element);
		}
		if (instrument != null)
		{
			// *** IGNORING *** Skipped back reference InstrumentRef
		}
		if (objectiveSettings != null)
		{
			// Element property ObjectiveSettings which is complex (has
			// sub-elements)
			ImageProfile_element.appendChild(objectiveSettings.asXMLElement(document));
		}
		return super.asXMLElement(document, ImageProfile_element);
	}
}
