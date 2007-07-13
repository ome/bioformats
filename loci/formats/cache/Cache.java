//
// Cache.java
//

package loci.formats.cache;

import java.io.*;
import java.util.Arrays;
import javax.swing.JFrame;
import loci.formats.*;

public class Cache {

  // -- Static fields --

  /** Whether to generate debugging output. */
  private static boolean debug = false;

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
      if (cache[ndx] == null) {
        if (debug) printArray("Loading position", indices[i]);
        cache[ndx] = source.getObject(len, indices[i]);
      }
      else if (debug) printArray("Already in cache", indices[i]);
    }
  }

  /** Helper utility for outputting contents of an int array, used by main. */
  private static final void printArray(String name, int[] array) {
    LogTools.print(name + " =");
    for (int i=0; i<array.length; i++) LogTools.print(" " + array[i]);
    LogTools.println();
  }

  /** Helper utility for constructing lengths array, used by main. */
  private static final int[] getLengths(IFormatReader r) {
    return new int[] {r.getSizeZ(), r.getSizeC(), r.getSizeT()};
  }

  // -- Main method --

  /** Interactive interpreter for testing Bio-Formats caching implementation. */
  public static void main(String[] args) throws FormatException, IOException {
    if (args.length < 1) {
      LogTools.println("Please specify a filename containing image data.");
      System.exit(1);
    }
    debug = true;
    ImageReader reader = new ImageReader();
    String id = args[0];
    LogTools.println("Reading " + id);
    reader.setId(id);
    LogTools.println("Initializing cache");
    Cache cache = new Cache(
      new CrosshairStrategy(getLengths(reader)),
      new BufferedImageSource(reader));
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    LogTools.println("Entering Bio-Formats caching test console");
    while (true) {
      LogTools.print("> ");
      String cmd = r.readLine().trim();
      if (cmd.equals("")) continue;
      else if (cmd.startsWith("e") || cmd.startsWith("q")) break; // exit/quit
      else if (cmd.startsWith("g")) { // gui
        JFrame frame = new JFrame("Cache controls");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ReflectedUniverse ru = new ReflectedUniverse();
        try {
          ru.exec("import loci.formats.gui.CacheComponent");
          ru.setVar("cache", cache);
          ru.setVar("doSource", true);
          ru.setVar("axes", new String[] {"Z", "C", "T"});
          ru.setVar("id", id);
          ru.exec("widget = new CacheComponent(cache, doSource, axes, id)");
          ru.setVar("frame", frame);
          ru.exec("frame.setContentPane(widget)");
          frame.pack();
          frame.setVisible(true);
        }
        catch (ReflectException exc) { LogTools.trace(exc); }
      }
      else if (cmd.startsWith("h")) { // help
        LogTools.println("Available commands:");
        LogTools.println("  gui      -- pops up a GUI to configure the cache");
        LogTools.println("  info     -- displays the cache state");
        LogTools.println("  position -- changes the current position");
        LogTools.println("  strategy -- changes the cache strategy");
        LogTools.println("  source   -- changes the cache source");
        LogTools.println("  range    -- changes the cache ranges");
        LogTools.println("  priority -- changes the cache priorities");
        LogTools.println("  read     -- gets a plane from the cache");
        LogTools.println("  exit     -- quits the interpreter");
      }
      else if (cmd.startsWith("i")) { // info
        // output dimensional position
        printArray("pos", cache.getCurrentPos());
        // output source information
        ICacheSource source = cache.getSource();
        LogTools.print("source = ");
        Class sourceClass = source.getClass();
        if (sourceClass == BufferedImageSource.class) {
          LogTools.println("BufferedImage");
        }
        else if (sourceClass == ByteArraySource.class) {
          LogTools.println("byte array");
        }
        else if (sourceClass == ImageProcessorSource.class) {
          LogTools.println("ImageProcessor");
        }
        else LogTools.println("unknown");
        LogTools.println("object count = " + source.getObjectCount());
        // output strategy information
        ICacheStrategy strategy = cache.getStrategy();
        LogTools.print("strategy = ");
        Class strategyClass = strategy.getClass();
        if (strategyClass == CrosshairStrategy.class) {
          LogTools.println("crosshair");
        }
        else if (strategyClass == RectangleStrategy.class) {
          LogTools.println("crosshair");
        }
        else LogTools.println("unknown");
        printArray("priorities", strategy.getPriorities());
        int[] order = strategy.getOrder();
        LogTools.print("order =");
        for (int i=0; i<order.length; i++) {
          switch (order[i]) {
            case ICacheStrategy.CENTERED_ORDER:
              LogTools.print(" C");
              break;
            case ICacheStrategy.FORWARD_ORDER:
              LogTools.print(" F");
              break;
            case ICacheStrategy.BACKWARD_ORDER:
              LogTools.print(" B");
              break;
            default:
              LogTools.print(" ?");
          }
        }
        LogTools.println();
        printArray("range", strategy.getRange());
        printArray("lengths", strategy.getLengths());
      }
      else if (cmd.startsWith("po")) { // position
        LogTools.print("Z: ");
        int z = Integer.parseInt(r.readLine().trim());
        LogTools.print("C: ");
        int c = Integer.parseInt(r.readLine().trim());
        LogTools.print("T: ");
        int t = Integer.parseInt(r.readLine().trim());
        cache.setCurrentPos(new int[] {z, c, t});
      }
      else if (cmd.startsWith("pr")) { // priority
        LogTools.println(ICacheStrategy.MIN_PRIORITY + " => min priority");
        LogTools.println(ICacheStrategy.LOW_PRIORITY + " => low priority");
        LogTools.println(
          ICacheStrategy.NORMAL_PRIORITY + " => normal priority");
        LogTools.println(ICacheStrategy.HIGH_PRIORITY + " => high priority");
        LogTools.println(ICacheStrategy.MAX_PRIORITY + " => max priority");
        LogTools.print("Z: ");
        int z = Integer.parseInt(r.readLine().trim());
        LogTools.print("C: ");
        int c = Integer.parseInt(r.readLine().trim());
        LogTools.print("T: ");
        int t = Integer.parseInt(r.readLine().trim());
        ICacheStrategy strategy = cache.getStrategy();
        strategy.setPriority(z, 0);
        strategy.setPriority(c, 1);
        strategy.setPriority(t, 2);
      }
      else if (cmd.startsWith("ra")) { // range
        LogTools.print("Z: ");
        int z = Integer.parseInt(r.readLine().trim());
        LogTools.print("C: ");
        int c = Integer.parseInt(r.readLine().trim());
        LogTools.print("T: ");
        int t = Integer.parseInt(r.readLine().trim());
        cache.getStrategy().setRange(z, 0);
        cache.getStrategy().setRange(c, 1);
        cache.getStrategy().setRange(t, 2);
      }
      else if (cmd.startsWith("re")) { // read
        LogTools.print("Z: ");
        int z = Integer.parseInt(r.readLine().trim());
        LogTools.print("C: ");
        int c = Integer.parseInt(r.readLine().trim());
        LogTools.print("T: ");
        int t = Integer.parseInt(r.readLine().trim());
        LogTools.println("Retrieving Z" + z + "-C" + c + "-T" + t);
        Object o = cache.getObject(new int[] {z, c, t});
        LogTools.println(o);
      }
      else if (cmd.startsWith("so")) { // source
        LogTools.println("0: BufferedImage");
        LogTools.println("1: byte array");
        LogTools.println("2: ImageProcessor");
        LogTools.print("> ");
        int n = Integer.parseInt(r.readLine().trim());
        switch (n) {
          case 0:
            cache.setSource(new BufferedImageSource(reader));
            LogTools.println("Source set to BufferedImage");
            break;
          case 1:
            cache.setSource(new ByteArraySource(reader));
            LogTools.println("Source set to byte array");
            break;
          case 2:
            cache.setSource(new ImageProcessorSource(reader));
            LogTools.println("Source set to ImageProcessor");
            break;
          default:
            LogTools.println("Unknown source: " + n);
        }
      }
      else if (cmd.startsWith("st")) { // strategy
        LogTools.println("0: crosshair");
        LogTools.println("1: rectangle");
        LogTools.print("> ");
        int n = Integer.parseInt(r.readLine().trim());
        int[] l = getLengths(reader);
        switch (n) {
          case 0:
            cache.setStrategy(new CrosshairStrategy(l));
            LogTools.println("Strategy set to crosshair");
            break;
          case 1:
            cache.setStrategy(new RectangleStrategy(l));
            LogTools.println("Strategy set to rectangle");
            break;
          default:
            LogTools.println("Unknown strategy: " + n);
        }
      }
      else LogTools.println("Unknown command: " + cmd);
    }
    reader.close();
  }

}
