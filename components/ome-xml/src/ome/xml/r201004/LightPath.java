/*
 * ome.xml.r201004.LightPath
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
 * Created by callan via xsd-fu on 2010-04-22 16:50:50+0100
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

public class LightPath extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2010-04";

	// -- Instance variables --

	// Reference ExcitationFilterRef
	private List<Filter> excitationFilterList = new ArrayList<Filter>();

	// Property
	private Dichroic dichroic;

	// Reference EmissionFilterRef
	private List<Filter> emissionFilterList = new ArrayList<Filter>();

	// -- Constructors --

	/** Default constructor. */
	public LightPath()
	{
		super();
	}

	/** 
	 * Constructs LightPath recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public LightPath(Element element) throws EnumerationException
	{
		update(element);
	}

	/** 
	 * Updates LightPath recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"LightPath".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of LightPath got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of LightPath got %s",
			//		tagName));
		}
		// *** IGNORING *** Skipped back reference ExcitationFilterRef
		// *** IGNORING *** Skipped back reference DichroicRef
		// *** IGNORING *** Skipped back reference EmissionFilterRef
	}

	// -- LightPath API methods --


	// Reference which occurs more than once
	public int sizeOfLinkedExcitationFilterList()
	{
		return excitationFilterList.size();
	}

	public List<Filter> copyLinkedExcitationFilterList()
	{
		return new ArrayList<Filter>(excitationFilterList);
	}

	public Filter getLinkedExcitationFilter(int index)
	{
		return excitationFilterList.get(index);
	}

	public Filter setLinkedExcitationFilter(int index, Filter o)
	{
		return excitationFilterList.set(index, o);
	}

	public boolean linkExcitationFilter(Filter o)
	{
		o.linkLightPath(this);
		return excitationFilterList.add(o);
	}

	public boolean unlinkExcitationFilter(Filter o)
	{
		o.unlinkLightPath(this);
		return excitationFilterList.remove(o);
	}

	// Reference
	public Dichroic getLinkedDichroic()
	{
		return dichroic;
	}

	public void linkDichroic(Dichroic o)
	{
		dichroic = o;
	}

	public void unlinkDichroic(Dichroic o)
	{
		if (dichroic == o)
		{
			dichroic = null;
		}
	}

	// Reference which occurs more than once
	public int sizeOfLinkedEmissionFilterList()
	{
		return emissionFilterList.size();
	}

	public List<Filter> copyLinkedEmissionFilterList()
	{
		return new ArrayList<Filter>(emissionFilterList);
	}

	public Filter getLinkedEmissionFilter(int index)
	{
		return emissionFilterList.get(index);
	}

	public Filter setLinkedEmissionFilter(int index, Filter o)
	{
		return emissionFilterList.set(index, o);
	}

	public boolean linkEmissionFilter(Filter o)
	{
		o.linkLightPath(this);
		return emissionFilterList.add(o);
	}

	public boolean unlinkEmissionFilter(Filter o)
	{
		o.unlinkLightPath(this);
		return emissionFilterList.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element LightPath_element)
	{
		// Creating XML block for LightPath
		if (LightPath_element == null)
		{
			LightPath_element =
					document.createElementNS(NAMESPACE, "LightPath");
		}

		if (excitationFilterList != null)
		{
			// Reference property ExcitationFilterRef
			for (Filter o : excitationFilterList)
			{
				Element excitationFilterList_element = 
						document.createElementNS(NAMESPACE, "ExcitationFilterRefRef");
				excitationFilterList_element.setAttribute("ID", o.getID());
				LightPath_element.appendChild(excitationFilterList_element);
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
			// Reference property EmissionFilterRef
			for (Filter o : emissionFilterList)
			{
				Element emissionFilterList_element = 
						document.createElementNS(NAMESPACE, "EmissionFilterRefRef");
				emissionFilterList_element.setAttribute("ID", o.getID());
				LightPath_element.appendChild(emissionFilterList_element);
			}
		}
		return super.asXMLElement(document, LightPath_element);
	}
}
