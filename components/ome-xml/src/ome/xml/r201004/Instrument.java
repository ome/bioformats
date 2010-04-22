/*
 * ome.xml.r201004.Instrument
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
 * Created by callan via xsd-fu on 2010-04-22 17:37:18+0100
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

public class Instrument extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2010-04";

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

	/** Default constructor. */
	public Instrument()
	{
		super();
	}

	/** 
	 * Constructs Instrument recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Instrument(Element element) throws EnumerationException
	{
		update(element);
	}

	/** 
	 * Updates Instrument recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Instrument".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of Instrument got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of Instrument got %s",
			//		tagName));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
		NodeList Microscope_nodeList = element.getElementsByTagName("Microscope");
		if (Microscope_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Microscope node list size %d != 1",
					Microscope_nodeList.getLength()));
		}
		else if (Microscope_nodeList.getLength() != 0)
		{
			// Element property Microscope which is complex (has
			// sub-elements)
			setMicroscope(new Microscope(
					(Element) Microscope_nodeList.item(0)));
		}
		// Element property LightSource which is complex (has
		// sub-elements) and occurs more than once. The element's model
		// object type is also abstract so we need to have a handler for
		// each "subclass".
		NodeList LightSource_nodeList = element.getElementsByTagName("LightSource");
		for (int i = 0; i < LightSource_nodeList.getLength(); i++)
		{
			Element LightSource_element = (Element) LightSource_nodeList.item(i);
			NodeList Laser_nodeList = 
					LightSource_element.getElementsByTagName("Laser");
			for (int j = 0; j < Laser_nodeList.getLength(); j++)
			{
				Element Laser_element = (Element) Laser_nodeList.item(j);
				Laser o = new Laser(Laser_element);
				o.update(LightSource_element);
				addLightSource(o);
			}
			NodeList Filament_nodeList = 
					LightSource_element.getElementsByTagName("Filament");
			for (int j = 0; j < Filament_nodeList.getLength(); j++)
			{
				Element Filament_element = (Element) Filament_nodeList.item(j);
				Filament o = new Filament(Filament_element);
				o.update(LightSource_element);
				addLightSource(o);
			}
			NodeList Arc_nodeList = 
					LightSource_element.getElementsByTagName("Arc");
			for (int j = 0; j < Arc_nodeList.getLength(); j++)
			{
				Element Arc_element = (Element) Arc_nodeList.item(j);
				Arc o = new Arc(Arc_element);
				o.update(LightSource_element);
				addLightSource(o);
			}
			NodeList LightEmittingDiode_nodeList = 
					LightSource_element.getElementsByTagName("LightEmittingDiode");
			for (int j = 0; j < LightEmittingDiode_nodeList.getLength(); j++)
			{
				Element LightEmittingDiode_element = (Element) LightEmittingDiode_nodeList.item(j);
				LightEmittingDiode o = new LightEmittingDiode(LightEmittingDiode_element);
				o.update(LightSource_element);
				addLightSource(o);
			}
		}
		// Element property Detector which is complex (has
		// sub-elements) and occurs more than once
		NodeList Detector_nodeList = element.getElementsByTagName("Detector");
		for (int i = 0; i < Detector_nodeList.getLength(); i++)
		{
			Element Detector_element = (Element) Detector_nodeList.item(i);
			addDetector(
					new Detector(Detector_element));
		}
		// Element property Objective which is complex (has
		// sub-elements) and occurs more than once
		NodeList Objective_nodeList = element.getElementsByTagName("Objective");
		for (int i = 0; i < Objective_nodeList.getLength(); i++)
		{
			Element Objective_element = (Element) Objective_nodeList.item(i);
			addObjective(
					new Objective(Objective_element));
		}
		// Element property FilterSet which is complex (has
		// sub-elements) and occurs more than once
		NodeList FilterSet_nodeList = element.getElementsByTagName("FilterSet");
		for (int i = 0; i < FilterSet_nodeList.getLength(); i++)
		{
			Element FilterSet_element = (Element) FilterSet_nodeList.item(i);
			addFilterSet(
					new FilterSet(FilterSet_element));
		}
		// Element property Filter which is complex (has
		// sub-elements) and occurs more than once
		NodeList Filter_nodeList = element.getElementsByTagName("Filter");
		for (int i = 0; i < Filter_nodeList.getLength(); i++)
		{
			Element Filter_element = (Element) Filter_nodeList.item(i);
			addFilter(
					new Filter(Filter_element));
		}
		// Element property Dichroic which is complex (has
		// sub-elements) and occurs more than once
		NodeList Dichroic_nodeList = element.getElementsByTagName("Dichroic");
		for (int i = 0; i < Dichroic_nodeList.getLength(); i++)
		{
			Element Dichroic_element = (Element) Dichroic_nodeList.item(i);
			addDichroic(
					new Dichroic(Dichroic_element));
		}
		// Element property OTF which is complex (has
		// sub-elements) and occurs more than once
		NodeList OTF_nodeList = element.getElementsByTagName("OTF");
		for (int i = 0; i < OTF_nodeList.getLength(); i++)
		{
			Element OTF_element = (Element) OTF_nodeList.item(i);
			addOTF(
					new OTF(OTF_element));
		}
		// *** IGNORING *** Skipped back reference Image_BackReference
		// *** IGNORING *** Skipped back reference ImageProfile_BackReference
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

	// Reference which occurs more than once
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

	public Image setLinkedImage(int index, Image o)
	{
		return image_BackReferenceList.set(index, o);
	}

	public boolean linkImage(Image o)
	{
		return image_BackReferenceList.add(o);
	}

	public boolean unlinkImage(Image o)
	{
		return image_BackReferenceList.remove(o);
	}

	// Reference which occurs more than once
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

	public ImageProfile setLinkedImageProfile(int index, ImageProfile o)
	{
		return imageProfile_BackReferenceList.set(index, o);
	}

	public boolean linkImageProfile(ImageProfile o)
	{
		return imageProfile_BackReferenceList.add(o);
	}

	public boolean unlinkImageProfile(ImageProfile o)
	{
		return imageProfile_BackReferenceList.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Instrument_element)
	{
		// Creating XML block for Instrument
		if (Instrument_element == null)
		{
			Instrument_element =
					document.createElementNS(NAMESPACE, "Instrument");
		}

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
		return super.asXMLElement(document, Instrument_element);
	}
}
