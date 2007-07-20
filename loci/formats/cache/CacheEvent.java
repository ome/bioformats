//
// CacheEvent.java
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

/** A event indicating a cache update. */
public class CacheEvent {

  // -- Constants --

  /** Event type indicating a new cache source. */
  public static final int SOURCE_CHANGED = 1;

  /** Event type indicating a new cache strategy. */
  public static final int STRATEGY_CHANGED = 2;

  /** Event type indicating an updated current dimensional position. */
  public static final int POSITION_CHANGED = 3;

  /** Event type indicating new axis priorities. */
  public static final int PRIORITIES_CHANGED = 4;

  /** Event type indicating new planar ordering. */
  public static final int ORDER_CHANGED = 5;

  /** Event type indicating new planar ordering. */
  public static final int RANGE_CHANGED = 6;

  /** Event type indicating an object has been added to the cache. */
  public static final int OBJECT_LOADED = 7;

  /** Event type indicating an object has been removed from the cache. */
  public static final int OBJECT_DROPPED = 8;

  // -- Fields --

  /** Source of the cache update. */
  protected Object source;

  /** Type of cache event. */
  protected int type;

  /** Relevant index to the event, if any. */
  protected int index;

  // -- Constructor --

  /** Constructs a cache event. */
  public CacheEvent(Object source, int type) { this(source, type, -1); }

  /** Constructs a cache event. */
  public CacheEvent(Object source, int type, int index) {
    this.source = source;
    this.type = type;
    this.index = index;
  }

  // -- CacheEvent API methods --

  /** Gets the source of the cache update. */
  public Object getSource() { return source; }

  /** Gets the type of cache update. */
  public int getType() { return type; }

  /**
   * Gets the index relevant to the cache update, if any.
   * This parameter is only set for events POSITION_CHANGED,
   * OBJECT_LOADED and OBJECT_DROPPED.
   */
  public int getIndex() { return index; }

}
