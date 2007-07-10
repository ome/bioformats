//
// Cache.java
//

package loci.formats.cache;

import java.io.*;
import java.util.Arrays;
import loci.formats.*;

public class Cache {

  // -- Fields --

  private ICacheStrategy strategy;
  private ICacheSource source;
  private int[] currentPos;
  private Object[] cache;
  private boolean[] inCache;

  // -- Constructors --

  public Cache() {
  }

  public Cache(ICacheStrategy strategy, ICacheSource source) 
    throws CacheException
  {
    this.strategy = strategy;
    this.source = source;
    reset(); 
  }

  // -- Cache API methods --

  public Object getObject(int[] pos) throws CacheException {
    if (pos.length != strategy.getLengths().length) {
      throw new CacheException("Invalid number of axes; got " + pos.length +
        "; expected " + strategy.getLengths().length);
    }

    int ndx = FormatTools.positionToRaster(strategy.getLengths(), pos);
    load(pos);
    return cache[ndx];
  }

  public void reset() throws CacheException {
    currentPos = new int[strategy.getLengths().length];
    cache = new Object[source.getObjectCount()];
    inCache = new boolean[source.getObjectCount()];
    Arrays.fill(inCache, false);
  }

  public ICacheStrategy getStrategy() { return strategy; }
  public ICacheSource getSource() { return source; }
  public int[] getCurrentPos() { return currentPos; }

  public void setStrategy(ICacheStrategy strategy) throws CacheException {
    this.strategy = strategy;
    if (source != null && strategy != null) reset(); 
    if (currentPos != null) load(currentPos);
  }

  public void setSource(ICacheSource source) throws CacheException {
    this.source = source;
    if (source != null && strategy != null) reset(); 
    if (currentPos != null) load(currentPos);
  }

  // -- Helper methods --

  private void load(int[] pos) throws CacheException {
    int[][] indices = strategy.getLoadList(pos); 

    Arrays.fill(inCache, false);
    for (int i=0; i<indices.length; i++) {
      int ndx = FormatTools.positionToRaster(strategy.getLengths(), indices[i]);
      if (ndx >= 0) inCache[ndx] = true;
    } 

    for (int i=0; i<cache.length; i++) {
      if (!inCache[i]) {
        cache[i] = null;
      } 
    }    

    for (int i=0; i<indices.length; i++) {
      int ndx = FormatTools.positionToRaster(strategy.getLengths(), indices[i]);
      if (ndx >= 0 && cache[ndx] == null) {
        cache[ndx] = source.getObject(strategy.getLengths(), indices[i]);
      } 
    }
  }

  // -- Main method -- 

  public static void main(String[] args) throws Exception {
    Cache cache = new Cache();;
    ImageReader reader = new ImageReader();
    reader.setId(args[0]);
    String cmd = "";
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    while (!cmd.equals("exit")) {
      System.out.print("> ");
      cmd = r.readLine().trim();

      if (cmd.equals("exit")) break;
      else if (cmd.equals("help")) {
        System.out.println("Available commands:");
        System.out.println("  source");
        System.out.println("  strategy");
        System.out.println("  forward");
        System.out.println("  backward");
        System.out.println("  priority");
        System.out.println("  read");
      }
      else if (cmd.equals("read")) {
        System.out.print("Z: ");
        int z = Integer.parseInt(r.readLine().trim());
        System.out.print("C: ");
        int c = Integer.parseInt(r.readLine().trim());
        System.out.print("T: ");
        int t = Integer.parseInt(r.readLine().trim());
        Object o = cache.getObject(new int[] {z, c, t});
      }
      else if (cmd.equals("strategy")) {
        System.out.println("0: crosshair");
        System.out.println("1: rectangle");
        System.out.print("> ");
        int n = Integer.parseInt(r.readLine().trim());
        int[] l = new int[] {reader.getSizeZ(), reader.getSizeC(), 
          reader.getSizeT()};
        int[] f = new int[] {0, 0, 0};
        switch (n) {
          case 0:
            cache.setStrategy(new CrosshairStrategy(true, l, f, f));
            break;
          case 1:
            cache.setStrategy(new RectangleStrategy(true, l, f, f));
            break;
        }
      }
      else if (cmd.equals("source")) {
        System.out.println("0: BufferedImage");
        System.out.println("1: byte array");
        System.out.println("2: ImageProcessor");
        System.out.print("> ");
        int n = Integer.parseInt(r.readLine().trim());
        switch (n) {
          case 0: cache.setSource(new BufferedImageSource(reader)); break; 
          case 1: cache.setSource(new ByteArraySource(reader)); break;
          case 2: cache.setSource(new ImageProcessorSource(reader)); break;
        }
      }
      else if (cmd.equals("forward")) {
        System.out.print("Z: ");
        int z = Integer.parseInt(r.readLine().trim());
        System.out.print("C: ");
        int c = Integer.parseInt(r.readLine().trim());
        System.out.print("T: ");
        int t = Integer.parseInt(r.readLine().trim());
        cache.getStrategy().setForward(z, 0);
        cache.getStrategy().setForward(c, 1);
        cache.getStrategy().setForward(t, 2);
      }
      else if (cmd.equals("backward")) {
        System.out.print("Z: ");
        int z = Integer.parseInt(r.readLine().trim());
        System.out.print("C: ");
        int c = Integer.parseInt(r.readLine().trim());
        System.out.print("T: ");
        int t = Integer.parseInt(r.readLine().trim());
        cache.getStrategy().setBackward(z, 0);
        cache.getStrategy().setBackward(c, 1);
        cache.getStrategy().setBackward(t, 2);
      }
      else if (cmd.equals("priority")) {
        System.out.println("0 => high priority");
        System.out.println("1 => medium priority");
        System.out.println("2 => low priority");
        System.out.print("Z: ");
        int z = Integer.parseInt(r.readLine().trim());
        System.out.print("C: ");
        int c = Integer.parseInt(r.readLine().trim());
        System.out.print("T: ");
        int t = Integer.parseInt(r.readLine().trim());
        cache.getStrategy().setPriority(z, 0);
        cache.getStrategy().setPriority(c, 1);
        cache.getStrategy().setPriority(t, 2);
      }
    }
    reader.close(); 
  }

}
