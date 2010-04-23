/*
 * ome.xml.r201004.Pixels
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
 * Created by callan via xsd-fu on 2010-04-23 17:38:00+0100
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

public class Pixels extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2010-04";

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
	private List<BinData> binDataList = new ArrayList<BinData>();

	// Property which occurs more than once
	private List<TiffData> tiffDataList = new ArrayList<TiffData>();

	// Property
	private MetadataOnly metadataOnly;

	// Property which occurs more than once
	private List<Plane> planeList = new ArrayList<Plane>();

	// Reference AnnotationRef
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// -- Constructors --

	/** Default constructor. */
	public Pixels()
	{
		super();
	}

	/** 
	 * Constructs Pixels recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Pixels(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}
	
	// -- Custom content from Pixels specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Pixels recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Pixels".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of Pixels got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of Pixels got %s",
			//		tagName));
		}
		if (element.hasAttribute("SizeT"))
		{
			// Attribute property SizeT
			setSizeT(Integer.valueOf(
					element.getAttribute("SizeT")));
		}
		if (element.hasAttribute("DimensionOrder"))
		{
			// Attribute property which is an enumeration DimensionOrder
			setDimensionOrder(DimensionOrder.fromString(
					element.getAttribute("DimensionOrder")));
		}
		if (element.hasAttribute("TimeIncrement"))
		{
			// Attribute property TimeIncrement
			setTimeIncrement(Double.valueOf(
					element.getAttribute("TimeIncrement")));
		}
		if (element.hasAttribute("PhysicalSizeY"))
		{
			// Attribute property PhysicalSizeY
			setPhysicalSizeY(Double.valueOf(
					element.getAttribute("PhysicalSizeY")));
		}
		if (element.hasAttribute("PhysicalSizeX"))
		{
			// Attribute property PhysicalSizeX
			setPhysicalSizeX(Double.valueOf(
					element.getAttribute("PhysicalSizeX")));
		}
		if (element.hasAttribute("PhysicalSizeZ"))
		{
			// Attribute property PhysicalSizeZ
			setPhysicalSizeZ(Double.valueOf(
					element.getAttribute("PhysicalSizeZ")));
		}
		if (element.hasAttribute("SizeX"))
		{
			// Attribute property SizeX
			setSizeX(Integer.valueOf(
					element.getAttribute("SizeX")));
		}
		if (element.hasAttribute("SizeY"))
		{
			// Attribute property SizeY
			setSizeY(Integer.valueOf(
					element.getAttribute("SizeY")));
		}
		if (element.hasAttribute("SizeZ"))
		{
			// Attribute property SizeZ
			setSizeZ(Integer.valueOf(
					element.getAttribute("SizeZ")));
		}
		if (element.hasAttribute("SizeC"))
		{
			// Attribute property SizeC
			setSizeC(Integer.valueOf(
					element.getAttribute("SizeC")));
		}
		if (element.hasAttribute("Type"))
		{
			// Attribute property which is an enumeration Type
			setType(PixelType.fromString(
					element.getAttribute("Type")));
		}
		if (!element.hasAttribute("ID"))
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Pixels missing required ID property."));
		}
		// ID property
		setID(String.valueOf(
					element.getAttribute("ID")));
		// Adding this model object to the model handler
	    	model.addModelObject(getID(), this);
		// Element property Channel which is complex (has
		// sub-elements) and occurs more than once
		NodeList Channel_nodeList = element.getElementsByTagName("Channel");
		for (int i = 0; i < Channel_nodeList.getLength(); i++)
		{
			Element Channel_element = (Element) Channel_nodeList.item(i);
			addChannel(
					new Channel(Channel_element, model));
		}
		// Element property BinData which is complex (has
		// sub-elements) and occurs more than once
		NodeList BinData_nodeList = element.getElementsByTagName("BinData");
		for (int i = 0; i < BinData_nodeList.getLength(); i++)
		{
			Element BinData_element = (Element) BinData_nodeList.item(i);
			addBinData(
					new BinData(BinData_element, model));
		}
		// Element property TiffData which is complex (has
		// sub-elements) and occurs more than once
		NodeList TiffData_nodeList = element.getElementsByTagName("TiffData");
		for (int i = 0; i < TiffData_nodeList.getLength(); i++)
		{
			Element TiffData_element = (Element) TiffData_nodeList.item(i);
			addTiffData(
					new TiffData(TiffData_element, model));
		}
		NodeList MetadataOnly_nodeList = element.getElementsByTagName("MetadataOnly");
		if (MetadataOnly_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"MetadataOnly node list size %d != 1",
					MetadataOnly_nodeList.getLength()));
		}
		else if (MetadataOnly_nodeList.getLength() != 0)
		{
			// Element property MetadataOnly which is complex (has
			// sub-elements)
			setMetadataOnly(new MetadataOnly(
					(Element) MetadataOnly_nodeList.item(0), model));
		}
		// Element property Plane which is complex (has
		// sub-elements) and occurs more than once
		NodeList Plane_nodeList = element.getElementsByTagName("Plane");
		for (int i = 0; i < Plane_nodeList.getLength(); i++)
		{
			Element Plane_element = (Element) Plane_nodeList.item(i);
			addPlane(
					new Plane(Plane_element, model));
		}
		// Element reference AnnotationRef
		NodeList AnnotationRef_nodeList = element.getElementsByTagName("AnnotationRef");
		for (int i = 0; i < AnnotationRef_nodeList.getLength(); i++)
		{
			Element AnnotationRef_element = (Element) AnnotationRef_nodeList.item(i);
			AnnotationRef annotationList_reference = new AnnotationRef();
			annotationList_reference.setID(AnnotationRef_element.getAttribute("ID"));
			model.addReference(this, annotationList_reference);
		}
	}

	// -- Pixels API methods --

	public void link(Reference reference, OMEModelObject o)
	{
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkPixels(this);
			annotationList.add(o_casted);
			return;
		}
		// TODO: Should be its own Exception
		throw new RuntimeException(
				"Unable to handle reference of type: " + reference.getClass());
	}


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

	public List<BinData> copyBinDataList()
	{
		return new ArrayList<BinData>(binDataList);
	}

	public BinData getBinData(int index)
	{
		return binDataList.get(index);
	}

	public BinData setBinData(int index, BinData binData)
	{
		return binDataList.set(index, binData);
	}

	public void addBinData(BinData binData)
	{
		binDataList.add(binData);
	}

	public void removeBinData(BinData binData)
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
		o.linkPixels(this);
		return annotationList.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{
		o.unlinkPixels(this);
		return annotationList.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Pixels_element)
	{
		// Creating XML block for Pixels
		if (Pixels_element == null)
		{
			Pixels_element =
					document.createElementNS(NAMESPACE, "Pixels");
		}

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
			// Element property BinData which is complex (has
			// sub-elements) and occurs more than once
			for (BinData binDataList_value : binDataList)
			{
				Pixels_element.appendChild(binDataList_value.asXMLElement(document));
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
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationList_value.getID());
				Pixels_element.appendChild(o.asXMLElement(document));
			}
		}
		return super.asXMLElement(document, Pixels_element);
	}
}
