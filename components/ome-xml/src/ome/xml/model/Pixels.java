/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by rleigh via xsd-fu on 2013-02-04 15:52:35.744474
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

public class Pixels extends AbstractOMEModelObject
{
	// Base:  -- Name: Pixels -- Type: Pixels -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Pixels.class);

	// -- Instance variables --


	// Property
	private PositiveInteger sizeT;

	// Property
	private DimensionOrder dimensionOrder;

	// Property
	private Double timeIncrement;

	// Property
	private PositiveFloat physicalSizeY;

	// Property
	private PositiveFloat physicalSizeX;

	// Property
	private PositiveFloat physicalSizeZ;

	// Property
	private PositiveInteger sizeX;

	// Property
	private PositiveInteger sizeY;

	// Property
	private PositiveInteger sizeZ;

	// Property
	private PositiveInteger sizeC;

	// Property
	private PixelType type;

	// Property
	private String id;

	// Property which occurs more than once
	private List<Channel> channels = new ArrayList<Channel>();

	// Property which occurs more than once
	private List<BinData> binDataBlocks = new ArrayList<BinData>();

	// Property which occurs more than once
	private List<TiffData> tiffDataBlocks = new ArrayList<TiffData>();

	// Property
	private MetadataOnly metadataOnly;

	// Property which occurs more than once
	private List<Plane> planes = new ArrayList<Plane>();

	// Reference AnnotationRef
	private List<Annotation> annotationLinks = new ArrayList<Annotation>();

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
			LOGGER.debug("Expecting node name of Pixels got {}", tagName);
		}
		if (element.hasAttribute("SizeT"))
		{
			// Attribute property SizeT
			setSizeT(PositiveInteger.valueOf(
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
			setPhysicalSizeY(PositiveFloat.valueOf(
					element.getAttribute("PhysicalSizeY")));
		}
		if (element.hasAttribute("PhysicalSizeX"))
		{
			// Attribute property PhysicalSizeX
			setPhysicalSizeX(PositiveFloat.valueOf(
					element.getAttribute("PhysicalSizeX")));
		}
		if (element.hasAttribute("PhysicalSizeZ"))
		{
			// Attribute property PhysicalSizeZ
			setPhysicalSizeZ(PositiveFloat.valueOf(
					element.getAttribute("PhysicalSizeZ")));
		}
		if (element.hasAttribute("SizeX"))
		{
			// Attribute property SizeX
			setSizeX(PositiveInteger.valueOf(
					element.getAttribute("SizeX")));
		}
		if (element.hasAttribute("SizeY"))
		{
			// Attribute property SizeY
			setSizeY(PositiveInteger.valueOf(
					element.getAttribute("SizeY")));
		}
		if (element.hasAttribute("SizeZ"))
		{
			// Attribute property SizeZ
			setSizeZ(PositiveInteger.valueOf(
					element.getAttribute("SizeZ")));
		}
		if (element.hasAttribute("SizeC"))
		{
			// Attribute property SizeC
			setSizeC(PositiveInteger.valueOf(
					element.getAttribute("SizeC")));
		}
		if (element.hasAttribute("Type"))
		{
			// Attribute property which is an enumeration Type
			setType(PixelType.fromString(
					element.getAttribute("Type")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Pixels missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		// Element property Channel which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Channel_nodeList =
				getChildrenByTagName(element, "Channel");
		for (Element Channel_element : Channel_nodeList)
		{
			addChannel(
					new Channel(Channel_element, model));
		}
		// Element property BinData which is complex (has
		// sub-elements) and occurs more than once
		List<Element> BinData_nodeList =
				getChildrenByTagName(element, "BinData");
		for (Element BinData_element : BinData_nodeList)
		{
			addBinData(
					new BinData(BinData_element, model));
		}
		// Element property TiffData which is complex (has
		// sub-elements) and occurs more than once
		List<Element> TiffData_nodeList =
				getChildrenByTagName(element, "TiffData");
		for (Element TiffData_element : TiffData_nodeList)
		{
			addTiffData(
					new TiffData(TiffData_element, model));
		}
		List<Element> MetadataOnly_nodeList =
				getChildrenByTagName(element, "MetadataOnly");
		if (MetadataOnly_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"MetadataOnly node list size %d != 1",
					MetadataOnly_nodeList.size()));
		}
		else if (MetadataOnly_nodeList.size() != 0)
		{
			// Element property MetadataOnly which is complex (has
			// sub-elements)
			setMetadataOnly(new MetadataOnly(
					(Element) MetadataOnly_nodeList.get(0), model));
		}
		// Element property Plane which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Plane_nodeList =
				getChildrenByTagName(element, "Plane");
		for (Element Plane_element : Plane_nodeList)
		{
			addPlane(
					new Plane(Plane_element, model));
		}
		// Element reference AnnotationRef
		List<Element> AnnotationRef_nodeList =
				getChildrenByTagName(element, "AnnotationRef");
		for (Element AnnotationRef_element : AnnotationRef_nodeList)
		{
			AnnotationRef annotationLinks_reference = new AnnotationRef();
			annotationLinks_reference.setID(AnnotationRef_element.getAttribute("ID"));
			model.addReference(this, annotationLinks_reference);
		}
	}

	// -- Pixels API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkPixels(this);
			if (!annotationLinks.contains(o_casted)) {
				annotationLinks.add(o_casted);
			}
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property
	public PositiveInteger getSizeT()
	{
		return sizeT;
	}

	public void setSizeT(PositiveInteger sizeT)
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
	public PositiveFloat getPhysicalSizeY()
	{
		return physicalSizeY;
	}

	public void setPhysicalSizeY(PositiveFloat physicalSizeY)
	{
		this.physicalSizeY = physicalSizeY;
	}

	// Property
	public PositiveFloat getPhysicalSizeX()
	{
		return physicalSizeX;
	}

	public void setPhysicalSizeX(PositiveFloat physicalSizeX)
	{
		this.physicalSizeX = physicalSizeX;
	}

	// Property
	public PositiveFloat getPhysicalSizeZ()
	{
		return physicalSizeZ;
	}

	public void setPhysicalSizeZ(PositiveFloat physicalSizeZ)
	{
		this.physicalSizeZ = physicalSizeZ;
	}

	// Property
	public PositiveInteger getSizeX()
	{
		return sizeX;
	}

	public void setSizeX(PositiveInteger sizeX)
	{
		this.sizeX = sizeX;
	}

	// Property
	public PositiveInteger getSizeY()
	{
		return sizeY;
	}

	public void setSizeY(PositiveInteger sizeY)
	{
		this.sizeY = sizeY;
	}

	// Property
	public PositiveInteger getSizeZ()
	{
		return sizeZ;
	}

	public void setSizeZ(PositiveInteger sizeZ)
	{
		this.sizeZ = sizeZ;
	}

	// Property
	public PositiveInteger getSizeC()
	{
		return sizeC;
	}

	public void setSizeC(PositiveInteger sizeC)
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
		return channels.size();
	}

	public List<Channel> copyChannelList()
	{
		return new ArrayList<Channel>(channels);
	}

	public Channel getChannel(int index)
	{
		return channels.get(index);
	}

	public Channel setChannel(int index, Channel channel)
	{
        channel.setPixels(this);
		return channels.set(index, channel);
	}

	public void addChannel(Channel channel)
	{
        channel.setPixels(this);
		channels.add(channel);
	}

	public void removeChannel(Channel channel)
	{
		channels.remove(channel);
	}

	// Property which occurs more than once
	public int sizeOfBinDataList()
	{
		return binDataBlocks.size();
	}

	public List<BinData> copyBinDataList()
	{
		return new ArrayList<BinData>(binDataBlocks);
	}

	public BinData getBinData(int index)
	{
		return binDataBlocks.get(index);
	}

	public BinData setBinData(int index, BinData binData)
	{
        binData.setPixels(this);
		return binDataBlocks.set(index, binData);
	}

	public void addBinData(BinData binData)
	{
        binData.setPixels(this);
		binDataBlocks.add(binData);
	}

	public void removeBinData(BinData binData)
	{
		binDataBlocks.remove(binData);
	}

	// Property which occurs more than once
	public int sizeOfTiffDataList()
	{
		return tiffDataBlocks.size();
	}

	public List<TiffData> copyTiffDataList()
	{
		return new ArrayList<TiffData>(tiffDataBlocks);
	}

	public TiffData getTiffData(int index)
	{
		return tiffDataBlocks.get(index);
	}

	public TiffData setTiffData(int index, TiffData tiffData)
	{
        tiffData.setPixels(this);
		return tiffDataBlocks.set(index, tiffData);
	}

	public void addTiffData(TiffData tiffData)
	{
        tiffData.setPixels(this);
		tiffDataBlocks.add(tiffData);
	}

	public void removeTiffData(TiffData tiffData)
	{
		tiffDataBlocks.remove(tiffData);
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
		return planes.size();
	}

	public List<Plane> copyPlaneList()
	{
		return new ArrayList<Plane>(planes);
	}

	public Plane getPlane(int index)
	{
		return planes.get(index);
	}

	public Plane setPlane(int index, Plane plane)
	{
        plane.setPixels(this);
		return planes.set(index, plane);
	}

	public void addPlane(Plane plane)
	{
        plane.setPixels(this);
		planes.add(plane);
	}

	public void removePlane(Plane plane)
	{
		planes.remove(plane);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedAnnotationList()
	{
		return annotationLinks.size();
	}

	public List<Annotation> copyLinkedAnnotationList()
	{
		return new ArrayList<Annotation>(annotationLinks);
	}

	public Annotation getLinkedAnnotation(int index)
	{
		return annotationLinks.get(index);
	}

	public Annotation setLinkedAnnotation(int index, Annotation o)
	{
		return annotationLinks.set(index, o);
	}

	public boolean linkAnnotation(Annotation o)
	{

			o.linkPixels(this);
		if (!annotationLinks.contains(o)) {
			return annotationLinks.add(o);
		}
		return false;
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkPixels(this);
		return annotationLinks.remove(o);
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
		if (channels != null)
		{
			// Element property Channel which is complex (has
			// sub-elements) and occurs more than once
			for (Channel channels_value : channels)
			{
				Pixels_element.appendChild(channels_value.asXMLElement(document));
			}
		}
		if (binDataBlocks != null)
		{
			// Element property BinData which is complex (has
			// sub-elements) and occurs more than once
			for (BinData binDataBlocks_value : binDataBlocks)
			{
				Pixels_element.appendChild(binDataBlocks_value.asXMLElement(document));
			}
		}
		if (tiffDataBlocks != null)
		{
			// Element property TiffData which is complex (has
			// sub-elements) and occurs more than once
			for (TiffData tiffDataBlocks_value : tiffDataBlocks)
			{
				Pixels_element.appendChild(tiffDataBlocks_value.asXMLElement(document));
			}
		}
		if (metadataOnly != null)
		{
			// Element property MetadataOnly which is complex (has
			// sub-elements)
			Pixels_element.appendChild(metadataOnly.asXMLElement(document));
		}
		if (planes != null)
		{
			// Element property Plane which is complex (has
			// sub-elements) and occurs more than once
			for (Plane planes_value : planes)
			{
				Pixels_element.appendChild(planes_value.asXMLElement(document));
			}
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Pixels_element.appendChild(o.asXMLElement(document));
			}
		}
		return super.asXMLElement(document, Pixels_element);
	}
}
