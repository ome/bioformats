/*
 * ome.xml.r201004.TiffData
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

public class TiffData extends Object
{
	// -- Instance variables --

	// Property
	private Integer ifd;

	// Property
	private Integer planeCount;

	// Property
	private Integer firstZ;

	// Property
	private Integer firstC;

	// Property
	private Integer firstT;

	// Property
	private UUID uuid;

	// -- Constructors --

	/** Constructs a TiffData. */
	public TiffData()
	{
	}

	// -- TiffData API methods --

	// Property
	public Integer getIFD()
	{
		return ifd;
	}

	public void setIFD(Integer ifd)
	{
		this.ifd = ifd;
	}

	// Property
	public Integer getPlaneCount()
	{
		return planeCount;
	}

	public void setPlaneCount(Integer planeCount)
	{
		this.planeCount = planeCount;
	}

	// Property
	public Integer getFirstZ()
	{
		return firstZ;
	}

	public void setFirstZ(Integer firstZ)
	{
		this.firstZ = firstZ;
	}

	// Property
	public Integer getFirstC()
	{
		return firstC;
	}

	public void setFirstC(Integer firstC)
	{
		this.firstC = firstC;
	}

	// Property
	public Integer getFirstT()
	{
		return firstT;
	}

	public void setFirstT(Integer firstT)
	{
		this.firstT = firstT;
	}

	// Property
	public UUID getUUID()
	{
		return uuid;
	}

	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for TiffData
		Element TiffData_element = document.createElement("TiffData");
		if (ifd != null)
		{
			// Attribute property IFD
			TiffData_element.setAttribute("IFD", ifd.toString());
		}
		if (planeCount != null)
		{
			// Attribute property PlaneCount
			TiffData_element.setAttribute("PlaneCount", planeCount.toString());
		}
		if (firstZ != null)
		{
			// Attribute property FirstZ
			TiffData_element.setAttribute("FirstZ", firstZ.toString());
		}
		if (firstC != null)
		{
			// Attribute property FirstC
			TiffData_element.setAttribute("FirstC", firstC.toString());
		}
		if (firstT != null)
		{
			// Attribute property FirstT
			TiffData_element.setAttribute("FirstT", firstT.toString());
		}
		if (uuid != null)
		{
			// Element property UUID which is complex (has
			// sub-elements)
			TiffData_element.appendChild(uuid.asXMLElement(document));
		}
		return TiffData_element;
	}
}
