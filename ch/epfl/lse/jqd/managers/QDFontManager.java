//                              -*- Mode: Java -*- 
// QDFontManager.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  2 13:49:34 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Tue Nov 21 15:32:35 2000
// Update Count    : 3
// Status          : Renamed
// 

package ch.epfl.lse.jqd.managers;

import ch.epfl.lse.jqd.basics.QDException;
import java.awt.Font;
import java.util.Hashtable;

/** This class represents the QuickDraw Font Manager
 *  It offers services to translate QuickDraw fonts 
 *  into their AWT equivalent.
 *  @author Matthias Wiesmann
 *  @version 1.0 revised
 *  @see java.awt.Font
 */

public class QDFontManager 
{
	
    /** Helvetica fond identifier (default font) */
    public static final short HELVETICA_ID = 21 ;
	
    /** This table contains the different fonts */ 
    protected Hashtable fonts  ;
	
    /** Constructor, initializes the font table */
    public QDFontManager() {
	fonts = new Hashtable() ;
	addFont("Helvetica",HELVETICA_ID);
    } // QDFontManager
    
    /** links a font name with a given font id
     * @param name the name of the font to insert
     * @number the id the font is linked to 
     */ 
    public void addFont(String name, int number) {
	
	Integer index= new Integer(number);
	if (fonts.containsKey(index)) return ;
	System.out.println("adding font \""+name+"\" "+index);
	fonts.put(index,name);
    } // addFont
    
    /** returns the name of a font for an id 
     * @param number the id of the font
     * @return the name of the font 
     */ 
		
    public String getName(int number) throws QDException {
	Integer index= new Integer(number);
	String name = (String) fonts.get(index) ;
	if (name==null) throw new QDUnknownFont(number);
	return name ;
    } // getFont
    
    /** builds a font 
     * @param number the QuickDraw id of the font
     * @param style the AWT style of the font
     * @param size the point size of the font
     * @return the font, <code>helvetica</code> if the font is not found
     */
		
    public Font getFont(int number, int style, int size) throws QDException {
	String name ;
	try {
	    name =getName(number);
	} catch (QDUnknownFont e) { 
	    name="Helvetica" ; }
	final Font result= new Font(name,style,size) ;
	System.out.println(number +"->"+name+"->"+result);
	return(result);
    } // getFont 
    
    /** Text information about the font manager
     * @return information string
     */ 
    public String toString() {
	return("Font Manager "+fonts);
    } // toString
    
} // QDFontManager
	
/** protected exception, signals a unknown font */
	
class QDUnknownFont extends QDException 
{
    final int number ;
    public QDUnknownFont(int number)
	{
	    this.number=number ;
	} // QDUnknowFont

    public String toString()
	{
	    return("Unknown font number: "+number);
	} // toString
} // QDUnknowFont
