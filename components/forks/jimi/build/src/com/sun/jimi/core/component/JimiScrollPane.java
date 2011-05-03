package com.sun.jimi.core.component;

import java.awt.*;

public class JimiScrollPane extends Container// implements AdjustmentListener
{
	/**
	 * always show horizontal scrollbar
	 */
	public final static int SCROLL_HORIZONTAL = 0;
	/**
	 * always show vertical scrollbar
	 */
	public final static int SCROLL_VERTICAL   = 1;
	/**
	 * scrollbars as needed
	 */
	public final static int SCROLL_AS_NEEDED  = 2;
	/**
	 * always show scrollbars
	 */
	public final static int SCROLL_ALWAYS     = 3;
	
	// default scrollbar policy
	private final static int DEFAULT_POLICY   = SCROLL_AS_NEEDED;
	// current state of scrollpane
	private int policy;	
	
	// awt stuff
	private Scrollbar horizontal;
	private Scrollbar vertical;
	private Component comp;
	/* this is the little box in the bottom right corner */
	private Button box;
	// component dimensions and painting coords
	private int compHeight, compWidth;
	private int paintX, paintY;
	
	/**
	 * create a new BufferedScollPane with the default 
	 * scroll policy which is SCROLL_AS_NEEDED.
	 */

	public JimiScrollPane()
	{
		policy = DEFAULT_POLICY;

		setLayout(new ScrollPaneLayout());

		horizontal = new Scrollbar(Scrollbar.HORIZONTAL);
		//horizontal.addAdjustmentListener(this);
		horizontal.setLineIncrement(10);

		vertical   = new Scrollbar(Scrollbar.VERTICAL);
		//vertical.addAdjustmentListener(this);
		vertical.setLineIncrement(10);

		// magic box
		box = new Button();
	}

	/**
	 * create a new scrollpane based on policy
	 *
	 * @param policy the policy you want to use
	 *
	 * @see SCROLL_VERTICAL
	 * @see SCROLL_HORIZONTAL
	 * @see SCROLL_AS_NEEDED
	 * @see SCROLL_AWAYS
	 */
	public JimiScrollPane(int policy)
	{
		this();
		this.policy = policy;
	}

	/**
	 * create a new scrollpane based on policy and the component specified.
	 *
	 * @param comp the component you wish to scroll (instead of calling add(Component))
	 * @param policy the policy you want to use
	 *
	 * @see SCROLL_VERTICAL
	 * @see SCROLL_HORIZONTAL
	 * @see SCROLL_AS_NEEDED
	 * @see SCROLL_AWAYS
	 */
	public JimiScrollPane(Component comp, int policy)
	{
		this(policy);
		this.comp = comp;
		super.add(comp, 0);
   }

	/**
	 * create a new scrollpane with default scrollpolicy and the specified component.
	 *
	 * @param comp the component you wish to scroll (instead of calling add(Component))
	 *
	 * @see SCROLL_VERTICAL
	 * @see SCROLL_HORIZONTAL
	 * @see SCROLL_AS_NEEDED
	 * @see SCROLL_AWAYS
	 */
	public JimiScrollPane(Component comp)
	{
		this(comp, DEFAULT_POLICY);
	}

	/**
	 * adds a component to the viewport of the scrollpane
	 *
	 * @param comp the component you wish to scroll
	 */
	public Component add(Component comp)
	{
		//System.out.println("adding");
		this.comp = comp;
		return super.add(comp, 0);
	}

   //go figure
   public Dimension preferredSize() {
      return super.preferredSize();
   }

   /**
   * @return Vertical position of the scrollable object.
   */
   public int getVerticalPosition() {
      return paintY;
   }

   /**
   * @return Horizontal position of the scrollable object.
   */
   public int getHorizontalPosition() {
      return paintX;
   }


	/**
	* @return Vertical scrollbar width
	*/
	public int getVBarOffset()
	{
		int max = Math.max(vertical.size().width, vertical.preferredSize().width);
		//System.out.println("getVBarOffset->"+max);
		return max;
	}

	/**
	* @return Horizontal scrollbar height
	*/
	public int getHBarOffset()
	{
		int max = Math.max(horizontal.size().height, horizontal.preferredSize().height);
		//System.out.println("getHBarOffset->"+max);
		return max;
	}

   /**
   * @return vertical scrollbar
   */
   public Scrollbar getVerticalBar() {
      return vertical;
   }

   /**
   * @return horizontal scrollbar
   */
   public Scrollbar getHorizontalBar() {
      return horizontal;
   }

	/**
	 * @param policy the policy you want to use
	 *
	 * @see SCROLL_VERTICAL
	 * @see SCROLL_HORIZONTAL
	 * @see SCROLL_AS_NEEDED
	 * @see SCROLL_AWAYS
	 */
	public void setScrollPolicy(int policy) {
		this.policy = policy;
	}

	/**
	 * @return current policy
	 */
	public int getScrollPolicy() {
		return policy;
	}

	/**
	 * determines which scrollbars to enable/disable.
	 * this method should no be called directly.
	 */
	public void layout()
	{
		switch(policy)
		{
			case SCROLL_HORIZONTAL:
			{
				add("south", horizontal);
				add("center", comp);
				break;
			}
			case SCROLL_VERTICAL:
			{
				add("east", vertical);
				add("center", comp);
				break;
			}
			case SCROLL_AS_NEEDED:
			{
				//TODO: make scrollbars go away if they are not needed
				boolean horizontalNeeded = false;
				boolean verticalNeeded   = false;

				int compWidth = comp.preferredSize().width;
				int compHeight = comp.preferredSize().height;

				// VERTICAL CHECK
				if(compHeight > this.size().height - vertical.preferredSize().height)
				{
					//System.out.println("need vertical");
					add("east", vertical);
					verticalNeeded = true;
				}

				// HORIZONTAL CHECK
				if(compWidth > this.size().width - horizontal.preferredSize().width)
				{
					//System.out.println("need horizontal");
					add("south", horizontal);
					horizontalNeeded = true;
				}

				if(horizontalNeeded && verticalNeeded) {
					add("corner", box);
				}

				add("center", comp);
				break;
			}
			case SCROLL_ALWAYS:
			{
				add("east", vertical);
				add("south", horizontal);
				add("corner", box);
				add("center", comp);
				break;
			}
			default:
			//NONE
				break;
		}

		// update scrollbar settings
		if(vertical != null)
		{
			compHeight = comp.preferredSize().height - this.getViewPort().height;
			//System.out.println("vertical: "+this.getViewPort().height + " " + comp.size().height + " " + comp.preferredSize().height);
			vertical.setValues(0, 20, 0, compHeight+20);
		}

		if(horizontal != null)
		{
			compWidth = comp.preferredSize().width - this.getViewPort().width;
			//System.out.println("horizontal: "+this.getViewPort().width + " " + comp.size().width + " " + comp.preferredSize().width);
			horizontal.setValues(0, 20, 0, compWidth+20);
		}

		// finally call the super.layout() to notify the layout manager.
		super.layout();
	}

	/**
	 * @return the viewport dimensions based on scrollbar policy and whatnot
	 */
	protected Dimension getViewPort()
	{
		int width  = 0;
		int height = 0;

		if(policy == SCROLL_VERTICAL)
		{
			//VERTICAL (works)
			if(vertical != null)
			{
				width = size().width;
				height = size().height;
				//System.out.println("vertical, returning "+ width + " x " +height);
			}
		}
		else
		if(policy == SCROLL_HORIZONTAL)
		{
			//HORIZONTAL (works)
			width = size().width;
			height = size().height;
			//System.out.println("horizontal, returning "+ width + " x " +height);
		}
		else
		if(policy == SCROLL_ALWAYS)
		{
			//ALWAYS (works)
			width = size().width - vertical.preferredSize().width;
			//System.out.println(vertical.size().width + " " + vertical.preferredSize().width);
			height = size().height - horizontal.preferredSize().height;
			//System.out.println(horizontal.size().height + " " + horizontal.preferredSize().height);
			//System.out.println("both, returning "+width + " x " +height);
		}
		else
		if(policy == SCROLL_AS_NEEDED)
		{
			//AS NEEDED (works)
			if(vertical != null)
			{
      		width = size().width - vertical.preferredSize().width;
				//System.out.println(vertical.size().width + " " + vertical.preferredSize().width);
				if(horizontal != null)
				{
					//System.out.print("horizontal != null ");
					height = size().height - horizontal.preferredSize().height;
					//System.out.println(horizontal.size().width + " " + horizontal.preferredSize().width);
				}
				else {
					height = size().height;
				}
			}

			if(horizontal != null)
			{
				height = size().height - horizontal.preferredSize().height;
				//System.out.println(horizontal.size().height + " " + horizontal.preferredSize().height);
				if(vertical != null)
				{
					//System.out.print("vertical != null ");
					width = size().width - vertical.preferredSize().width;
					//System.out.println(vertical.size().height + " " + vertical.preferredSize().height);
				}
				else {
					width = size().width;
				}
			}
			//System.out.println("as needed, returning "+width + " x " +height);
		}
		return new Dimension(width, height);
	}

	/*
	 * handles what has to be handled
	 */
	public boolean handleEvent(Event e)
	{
		Object source = e.target;

		/*
		* maybe i should have the box be a canvas with the little Activted logo
		* like the frame logo in the JBA demo.
		*/
		if(source instanceof Button)
		{
			comp.move(-compWidth/2, -compHeight/2);

			vertical.setValue(compHeight/2);
			horizontal.setValue(compWidth/2);
			//System.out.println(e.arg);
		}
		else
		if(source instanceof  Scrollbar)
		{
			int value = ((Integer)e.arg).intValue();

			//VERTICAL
			if(policy == SCROLL_VERTICAL)
			{
				paintY = -value;
				paintX = 0;
				comp.move(paintX, paintY);
			}
			else
			//HORIZONTAL
			if(policy == SCROLL_HORIZONTAL)
			{
				paintY = 0;
				paintX = -value;
				comp.move(paintX, paintY);
			}
			else
			//BOTH & AS_NEEDED
			if(policy == SCROLL_ALWAYS || policy == SCROLL_AS_NEEDED)
			{
				if(source == horizontal)
				{
					paintX = -value;
					paintY = -vertical.getValue();
				}
				else
				if(source == vertical)
				{
					paintY = -value;
					paintX = -horizontal.getValue();
				}
				comp.move(paintX, paintY);
			}
		}
		else {
			return false;
		}
		return super.handleEvent(e);
	}

	/*
	public void adjustmentValueChanged(java.awt.event.AdjustmentEvent e)
	{
		Object source = e.getSource();

		int value = e.getValue();

		//VERTICAL
		if(policy == SCROLL_VERTICAL)
		{
			paintY = -value;
			paintX = 0;
			comp.move(paintX, paintY);
		}
		else
		//HORIZONTAL
		if(policy == SCROLL_HORIZONTAL)
		{
			paintY = 0;
			paintX = -value;
			comp.move(paintX, paintY);
		}
		else
		//BOTH & AS_NEEDED
		if(policy == SCROLL_BOTH || policy == SCROLL_AS_NEEDED)
		{
			if(source == horizontal)
			{
				paintX = -value;
				paintY = -vertical.getValue();
			}
			else
			if(source == vertical)
			{
				paintY = -value;
				paintX = -horizontal.getValue();
			}
			comp.move(paintX, paintY);
		}
	}
	*/
}
