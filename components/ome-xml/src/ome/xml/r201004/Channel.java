/*
 * ome.xml.r201004.Channel
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
 * Created by callan via xsd-fu on 2010-04-22 16:29:38+0100
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

public class Channel extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2010-04";

	// -- Instance variables --

	// Property
	private Double pinholeSize;

	// Property
	private String name;

	// Property
	private AcquisitionMode acquisitionMode;

	// Property
	private Integer color;

	// Property
	private ContrastMethod contrastMethod;

	// Property
	private Integer excitationWavelength;

	// Property
	private IlluminationType illuminationType;

	// Property
	private String fluor;

	// Property
	private Integer pockelCellSetting;

	// Property
	private Integer emissionWavelength;

	// Property
	private Double ndfilter;

	// Property
	private String id;

	// Property
	private Integer samplesPerPixel;

	// Property
	private LightSourceSettings lightSourceSettings;

	// Property
	private OTF otf;

	// Property
	private DetectorSettings detectorSettings;

	// Property
	private FilterSet filterSet;

	// Reference AnnotationRef
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Property
	private LightPath lightPath;

	// -- Constructors --

	/** Default constructor. */
	public Channel()
	{
		super();
	}

	/** 
	 * Constructs Channel recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Channel(Element element) throws EnumerationException
	{
		update(element);
	}

	/** 
	 * Updates Channel recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Channel".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of Channel got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of Channel got %s",
			//		tagName));
		}
		if (element.hasAttribute("PinholeSize"))
		{
			// Attribute property PinholeSize
			setPinholeSize(Double.valueOf(
					element.getAttribute("PinholeSize")));
		}
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			setName(String.valueOf(
					element.getAttribute("Name")));
		}
		if (element.hasAttribute("AcquisitionMode"))
		{
			// Attribute property which is an enumeration AcquisitionMode
			setAcquisitionMode(AcquisitionMode.fromString(
					element.getAttribute("AcquisitionMode")));
		}
		if (element.hasAttribute("Color"))
		{
			// Attribute property Color
			setColor(Integer.valueOf(
					element.getAttribute("Color")));
		}
		if (element.hasAttribute("ContrastMethod"))
		{
			// Attribute property which is an enumeration ContrastMethod
			setContrastMethod(ContrastMethod.fromString(
					element.getAttribute("ContrastMethod")));
		}
		if (element.hasAttribute("ExcitationWavelength"))
		{
			// Attribute property ExcitationWavelength
			setExcitationWavelength(Integer.valueOf(
					element.getAttribute("ExcitationWavelength")));
		}
		if (element.hasAttribute("IlluminationType"))
		{
			// Attribute property which is an enumeration IlluminationType
			setIlluminationType(IlluminationType.fromString(
					element.getAttribute("IlluminationType")));
		}
		if (element.hasAttribute("Fluor"))
		{
			// Attribute property Fluor
			setFluor(String.valueOf(
					element.getAttribute("Fluor")));
		}
		if (element.hasAttribute("PockelCellSetting"))
		{
			// Attribute property PockelCellSetting
			setPockelCellSetting(Integer.valueOf(
					element.getAttribute("PockelCellSetting")));
		}
		if (element.hasAttribute("EmissionWavelength"))
		{
			// Attribute property EmissionWavelength
			setEmissionWavelength(Integer.valueOf(
					element.getAttribute("EmissionWavelength")));
		}
		if (element.hasAttribute("NDFilter"))
		{
			// Attribute property NDFilter
			setNDFilter(Double.valueOf(
					element.getAttribute("NDFilter")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
		if (element.hasAttribute("SamplesPerPixel"))
		{
			// Attribute property SamplesPerPixel
			setSamplesPerPixel(Integer.valueOf(
					element.getAttribute("SamplesPerPixel")));
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
					(Element) LightSourceSettings_nodeList.item(0)));
		}
		// *** IGNORING *** Skipped back reference OTFRef
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
					(Element) DetectorSettings_nodeList.item(0)));
		}
		// *** IGNORING *** Skipped back reference FilterSetRef
		// *** IGNORING *** Skipped back reference AnnotationRef
		NodeList LightPath_nodeList = element.getElementsByTagName("LightPath");
		if (LightPath_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"LightPath node list size %d != 1",
					LightPath_nodeList.getLength()));
		}
		else if (LightPath_nodeList.getLength() != 0)
		{
			// Element property LightPath which is complex (has
			// sub-elements)
			setLightPath(new LightPath(
					(Element) LightPath_nodeList.item(0)));
		}
	}

	// -- Channel API methods --


	// Property
	public Double getPinholeSize()
	{
		return pinholeSize;
	}

	public void setPinholeSize(Double pinholeSize)
	{
		this.pinholeSize = pinholeSize;
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
	public AcquisitionMode getAcquisitionMode()
	{
		return acquisitionMode;
	}

	public void setAcquisitionMode(AcquisitionMode acquisitionMode)
	{
		this.acquisitionMode = acquisitionMode;
	}

	// Property
	public Integer getColor()
	{
		return color;
	}

	public void setColor(Integer color)
	{
		this.color = color;
	}

	// Property
	public ContrastMethod getContrastMethod()
	{
		return contrastMethod;
	}

	public void setContrastMethod(ContrastMethod contrastMethod)
	{
		this.contrastMethod = contrastMethod;
	}

	// Property
	public Integer getExcitationWavelength()
	{
		return excitationWavelength;
	}

	public void setExcitationWavelength(Integer excitationWavelength)
	{
		this.excitationWavelength = excitationWavelength;
	}

	// Property
	public IlluminationType getIlluminationType()
	{
		return illuminationType;
	}

	public void setIlluminationType(IlluminationType illuminationType)
	{
		this.illuminationType = illuminationType;
	}

	// Property
	public String getFluor()
	{
		return fluor;
	}

	public void setFluor(String fluor)
	{
		this.fluor = fluor;
	}

	// Property
	public Integer getPockelCellSetting()
	{
		return pockelCellSetting;
	}

	public void setPockelCellSetting(Integer pockelCellSetting)
	{
		this.pockelCellSetting = pockelCellSetting;
	}

	// Property
	public Integer getEmissionWavelength()
	{
		return emissionWavelength;
	}

	public void setEmissionWavelength(Integer emissionWavelength)
	{
		this.emissionWavelength = emissionWavelength;
	}

	// Property
	public Double getNDFilter()
	{
		return ndfilter;
	}

	public void setNDFilter(Double ndfilter)
	{
		this.ndfilter = ndfilter;
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
	public Integer getSamplesPerPixel()
	{
		return samplesPerPixel;
	}

	public void setSamplesPerPixel(Integer samplesPerPixel)
	{
		this.samplesPerPixel = samplesPerPixel;
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

	// Reference which occurs more than once
	public int sizeOfLinkedAnnotationList()
	{
		return annotationList.size();
	}

	public List<Annotation> copyLinkedAnnotationList()
	{
		return new ArrayList<Annotation>(annotationList);
	}

	public Annotation getLinkedAnnotation(int index)
	{
		return annotationList.get(index);
	}

	public Annotation setLinkedAnnotation(int index, Annotation o)
	{
		return annotationList.set(index, o);
	}

	public boolean linkAnnotation(Annotation o)
	{

		o.linkChannel(this);
		return annotationList.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{

		o.unlinkChannel(this);
		return annotationList.remove(o);
	}

	// Property
	public LightPath getLightPath()
	{
		return lightPath;
	}

	public void setLightPath(LightPath lightPath)
	{
		this.lightPath = lightPath;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Channel_element)
	{
		// Creating XML block for Channel
		if (Channel_element == null)
		{
			Channel_element =
					document.createElementNS(NAMESPACE, "Channel");
		}

		if (pinholeSize != null)
		{
			// Attribute property PinholeSize
			Channel_element.setAttribute("PinholeSize", pinholeSize.toString());
		}
		if (name != null)
		{
			// Attribute property Name
			Channel_element.setAttribute("Name", name.toString());
		}
		if (acquisitionMode != null)
		{
			// Attribute property AcquisitionMode
			Channel_element.setAttribute("AcquisitionMode", acquisitionMode.toString());
		}
		if (color != null)
		{
			// Attribute property Color
			Channel_element.setAttribute("Color", color.toString());
		}
		if (contrastMethod != null)
		{
			// Attribute property ContrastMethod
			Channel_element.setAttribute("ContrastMethod", contrastMethod.toString());
		}
		if (excitationWavelength != null)
		{
			// Attribute property ExcitationWavelength
			Channel_element.setAttribute("ExcitationWavelength", excitationWavelength.toString());
		}
		if (illuminationType != null)
		{
			// Attribute property IlluminationType
			Channel_element.setAttribute("IlluminationType", illuminationType.toString());
		}
		if (fluor != null)
		{
			// Attribute property Fluor
			Channel_element.setAttribute("Fluor", fluor.toString());
		}
		if (pockelCellSetting != null)
		{
			// Attribute property PockelCellSetting
			Channel_element.setAttribute("PockelCellSetting", pockelCellSetting.toString());
		}
		if (emissionWavelength != null)
		{
			// Attribute property EmissionWavelength
			Channel_element.setAttribute("EmissionWavelength", emissionWavelength.toString());
		}
		if (ndfilter != null)
		{
			// Attribute property NDFilter
			Channel_element.setAttribute("NDFilter", ndfilter.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Channel_element.setAttribute("ID", id.toString());
		}
		if (samplesPerPixel != null)
		{
			// Attribute property SamplesPerPixel
			Channel_element.setAttribute("SamplesPerPixel", samplesPerPixel.toString());
		}
		if (lightSourceSettings != null)
		{
			// Element property LightSourceSettings which is complex (has
			// sub-elements)
			Channel_element.appendChild(lightSourceSettings.asXMLElement(document));
		}
		if (otf != null)
		{
			// Element property OTFRef which is complex (has
			// sub-elements)
			Channel_element.appendChild(otf.asXMLElement(document));
		}
		if (detectorSettings != null)
		{
			// Element property DetectorSettings which is complex (has
			// sub-elements)
			Channel_element.appendChild(detectorSettings.asXMLElement(document));
		}
		if (filterSet != null)
		{
			// Element property FilterSetRef which is complex (has
			// sub-elements)
			Channel_element.appendChild(filterSet.asXMLElement(document));
		}
		if (annotationList != null)
		{
			// Reference property AnnotationRef
			for (Annotation o : annotationList)
			{
				Element annotationList_element = 
						document.createElementNS(NAMESPACE, "AnnotationRefRef");
				annotationList_element.setAttribute("ID", o.getID());
				Channel_element.appendChild(annotationList_element);
			}
		}
		if (lightPath != null)
		{
			// Element property LightPath which is complex (has
			// sub-elements)
			Channel_element.appendChild(lightPath.asXMLElement(document));
		}
		return super.asXMLElement(document, Channel_element);
	}
}
