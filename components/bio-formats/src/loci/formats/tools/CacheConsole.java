//
// CacheConsole.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.cache.ByteArraySource;
import loci.formats.cache.Cache;
import loci.formats.cache.CacheEvent;
import loci.formats.cache.CacheListener;
import loci.formats.cache.CrosshairStrategy;
import loci.formats.cache.ICacheSource;
import loci.formats.cache.ICacheStrategy;
import loci.formats.cache.RectangleStrategy;
import loci.formats.gui.BufferedImageSource;
import loci.formats.gui.CacheComponent;

/**
 * CacheConsole provides an interactive interpreter for
 * testing the Bio-Formats caching implementation.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/tools/CacheConsole.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/tools/CacheConsole.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public final class CacheConsole {

  // -- Constructor --

  private CacheConsole() { }

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
    final Cache cache = new Cache(
      new CrosshairStrategy(getLengths(reader)),
      new BufferedImageSource(reader), true);
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
    System.out.println("Entering Bio-Formats caching test console");
    while (true) {
      System.out.print("> ");
      String cmd = r.readLine().trim();
      if (cmd.equals("")) continue;
      else if (cmd.startsWith("c")) { // cache
        cache.recache();
      }
      else if (cmd.startsWith("e") || cmd.startsWith("q")) break; // exit/quit
      else if (cmd.startsWith("g")) { // gui
        String[] axes = {"Z", "C", "T"};
        CacheComponent widget = new CacheComponent(cache, axes, id);
        widget.setBorder(new EmptyBorder(15, 15, 15, 15));
        JFrame frame = new JFrame("Cache controls");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(widget);
        frame.pack();
        frame.setVisible(true);
      }
      else if (cmd.startsWith("h")) { // help
        System.out.println("Available commands:");
        System.out.println(
          "  cache    -- begins loading planes into the cache");
        System.out.println(
          "  gui      -- pops up a GUI to configure the cache");
        System.out.println("  info     -- displays the cache state");
        System.out.println("  position -- changes the current position");
        System.out.println("  strategy -- changes the cache strategy");
        System.out.println("  source   -- changes the cache source");
        System.out.println("  priority -- changes the cache priorities");
        System.out.println("  order    -- changes the cache order");
        System.out.println("  range    -- changes the cache ranges");
        System.out.println("  read     -- gets a plane from the cache");
        System.out.println("  exit     -- quits the interpreter");
      }
      else if (cmd.startsWith("i")) { // info
        // output dimensional position
        printArray("pos =", cache.getCurrentPos());
        // output source information
        ICacheSource source = cache.getSource();
        printSource("source =", cache);
        System.out.println("object count = " + source.getObjectCount());
        // output strategy information
        ICacheStrategy strategy = cache.getStrategy();
        printStrategy("strategy =", cache);
        printArray("priorities =", strategy.getPriorities());
        printOrder("order =", cache);
        printArray("range =", strategy.getRange());
        printArray("lengths =", strategy.getLengths());
      }
      else if (cmd.startsWith("o")) { // order
        System.out.println(ICacheStrategy.CENTERED_ORDER + " => centered");
        System.out.println(ICacheStrategy.FORWARD_ORDER + " => forward");
        System.out.println(ICacheStrategy.BACKWARD_ORDER + " => backward");
        System.out.print("Z: ");
        int z = Integer.parseInt(r.readLine().trim());
        System.out.print("C: ");
        int c = Integer.parseInt(r.readLine().trim());
        System.out.print("T: ");
        int t = Integer.parseInt(r.readLine().trim());
        ICacheStrategy strategy = cache.getStrategy();
        strategy.setOrder(z, 0);
        strategy.setOrder(c, 1);
        strategy.setOrder(t, 2);
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
      else if (cmd.startsWith("so")) { // source
        System.out.println("0: BufferedImage");
        System.out.println("1: byte array");
        System.out.print("> ");
        int n = Integer.parseInt(r.readLine().trim());
        switch (n) {
          case 0:
            cache.setSource(new BufferedImageSource(reader));
            break;
          case 1:
            cache.setSource(new ByteArraySource(reader));
            break;
          default:
            System.out.println("Unknown source: " + n);
        }
      }
      else if (cmd.startsWith("st")) { // strategy
        System.out.println("0: crosshair");
        System.out.println("1: rectangle");
        System.out.print("> ");
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
            System.out.println("Unknown strategy: " + n);
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
      else System.out.println("Unknown command: " + cmd);
    }
    reader.close();
  }

  // -- Helper methods --

  /** Helper utility for outputing contents of an int array, used by main. */
  private static final void printArray(String prefix, int[] array) {
    System.out.print(prefix);
    if (array == null) System.out.println(" null");
    else {
      for (int i=0; i<array.length; i++) System.out.print(" " + array[i]);
      System.out.println();
    }
  }

  /** Helper utility for outputing cache's associated source, used by main. */
  private static final void printSource(String prefix, Cache cache) {
    ICacheSource source = cache.getSource();
    System.out.print(prefix + " ");
    Class sourceClass = source.getClass();
    if (sourceClass == BufferedImageSource.class) {
      System.out.println("BufferedImage");
    }
    else if (sourceClass == ByteArraySource.class) {
      System.out.println("byte array");
    }
    else System.out.println("unknown");
  }

  /** Helper utility for outputing cache's associated strategy, used by main. */
  private static final void printStrategy(String prefix, Cache cache) {
    ICacheStrategy strategy = cache.getStrategy();
    System.out.print(prefix + " ");
    Class strategyClass = strategy.getClass();
    if (strategyClass == CrosshairStrategy.class) {
      System.out.println("crosshair");
    }
    else if (strategyClass == RectangleStrategy.class) {
      System.out.println("rectangle");
    }
    else System.out.println("unknown");
  }

  /** Helper utility for outputing cache strategy's order, used by main. */
  private static final void printOrder(String prefix, Cache cache) {
    ICacheStrategy strategy = cache.getStrategy();
    int[] order = strategy.getOrder();
    System.out.print(prefix);
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
  }

  /** Helper utility for constructing lengths array, used by main. */
  private static final int[] getLengths(IFormatReader r) {
    return new int[] {r.getSizeZ(), r.getSizeC(), r.getSizeT()};
  }

}
