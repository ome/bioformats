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
 * Created by callan via xsd-fu on 2010-04-21 11:45:19+0100
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
	// -- Instance variables --

	// Back reference ExcitationFilterRef
	private List<Filter> excitationFilterList = new ArrayList<Filter>();

	// Back reference DichroicRef
	private List<Dichroic> dichroic = new ArrayList<Dichroic>();

	// Back reference EmissionFilterRef
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
		super(element);
		String tagName = element.getTagName();
		if (!"LightPath".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of LightPath got %s",
					tagName));
		}
		// *** IGNORING *** Skipped back reference ExcitationFilterRef
		// *** IGNORING *** Skipped back reference DichroicRef
		// *** IGNORING *** Skipped back reference EmissionFilterRef
	}

	// -- LightPath API methods --

	// Reference ExcitationFilterRef
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

	public void linkExcitationFilter(Filter o)
	{
		this.excitationFilterList.add(o);
	}

	public void unlinkExcitationFilter(Filter o)
	{
		this.excitationFilterList.add(o);
	}

	// Reference DichroicRef
	public int sizeOfLinkedDichroicList()
	{
		return dichroic.size();
	}

	public List<Dichroic> copyLinkedDichroicList()
	{
		return new ArrayList<Dichroic>(dichroic);
	}

	public Dichroic getLinkedDichroic(int index)
	{
		return dichroic.get(index);
	}

	public Dichroic setLinkedDichroic(int index, Dichroic o)
	{
		return dichroic.set(index, o);
	}

	public void linkDichroic(Dichroic o)
	{
		this.dichroic.add(o);
	}

	public void unlinkDichroic(Dichroic o)
	{
		this.dichroic.add(o);
	}

	// Reference EmissionFilterRef
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

	public void linkEmissionFilter(Filter o)
	{
		this.emissionFilterList.add(o);
	}

	public void unlinkEmissionFilter(Filter o)
	{
		this.emissionFilterList.add(o);
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
			LightPath_element = document.createElement("LightPath");
		}
		LightPath_element = super.asXMLElement(document, LightPath_element);

		if (excitationFilterList != null)
		{
			// *** IGNORING *** Skipped back reference ExcitationFilterRef
		}
		if (dichroic != null)
		{
			// *** IGNORING *** Skipped back reference DichroicRef
		}
		if (emissionFilterList != null)
		{
			// *** IGNORING *** Skipped back reference EmissionFilterRef
		}
		return LightPath_element;
	}
}
