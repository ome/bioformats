
// for all browsers but IE, we can check the supported mime types, so do that
// first.
function checkSVGViewer() {

	var hasPlugin = false;

	// this takes care of browsers that support mime types explicitly.
	if ( navigator.mimeTypes ) {
		var mimeObj = navigator.mimeTypes["image/svg-xml"];

		if (mimeObj == null)
			mimeObj = navigator.mimeTypes["image/svg+xml"];

		if (mimeObj != null && mimeObj.enabledPlugin != null) {
			hasPlugin = true;
		}

		// IE might pretend to have MIME types registered, but doesn't list any.
		if (navigator.mimeTypes.length == 0) {
			hasPlugin = document.embeds.length > 0;
		}
	}
	else {
		// this is easy - if the embed tags count, then we have the viewer.
		hasPlugin = document.embeds.length > 0;
	}

	// did we find the plugin?
	if (!hasPlugin) {
		// nope, ask if they would like to install.
		if (confirm("You do not appear to have an SVG viewer installed.  Would you like to install one now?") ) {
			document.location = "http:/" + "/www.adobe.com/svg/viewer/install/";
		}
	}
}

// Detect and switch the display of CDATA and comments from an inline view
//  to a block view if the comment or CDATA is multi-line.
function f(e)
{
	// if this element is an inline comment, and contains more than a single
	//  line, turn it into a block comment.
	if (e.className == "ci") {
		if (e.children(0).innerText.indexOf("\n") > 0)
			fix(e, "cb");
	}
 
	// if this element is an inline cdata, and contains more than a single
	//  line, turn it into a block cdata.
	if (e.className == "di") {
		if (e.children(0).innerText.indexOf("\n") > 0)
			fix(e, "db");
	}
 
	// remove the id since we only used it for cleanup
	e.id = "";
}

// Fix up the element as a "block" display and enable expand/collapse on it
function fix(e, cl)
{
	// change the class name and display value
	e.className = cl;
	e.style.display = "block";
 
	// mark the comment or cdata display as a expandable container
	j = e.parentElement.children(0);
	j.className = "c";

	// find the +/- symbol and make it visible - the dummy link enables tabbing
	k = j.children(0);
	k.style.visibility = "visible";
	k.href = "#";
}

// Change the +/- symbol and hide the children.  This function works on "element"
//  displays
function ch(e)
{
	// find the +/- symbol
	mark = e.children(0).children(0);
 
	// if it is already collapsed, expand it by showing the children
	if (mark.innerText == "+")
	{
		mark.innerText = "-";
		for (var i = 1; i < e.children.length; i++)
			e.children(i).style.display = "block";
	}
 
	// if it is expanded, collapse it by hiding the children
	else if (mark.innerText == "-")
	{
		mark.innerText = "+";
		for (var i = 1; i < e.children.length; i++)
			e.children(i).style.display="none";
	}
}

// Change the +/- symbol and hide the children.  This function work on "comment"
//  and "cdata" displays
function ch2(e)
{
	// find the +/- symbol, and the "PRE" element that contains the content
	mark = e.children(0).children(0);
	contents = e.children(1);
 
	// if it is already collapsed, expand it by showing the children
	if (mark.innerText == "+")
	{
		mark.innerText = "-";
		// restore the correct "block"/"inline" display type to the PRE
		if (contents.className == "db" || contents.className == "cb")
			contents.style.display = "block";
		else
			contents.style.display = "inline";
	}
 
	// if it is expanded, collapse it by hiding the children
	else if (mark.innerText == "-")
	{
		mark.innerText = "+";
		contents.style.display = "none";
	}
}

// Handle a mouse click
function cl()
{
	e = window.event.srcElement;
 
	// make sure we are handling clicks upon expandable container elements
	if (e.className != "c")
	{
		e = e.parentElement;
		if (e.className != "c")
		{
			return;
		}
	}
	e = e.parentElement;
 
	// call the correct funtion to change the collapse/expand state and display
	if (e.className == "e")
		ch(e);
	if (e.className == "k")
		ch2(e);
}

// Erase bogus link info from the status window
function h()
{
	window.status=" ";
}

// Set the onclick handler
document.onclick = cl;

