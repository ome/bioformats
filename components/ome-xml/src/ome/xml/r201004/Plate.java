/*
 * ome.xml.r201004.Plate
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
 * Created by callan via xsd-fu on 2010-04-20 12:31:20+0100
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

public class Plate extends Object
{
	// -- Instance variables --

	// Property
	private String status;

	// Property
	private Integer rows;

	// Property
	private String externalIdentifier;

	// Property
	private NamingConvention rowNamingConvention;

	// Property
	private NamingConvention columnNamingConvention;

	// Property
	private Double wellOriginY;

	// Property
	private Double wellOriginX;

	// Property
	private String id;

	// Property
	private Integer columns;

	// Property
	private String name;

	// Property
	private String description;

	// Property which occurs more than once
	private List<Screen> screenList = new ArrayList<Screen>();

	// Property which occurs more than once
	private List<Well> wellList = new ArrayList<Well>();

	// Property which occurs more than once
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Property which occurs more than once
	private List<PlateAcquisition> plateAcquisitionList = new ArrayList<PlateAcquisition>();

	// Back reference Screen_BackReference
	private List<Screen> screen_BackReferenceList = new ArrayList<Screen>();

	// -- Constructors --

	/** Constructs a Plate. */
	public Plate()
	{
	}

	// -- Plate API methods --

	// Property
	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	// Property
	public Integer getRows()
	{
		return rows;
	}

	public void setRows(Integer rows)
	{
		this.rows = rows;
	}

	// Property
	public String getExternalIdentifier()
	{
		return externalIdentifier;
	}

	public void setExternalIdentifier(String externalIdentifier)
	{
		this.externalIdentifier = externalIdentifier;
	}

	// Property
	public NamingConvention getRowNamingConvention()
	{
		return rowNamingConvention;
	}

	public void setRowNamingConvention(NamingConvention rowNamingConvention)
	{
		this.rowNamingConvention = rowNamingConvention;
	}

	// Property
	public NamingConvention getColumnNamingConvention()
	{
		return columnNamingConvention;
	}

	public void setColumnNamingConvention(NamingConvention columnNamingConvention)
	{
		this.columnNamingConvention = columnNamingConvention;
	}

	// Property
	public Double getWellOriginY()
	{
		return wellOriginY;
	}

	public void setWellOriginY(Double wellOriginY)
	{
		this.wellOriginY = wellOriginY;
	}

	// Property
	public Double getWellOriginX()
	{
		return wellOriginX;
	}

	public void setWellOriginX(Double wellOriginX)
	{
		this.wellOriginX = wellOriginX;
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
	public Integer getColumns()
	{
		return columns;
	}

	public void setColumns(Integer columns)
	{
		this.columns = columns;
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
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	// Property which occurs more than once
	public int sizeOfScreenList()
	{
		return screenList.size();
	}

	public List<Screen> copyScreenList()
	{
		return new ArrayList<Screen>(screenList);
	}

	public Screen getScreen(int index)
	{
		return screenList.get(index);
	}

	public Screen setScreen(int index, Screen screen)
	{
		return screenList.set(index, screen);
	}

	public void addScreen(Screen screen)
	{
		screenList.add(screen);
	}

	public void removeScreen(Screen screen)
	{
		screenList.remove(screen);
	}

	// Property which occurs more than once
	public int sizeOfWellList()
	{
		return wellList.size();
	}

	public List<Well> copyWellList()
	{
		return new ArrayList<Well>(wellList);
	}

	public Well getWell(int index)
	{
		return wellList.get(index);
	}

	public Well setWell(int index, Well well)
	{
		return wellList.set(index, well);
	}

	public void addWell(Well well)
	{
		wellList.add(well);
	}

	public void removeWell(Well well)
	{
		wellList.remove(well);
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

	// Property which occurs more than once
	public int sizeOfPlateAcquisitionList()
	{
		return plateAcquisitionList.size();
	}

	public List<PlateAcquisition> copyPlateAcquisitionList()
	{
		return new ArrayList<PlateAcquisition>(plateAcquisitionList);
	}

	public PlateAcquisition getPlateAcquisition(int index)
	{
		return plateAcquisitionList.get(index);
	}

	public PlateAcquisition setPlateAcquisition(int index, PlateAcquisition plateAcquisition)
	{
		return plateAcquisitionList.set(index, plateAcquisition);
	}

	public void addPlateAcquisition(PlateAcquisition plateAcquisition)
	{
		plateAcquisitionList.add(plateAcquisition);
	}

	public void removePlateAcquisition(PlateAcquisition plateAcquisition)
	{
		plateAcquisitionList.remove(plateAcquisition);
	}

	// Back reference Screen_BackReference
	public int sizeOfLinkedScreenList()
	{
		return screen_BackReferenceList.size();
	}

	public List<Screen> copyLinkedScreenList()
	{
		return new ArrayList<Screen>(screen_BackReferenceList);
	}

	public Screen getLinkedScreen(int index)
	{
		return screen_BackReferenceList.get(index);
	}

	public Screen setLinkedScreen(int index, Screen screen_BackReference)
	{
		return screen_BackReferenceList.set(index, screen_BackReference);
	}

	public void linkScreen(Screen screen_BackReference)
	{
		this.screen_BackReferenceList.add(screen_BackReference);
	}

	public void unlinkScreen(Screen screen_BackReference)
	{
		this.screen_BackReferenceList.add(screen_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Plate
		Element Plate_element = document.createElement("Plate");
		if (status != null)
		{
			// Attribute property Status
			Plate_element.setAttribute("Status", status.toString());
		}
		if (rows != null)
		{
			// Attribute property Rows
			Plate_element.setAttribute("Rows", rows.toString());
		}
		if (externalIdentifier != null)
		{
			// Attribute property ExternalIdentifier
			Plate_element.setAttribute("ExternalIdentifier", externalIdentifier.toString());
		}
		if (rowNamingConvention != null)
		{
			// Attribute property RowNamingConvention
			Plate_element.setAttribute("RowNamingConvention", rowNamingConvention.toString());
		}
		if (columnNamingConvention != null)
		{
			// Attribute property ColumnNamingConvention
			Plate_element.setAttribute("ColumnNamingConvention", columnNamingConvention.toString());
		}
		if (wellOriginY != null)
		{
			// Attribute property WellOriginY
			Plate_element.setAttribute("WellOriginY", wellOriginY.toString());
		}
		if (wellOriginX != null)
		{
			// Attribute property WellOriginX
			Plate_element.setAttribute("WellOriginX", wellOriginX.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Plate_element.setAttribute("ID", id.toString());
		}
		if (columns != null)
		{
			// Attribute property Columns
			Plate_element.setAttribute("Columns", columns.toString());
		}
		if (name != null)
		{
			// Attribute property Name
			Plate_element.setAttribute("Name", name.toString());
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = document.createElement("Description");
			description_element.setTextContent(description);
			Plate_element.appendChild(description_element);
		}
		if (screenList != null)
		{
			// Element property ScreenRef which is complex (has
			// sub-elements) and occurs more than once
			for (Screen screenList_value : screenList)
			{
				Plate_element.appendChild(screenList_value.asXMLElement(document));
			}
		}
		if (wellList != null)
		{
			// Element property Well which is complex (has
			// sub-elements) and occurs more than once
			for (Well wellList_value : wellList)
			{
				Plate_element.appendChild(wellList_value.asXMLElement(document));
			}
		}
		if (annotationList != null)
		{
			// Element property AnnotationRef which is complex (has
			// sub-elements) and occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				Plate_element.appendChild(annotationList_value.asXMLElement(document));
			}
		}
		if (plateAcquisitionList != null)
		{
			// Element property PlateAcquisition which is complex (has
			// sub-elements) and occurs more than once
			for (PlateAcquisition plateAcquisitionList_value : plateAcquisitionList)
			{
				Plate_element.appendChild(plateAcquisitionList_value.asXMLElement(document));
			}
		}
		if (screen_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Screen_BackReference
		}
		return Plate_element;
	}

	public static Plate fromXMLElement(Element element)
		throws EnumerationException
	{
		String tagName = element.getTagName();
		if (!"Plate".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of Plate got %s",
					tagName));
		}
		Plate instance = new Plate();
		if (element.hasAttribute("Status"))
		{
			// Attribute property Status
			instance.setStatus(String.valueOf(
					element.getAttribute("Status")));
		}
		if (element.hasAttribute("Rows"))
		{
			// Attribute property Rows
			instance.setRows(Integer.valueOf(
					element.getAttribute("Rows")));
		}
		if (element.hasAttribute("ExternalIdentifier"))
		{
			// Attribute property ExternalIdentifier
			instance.setExternalIdentifier(String.valueOf(
					element.getAttribute("ExternalIdentifier")));
		}
		if (element.hasAttribute("RowNamingConvention"))
		{
			// Attribute property which is an enumeration RowNamingConvention
			instance.setRowNamingConvention(NamingConvention.fromString(
					element.getAttribute("RowNamingConvention")));
		}
		if (element.hasAttribute("ColumnNamingConvention"))
		{
			// Attribute property which is an enumeration ColumnNamingConvention
			instance.setColumnNamingConvention(NamingConvention.fromString(
					element.getAttribute("ColumnNamingConvention")));
		}
		if (element.hasAttribute("WellOriginY"))
		{
			// Attribute property WellOriginY
			instance.setWellOriginY(Double.valueOf(
					element.getAttribute("WellOriginY")));
		}
		if (element.hasAttribute("WellOriginX"))
		{
			// Attribute property WellOriginX
			instance.setWellOriginX(Double.valueOf(
					element.getAttribute("WellOriginX")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			instance.setID(String.valueOf(
					element.getAttribute("ID")));
		}
		if (element.hasAttribute("Columns"))
		{
			// Attribute property Columns
			instance.setColumns(Integer.valueOf(
					element.getAttribute("Columns")));
		}
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			instance.setName(String.valueOf(
					element.getAttribute("Name")));
		}
		NodeList Description_nodeList = element.getElementsByTagName("Description");
		if (Description_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Description node list size %d != 1",
					Description_nodeList.getLength()));
		}
		else if (Description_nodeList.getLength() != 0)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			instance.setDescription(Description_nodeList.item(0).getTextContent());
		}
		// Element property ScreenRef which is complex (has
		// sub-elements) and occurs more than once
		NodeList ScreenRef_nodeList = element.getElementsByTagName("ScreenRef");
		for (int i = 0; i < ScreenRef_nodeList.getLength(); i++)
		{
			instance.addScreen(Screen.fromXMLElement(
					(Element) ScreenRef_nodeList.item(i)));
		}
		// Element property Well which is complex (has
		// sub-elements) and occurs more than once
		NodeList Well_nodeList = element.getElementsByTagName("Well");
		for (int i = 0; i < Well_nodeList.getLength(); i++)
		{
			instance.addWell(Well.fromXMLElement(
					(Element) Well_nodeList.item(i)));
		}
		// Element property AnnotationRef which is complex (has
		// sub-elements) and occurs more than once
		NodeList AnnotationRef_nodeList = element.getElementsByTagName("AnnotationRef");
		for (int i = 0; i < AnnotationRef_nodeList.getLength(); i++)
		{
			instance.addAnnotation(Annotation.fromXMLElement(
					(Element) AnnotationRef_nodeList.item(i)));
		}
		// Element property PlateAcquisition which is complex (has
		// sub-elements) and occurs more than once
		NodeList PlateAcquisition_nodeList = element.getElementsByTagName("PlateAcquisition");
		for (int i = 0; i < PlateAcquisition_nodeList.getLength(); i++)
		{
			instance.addPlateAcquisition(PlateAcquisition.fromXMLElement(
					(Element) PlateAcquisition_nodeList.item(i)));
		}
		// *** IGNORING *** Skipped back reference Screen_BackReference
		return instance;
	}
}
