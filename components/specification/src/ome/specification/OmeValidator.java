/*
 * #%L
 * The OME Data Model specification
 * %%
 * Copyright (C) 2003 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
/*
 * $Id$
 *
 *   Copyright 2006-2014 University of Dundee. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */

package ome.specification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.XMLConstants;

import org.w3c.dom.Document;

/**
 * Provides methods to validate OME instance documents against
 *
 * @since Beta4.4
 */
public class OmeValidator
{


   /** Path the schema language. */
    public static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    /** W3C. */
    public static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";

    /** The source. */
    public static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";

    public OmeValidator()
    {
    }
    /*
     * Validate the specified file
     * @param file The file to parse.
     * @param schema The schema used to validate the specified file.
     * @return true if no errors
     */
    public boolean isValidFile(File file, File schema)
    {
        try {
            // try parsing the file and return true if no errors
            Document theDocument = parseFile(file, schema);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * Validate the specified file
     * @param file The file to parse.
     * @param schemaStreamArray The schema as array of stream sources used to validate the specified file.
     * @return true if no errors
     */
    public boolean isValidFile(File file, StreamSource[] schemaStreamArray)
    {
        try {
            // try parsing the file and return true if no errors
            Document theDocument = parseFileWithStreamArray(file, schemaStreamArray);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * Validate the specified file
     * Any validation errors thrown as an exception.
     * @param file The file to parse.
     * @param schemaStreamArray The schema as array of stream sources used to validate the specified file.
     * @throws Exception Thrown if an error occurred.
     */
    public void validateFile(File file, StreamSource[] schemaStreamArray)
        throws Exception
    {
        // try parsing the file
        Document theDocument = parseFileWithStreamArray(file, schemaStreamArray);
    }

    /*
     * Validate the specified file
     * Any validation errors thrown as an exception.
     * @param file The file to parse.
     * @param schema The schema used to validate the specified file.
     * @throws Exception Thrown if an error occurred.
     */
    public void validateFile(File file, File schema)
        throws Exception
    {
        // try parsing the file
        Document theDocument = parseFile(file, schema);
    }

    /*
     * Validate the specified file
     * Any validation errors are sent to StdErr, not thrown as an exception.
     * @param file The file to parse.
     * @param schemaStreamArray The schema as array of stream sources used to validate the specified file.
     * @throws Exception Thrown if other (NON-VALIDATION) errors occurred.
     */
    public void validateFileToStdError(File file, StreamSource[] schemaStreamArray)
        throws Exception
    {
        // try parsing the file
        Document theDocument = parseFileWithStreamArrayToSdtErr(file, schemaStreamArray);
    }

    /**
     * Parses the specified file and returns the document.
     *
     * @param file The file to parse.
     * @param schema The schema used to validate the specified file.
     * @return
     * @throws Exception Thrown if an error occurred.
     */
    public Document parseFile(File file, File schema)
        throws Exception
    {
        if (file == null)
            throw new IllegalArgumentException("No file to parse.");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        if (schema != null) {
            dbf.setValidating(true);
            dbf.setNamespaceAware(true);
            dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            // Set the schema file
            dbf.setAttribute(JAXP_SCHEMA_SOURCE, schema);
        }
        DocumentBuilder builder = dbf.newDocumentBuilder();
        return builder.parse(file);
    }


    /**
     * Parses the specified file and returns the document.
     * Any validation errors are thrown as an exception.
     * @param file The file to parse.
     * @param schemaStreamArray The schema as array of stream sources used to validate the specified file.
     * @return
     * @throws Exception Thrown if an error occurred.
     */
    public Document parseFileWithStreamArray(File file, StreamSource[] schemaStreamArray)
        throws Exception
    {
        if (file == null)
            throw new IllegalArgumentException("No file to parse.");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true); // This must be set to avoid error : cvc-elt.1: Cannot find the declaration of element 'OME'.
        SchemaFactory sFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        SchemaResolver theTestClassResolver = new SchemaResolver();
        sFactory.setResourceResolver(theTestClassResolver);

        Schema theSchema = sFactory.newSchema( schemaStreamArray );

        // Version - two step parse then validate (throws error as exception)
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document theDoc = builder.parse(file);
        Validator validator=theSchema.newValidator();
        validator.validate(new DOMSource(theDoc));
        return theDoc;
    }

    /**
     * Parses the specified file and returns the document.
     * Any validation errors are sent to StdErr, not thrown as an exception.
     * @param file The file to parse.
     * @param schemaStreamArray The schema as array of stream sources used to validate the specified file.
     * @return
     * @throws Exception Thrown if other (NON-VALIDATION) errors occurred.
     */
    public Document parseFileWithStreamArrayToSdtErr(File file, StreamSource[] schemaStreamArray)
        throws Exception
    {
        if (file == null)
            throw new IllegalArgumentException("No file to parse.");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true); // This must be set to avoid error : cvc-elt.1: Cannot find the declaration of element 'OME'.
        SchemaFactory sFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        SchemaResolver theTestClassResolver = new SchemaResolver();
        sFactory.setResourceResolver(theTestClassResolver);

        Schema theSchema = sFactory.newSchema( schemaStreamArray );

        // Version - one step parse and validate (print error to stdErr)
        dbf.setSchema(theSchema);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document theDoc = builder.parse(file);
        return theDoc;
    }

}
