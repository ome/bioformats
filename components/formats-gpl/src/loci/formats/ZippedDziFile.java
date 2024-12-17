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
package loci.formats;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * DZI file reader and writer
 *
 * @author Antoine Vandecreme 
 */
public class ZippedDziFile {

    private final int tileSize;
    private final int overlap;
    private final String format;
    private final int width;
    private final int height;
    private final float pixelpermicron;

    public ZippedDziFile(int tileSize, int overlap, String format, int width, int height, float pixelpermicron) {
        this.tileSize = tileSize;
        this.overlap = overlap;
        this.format = format;
        this.width = width;
        this.height = height;
        this.pixelpermicron = pixelpermicron;
    }

    public ZippedDziFile(InputStream dziFile) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            Document doc = db.parse(dziFile);
            Element imageNode = doc.getDocumentElement();
            if (!"Image".equals(imageNode.getNodeName())) {
                throw new IOException("Unsupported dzi file.");
            }

            tileSize = Integer.parseInt(imageNode.getAttribute("TileSize"));
            overlap = Integer.parseInt(imageNode.getAttribute("Overlap"));
            format = imageNode.getAttribute("Format");
            pixelpermicron = Float.parseFloat(imageNode.getAttribute("PixelPerMicron"));

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

    public float getPixelPerMicron() { return pixelpermicron; }

//    commented out for .vmic reader function
//
//    public void write(File file) throws FileNotFoundException, IOException {
//        try (OutputStreamWriter osw = new OutputStreamWriter(
//                new FileOutputStream(file))) {
//            osw.write(toXml());
//        }
//    }
//
//    public void write(String name, FilesArchiver archiver) throws IOException {
//        archiver.appendFile(name, new FilesArchiver.FileAppender<Void>() {
//            @Override
//            public Void append(OutputStream outputStream) throws IOException {
//                try (Writer out = new OutputStreamWriter(outputStream,
//                        Charset.forName("UTF-8"))) {
//                    out.write(toXml());
//                }
//                return null;
//            }
//        });
//    }
//
//    private String toXml() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
//        sb.append("<Image TileSize=\"").append(tileSize).
//                append("\" Overlap=\"").append(overlap).append("\" Format=\"").
//                append(format).append(
//                        "\" xmlns=\"http://schemas.microsoft.com/deepzoom/2009\">\n");
//        sb.append("<Size Width=\"").append(width).append("\" Height=\"")
//                .append(height).append("\" />\n");
//        sb.append("</Image>\n");
//        return sb.toString();
//    }
}
