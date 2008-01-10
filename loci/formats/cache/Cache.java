//
// Cache.java
//

package loci.formats.cache;

import java.io.*;
import java.util.Vector;
import javax.swing.JFrame;
import loci.formats.*;

/**
 * Cache provides a means of managing subsets of large collections of image
 * planes in memory. Each cache has a source, which provides image planes or
 * other objects from somewhere (typically derived from an IFormatReader),
 * and a strategy dictating which image planes should be loaded into the cache,
 * in which order, and which planes should be dropped from the cache. The
 * essence of the logic is the idea that the cache has a "current" position
 * across the multidimensional image series's dimensional axes, with the
 * strategy indicating which surrounding planes to load into the cache (i.e.,
 * planes within a certain range along each dimensional axis).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/cache/Cache.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/cache/Cache.java">SVN</a></dd></dl>
 */
public class Cache implements CacheReporter {

  // -- Fields --

  /** Current cache strategy. */
  protected ICacheStrategy strategy;

  /** Current cache source. */
  protected ICacheSource source;

  /** Current dimensional position. */
  protected int[] currentPos;

  /** Master array containing cached objects. */
  protected Object[] cache;

  /** Whether each position is currently supposed to be cached. */
  protected boolean[] inCache;

  /** List of cache event listeners. */
  protected Vector listeners;

  /** Whether or not we want to manually update the cache. */
  protected boolean manualUpdate;

  // -- Constructors --

  /** Constructs an object cache with the given cache strategy and source. */
  public Cache(ICacheStrategy strategy, ICacheSource source,
    boolean manualUpdate) throws CacheException
  {
    if (strategy == null) throw new CacheException("strategy is null");
    if (source == null) throw new CacheException("source is null");
    this.strategy = strategy;
    this.source = source;
    this.manualUpdate = manualUpdate;
    listeners = new Vector();
    reset();
    recache();
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

  /**
   * Returns true if the object at the given dimensional position is
   * in the cache.
   */
  public boolean isInCache(int[] pos) throws CacheException {
    return isInCache(FormatTools.positionToRaster(strategy.getLengths(), pos));
  }

  /** Returns true if the object at the given index is in the cache. */
  public boolean isInCache(int pos) throws CacheException {
    return inCache[pos];
  }

  /** Reallocates the cache. */
  public void reset() throws CacheException {
    currentPos = new int[strategy.getLengths().length];
    cache = new Object[source.getObjectCount()];
    inCache = new boolean[source.getObjectCount()];
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
    synchronized (listeners) {
      for (int i=0; i<listeners.size(); i++) {
        CacheListener l = (CacheListener) listeners.elementAt(i);
        this.strategy.removeCacheListener(l);
        strategy.addCacheListener(l);
      }
    }
    this.strategy = strategy;
    notifyListeners(new CacheEvent(this, CacheEvent.STRATEGY_CHANGED));
    reset();
    if (!manualUpdate) recache();
  }

  /** Sets the cache's caching source. */
  public void setSource(ICacheSource source) throws CacheException {
    if (source == null) throw new CacheException("source is null");
    this.source = source;
    notifyListeners(new CacheEvent(this, CacheEvent.SOURCE_CHANGED));
    reset();
    if (!manualUpdate) recache();
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
    int ndx = FormatTools.positionToRaster(len, pos);
    notifyListeners(new CacheEvent(this, CacheEvent.POSITION_CHANGED, ndx));
    if (!manualUpdate) recache();
  }

  /** Updates the given plane. */
  public void recache(int n) throws CacheException {
    int[][] indices = strategy.getLoadList(currentPos);
    int[] len = strategy.getLengths();

    for (int i=0; i<inCache.length; i++) {
      boolean found = false;
      for (int j=0; j<indices.length; j++) {
        if (i == FormatTools.positionToRaster(len, indices[j])) {
          found = true;
          break;
        }
      }
      if (!found) {
        inCache[i] = false;
        if (cache[i] != null) {
          cache[i] = null;
          notifyListeners(new CacheEvent(this, CacheEvent.OBJECT_DROPPED, i));
        }
      }
    }

    int ndx = FormatTools.positionToRaster(len, indices[n]);
    if (ndx >= 0) inCache[ndx] = true;

    if (cache[ndx] == null) {
      cache[ndx] = source.getObject(ndx);
      notifyListeners(new CacheEvent(this, CacheEvent.OBJECT_LOADED, ndx));
    }
  }

  // -- CacheReporter API methods --

  /* @see CacheReporter#addCacheListener(CacheListener) */
  public void addCacheListener(CacheListener l) {
    synchronized (listeners) {
      listeners.add(l);
      strategy.addCacheListener(l);
    }
  }

  /* @see CacheReporter#removeCacheListener(CacheListener) */
  public void removeCacheListener(CacheListener l) {
    synchronized (listeners) {
      listeners.remove(l);
      strategy.removeCacheListener(l);
    }
  }

  /* @see CacheReporter#getCacheListeners() */
  public CacheListener[] getCacheListeners() {
    CacheListener[] l;
    synchronized (listeners) {
      l = new CacheListener[listeners.size()];
      listeners.copyInto(l);
    }
    return l;
  }

  // -- Helper methods --

  protected void recache() throws CacheException {
    // what happens if cache source and cache strategy lengths do not match?
    // throw exception in that case
    // what if developer wants to change both source and strategy to something
    // completely different -- make sure it works
    // in general, what if developer wants to tweak a few parameters before
    // starting to reload things? probably should have a recache method that
    // you must explicitly call to trigger the separate thread refresh
    // need to be careful -- don't want cache parameter changes affecting the
    // recaching thread on the fly -- should refresh those parameter values
    // each time through the loop only (i.e., only when a recache call occurs)
    //
    // /lo
    for (int i=0; i<strategy.getLoadList(currentPos).length; i++) {
      recache(i);
    }
  }

  /** Informs listeners of a cache update. */
  protected void notifyListeners(CacheEvent e) {
    synchronized (listeners) {
      for (int i=0; i<listeners.size(); i++) {
        CacheListener l = (CacheListener) listeners.elementAt(i);
        l.cacheUpdated(e);
      }
    }
  }

  // -- Main method --

  /** Interactive interpreter for testing Bio-Formats caching implementation. */
  public static void main(String[] args) throws FormatException, IOException {
    if (args.length < 1) {
      LogTools.println("Please specify a filename containing image data.");
      System.exit(1);
    }
    ImageReader reader = new ImageReader();
    String id = args[0];
    LogTools.println("Reading " + id);
    reader.setId(id);
    LogTools.println("Initializing cache");
    final Cache cache = new Cache(
      new CrosshairStrategy(getLengths(reader)),
      new BufferedImageSource(reader), false);
    CacheListener l = new CacheListener() {
      public void cacheUpdated(CacheEvent e) {
        int type = e.getType();
        int ndx = e.getIndex();
        int[] len, pos;
        switch (type) {
          case CacheEvent.SOURCE_CHANGED:
            printSource("source ->", cache);
            break;
          case CacheEvent.STRATEGY_CHANGED:
            printStrategy("strategy ->", cache);
            break;
          case CacheEvent.POSITION_CHANGED:
            len = cache.getStrategy().getLengths();
            pos = FormatTools.rasterToPosition(len, ndx);
            printArray("pos ->", pos);
            break;
          case CacheEvent.PRIORITIES_CHANGED:
            printArray("priorities ->", cache.getStrategy().getPriorities());
            break;
          case CacheEvent.ORDER_CHANGED:
            printOrder("order ->", cache);
            break;
          case CacheEvent.RANGE_CHANGED:
            printArray("range ->", cache.getStrategy().getRange());
            break;
          case CacheEvent.OBJECT_LOADED:
            len = cache.getStrategy().getLengths();
            pos = FormatTools.rasterToPosition(len, ndx);
            printArray("loaded:", pos);
            break;
          case CacheEvent.OBJECT_DROPPED:
            len = cache.getStrategy().getLengths();
            pos = FormatTools.rasterToPosition(len, ndx);
            printArray("dropped:", pos);
            break;
        }
      }
    };
    cache.addCacheListener(l);
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    LogTools.println("Entering Bio-Formats caching test console");
    while (true) {
      LogTools.print("> ");
      String cmd = r.readLine().trim();
      if (cmd.equals("")) continue;
      else if (cmd.startsWith("c")) { // cache
        cache.recache();
      }
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
          ru.exec("widget = new CacheComponent(cache, axes, id)");
          ru.setVar("frame", frame);
          ru.exec("frame.setContentPane(widget)");
          frame.pack();
          frame.setVisible(true);
        }
        catch (ReflectException exc) { LogTools.trace(exc); }
      }
      else if (cmd.startsWith("h")) { // help
        LogTools.println("Available commands:");
        LogTools.println("  cache    -- begins loading planes into the cache");
        LogTools.println("  gui      -- pops up a GUI to configure the cache");
        LogTools.println("  info     -- displays the cache state");
        LogTools.println("  position -- changes the current position");
        LogTools.println("  strategy -- changes the cache strategy");
        LogTools.println("  source   -- changes the cache source");
        LogTools.println("  priority -- changes the cache priorities");
        LogTools.println("  order    -- changes the cache order");
        LogTools.println("  range    -- changes the cache ranges");
        LogTools.println("  read     -- gets a plane from the cache");
        LogTools.println("  exit     -- quits the interpreter");
      }
      else if (cmd.startsWith("i")) { // info
        // output dimensional position
        printArray("pos =", cache.getCurrentPos());
        // output source information
        ICacheSource source = cache.getSource();
        printSource("source =", cache);
        LogTools.println("object count = " + source.getObjectCount());
        // output strategy information
        ICacheStrategy strategy = cache.getStrategy();
        printStrategy("strategy =", cache);
        printArray("priorities =", strategy.getPriorities());
        printOrder("order =", cache);
        printArray("range =", strategy.getRange());
        printArray("lengths =", strategy.getLengths());
      }
      else if (cmd.startsWith("o")) { // order
        LogTools.println(ICacheStrategy.CENTERED_ORDER + " => centered");
        LogTools.println(ICacheStrategy.FORWARD_ORDER + " => forward");
        LogTools.println(ICacheStrategy.BACKWARD_ORDER + " => backward");
        LogTools.print("Z: ");
        int z = Integer.parseInt(r.readLine().trim());
        LogTools.print("C: ");
        int c = Integer.parseInt(r.readLine().trim());
        LogTools.print("T: ");
        int t = Integer.parseInt(r.readLine().trim());
        ICacheStrategy strategy = cache.getStrategy();
        strategy.setOrder(z, 0);
        strategy.setOrder(c, 1);
        strategy.setOrder(t, 2);
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
            break;
          case 1:
            cache.setSource(new ByteArraySource(reader));
            break;
          case 2:
            cache.setSource(new ImageProcessorSource(reader));
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
        int[] zct = getLengths(reader);
        ICacheStrategy strategy = null;
        switch (n) {
          case 0:
            strategy = new CrosshairStrategy(zct);
            break;
          case 1:
            strategy = new RectangleStrategy(zct);
            break;
          default:
            LogTools.println("Unknown strategy: " + n);
        }
        if (strategy != null) {
          ICacheStrategy old = cache.getStrategy();
          int[] priorities = old.getPriorities();
          int[] range = old.getRange();
          int[] order = old.getOrder();
          for (int i=0; i<zct.length; i++) {
            strategy.setPriority(priorities[i], i);
            strategy.setRange(range[i], i);
            strategy.setOrder(order[i], i);
          }
          cache.setStrategy(strategy);
        }
      }
      else LogTools.println("Unknown command: " + cmd);
    }
    reader.close();
  }

  /** Helper utility for outputing contents of an int array, used by main. */
  private static final void printArray(String prefix, int[] array) {
    LogTools.print(prefix);
    if (array == null) LogTools.println(" null");
    else {
      for (int i=0; i<array.length; i++) LogTools.print(" " + array[i]);
      LogTools.println();
    }
  }

  /** Helper utility for outputing cache's associated source, used by main. */
  private static final void printSource(String prefix, Cache cache) {
    ICacheSource source = cache.getSource();
    LogTools.print(prefix + " ");
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
  }

  /** Helper utility for outputing cache's associated strategy, used by main. */
  private static final void printStrategy(String prefix, Cache cache) {
    ICacheStrategy strategy = cache.getStrategy();
    LogTools.print(prefix + " ");
    Class strategyClass = strategy.getClass();
    if (strategyClass == CrosshairStrategy.class) {
      LogTools.println("crosshair");
    }
    else if (strategyClass == RectangleStrategy.class) {
      LogTools.println("rectangle");
    }
    else LogTools.println("unknown");
  }

  /** Helper utility for outputing cache strategy's order, used by main. */
  private static final void printOrder(String prefix, Cache cache) {
    ICacheStrategy strategy = cache.getStrategy();
    int[] order = strategy.getOrder();
    LogTools.print(prefix);
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
  }

  /** Helper utility for constructing lengths array, used by main. */
  private static final int[] getLengths(IFormatReader r) {
    return new int[] {r.getSizeZ(), r.getSizeC(), r.getSizeT()};
  }

}
