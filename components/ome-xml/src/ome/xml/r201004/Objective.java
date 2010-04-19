/*
 * ome.xml.r201004.Objective
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

public class Objective extends ManufacturerSpec
{
	// -- Instance variables --

	// Property
	private Boolean iris;

	// Property
	private Double workingDistance;

	// Property
	private Immersion immersion;

	// Property
	private Correction correction;

	// Property
	private Double lensNA;

	// Property
	private Integer nominalMagnification;

	// Property
	private Double calibratedMagnification;

	// Property
	private String id;

	// -- Constructors --

	/** Constructs a Objective. */
	public Objective()
	{
	}

	// -- Objective API methods --

	// Property
	public Boolean getIris()
	{
		return iris;
	}

	public void setIris(Boolean iris)
	{
		this.iris = iris;
	}

	// Property
	public Double getWorkingDistance()
	{
		return workingDistance;
	}

	public void setWorkingDistance(Double workingDistance)
	{
		this.workingDistance = workingDistance;
	}

	// Property
	public Immersion getImmersion()
	{
		return immersion;
	}

	public void setImmersion(Immersion immersion)
	{
		this.immersion = immersion;
	}

	// Property
	public Correction getCorrection()
	{
		return correction;
	}

	public void setCorrection(Correction correction)
	{
		this.correction = correction;
	}

	// Property
	public Double getLensNA()
	{
		return lensNA;
	}

	public void setLensNA(Double lensNA)
	{
		this.lensNA = lensNA;
	}

	// Property
	public Integer getNominalMagnification()
	{
		return nominalMagnification;
	}

	public void setNominalMagnification(Integer nominalMagnification)
	{
		this.nominalMagnification = nominalMagnification;
	}

	// Property
	public Double getCalibratedMagnification()
	{
		return calibratedMagnification;
	}

	public void setCalibratedMagnification(Double calibratedMagnification)
	{
		this.calibratedMagnification = calibratedMagnification;
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

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Objective
		Element Objective_element = document.createElement("Objective");
		if (iris != null)
		{
			// Attribute property Iris
			Objective_element.setAttribute("Iris", iris.toString());
		}
		if (workingDistance != null)
		{
			// Attribute property WorkingDistance
			Objective_element.setAttribute("WorkingDistance", workingDistance.toString());
		}
		if (immersion != null)
		{
			// Attribute property Immersion
			Objective_element.setAttribute("Immersion", immersion.toString());
		}
		if (correction != null)
		{
			// Attribute property Correction
			Objective_element.setAttribute("Correction", correction.toString());
		}
		if (lensNA != null)
		{
			// Attribute property LensNA
			Objective_element.setAttribute("LensNA", lensNA.toString());
		}
		if (nominalMagnification != null)
		{
			// Attribute property NominalMagnification
			Objective_element.setAttribute("NominalMagnification", nominalMagnification.toString());
		}
		if (calibratedMagnification != null)
		{
			// Attribute property CalibratedMagnification
			Objective_element.setAttribute("CalibratedMagnification", calibratedMagnification.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Objective_element.setAttribute("ID", id.toString());
		}
		return Objective_element;
	}
}
