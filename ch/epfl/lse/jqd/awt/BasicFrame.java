//                              -*- Mode: Java -*- 
// BasicFrame.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Sep 25 15:17:45 1998
// Last Modified By: Matthias Wiesmann
// Last Modified On: Tue Nov 21 15:18:57 2000
// Update Count    : 31
// Status          : Working
// 

package ch.epfl.lse.jqd.awt;

import java.awt.Frame; 
import java.awt.MenuBar; 
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.MenuShortcut;

import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.Graphics;

import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** This frame is a minimal implementation of a window.
 *  It  builds a simple frame, with a default menu. 
 * The item are the following:
 * <dl compact>
 * <dt>print
 * <dd>builds a print job and calls the 
 *     <code>print</code> with the right graphics
 * <dt>quit
 * <dd>calls <code>System.exit(0)</code>
 * </dl>
 * Both method <code>doPrint</code> and <code>doQuit</code>
 * can be overidden to obtain more advanced behaviour. 
 * 
 * @author Matthias Wiesmann
 * @version 1.1
 */

public abstract class BasicFrame extends Frame implements ActionListener
{
   

    /** File menu label */
    protected static final String FILE_MENU = "file" ;
    /** Open Item */
    protected static final String OPEN_ITEM = "open" ;
    /** Print item */
    protected static final String PRINT_ITEM = "print" ;
    /** Quit item */
    protected static final String QUIT_ITEM = "quit" ;
    /** Quit keyboard Shortcut */
    protected static final MenuShortcut QUIT_SHORTCUT = 
    new MenuShortcut('q');
    /** Print keyboard Shortcut */
    protected static final MenuShortcut PRINT_SHORTCUT = 
    new MenuShortcut('p');

    /** Constructor 
     * Sets up the frame and inserts a basic menu and the appropriate 
     * event handlers.
     * @param title the title of the frame
     */
    public BasicFrame(String title) 
	{ 
	    super(title) ; 
	    setMenuBar(buildMenus());
	    // enabling window events 
	    enableEvents(WindowEvent.WINDOW_CLOSING);
	} // BasicFrame

    /** Builds the menu bar 
     * @return the menu bar
     */ 
    protected MenuBar buildMenus()
	{
	    // Setting up the quit item
	    MenuItem quitItem = new MenuItem(QUIT_ITEM);
	    quitItem.addActionListener(this); 
	    quitItem.setShortcut(QUIT_SHORTCUT);
	    quitItem.setEnabled( true);
	    // Setting up the print item
	    MenuItem printItem = new MenuItem(PRINT_ITEM);
	    printItem.addActionListener(this);
	    printItem.setShortcut(PRINT_SHORTCUT);
	    printItem.setEnabled( true);
	    // Setting up the file menu
	    Menu fileMenu = new Menu(FILE_MENU);
	    // adding the items
	    fileMenu.add(printItem);
	    fileMenu.addSeparator();
	    fileMenu.add(quitItem);
	    // attaching it to the menubar
	    MenuBar menuBar = new MenuBar();
	    menuBar.add(fileMenu);
	    return menuBar ;
	} // buildMenus


    /** Processses windows events, 
     * only closing events are handled and dispateched on
     * the <code>doQuit</code> method
     * @param e the event 
     */
    public void processWindowEvent(WindowEvent e)
	{
	    super.processWindowEvent(e);
	    int id = e.getID();
	    switch (id)
		{
		case WindowEvent.WINDOW_CLOSING : 
		    dispose();
		    doQuit();
		    break;
		} // switch 
	}//processWindowEvent

    /** This method is called when the <code>quit</code> item 
     *  is selected. <br>
     *  This version simply quits the java virtual machine
     */ 

    public void doQuit()
	{
	    System.exit(0);
	} // doQuit

    /** This method is called when the <code>print</code> item
     *  is selected. <br>
     *  This version builds a printJob and prints the component in i.
     */
    
    public void doPrint()
	{
	    Toolkit toolkit = Toolkit.getDefaultToolkit();
	    PrintJob job = toolkit.getPrintJob(this,getTitle(),null);
	    if (job== null) return ;
	    Graphics graph = job.getGraphics();
	    print(graph);
	    graph.dispose();
	    job.end();
	} // doPrint

    /** Tracks actions, more precisely menu events.
     * The quit menu event is dispateched to the <code>doQuit</code>
     * method
     * @param evt the event
     */ 

    public void actionPerformed(ActionEvent evt)
	{
	    final String action = evt.getActionCommand();
	    if (action == null) return ;
	    if (action.equals(QUIT_ITEM)) doQuit(); 
	    if (action.equals(PRINT_ITEM)) doPrint();
	} // actionPerformed
    
    public String toString()
	{
	    return ("Basic Java Frame"+super.toString());
	} //toString

} // BasicFrame
