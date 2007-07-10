//
// Cache.java
//

package loci.formats.cache;

import java.io.*;
import java.util.Arrays;
import loci.formats.*;

public class Cache {

  // -- Fields --

  /** Current cache strategy. */
  private ICacheStrategy strategy;

  /** Current cache source. */
  private ICacheSource source;

  /** Current dimensional position. */
  private int[] currentPos;

  /** Master array containing cached objects. */
  private Object[] cache;

  /** Whether each position is currently supposed to be cached. */
  private boolean[] inCache;

  // -- Constructors --

  /** Constructs an object cache with the given cache strategy and source. */
  public Cache(ICacheStrategy strategy, ICacheSource source) 
    throws CacheException
  {
    if (strategy == null) throw new CacheException("strategy is null");
    if (source == null) throw new CacheException("source is null");
    this.strategy = strategy;
    this.source = source;
    reset(); 
  }

  // -- Cache API methods --

  /** Gets the cached object at the given dimensional position. */
  public Object getObject(int[] pos) throws CacheException {
    if (pos.length != strategy.getLengths().length) {
      throw new CacheException("Invalid number of axes; got " + pos.length +
        "; expected " + strategy.getLengths().length);
    }

    int ndx = FormatTools.positionToRaster(strategy.getLengths(), pos);
    load(pos);
    return cache[ndx];
  }

  /** Clears the cache. */
  public void reset() throws CacheException {
    currentPos = new int[strategy.getLengths().length];
    cache = new Object[source.getObjectCount()];
    inCache = new boolean[source.getObjectCount()];
    Arrays.fill(inCache, false);
  }

  /** Gets the cache's caching strategy. */
  public ICacheStrategy getStrategy() { return strategy; }

  /** Gets the cache's caching source. */
  public ICacheSource getSource() { return source; }

  /** Gets the current dimensional position. */
  public int[] getCurrentPos() { return currentPos; }

  /** Sets the cache's caching strategy. */
  public void setStrategy(ICacheStrategy strategy) throws CacheException {
    if (strategy == null) throw new CacheException("strategy is null");
    this.strategy = strategy;
    reset(); 
    load(currentPos);
  }

  /** Sets the cache's caching source. */
  public void setSource(ICacheSource source) throws CacheException {
    if (source == null) throw new CacheException("source is null");
    this.source = source;
    reset(); 
    load(currentPos);
  }

  /** Sets the current dimensional position. */
  public void setCurrentPos(int[] pos) throws CacheException {
    if (pos == null) throw new CacheException("pos is null");
    if (pos.length != currentPos.length) {
      throw new CacheException("pos length mismatch (is " +
        pos.length + ", expected " + currentPos.length);
    }
    int[] len = strategy.getLengths();
    for (int i=0; i<pos.length; i++) {
      if (pos[i] < 0 || pos[i] >= len[i]) {
        throw new CacheException("invalid pos[" + i + "] (is " +
          pos[i] + ", expected [0, " + (len[i] - 1) + "])");
      }
    }
    System.arraycopy(pos, 0, currentPos, 0, pos.length);
    load(currentPos);
  }

  // -- Helper methods --

  private void load(int[] pos) throws CacheException {
    int[][] indices = strategy.getLoadList(pos); 
    int[] len = strategy.getLengths();

    Arrays.fill(inCache, false);
    for (int i=0; i<indices.length; i++) {
      int ndx = FormatTools.positionToRaster(len, indices[i]);
      if (ndx >= 0) inCache[ndx] = true;
    } 

    for (int i=0; i<cache.length; i++) {
      if (!inCache[i]) cache[i] = null;
    }    

    for (int i=0; i<indices.length; i++) {
      int ndx = FormatTools.positionToRaster(len, indices[i]);
      if (ndx >= 0 && cache[ndx] == null) {
        cache[ndx] = source.getObject(len, indices[i]);
      } 
    }
  }

  /** Helper utility for outputting contents of an int array, used by main. */
  private static final void printArray(String name, int[] array) {
    System.out.print(name + " =");
    for (int i=0; i<array.length; i++) System.out.print(" " + array[i]);
    System.out.println();
  }

  /** Helper utility for constructing lengths array, used by main. */
  private static final int[] getLengths(IFormatReader r) {
    return new int[] {r.getSizeZ(), r.getSizeC(), r.getSizeT()};
  }

  // -- Main method -- 

  /** Interactive interpreter for testing Bio-Formats caching implementation. */
  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Please specify a filename containing image data.");
      System.exit(1);
    }
    ImageReader reader = new ImageReader();
    String id = args[0];
    System.out.println("Reading " + id);
    reader.setId(id);
    System.out.println("Initializing cache");
    Cache cache = new Cache(
      new CrosshairStrategy(getLengths(reader)),
      new BufferedImageSource(reader));
    String cmd = "";
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Entering Bio-Formats caching test console");
    while (true) {
      System.out.print("> ");
      cmd = r.readLine().trim();

      if (cmd.equals("exit")) break;
      else if (cmd.equals("help")) {
        System.out.println("Available commands:");
        System.out.println("  info");
        System.out.println("  strategy");
        System.out.println("  source");
        System.out.println("  position");
        System.out.println("  forward");
        System.out.println("  backward");
        System.out.println("  priority");
        System.out.println("  read");
        System.out.println("  exit");
      }
      else if (cmd.equals("read")) {
        System.out.print("Z: ");
        int z = Integer.parseInt(r.readLine().trim());
        System.out.print("C: ");
        int c = Integer.parseInt(r.readLine().trim());
        System.out.print("T: ");
        int t = Integer.parseInt(r.readLine().trim());
        System.out.println("Retrieving Z" + z + "-C" + c + "-T" + t);
        Object o = cache.getObject(new int[] {z, c, t});
      }
      else if (cmd.equals("info")) {
        // output strategy information
        ICacheStrategy strategy = cache.getStrategy();
        System.out.println("strategy = " + strategy.getClass().getName());
        printArray("\tpriorities", strategy.getPriorities());
        System.out.println("\tforwardFirst = " + strategy.getForwardFirst());
        printArray("\tforward", strategy.getForward());
        printArray("\tbackward", strategy.getBackward());
        printArray("\tlengths", strategy.getLengths());
        // output source information
        ICacheSource source = cache.getSource();
        System.out.println("source = " + source.getClass().getName());
        System.out.println("\tobject count = " + source.getObjectCount());
        // output dimensional position
        printArray("pos", cache.getCurrentPos());
      }
      else if (cmd.equals("strategy")) {
        System.out.println("0: crosshair");
        System.out.println("1: rectangle");
        System.out.print("> ");
        int n = Integer.parseInt(r.readLine().trim());
        int[] l = getLengths(reader);
        switch (n) {
          case 0:
            cache.setStrategy(new CrosshairStrategy(l));
            System.out.println("Strategy set to crosshair");
            break;
          case 1:
            cache.setStrategy(new RectangleStrategy(l));
            System.out.println("Strategy set to rectangle");
            break;
          default:
            System.out.println("Unknown strategy: " + n);
        }
      }
      else if (cmd.equals("source")) {
        System.out.println("0: BufferedImage");
        System.out.println("1: byte array");
        System.out.println("2: ImageProcessor");
        System.out.print("> ");
        int n = Integer.parseInt(r.readLine().trim());
        switch (n) {
          case 0:
            cache.setSource(new BufferedImageSource(reader));
            System.out.println("Source set to BufferedImage");
            break; 
          case 1:
            cache.setSource(new ByteArraySource(reader));
            System.out.println("Source set to byte array");
            break;
          case 2:
            cache.setSource(new ImageProcessorSource(reader));
            System.out.println("Source set to ImageProcessor");
            break;
          default:
            System.out.println("Unknown source: " + n);
        }
      }
      else if (cmd.equals("position")) {
        System.out.print("Z: ");
        int z = Integer.parseInt(r.readLine().trim());
        System.out.print("C: ");
        int c = Integer.parseInt(r.readLine().trim());
        System.out.print("T: ");
        int t = Integer.parseInt(r.readLine().trim());
        cache.setCurrentPos(new int[] {z, c, t});
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
        System.out.println(ICacheStrategy.MIN_PRIORITY + " => min priority");
        System.out.println(ICacheStrategy.LOW_PRIORITY + " => low priority");
        System.out.println(
          ICacheStrategy.NORMAL_PRIORITY + " => normal priority");
        System.out.println(ICacheStrategy.HIGH_PRIORITY + " => high priority");
        System.out.println(ICacheStrategy.MAX_PRIORITY + " => max priority");
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
      else if (cmd.equals("exit")) break;
      else System.out.println("Unknown command: " + cmd);
    }
    reader.close(); 
  }

}
