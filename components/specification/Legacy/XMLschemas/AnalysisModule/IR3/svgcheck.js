// Copyright 2000 Adobe Systems Incorporated. All rights reserved. Permission
// to use, modify, distribute, and publicly display this file is hereby
// granted. This file is provided "AS-IS" with absolutely no warranties of any
// kind. Portions (C) Netscape Communications 1999.

// If you modify this file, please share your changes with Adobe and other SVG
// developers at http://www.adobe.com/svg/.

// Version 3/23/00

function setCookie(name, value, expires, path, domain, secure) {
var curCookie=name+"="+escape(value)+
	((expires)?"; expires="+expires.toGMTString():"")+
	((path)?"; path="+path:"")+
	((domain)?"; domain="+domain:"")+
	((secure)?"; secure":"");
document.cookie=curCookie;
}

var svgInstallBase="http://www.adobe.com/svg/viewer/install/";
var svgInstallPage=svgInstallBase+"auto/";
var svgInfoPage="http://www.adobe.com/svg/";
var svgDownloadPage=svgInstallBase;
var checkIntervalDays=30;
var firstSVG=true; // Ask only once per page even without cookies

function getSVGInstallPage() {
return svgInstallPage+"?"+location;
}

function isSVGPluginInstalled() {
return (true);
}

var g_askForSVGViewer = false;
var g_svgInstalled = false;

function checkSVGViewer() {
	g_askForSVGViewer=false;
	if(g_svgInstalled)
		return;
	
	if (navigator.mimeTypes.length > 0) {
		g_svgInstalled=isSVGPluginInstalled();
	}
	else {
		g_svgInstalled=isSVGControlInstalled();
	}
	
	g_askForSVGViewer = !g_svgInstalled;

}
function getSVGViewer() {
if(confirm('The Adobe SVG Viewer is not installed. Download now?'))
	location=getSVGInstallPage();
}

function checkAndGetSVGViewer() {
	checkSVGViewer();
	if(g_askForSVGViewer) {
		getSVGViewer();
		}
}

function emitSVG(embedAttrs) {
	if(g_svgInstalled)
		document.writeln('<embed '+embedAttrs+ 'pluginspage="'+svgDownloadPage+'">');
	else if(g_askForSVGViewer)	{
		document.writeln('<p>To view this page you need an SVG viewer.');
		document.writeln('<a href="'+getSVGInstallPage()+'">Click here</a> for more information.</p>');
	}
}
