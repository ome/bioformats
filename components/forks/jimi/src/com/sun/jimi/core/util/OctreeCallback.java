package com.sun.jimi.core.util;

/**
 * Interface to allow callback from OctreeNode to the
 * Octree implementation class primarilly for marking and
 * searching for reducible color nodes. A secondary application
 * for this interface was created when methods for caching of
 * OctreeNode objects no longer required and returning of those
 * cached objects to test for improved performance by avoiding
 * the new object creation overhead. [this may not be worth it] 
 *
 * @author	Robin Luiten
 * @version	1.0	12/Sep/1997
 */
interface OctreeCallback
{
	/** The maximum number of levels in the octree */
	public final static int MAXLEV = 8;

	/**
	 * @param on This octree is reducible, keep a reference in
	 * an internal datastructure for later return by getReducible()
	 * when and if requested. Also set the "marked" field to true
	 * in the OctreeNode.
	 */
	public void markReducible(OctreeNode on);

	/**
	 * This method can implement the "best" color reduction as it
	 * sees fit by returning the appropiatte octree node which can
	 * be color reduced. This routine is only in this interface because
	 * it logically belongs with markReducible().
	 *
	 * @return a candidate node in the octree that has been previously
	 * registed with markReducible() which is the "best" candidate for colour
	 * reduction.
	 */
	public OctreeNode getReducible();

	/**
	 * Usefull mechanism to allow the caching of nodes which are
	 * released by the colapseTree method(). This should reduce the
	 * totally amount of time initialising new objects during the
	 * creation of the Octree.
	 *
	 * Minimal implementation for this is Nothing at all.
	 */
	public void cacheONode(OctreeNode on);

	/** 
	 * Minimal implementation for this is
	 * <pre>
	 * 	return new OctreeNode(oc, level);
	 * </pre>
	 *
	 * @return cached OctreeNode. Or create a new one if none cached
	 */
	public OctreeNode getONode(OctreeCallback oc, int level);
}

