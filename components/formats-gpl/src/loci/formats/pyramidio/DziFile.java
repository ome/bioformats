/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package loci.formats.pyramidio;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * DZI file reader
 *
 * @author Antoine Vandecreme
 *
 * reused and adapted for PreciPoint .vmic WSI files reader
 *
 */
public class DziFile {

    private final int tileSize;
    private final int overlap;
    private final String format;
    private final int width;
    private final int height;

    public DziFile(int tileSize, int overlap, String format, int width, int height) {
        this.tileSize = tileSize;
        this.overlap = overlap;
        this.format = format;
        this.width = width;
        this.height = height;
    }

    public DziFile(InputStream dzi_is) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            Document doc = db.parse(dzi_is);
            Element imageNode = doc.getDocumentElement();
            if (!"Image".equals(imageNode.getNodeName())) {
                throw new IOException("Unsupported dzi file.");
            }

            tileSize = Integer.parseInt(imageNode.getAttribute("TileSize"));
            overlap = Integer.parseInt(imageNode.getAttribute("Overlap"));
            format = imageNode.getAttribute("Format");

            NodeList childNodes = imageNode.getChildNodes();
            int length = childNodes.getLength();
            String w = null;
            String h = null;
            for (int i = 0; i < length; i++) {
                Node node = childNodes.item(i);
                if ("Size".equals(node.getNodeName())) {
                    NamedNodeMap attributes = node.getAttributes();
                    w = attributes.getNamedItem("Width").getNodeValue();
                    h = attributes.getNamedItem("Height").getNodeValue();
                }
            }
            width = Integer.parseInt(w);
            height = Integer.parseInt(h);

            dzi_is.close();

        } catch (ParserConfigurationException | SAXException ex) {
            throw new IOException(ex);
        }
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getOverlap() {
        return overlap;
    }

    public String getFormat() {
        return format;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMaxLevel() {
        int maxDim = Math.max(width, height);
        return (int) Math.ceil(Math.log(maxDim) / Math.log(2));
    }
}
