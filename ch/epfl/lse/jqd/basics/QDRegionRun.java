//                              -*- Mode: Java -*- 
// QDRegionRun.java --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Aug  5 13:18:58 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Tue Nov 21 15:20:10 2000
// Update Count    : 33
// Status          : ~ working
// 

package ch.epfl.lse.jqd.basics;

import java.awt.*;

import java.util.Vector; 

import java.io.DataInputStream;
import java.io.IOException;

public class QDRegionRun 
{
    protected final int y ;
    protected final int start ;
    protected final int end ; 
    protected static final int END_MARK = 0x7FFF ;

    protected QDRegionRun(int y, int start, int end) {
	this.start = start ;
	this.end = end ;
	this.y = y ;
    } // QDRegionRun

    public Rectangle getBounds() {
	return new Rectangle(start,y,end-start,1);
    } // getBounds

    public Rectangle getBounds(QDRegionRun r) {
	return new Rectangle(start,y,r.end-start,1);
    } // getBounds

    public boolean isBorder() {
	return false ;
    } // isBorder

    /** builds a region run
     *  @return the run, or null if end of y run
     *  @param stream the input stream
     *  @param y the y axis of the run
     *  @exception IOException I/O problem
     *  @return the run
     */

    protected static QDRegionRun eFactory(DataInputStream stream, int y)
	throws IOException {
	final int start = stream.readShort();
	if (start == END_MARK) return  null;
	final int end = stream.readShort();
	return new QDRegionRun(y,start,end);
    } // factory

    /** Builds a vector of RegionRuns
     *  @param stream the input stream
     *  @param size the maximum size of data to read (not used).
     *  @exception IOException I/O problem
     *  @return the vector of runs
     */
    protected static Vector vFactory(DataInputStream stream, int size)
	throws IOException {
	int total = 0 ;
	Vector v = new Vector();
	while(true) {
	    final int y = stream.readShort();
	    if (y==END_MARK) return v ;
	    while(true) {
		final QDRegionRun run = eFactory(stream,y);  
		if (run==null) break ;
		v.addElement(run);
	    } // while
	}// while
    } // factory
    
    /** builds an array of RegionRuns 
     *  @param stream the input stream
     *  @param size the maximum size of data to read (not used).
     *  @exception IOException I/O problem
     *  @return the array of runs
     */

    protected static QDRegionRun[] factory(DataInputStream stream, int size)
	throws IOException {
	Vector v = vFactory(stream,size);
	final int s = v.size();
	QDRegionRun array[] = new QDRegionRun[s];
	for(int i=0;i<s;i++) {
	    array[i] = (QDRegionRun) v.elementAt(i);
	} // for
	return array;
    } // factory 

    public String toString() {
	return "QD Region Run [y="+y+
	    " start="+start+
	    " end="+end+"]";
    } // toSring
} // QDRegionRun
