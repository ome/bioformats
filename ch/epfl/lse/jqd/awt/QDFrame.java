//                              -*- Mode: Java -*- 
// QDFrame.java --- 
// Author          : Matthias Wiesmann
// Created On      : Mon Jul 12 16:44:10 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Wed Dec  6 11:37:34 2000
// Update Count    : 10
// Status          : OK
// 

package ch.epfl.lse.jqd.awt;

import java.io.IOException;
import java.io.File;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Label;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import ch.epfl.lse.jqd.awt.BasicFrame;

import ch.epfl.lse.jqd.basics.QDException;
import ch.epfl.lse.jqd.basics.QDPicture; 

/** This frames displays a QuickDraw File 
 *  when the user clicks on a text item, 
 *  the string is displayed
 *  on standart output
 *  @author Matthias Wiesmann
 *  @version 1.0 
 */ 

public class QDFrame extends BasicFrame
    implements MouseListener
{
    /** the canvas were the drawing is done */
    protected QDCanvas canvas ;
    
    /** how to handle the quit event */
    public void doQuit() {
	System.exit(0); 
    } // doQuit 
    
    
    /** builds a QD Frame from a QuickDraw file 
     * @param file the QuickDraw file
     * @exception IOException problem with the file
     * @exception QDException problem with the picture
     */

    public QDFrame(File file)
	throws IOException, QDException  {
	super(file.getName());
	QDPicture pict = new QDPicture(file);
	canvas = new QDCanvas(pict);
	canvas.addMouseListener(this);
	final Label label = new Label("length: "+file.length());
	add(BorderLayout.SOUTH,label);
	add(BorderLayout.CENTER,canvas);
	pack();
    } // QDFrame

    /** handles the mouse clicked event 
     * it prints the text at mouse point
     * @param e the event
     */ 

    public void mouseClicked(MouseEvent e) {
	final Point p = e.getPoint();
	System.err.println(canvas.getText(p));
    } // mouseClicked
    
    public void mouseEntered(MouseEvent e) {}
    
    public void mouseExited(MouseEvent e) {} 
    
    public void mousePressed(MouseEvent e) {}
    
    public void mouseReleased(MouseEvent e) {} 

    /** Sets up a simpe viever program
     *  This program load a QDCanvas and displays it.
     */ 

    public static void main(String[] args)
	throws IOException, QDException {
	if (args.length<1) { System.err.println("usage <filename>"); System.exit(-1); } 
	final String fileName = args[0] ;
	File file = new File(fileName);
	QDFrame frame = new QDFrame(file);
	frame.show();
    } // main
    
    public void print(Graphics g) {
	canvas.print(g);  
    } // print
    
} // QDFrame

