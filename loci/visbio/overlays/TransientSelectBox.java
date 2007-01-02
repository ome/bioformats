package loci.visbio.overlays;

import java.rmi.RemoteException;
import java.util.Arrays;
import visad.*;
import java.awt.Color;

public class TransientSelectBox {

  private Color color;
  private float x1, x2, y1, y2;
  private OverlayTransform overlay;

  public TransientSelectBox(OverlayTransform overlay, float downX, float downY) {
    this.overlay = overlay;
    x1 = downX;
    x2 = downX;
    y1 = downY;
    y2 = downY;
    color = Color.green;
    // System.out.println("new transient select box at ("+x1+","+y1+")("+x2+","+y2+")"); // TEMP
  }

  public void setCorner (float x, float y) {
    x2 = x;
    y2 = y;
    //System.out.println("("+x1+","+y1+")("+x2+","+y2+")"); // TEMP
  }

  public DataImpl getData() {
    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    float[][] setSamples = null;
    GriddedSet fieldSet = null;

    try {
      setSamples = new float[][] {
        {x1, x2, x2, x1, x1}, 
        {y1, y1, y2, y2, y1}
      };
    
      fieldSet = new Gridded2DSet (domain, 
          setSamples, setSamples[0].length, null, null, null, false);
    }
    catch (VisADException exc) { exc.printStackTrace(); }

    float r = color.getRed() / 255f;
    float g = color.getGreen() / 255f;
    float b = color.getBlue() / 255f;
    float[][] rangeSamples = new float[3][setSamples[0].length];
    Arrays.fill(rangeSamples[0], r);
    Arrays.fill(rangeSamples[1], g);
    Arrays.fill(rangeSamples[2], b);

    FlatField field = null;
    try {
      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return field;  
  }
}
