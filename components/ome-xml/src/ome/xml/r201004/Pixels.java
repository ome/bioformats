/*
 * ome.xml.r201004.Pixels
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

public class Pixels extends Object
{
	// -- Instance variables --

	// Property
	private Integer sizeT;

	// Property
	private DimensionOrder dimensionOrder;

	// Property
	private Double timeIncrement;

	// Property
	private Double physicalSizeY;

	// Property
	private Double physicalSizeX;

	// Property
	private Double physicalSizeZ;

	// Property
	private Integer sizeX;

	// Property
	private Integer sizeY;

	// Property
	private Integer sizeZ;

	// Property
	private Integer sizeC;

	// Property
	private PixelType type;

	// Property
	private String id;

	// Property which occurs more than once
	private List<Channel> channelList = new ArrayList<Channel>();

	// Property which occurs more than once
	private List<String> binDataList = new ArrayList<String>();

	// Property which occurs more than once
	private List<TiffData> tiffDataList = new ArrayList<TiffData>();

	// Property
	private MetadataOnly metadataOnly;

	// Property which occurs more than once
	private List<Plane> planeList = new ArrayList<Plane>();

	// Property which occurs more than once
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// -- Constructors --

	/** Constructs a Pixels. */
	public Pixels()
	{
	}

	// -- Pixels API methods --

	// Property
	public Integer getSizeT()
	{
		return sizeT;
	}

	public void setSizeT(Integer sizeT)
	{
		this.sizeT = sizeT;
	}

	// Property
	public DimensionOrder getDimensionOrder()
	{
		return dimensionOrder;
	}

	public void setDimensionOrder(DimensionOrder dimensionOrder)
	{
		this.dimensionOrder = dimensionOrder;
	}

	// Property
	public Double getTimeIncrement()
	{
		return timeIncrement;
	}

	public void setTimeIncrement(Double timeIncrement)
	{
		this.timeIncrement = timeIncrement;
	}

	// Property
	public Double getPhysicalSizeY()
	{
		return physicalSizeY;
	}

	public void setPhysicalSizeY(Double physicalSizeY)
	{
		this.physicalSizeY = physicalSizeY;
	}

	// Property
	public Double getPhysicalSizeX()
	{
		return physicalSizeX;
	}

	public void setPhysicalSizeX(Double physicalSizeX)
	{
		this.physicalSizeX = physicalSizeX;
	}

	// Property
	public Double getPhysicalSizeZ()
	{
		return physicalSizeZ;
	}

	public void setPhysicalSizeZ(Double physicalSizeZ)
	{
		this.physicalSizeZ = physicalSizeZ;
	}

	// Property
	public Integer getSizeX()
	{
		return sizeX;
	}

	public void setSizeX(Integer sizeX)
	{
		this.sizeX = sizeX;
	}

	// Property
	public Integer getSizeY()
	{
		return sizeY;
	}

	public void setSizeY(Integer sizeY)
	{
		this.sizeY = sizeY;
	}

	// Property
	public Integer getSizeZ()
	{
		return sizeZ;
	}

	public void setSizeZ(Integer sizeZ)
	{
		this.sizeZ = sizeZ;
	}

	// Property
	public Integer getSizeC()
	{
		return sizeC;
	}

	public void setSizeC(Integer sizeC)
	{
		this.sizeC = sizeC;
	}

	// Property
	public PixelType getType()
	{
		return type;
	}

	public void setType(PixelType type)
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

	// Property which occurs more than once
	public int sizeOfChannelList()
	{
		return channelList.size();
	}

	public List<Channel> copyChannelList()
	{
		return new ArrayList<Channel>(channelList);
	}

	public Channel getChannel(int index)
	{
		return channelList.get(index);
	}

	public Channel setChannel(int index, Channel channel)
	{
		return channelList.set(index, channel);
	}

	public void addChannel(Channel channel)
	{
		channelList.add(channel);
	}

	public void removeChannel(Channel channel)
	{
		channelList.remove(channel);
	}

	// Property which occurs more than once
	public int sizeOfBinDataList()
	{
		return binDataList.size();
	}

	public List<String> copyBinDataList()
	{
		return new ArrayList<String>(binDataList);
	}

	public String getBinData(int index)
	{
		return binDataList.get(index);
	}

	public String setBinData(int index, String binData)
	{
		return binDataList.set(index, binData);
	}

	public void addBinData(String binData)
	{
		binDataList.add(binData);
	}

	public void removeBinData(String binData)
	{
		binDataList.remove(binData);
	}

	// Property which occurs more than once
	public int sizeOfTiffDataList()
	{
		return tiffDataList.size();
	}

	public List<TiffData> copyTiffDataList()
	{
		return new ArrayList<TiffData>(tiffDataList);
	}

	public TiffData getTiffData(int index)
	{
		return tiffDataList.get(index);
	}

	public TiffData setTiffData(int index, TiffData tiffData)
	{
		return tiffDataList.set(index, tiffData);
	}

	public void addTiffData(TiffData tiffData)
	{
		tiffDataList.add(tiffData);
	}

	public void removeTiffData(TiffData tiffData)
	{
		tiffDataList.remove(tiffData);
	}

	// Property
	public MetadataOnly getMetadataOnly()
	{
		return metadataOnly;
	}

	public void setMetadataOnly(MetadataOnly metadataOnly)
	{
		this.metadataOnly = metadataOnly;
	}

	// Property which occurs more than once
	public int sizeOfPlaneList()
	{
		return planeList.size();
	}

	public List<Plane> copyPlaneList()
	{
		return new ArrayList<Plane>(planeList);
	}

	public Plane getPlane(int index)
	{
		return planeList.get(index);
	}

	public Plane setPlane(int index, Plane plane)
	{
		return planeList.set(index, plane);
	}

	public void addPlane(Plane plane)
	{
		planeList.add(plane);
	}

	public void removePlane(Plane plane)
	{
		planeList.remove(plane);
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

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Pixels
		Element Pixels_element = document.createElement("Pixels");
		if (sizeT != null)
		{
			// Attribute property SizeT
			Pixels_element.setAttribute("SizeT", sizeT.toString());
		}
		if (dimensionOrder != null)
		{
			// Attribute property DimensionOrder
			Pixels_element.setAttribute("DimensionOrder", dimensionOrder.toString());
		}
		if (timeIncrement != null)
		{
			// Attribute property TimeIncrement
			Pixels_element.setAttribute("TimeIncrement", timeIncrement.toString());
		}
		if (physicalSizeY != null)
		{
			// Attribute property PhysicalSizeY
			Pixels_element.setAttribute("PhysicalSizeY", physicalSizeY.toString());
		}
		if (physicalSizeX != null)
		{
			// Attribute property PhysicalSizeX
			Pixels_element.setAttribute("PhysicalSizeX", physicalSizeX.toString());
		}
		if (physicalSizeZ != null)
		{
			// Attribute property PhysicalSizeZ
			Pixels_element.setAttribute("PhysicalSizeZ", physicalSizeZ.toString());
		}
		if (sizeX != null)
		{
			// Attribute property SizeX
			Pixels_element.setAttribute("SizeX", sizeX.toString());
		}
		if (sizeY != null)
		{
			// Attribute property SizeY
			Pixels_element.setAttribute("SizeY", sizeY.toString());
		}
		if (sizeZ != null)
		{
			// Attribute property SizeZ
			Pixels_element.setAttribute("SizeZ", sizeZ.toString());
		}
		if (sizeC != null)
		{
			// Attribute property SizeC
			Pixels_element.setAttribute("SizeC", sizeC.toString());
		}
		if (type != null)
		{
			// Attribute property Type
			Pixels_element.setAttribute("Type", type.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Pixels_element.setAttribute("ID", id.toString());
		}
		if (channelList != null)
		{
			// Element property Channel which is complex (has
			// sub-elements) and occurs more than once
			for (Channel channelList_value : channelList)
			{
				Pixels_element.appendChild(channelList_value.asXMLElement(document));
			}
		}
		if (binDataList != null)
		{
			// Element property BinData which is not complex (has no
			// sub-elements) which occurs more than once
			for (String binDataList_value : binDataList)
			{
				Element binDataList_element = document.createElement("BinData");
				binDataList_element.setTextContent(binDataList_value);
				Pixels_element.appendChild(binDataList_element);
			}
		}
		if (tiffDataList != null)
		{
			// Element property TiffData which is complex (has
			// sub-elements) and occurs more than once
			for (TiffData tiffDataList_value : tiffDataList)
			{
				Pixels_element.appendChild(tiffDataList_value.asXMLElement(document));
			}
		}
		if (metadataOnly != null)
		{
			// Element property MetadataOnly which is complex (has
			// sub-elements)
			Pixels_element.appendChild(metadataOnly.asXMLElement(document));
		}
		if (planeList != null)
		{
			// Element property Plane which is complex (has
			// sub-elements) and occurs more than once
			for (Plane planeList_value : planeList)
			{
				Pixels_element.appendChild(planeList_value.asXMLElement(document));
			}
		}
		if (annotationList != null)
		{
			// Element property AnnotationRef which is complex (has
			// sub-elements) and occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				Pixels_element.appendChild(annotationList_value.asXMLElement(document));
			}
		}
		return Pixels_element;
	}
}
