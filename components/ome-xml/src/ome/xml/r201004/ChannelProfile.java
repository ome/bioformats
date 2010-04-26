/*
 * ome.xml.r201004.ChannelProfile
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
 * Created by callan via xsd-fu on 2010-04-26 21:43:56+0100
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

public class ChannelProfile extends AbstractOMEModelObject
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

	// Property
	private LightSourceSettings lightSourceSettings;

	// Property
	private OTF otf;

	// Property
	private DetectorSettings detectorSettings;

	// Property
	private FilterSet filterSet;

	// -- Constructors --

	/** Default constructor. */
	public ChannelProfile()
	{
		super();
	}

	/** 
	 * Constructs ChannelProfile recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public ChannelProfile(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from ChannelProfile specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates ChannelProfile recursively from an XML DOM tree. <b>NOTE:</b> No
	 * properties are removed, only added or updated.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public void update(Element element, OMEModel model)
	    throws EnumerationException
	{	
		super.update(element, model);
		String tagName = element.getTagName();
		if (!"ChannelProfile".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of ChannelProfile got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of ChannelProfile got %s",
			//		tagName));
		}
		if (element.hasAttribute("Origin"))
		{
			// Attribute property which is an enumeration Origin
			setOrigin(ProfileSource.fromString(
					element.getAttribute("Origin")));
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
			setName(
					String.valueOf(Name_nodeList.item(0).getTextContent()));
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
			setDescription(
					String.valueOf(Description_nodeList.item(0).getTextContent()));
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
			setLightSourceSettings(new LightSourceSettings(
					(Element) LightSourceSettings_nodeList.item(0), model));
		}
		// Element reference OTFRef
		NodeList OTFRef_nodeList = element.getElementsByTagName("OTFRef");
		for (int i = 0; i < OTFRef_nodeList.getLength(); i++)
		{
			Element OTFRef_element = (Element) OTFRef_nodeList.item(i);
			OTFRef otf_reference = new OTFRef();
			otf_reference.setID(OTFRef_element.getAttribute("ID"));
			model.addReference(this, otf_reference);
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
			setDetectorSettings(new DetectorSettings(
					(Element) DetectorSettings_nodeList.item(0), model));
		}
		// Element reference FilterSetRef
		NodeList FilterSetRef_nodeList = element.getElementsByTagName("FilterSetRef");
		for (int i = 0; i < FilterSetRef_nodeList.getLength(); i++)
		{
			Element FilterSetRef_element = (Element) FilterSetRef_nodeList.item(i);
			FilterSetRef filterSet_reference = new FilterSetRef();
			filterSet_reference.setID(FilterSetRef_element.getAttribute("ID"));
			model.addReference(this, filterSet_reference);
		}
	}

	// -- ChannelProfile API methods --

	public void link(Reference reference, OMEModelObject o)
	{
		if (reference instanceof OTFRef)
		{
			OTF o_casted = (OTF) o;
			o_casted.linkChannelProfile(this);
			otf = o_casted;
			return;
		}
		if (reference instanceof FilterSetRef)
		{
			FilterSet o_casted = (FilterSet) o;
			o_casted.linkChannelProfile(this);
			filterSet = o_casted;
			return;
		}
		// TODO: Should be its own Exception
		throw new RuntimeException(
				"Unable to handle reference of type: " + reference.getClass());
	}


	// Property
	public ProfileSource getOrigin()
	{
		return origin;
	}

	public void setOrigin(ProfileSource origin)
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

	// Reference
	public OTF getLinkedOTF()
	{
		return otf;
	}

	public void linkOTF(OTF o)
	{
		otf = o;
	}

	public void unlinkOTF(OTF o)
	{
		if (otf == o)
		{
			otf = null;
		}
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

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element ChannelProfile_element)
	{
		// Creating XML block for ChannelProfile
		if (ChannelProfile_element == null)
		{
			ChannelProfile_element =
					document.createElementNS(NAMESPACE, "ChannelProfile");
		}

		if (origin != null)
		{
			// Attribute property Origin
			ChannelProfile_element.setAttribute("Origin", origin.toString());
		}
		if (name != null)
		{
			// Element property Name which is not complex (has no
			// sub-elements)
			Element name_element = 
					document.createElementNS(NAMESPACE, "Name");
			name_element.setTextContent(name.toString());
			ChannelProfile_element.appendChild(name_element);
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = 
					document.createElementNS(NAMESPACE, "Description");
			description_element.setTextContent(description.toString());
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
			// Reference property OTFRef
			OTFRef o = new OTFRef();
			o.setID(otf.getID());
			ChannelProfile_element.appendChild(o.asXMLElement(document));
		}
		if (detectorSettings != null)
		{
			// Element property DetectorSettings which is complex (has
			// sub-elements)
			ChannelProfile_element.appendChild(detectorSettings.asXMLElement(document));
		}
		if (filterSet != null)
		{
			// Reference property FilterSetRef
			FilterSetRef o = new FilterSetRef();
			o.setID(filterSet.getID());
			ChannelProfile_element.appendChild(o.asXMLElement(document));
		}
		return super.asXMLElement(document, ChannelProfile_element);
	}
}
