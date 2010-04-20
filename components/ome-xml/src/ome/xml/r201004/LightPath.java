/*
 * ome.xml.r201004.LightPath
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

public class LightPath extends Object
{
	// -- Instance variables --

	// Property which occurs more than once
	private List<Filter> excitationFilterList = new ArrayList<Filter>();

	// Property
	private Dichroic dichroic;

	// Property which occurs more than once
	private List<Filter> emissionFilterList = new ArrayList<Filter>();

	// -- Constructors --

	/** Constructs a LightPath. */
	public LightPath()
	{
	}

	// -- LightPath API methods --

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

	public Element asXMLElement(Document document)
	{
		// Creating XML block for LightPath
		Element LightPath_element = document.createElement("LightPath");
		if (excitationFilterList != null)
		{
			// Element property ExcitationFilterRef which is complex (has
			// sub-elements) and occurs more than once
			for (Filter excitationFilterList_value : excitationFilterList)
			{
				LightPath_element.appendChild(excitationFilterList_value.asXMLElement(document));
			}
		}
		if (dichroic != null)
		{
			// Element property DichroicRef which is complex (has
			// sub-elements)
			LightPath_element.appendChild(dichroic.asXMLElement(document));
		}
		if (emissionFilterList != null)
		{
			// Element property EmissionFilterRef which is complex (has
			// sub-elements) and occurs more than once
			for (Filter emissionFilterList_value : emissionFilterList)
			{
				LightPath_element.appendChild(emissionFilterList_value.asXMLElement(document));
			}
		}
		return LightPath_element;
	}

	public static LightPath fromXMLElement(Element element)
		throws EnumerationException
	{
		String tagName = element.getTagName();
		if (!"LightPath".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of LightPath got %s",
					tagName));
		}
		LightPath instance = new LightPath();
		// Element property ExcitationFilterRef which is complex (has
		// sub-elements) and occurs more than once
		NodeList ExcitationFilterRef_nodeList = element.getElementsByTagName("ExcitationFilterRef");
		for (int i = 0; i < ExcitationFilterRef_nodeList.getLength(); i++)
		{
			instance.addExcitationFilter(Filter.fromXMLElement(
					(Element) ExcitationFilterRef_nodeList.item(i)));
		}
		NodeList DichroicRef_nodeList = element.getElementsByTagName("DichroicRef");
		if (DichroicRef_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"DichroicRef node list size %d != 1",
					DichroicRef_nodeList.getLength()));
		}
		else if (DichroicRef_nodeList.getLength() != 0)
		{
			// Element property DichroicRef which is complex (has
			// sub-elements)
			instance.setDichroic(Dichroic.fromXMLElement(
					(Element) DichroicRef_nodeList.item(0)));
		}
		// Element property EmissionFilterRef which is complex (has
		// sub-elements) and occurs more than once
		NodeList EmissionFilterRef_nodeList = element.getElementsByTagName("EmissionFilterRef");
		for (int i = 0; i < EmissionFilterRef_nodeList.getLength(); i++)
		{
			instance.addEmissionFilter(Filter.fromXMLElement(
					(Element) EmissionFilterRef_nodeList.item(i)));
		}
		return instance;
	}
}
