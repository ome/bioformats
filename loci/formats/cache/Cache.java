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
        pos.length + ", expected " + currentPos.length + ")");
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
  public static void main(String[] args) throws FormatException, IOException {
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
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Entering Bio-Formats caching test console");
    while (true) {
      System.out.print("> ");
      String cmd = r.readLine().trim();
      if (cmd.equals("")) continue;
      else if (cmd.startsWith("e") || cmd.startsWith("q")) break; // exit/quit
      else if (cmd.startsWith("h")) { // help
        System.out.println("Available commands:");
        System.out.println("  info     -- displays the cache state");
        System.out.println("  strategy -- changes the cache strategy");
        System.out.println("  source   -- changes the cache source");
        System.out.println("  position -- changes the current position");
        System.out.println("  range    -- changes the cache ranges");
        System.out.println("  priority -- changes the cache priorities");
        System.out.println("  read     -- gets a plane from the cache");
        System.out.println("  exit     -- quits the interpreter");
      }
      else if (cmd.startsWith("re")) { // read
        System.out.print("Z: ");
        int z = Integer.parseInt(r.readLine().trim());
        System.out.print("C: ");
        int c = Integer.parseInt(r.readLine().trim());
        System.out.print("T: ");
        int t = Integer.parseInt(r.readLine().trim());
        System.out.println("Retrieving Z" + z + "-C" + c + "-T" + t);
        Object o = cache.getObject(new int[] {z, c, t});
        System.out.println(o);
      }
      else if (cmd.startsWith("i")) { // info
        // output dimensional position
        printArray("pos", cache.getCurrentPos());
        // output source information
        ICacheSource source = cache.getSource();
        System.out.print("source = ");
        Class sourceClass = source.getClass();
        if (sourceClass == BufferedImageSource.class) {
          System.out.println("BufferedImage");
        }
        else if (sourceClass == ByteArraySource.class) {
          System.out.println("byte array");
        }
        else if (sourceClass == ImageProcessorSource.class) {
          System.out.println("ImageProcessor");
        }
        else System.out.println("unknown");
        System.out.println("object count = " + source.getObjectCount());
        // output strategy information
        ICacheStrategy strategy = cache.getStrategy();
        System.out.print("strategy = ");
        Class strategyClass = strategy.getClass();
        if (strategyClass == CrosshairStrategy.class) {
          System.out.println("crosshair");
        }
        else if (strategyClass == RectangleStrategy.class) {
          System.out.println("crosshair");
        }
        else System.out.println("unknown");
        printArray("priorities", strategy.getPriorities());
        int[] order = strategy.getOrder();
        System.out.print("order =");
        for (int i=0; i<order.length; i++) {
          switch (order[i]) {
            case ICacheStrategy.CENTERED_ORDER:
              System.out.print(" C");
              break;
            case ICacheStrategy.FORWARD_ORDER:
              System.out.print(" F");
              break;
            case ICacheStrategy.BACKWARD_ORDER:
              System.out.print(" B");
              break;
            default:
              System.out.print(" ?");
          }
        }
        System.out.println();
        printArray("range", strategy.getRange());
        printArray("lengths", strategy.getLengths());
      }
      else if (cmd.startsWith("st")) { // strategy
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
      else if (cmd.startsWith("so")) { // source
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
      else if (cmd.startsWith("po")) { // position
        System.out.print("Z: ");
        int z = Integer.parseInt(r.readLine().trim());
        System.out.print("C: ");
        int c = Integer.parseInt(r.readLine().trim());
        System.out.print("T: ");
        int t = Integer.parseInt(r.readLine().trim());
        cache.setCurrentPos(new int[] {z, c, t});
      }
      else if (cmd.startsWith("ra")) { // range
        System.out.print("Z: ");
        int z = Integer.parseInt(r.readLine().trim());
        System.out.print("C: ");
        int c = Integer.parseInt(r.readLine().trim());
        System.out.print("T: ");
        int t = Integer.parseInt(r.readLine().trim());
        cache.getStrategy().setRange(z, 0);
        cache.getStrategy().setRange(c, 1);
        cache.getStrategy().setRange(t, 2);
      }
      else if (cmd.startsWith("pr")) { // priority
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
        ICacheStrategy strategy = cache.getStrategy();
        strategy.setPriority(z, 0);
        strategy.setPriority(c, 1);
        strategy.setPriority(t, 2);
      }
      else System.out.println("Unknown command: " + cmd);
    }
    reader.close();
  }

}
