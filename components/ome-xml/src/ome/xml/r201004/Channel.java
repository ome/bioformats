/*
 * ome.xml.r201004.Channel
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

public class Channel extends Object
{
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

	// Property which occurs more than once
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Property
	private LightPath lightPath;

	// -- Constructors --

	/** Constructs a Channel. */
	public Channel()
	{
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

	// Property
	public OTF getOTF()
	{
		return otf;
	}

	public void setOTF(OTF otf)
	{
		this.otf = otf;
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

	// Property
	public FilterSet getFilterSet()
	{
		return filterSet;
	}

	public void setFilterSet(FilterSet filterSet)
	{
		this.filterSet = filterSet;
	}

	// Property which occurs more than once
	public int sizeOfAnnotationList()
	{
		return annotationList.size();
	}

	public List<Annotation> copyAnnotationList()
	{
		return new ArrayList<Annotation>(annotationList);
	}

	public Annotation getAnnotation(int index)
	{
		return annotationList.get(index);
	}

	public Annotation setAnnotation(int index, Annotation annotation)
	{
		return annotationList.set(index, annotation);
	}

	public void addAnnotation(Annotation annotation)
	{
		annotationList.add(annotation);
	}

	public void removeAnnotation(Annotation annotation)
	{
		annotationList.remove(annotation);
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
		// Creating XML block for Channel
		Element Channel_element = document.createElement("Channel");
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
			// Element property AnnotationRef which is complex (has
			// sub-elements) and occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				Channel_element.appendChild(annotationList_value.asXMLElement(document));
			}
		}
		if (lightPath != null)
		{
			// Element property LightPath which is complex (has
			// sub-elements)
			Channel_element.appendChild(lightPath.asXMLElement(document));
		}
		return Channel_element;
	}
}
