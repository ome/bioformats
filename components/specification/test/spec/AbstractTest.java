/*
 * $Id$
 *
 *   Copyright 2010 Glencoe Software, Inc. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */
package spec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

//Application-internal dependencies
import spec.SchemaResolver;

/**
 * Base test for unit tests.
 *
 * @since Beta4.2
 */
@Test(groups = { "unit" })
public class AbstractTest
    extends TestCase
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

    /** The OME-XML format. */
    public static final String OME_FORMAT = "ome";
    
    /** The OME-XML format. */
    public static final String OME_XML_FORMAT = "ome.xml";
    
    /** Holds the error, info, warning. */
    protected Log log = LogFactory.getLog(getClass());
    
    protected String rootpass;

    /**
     * Calculates a SHA-1 digest.
     * @param data Data to calcluate a SHA-1 digest for.
     * @param offset Offset within the byte buffer to calculate from.
     * @param len Number of bytes from <code>offset</code> to calculate with.
     * @return Hex string of the SHA-1 digest.
     */
    protected String sha1(byte[] data, int offset, int len)
    {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(
                    "Required SHA-1 message digest algorithm unavailable.");
        }
        md.update(data, offset, len);
        return byteArrayToHexString(md.digest());
    }

    /**
     * Calculates a SHA-1 digest.
     * @param data Data to calcluate a SHA-1 digest for.
     * @return Hex string of the SHA-1 digest.
     */
    protected String sha1(byte[] data)
    {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(
                    "Required SHA-1 message digest algorithm unavailable.");
        }
        md.update(data);
        return byteArrayToHexString(md.digest());
    }

    /**
     * Converts a byte array to a hex string.
     * @param in Byte array to convert.
     * @return Hex string representing the byte array.
     */
    public static String byteArrayToHexString(byte in[]) {

        byte ch = 0x00;
        int i = 0;

        if (in == null || in.length <= 0) {
            return null;
        }

        String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "a", "b", "c", "d", "e", "f" };

        StringBuffer out = new StringBuffer(in.length * 2);

        while (i < in.length) {
            ch = (byte) (in[i] & 0xF0);
            ch = (byte) (ch >>> 4);
            ch = (byte) (ch & 0x0F);
            out.append(pseudo[ch]);
            ch = (byte) (in[i] & 0x0F);
            out.append(pseudo[ch]);
            i++;
        }

        String rslt = new String(out);
        return rslt;
    }

    /**
     * Initializes the various services.
     * @throws Exception Thrown if an error occurred.
     */
    @Override
    @BeforeClass
    protected void setUp() 
        throws Exception
    {
    }

    /**
     * Closes the session.
     * @throws Exception Thrown if an error occurred.
     */
    @Override
    @AfterClass
    public void tearDown() 
        throws Exception
    {
    }

    /**
     */
    protected void clean() throws Exception {
    }

    /**
     * Transforms the input file using the specified stylesheet file.
     * 
     * @param input  The file to transform.
     * @param output The destination file.
     * @param xslt   The stylesheet file to use.
     * @throws Exception Thrown if an error occurred while encoding the image.
     */
    protected void transformFile(File input, File output, File xslt)
        throws Exception
    {
        if (input == null) 
            throw new IllegalArgumentException("No file to transform.");
        if (output == null) 
            throw new IllegalArgumentException("No destination file.");
        if (xslt == null) 
            throw new IllegalArgumentException("No stylesheet provided.");
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(
                new StreamSource(xslt));
        StreamResult result = new StreamResult(new FileOutputStream(output));
        transformer.transform(new StreamSource(input), result);
    }
    
    /**
     * Parses the specified file and returns the document.
     * 
     * @param file The file to parse.
     * @param schema The schema used to validate the specified file.
     * @return
     * @throws Exception Thrown if an error occurred.
     */
    protected Document parseFile(File file, File schema)
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
     * 
     * @param file The file to parse.
     * @param schemaStreamArray The schema as array of stream sources used to validate the specified file.
     * @return
     * @throws Exception Thrown if an error occurred.
     */
    protected Document parseFileWithStreamArray(File file, StreamSource[] schemaStreamArray)
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

        /*
        // Version - one step parse and validate (print error to stdErr)
        dbf.setSchema(theSchema);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document theDoc = builder.parse(file);
        */

        // Version - two step parse then validate (throws error as exception)
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document theDoc = builder.parse(file);
        Validator validator=theSchema.newValidator();
        validator.validate(new DOMSource(theDoc));
        return theDoc;
    }
    
    /**
     * Transforms the input file using the specified stylesheet stream.
     * 
     * @param input  The file to transform.
     * @param output The destination file.
     * @param xslt   The stylesheet InputStream to use.
     * @throws Exception Thrown if an error occurred.
     */
    protected void transformFileWithStream(File input, File output, InputStream xslt)
        throws Exception
    {
        if (input == null) 
            throw new IllegalArgumentException("No file to transform.");
        if (output == null) 
            throw new IllegalArgumentException("No destination file.");
        if (xslt == null) 
            throw new IllegalArgumentException("No stylesheet provided.");

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(
                new StreamSource(xslt));
        StreamResult result = new StreamResult(new FileOutputStream(output));
        transformer.transform(new StreamSource(input), result);
    }

}
