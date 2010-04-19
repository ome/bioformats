/*
 * ome.xml.r201004.Instrument
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

public class Instrument extends Object
{
	// -- Instance variables --

	// Property
	private String id;

	// Property
	private Microscope microscope;

	// Property which occurs more than once
	private List<LightSource> lightSourceList = new ArrayList<LightSource>();

	// Property which occurs more than once
	private List<Detector> detectorList = new ArrayList<Detector>();

	// Property which occurs more than once
	private List<Objective> objectiveList = new ArrayList<Objective>();

	// Property which occurs more than once
	private List<FilterSet> filterSetList = new ArrayList<FilterSet>();

	// Property which occurs more than once
	private List<Filter> filterList = new ArrayList<Filter>();

	// Property which occurs more than once
	private List<Dichroic> dichroicList = new ArrayList<Dichroic>();

	// Property which occurs more than once
	private List<OTF> otfList = new ArrayList<OTF>();

	// Back reference Image_BackReference
	private List<Image> image_BackReferenceList = new ArrayList<Image>();

	// Back reference ImageProfile_BackReference
	private List<ImageProfile> imageProfile_BackReferenceList = new ArrayList<ImageProfile>();

	// -- Constructors --

	/** Constructs a Instrument. */
	public Instrument()
	{
	}

	// -- Instrument API methods --

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
	public Microscope getMicroscope()
	{
		return microscope;
	}

	public void setMicroscope(Microscope microscope)
	{
		this.microscope = microscope;
	}

	// Property which occurs more than once
	public int sizeOfLightSourceList()
	{
		return lightSourceList.size();
	}

	public List<LightSource> copyLightSourceList()
	{
		return new ArrayList<LightSource>(lightSourceList);
	}

	public LightSource getLightSource(int index)
	{
		return lightSourceList.get(index);
	}

	public LightSource setLightSource(int index, LightSource lightSource)
	{
		return lightSourceList.set(index, lightSource);
	}

	public void addLightSource(LightSource lightSource)
	{
		lightSourceList.add(lightSource);
	}

	public void removeLightSource(LightSource lightSource)
	{
		lightSourceList.remove(lightSource);
	}

	// Property which occurs more than once
	public int sizeOfDetectorList()
	{
		return detectorList.size();
	}

	public List<Detector> copyDetectorList()
	{
		return new ArrayList<Detector>(detectorList);
	}

	public Detector getDetector(int index)
	{
		return detectorList.get(index);
	}

	public Detector setDetector(int index, Detector detector)
	{
		return detectorList.set(index, detector);
	}

	public void addDetector(Detector detector)
	{
		detectorList.add(detector);
	}

	public void removeDetector(Detector detector)
	{
		detectorList.remove(detector);
	}

	// Property which occurs more than once
	public int sizeOfObjectiveList()
	{
		return objectiveList.size();
	}

	public List<Objective> copyObjectiveList()
	{
		return new ArrayList<Objective>(objectiveList);
	}

	public Objective getObjective(int index)
	{
		return objectiveList.get(index);
	}

	public Objective setObjective(int index, Objective objective)
	{
		return objectiveList.set(index, objective);
	}

	public void addObjective(Objective objective)
	{
		objectiveList.add(objective);
	}

	public void removeObjective(Objective objective)
	{
		objectiveList.remove(objective);
	}

	// Property which occurs more than once
	public int sizeOfFilterSetList()
	{
		return filterSetList.size();
	}

	public List<FilterSet> copyFilterSetList()
	{
		return new ArrayList<FilterSet>(filterSetList);
	}

	public FilterSet getFilterSet(int index)
	{
		return filterSetList.get(index);
	}

	public FilterSet setFilterSet(int index, FilterSet filterSet)
	{
		return filterSetList.set(index, filterSet);
	}

	public void addFilterSet(FilterSet filterSet)
	{
		filterSetList.add(filterSet);
	}

	public void removeFilterSet(FilterSet filterSet)
	{
		filterSetList.remove(filterSet);
	}

	// Property which occurs more than once
	public int sizeOfFilterList()
	{
		return filterList.size();
	}

	public List<Filter> copyFilterList()
	{
		return new ArrayList<Filter>(filterList);
	}

	public Filter getFilter(int index)
	{
		return filterList.get(index);
	}

	public Filter setFilter(int index, Filter filter)
	{
		return filterList.set(index, filter);
	}

	public void addFilter(Filter filter)
	{
		filterList.add(filter);
	}

	public void removeFilter(Filter filter)
	{
		filterList.remove(filter);
	}

	// Property which occurs more than once
	public int sizeOfDichroicList()
	{
		return dichroicList.size();
	}

	public List<Dichroic> copyDichroicList()
	{
		return new ArrayList<Dichroic>(dichroicList);
	}

	public Dichroic getDichroic(int index)
	{
		return dichroicList.get(index);
	}

	public Dichroic setDichroic(int index, Dichroic dichroic)
	{
		return dichroicList.set(index, dichroic);
	}

	public void addDichroic(Dichroic dichroic)
	{
		dichroicList.add(dichroic);
	}

	public void removeDichroic(Dichroic dichroic)
	{
		dichroicList.remove(dichroic);
	}

	// Property which occurs more than once
	public int sizeOfOTFList()
	{
		return otfList.size();
	}

	public List<OTF> copyOTFList()
	{
		return new ArrayList<OTF>(otfList);
	}

	public OTF getOTF(int index)
	{
		return otfList.get(index);
	}

	public OTF setOTF(int index, OTF otf)
	{
		return otfList.set(index, otf);
	}

	public void addOTF(OTF otf)
	{
		otfList.add(otf);
	}

	public void removeOTF(OTF otf)
	{
		otfList.remove(otf);
	}

	// Back reference Image_BackReference
	public int sizeOfLinkedImageList()
	{
		return image_BackReferenceList.size();
	}

	public List<Image> copyLinkedImageList()
	{
		return new ArrayList<Image>(image_BackReferenceList);
	}

	public Image getLinkedImage(int index)
	{
		return image_BackReferenceList.get(index);
	}

	public Image setLinkedImage(int index, Image image_BackReference)
	{
		return image_BackReferenceList.set(index, image_BackReference);
	}

	public void linkImage(Image image_BackReference)
	{
		this.image_BackReferenceList.add(image_BackReference);
	}

	public void unlinkImage(Image image_BackReference)
	{
		this.image_BackReferenceList.add(image_BackReference);
	}

	// Back reference ImageProfile_BackReference
	public int sizeOfLinkedImageProfileList()
	{
		return imageProfile_BackReferenceList.size();
	}

	public List<ImageProfile> copyLinkedImageProfileList()
	{
		return new ArrayList<ImageProfile>(imageProfile_BackReferenceList);
	}

	public ImageProfile getLinkedImageProfile(int index)
	{
		return imageProfile_BackReferenceList.get(index);
	}

	public ImageProfile setLinkedImageProfile(int index, ImageProfile imageProfile_BackReference)
	{
		return imageProfile_BackReferenceList.set(index, imageProfile_BackReference);
	}

	public void linkImageProfile(ImageProfile imageProfile_BackReference)
	{
		this.imageProfile_BackReferenceList.add(imageProfile_BackReference);
	}

	public void unlinkImageProfile(ImageProfile imageProfile_BackReference)
	{
		this.imageProfile_BackReferenceList.add(imageProfile_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Instrument
		Element Instrument_element = document.createElement("Instrument");
		if (id != null)
		{
			// Attribute property ID
			Instrument_element.setAttribute("ID", id.toString());
		}
		if (microscope != null)
		{
			// Element property Microscope which is complex (has
			// sub-elements)
			Instrument_element.appendChild(microscope.asXMLElement(document));
		}
		if (lightSourceList != null)
		{
			// Element property LightSource which is complex (has
			// sub-elements) and occurs more than once
			for (LightSource lightSourceList_value : lightSourceList)
			{
				Instrument_element.appendChild(lightSourceList_value.asXMLElement(document));
			}
		}
		if (detectorList != null)
		{
			// Element property Detector which is complex (has
			// sub-elements) and occurs more than once
			for (Detector detectorList_value : detectorList)
			{
				Instrument_element.appendChild(detectorList_value.asXMLElement(document));
			}
		}
		if (objectiveList != null)
		{
			// Element property Objective which is complex (has
			// sub-elements) and occurs more than once
			for (Objective objectiveList_value : objectiveList)
			{
				Instrument_element.appendChild(objectiveList_value.asXMLElement(document));
			}
		}
		if (filterSetList != null)
		{
			// Element property FilterSet which is complex (has
			// sub-elements) and occurs more than once
			for (FilterSet filterSetList_value : filterSetList)
			{
				Instrument_element.appendChild(filterSetList_value.asXMLElement(document));
			}
		}
		if (filterList != null)
		{
			// Element property Filter which is complex (has
			// sub-elements) and occurs more than once
			for (Filter filterList_value : filterList)
			{
				Instrument_element.appendChild(filterList_value.asXMLElement(document));
			}
		}
		if (dichroicList != null)
		{
			// Element property Dichroic which is complex (has
			// sub-elements) and occurs more than once
			for (Dichroic dichroicList_value : dichroicList)
			{
				Instrument_element.appendChild(dichroicList_value.asXMLElement(document));
			}
		}
		if (otfList != null)
		{
			// Element property OTF which is complex (has
			// sub-elements) and occurs more than once
			for (OTF otfList_value : otfList)
			{
				Instrument_element.appendChild(otfList_value.asXMLElement(document));
			}
		}
		if (image_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}
		if (imageProfile_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference ImageProfile_BackReference
		}
		return Instrument_element;
	}
}
