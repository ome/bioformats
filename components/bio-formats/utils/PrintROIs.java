//
// PrintROIs.java
//

import java.util.ArrayList;

import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;

import ome.xml.model.Ellipse;
import ome.xml.model.Line;
import ome.xml.model.Mask;
import ome.xml.model.OME;
import ome.xml.model.Path;
import ome.xml.model.Point;
import ome.xml.model.Polyline;
import ome.xml.model.Rectangle;
import ome.xml.model.Shape;
import ome.xml.model.Text;
import ome.xml.model.Union;

/**
 * A simple example of how to retrieve ROI data parsed from a file.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/utils/PrintROIs.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/utils/PrintROIs.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class PrintROIs {

  /** Print all of the ROIs that were parsed from the file. */
  public static void printAllROIs(IMetadata omexml) {
    int roiCount = omexml.getROICount();
    for (int roi=0; roi<roiCount; roi++) {
      printROI(omexml, roi);
    }
  }

  /** Print only the ROIs that are associated with the given series. */
  public static void printSeriesROIs(IMetadata omexml, int series) {
    int roiCount = omexml.getImageROIRefCount(series);

    ArrayList<String> roiIDs = new ArrayList<String>();
    int totalROICount = omexml.getROICount();
    for (int roi=0; roi<totalROICount; roi++) {
      String roiID = omexml.getROIID(roi);
      roiIDs.add(roiID);
    }

    for (int roi=0; roi<roiCount; roi++) {
      String roiRef = omexml.getImageROIRef(series, roi);
      int roiIndex = roiIDs.indexOf(roiRef);

      System.out.println("ROIs associated with series #" + series);
      printROI(omexml, roiIndex);
    }
  }

  /** Print all of the shapes associated with the specified ROI. */
  public static void printROI(IMetadata omexml, int roi) {
    String roiID = omexml.getROIID(roi);
    System.out.println("ROI #" + roi + " (ID = " + roiID + ")");
    System.out.println("  Name = " + omexml.getROIName(roi));

    int shapeCount = omexml.getShapeCount(roi);

    System.out.println("  Number of shapes = " + shapeCount);

    // Note that it is not possible to retrieve the shape's type
    // from the IMetadata object; you must use the underlying model
    // objects to determine the shape type.
    OME root = (OME) omexml.getRoot();
    Union allShapes = root.getROI(roi).getUnion();

    for (int shape=0; shape<shapeCount; shape++) {
      Shape shapeObject = allShapes.getShape(shape);
      printShape(shapeObject);
    }
  }

  /** Print the given shape. */
  public static void printShape(Shape shapeObject) {
    if (shapeObject instanceof Ellipse) {
      Ellipse ellipse = (Ellipse) shapeObject;
      System.out.println("    Ellipse:");
      System.out.println("      Name = " + ellipse.getName());
      System.out.println("      X = " + ellipse.getX());
      System.out.println("      Y = " + ellipse.getY());
      System.out.println("      Radius (X) = " + ellipse.getRadiusX());
      System.out.println("      Radius (Y) = " + ellipse.getRadiusY());
    }
    else if (shapeObject instanceof Line) {
      Line line = (Line) shapeObject;
      System.out.println("    Line:");
      System.out.println("      Name = " + line.getName());
      System.out.println("      X1 = " + line.getX1());
      System.out.println("      Y1 = " + line.getY1());
      System.out.println("      X2 = " + line.getX2());
      System.out.println("      Y2 = " + line.getY2());
    }
    else if (shapeObject instanceof Point) {
      Point point = (Point) shapeObject;
      System.out.println("    Point:");
      System.out.println("      Name = " + point.getName());
      System.out.println("      X = " + point.getX());
      System.out.println("      Y = " + point.getY());
    }
    else if (shapeObject instanceof Polyline) {
      Polyline polyline = (Polyline) shapeObject;
      System.out.println("    Polyline:");
      System.out.println("      Name = " + polyline.getName());
      System.out.println("      Closed = " + polyline.getClosed());
      System.out.println("      Points = " + polyline.getPoints());
    }
    else if (shapeObject instanceof Rectangle) {
      Rectangle rectangle = (Rectangle) shapeObject;
      System.out.println("    Rectangle:");
      System.out.println("      Name = " + rectangle.getName());
    }
    else if (shapeObject instanceof Mask) {
      Mask mask = (Mask) shapeObject;
      System.out.println("    Mask:");
      System.out.println("      Name = " + mask.getName());
      System.out.println("      X = " + mask.getX());
      System.out.println("      Y = " + mask.getY());
      System.out.println("      Width = " + mask.getWidth());
      System.out.println("      Height = " + mask.getHeight());
    }
    else if (shapeObject instanceof Path) {
      Path path = (Path) shapeObject;
      System.out.println("    Path:");
      System.out.println("      Name = " + path.getName());
      System.out.println("      Definition = " + path.getDefinition());
    }
    else if (shapeObject instanceof Text) {
      Text text = (Text) shapeObject;
      System.out.println("    Text:");
      System.out.println("      Name = " + text.getName());
      System.out.println("      Value = " + text.getValue());
      System.out.println("      X = " + text.getX());
      System.out.println("      Y = " + text.getY());
    }
  }

  public static void main(String[] args) throws Exception {
    ImageReader reader = new ImageReader();
    IMetadata omexml = MetadataTools.createOMEXMLMetadata();
    reader.setMetadataStore(omexml);
    reader.setId(args[0]);

    printAllROIs(omexml);
    System.out.println();
    for (int series=0; series<reader.getSeriesCount(); series++) {
      printSeriesROIs(omexml, series);
    }

    reader.close();
  }

}
