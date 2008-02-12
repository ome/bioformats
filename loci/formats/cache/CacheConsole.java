//
// CacheConsole.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.cache;

import java.io.*;
import loci.formats.*;

/**
 * CacheConsole provides an interactive interpreter for
 * testing the Bio-Formats caching implementation.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/cache/CacheConsole.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/cache/CacheConsole.java">SVN</a></dd></dl>
 */
public final class CacheConsole {

  // -- Constructor --

  private CacheConsole() { }

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
        ReflectedUniverse ru = new ReflectedUniverse();
        try {
          ru.exec("import loci.formats.gui.CacheComponent");
          ru.setVar("cache", cache);
          ru.setVar("doSource", true);
          ru.setVar("axes", new String[] {"Z", "C", "T"});
          ru.setVar("id", id);
          ru.exec("widget = new CacheComponent(cache, axes, id)");
          ru.exec("import javax.swing.JFrame");
          ru.exec("frame = new JFrame(\"Cache controls\")");
          ru.exec("frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)");
          ru.exec("frame.setContentPane(widget)");
          ru.exec("frame.pack()");
          ru.exec("frame.setVisible(true)");
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
        LogTools.print("> ");
        int n = Integer.parseInt(r.readLine().trim());
        switch (n) {
          case 0:
            cache.setSource(new BufferedImageSource(reader));
            break;
          case 1:
            cache.setSource(new ByteArraySource(reader));
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

  // -- Helper methods --

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
