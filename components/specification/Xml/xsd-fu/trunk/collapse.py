#!/usr/bin/env python

"""
Collapses a directory of OME SemanticType definition files into a single file.

Chris Allan <callan@blackcat.ca> Fri Aug 10 14:24:24 BST 2007
"""

import logging
import getopt
import sys
import os

from xml.dom.minidom import getDOMImplementation
from xml import sax

ROOT_NAME = "SemanticTypeDefinitions"
ROOT_NAMESPACE = "http://www.openmicroscopy.org/XMLschemas/STD/RC2/STD.xsd"

class ElementAggregator(sax.ContentHandler):
	"""
	Aggregates all elements in a SAX run.
	"""
	
	def __init__(self):
		# Setup the DOM tree
		impl = getDOMImplementation()
		self.dom = impl.createDocument(None, None, None)
		self.dom.appendChild(self.createRoot())
		self.stack = [self.dom.documentElement]
		self.skipped = False
		
	def createRoot(self):
		"""
		Creates an initial DOM tree to work from.
		"""
		element = self.dom.createElement(self.rootElementName)
		if self.namespace is not None:
			ns = self.dom.createAttribute("xmlns")
			ns.value = self.namespace
			element.setAttributeNode(ns)
		return element
	
	def startElement(self, name, attribs):
		'''
		Aggregates each element.
		'''
		logging.debug("Start element: %s" % name)
		if name in self.skip:
			logging.debug("Skipping START element logic for: %s" % name)
			return
		newElement = self.dom.createElement(name)
		for (attr, value) in attribs.items():
			newAttribute = self.dom.createAttribute(attr)
			newAttribute.value = value
			newElement.setAttributeNode(newAttribute)
		self.stack.append(newElement)
			
	def endElement(self, name):
		"""
		Populates the DOM tree.
		"""
		logging.debug("End element: %s" % name)
		logging.debug("Stack before: %s" % repr(self.stack))
		if name in self.skip:
			logging.debug("Skipping END element logic for: %s" % name)
			return
		if len(self.stack) > 1:
			newElement = self.stack.pop()
			self.stack[-1].appendChild(newElement)
		logging.debug("Stack after: %s" % repr(self.stack))

	def characters(self, content):
		# Strip trailing and/or leading whitespace, "\n", "\r", etc.
		content = content.strip().strip('\n\r')
		if len(content) > 0 and len(self.stack) > 0:
			textNode = self.dom.createTextNode(content)
			self.stack[-1].appendChild(textNode)
			
class DSLElementAggregator(ElementAggregator):
	skip = ["types"]
	namespace = None
	rootElementName = "types"
	
class STDElementAggregator(ElementAggregator):
	skip = ["OME", "SemanticTypeDefinitions"]
	namespace = "http://www.openmicroscopy.org/XMLschemas/STD/RC2/STD.xsd"
	rootElementName = "SemanticTypeDefinitions"

def usage(msg=None):
	"""
	Prints usage so that we don't have to. :)
	"""
	cmd = sys.argv[0]
	if msg:
		print msg
	print "Usage: %s [OPTIONS] ..." % cmd
	print """
Options:
  -r : recurse (only parses files that are of the type's extension)
  -d : enable debugging
  -o : output file name
  -t : either "dsl" or "std" XML file collapse (defaults to "std")

Examples:
  %s -r -d -o MySTDs.xml cvs/OME/src/xml/Core
  %s -o Experiment.xml cvs/OME/src/xml/Core/Experiment.ome
  %s -o base.xml -t dsl omero/acquisition.ome.xml
	
Report bugs to <callan@blackcat.ca>.""" % (cmd, cmd, cmd)
	sys.exit(1)
	
def parseTree(parser, path, extension):
	"""
	Parses a recursive directory tree (only files that match the extension)
	"""
	for root, dirs, files in os.walk(path):
		for f in files:
			i = len(extension) * -1
			if f[i:].lower() == extension:
				fullpath = os.path.join(root, f)
				logging.debug("Parsing file: %s" % fullpath)
				parser.parse(fullpath)
	
if __name__ == '__main__':
	try:
		options, args = getopt.getopt(sys.argv[1:], 'rdo:t:')
		if len(args) == 0:
			usage()
	except getopt.GetoptError, (msg, opt):
		usage(msg)
	
	outputFile = sys.stdout
	recursive = False
	aggregator = STDElementAggregator()
	extension = "ome"
	for option, argument in options:
		if option == "-r":
			logging.debug("Parsing recursively.")
			recursive = True
		if option == "-d":
			# Default logger
			logging.basicConfig(
				level=logging.DEBUG,
			    format='%(asctime)s %(levelname)s %(message)s')
		if option == "-o":
			logging.debug("Output file: %s" % argument)
			outputFile = open(argument, "w")
		if option == "-t":
			if argument == "dsl":
				aggregator = DSLElementAggregator()
				extension = "ome.xml"
			elif argument != "std":
				usage("option -t must be 'dsl' or 'std'")
	
	parser = sax.make_parser()
	parser.setContentHandler(aggregator)
	
	for path in args:
		if not recursive:
			if os.path.isdir(path):
				logging.warning("Not parsing directory: %s" % path)
				continue
			logging.debug("Parsing file: %s" % path)
			parser.parse(path)
		else:
			if os.path.isdir(path):
				logging.debug("Recursively parsing: %s" % path)
				parseTree(parser, path, extension)
			else:
				logging.debug("Parsing file: %s" % path)
				parser.parse(path)

	outputFile.write(aggregator.dom.toprettyxml())