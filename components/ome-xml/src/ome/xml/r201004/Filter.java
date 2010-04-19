/*
 * ome.xml.r201004.Filter
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

public class Filter extends ManufacturerSpec
{
	// -- Instance variables --

	// Property
	private String filterWheel;

	// Property
	private FilterType type;

	// Property
	private String id;

	// Property
	private TransmittanceRange transmittanceRange;

	// Back reference FilterSet_BackReference
	private List<FilterSet> filterSet_BackReferenceList = new ArrayList<FilterSet>();

	// Back reference LightPath_BackReference
	private List<LightPath> lightPath_BackReferenceList = new ArrayList<LightPath>();

	// -- Constructors --

	/** Constructs a Filter. */
	public Filter()
	{
	}

	// -- Filter API methods --

	// Property
	public String getFilterWheel()
	{
		return filterWheel;
	}

	public void setFilterWheel(String filterWheel)
	{
		this.filterWheel = filterWheel;
	}

	// Property
	public FilterType getType()
	{
		return type;
	}

	public void setType(FilterType type)
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
	public TransmittanceRange getTransmittanceRange()
	{
		return transmittanceRange;
	}

	public void setTransmittanceRange(TransmittanceRange transmittanceRange)
	{
		this.transmittanceRange = transmittanceRange;
	}

	// Back reference FilterSet_BackReference
	public int sizeOfLinkedFilterSetList()
	{
		return filterSet_BackReferenceList.size();
	}

	public List<FilterSet> copyLinkedFilterSetList()
	{
		return new ArrayList<FilterSet>(filterSet_BackReferenceList);
	}

	public FilterSet getLinkedFilterSet(int index)
	{
		return filterSet_BackReferenceList.get(index);
	}

	public FilterSet setLinkedFilterSet(int index, FilterSet filterSet_BackReference)
	{
		return filterSet_BackReferenceList.set(index, filterSet_BackReference);
	}

	public void linkFilterSet(FilterSet filterSet_BackReference)
	{
		this.filterSet_BackReferenceList.add(filterSet_BackReference);
	}

	public void unlinkFilterSet(FilterSet filterSet_BackReference)
	{
		this.filterSet_BackReferenceList.add(filterSet_BackReference);
	}

	// Back reference LightPath_BackReference
	public int sizeOfLinkedLightPathList()
	{
		return lightPath_BackReferenceList.size();
	}

	public List<LightPath> copyLinkedLightPathList()
	{
		return new ArrayList<LightPath>(lightPath_BackReferenceList);
	}

	public LightPath getLinkedLightPath(int index)
	{
		return lightPath_BackReferenceList.get(index);
	}

	public LightPath setLinkedLightPath(int index, LightPath lightPath_BackReference)
	{
		return lightPath_BackReferenceList.set(index, lightPath_BackReference);
	}

	public void linkLightPath(LightPath lightPath_BackReference)
	{
		this.lightPath_BackReferenceList.add(lightPath_BackReference);
	}

	public void unlinkLightPath(LightPath lightPath_BackReference)
	{
		this.lightPath_BackReferenceList.add(lightPath_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Filter
		Element Filter_element = document.createElement("Filter");
		if (filterWheel != null)
		{
			// Attribute property FilterWheel
			Filter_element.setAttribute("FilterWheel", filterWheel.toString());
		}
		if (type != null)
		{
			// Attribute property Type
			Filter_element.setAttribute("Type", type.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Filter_element.setAttribute("ID", id.toString());
		}
		if (transmittanceRange != null)
		{
			// Element property TransmittanceRange which is complex (has
			// sub-elements)
			Filter_element.appendChild(transmittanceRange.asXMLElement(document));
		}
		if (filterSet_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference FilterSet_BackReference
		}
		if (lightPath_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference LightPath_BackReference
		}
		return Filter_element;
	}
}
