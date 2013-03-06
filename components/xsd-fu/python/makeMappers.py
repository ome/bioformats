#!/usr/bin/env python

"""
Collapses a directory of OME SemanticType definition files into a single file.

Chris Allan <callan@blackcat.ca> Fri Aug 10 14:24:24 BST 2007
"""

import logging
import getopt
import sys
import os

import elementtree.ElementTree as et

logging.basicConfig(
	level=logging.WARN,
    format='%(asctime)s %(levelname)s %(message)s')

def usage(msg=None):
	"""
	Prints usage so that we don't have to. :)
	"""
	cmd = sys.argv[0]
	if msg:
		print msg
	print "Usage: %s [OPTIONS] <std_file> <dsl_file>" % cmd
	print """
Options:
  -d : enable debugging
  -o : output directory (Defaults to $CWD)
  -c : config file for explicit mappings

Examples:
  %s core.xml omero/acquisition.ome.xml
  %s -o OUT -d core.xml omero/all.xml
	
Report bugs to <callan@blackcat.ca>.""" % (cmd, cmd)
	sys.exit(1)
	
class AbstractObjectModel(object):
	"""
	An abstract object model atop an ElementTree.
	"""
	def __init__(self, xmlFile):
		self.tree = et.parse(xmlFile)
		self.root = self.tree.getroot()
		
	def types(self):
		return self.root.findall(self.ns + self.typeElementName)
		
	def typeNames(self):
		names = list()
		for modelType in self.types():
			names.append(self.getTypeName(modelType))
		return names
		
	def getTypeName(self, modelType):
		return modelType.get(self.idAttribute)
		
	def getType(self, typeName):
		types = self.types()
		for atype in types:
			if self.getTypeName(atype) == typeName:
				return atype
		return None
		
	def getTypeAttributes(self, atype):
		if atype == None:
			raise Exception("Unknown type: %s" % typeName)
		properties = self.getTypeProperties(atype)
		if properties is None:
			logging.warn("%s in %s has no attributes" % \
				(self.getTypeName(atype), type(self)))
			return
		attributes = dict()
		for property in properties:
			name = property.get(self.propertyNameAttribute)
			atype = property.get(self.propertyTypeAttribute)
			attributes[name] = atype
		return attributes

class DSLModel(AbstractObjectModel):
	"""
	An OMERO DSL model.
	"""
	ns = ""
	typeElementName = "type"
	idAttribute = "id"
	propertyNameAttribute = "name"
	propertyTypeAttribute = "type"
	
	def getTypeName(self, modelType):
		name = super(DSLModel, self).getTypeName(modelType)
		i = name.rfind(".") + 1
		return name[i:]
		
	def getTypeProperties(self, atype):
		a = atype.find(self.ns + "properties")
		if a is None:
			return
		return a.getchildren()

class STModel(AbstractObjectModel):
	"""
	An OME Semantic Type model.
	"""
	ns = "{http://www.openmicroscopy.org/XMLschemas/STD/RC2/STD.xsd}"
	typeElementName = "SemanticType"
	idAttribute = "Name"
	propertyNameAttribute = "Name"
	propertyTypeAttribute = "DataType"
	
	def getTypeProperties(self, atype):
		return atype.findall(self.ns + "Element")
		
def makeMapper(st, stType, dsl, dslType):
	stAttributes = st.getTypeAttributes(stType)
	dslAttributes = dsl.getTypeAttributes(dslType)
	print repr(stAttributes)
	print repr(dslAttributes)
	for stAttribute in stAttributes:
		matched = False
		if dslAttributes is None:
			print "  !! DSL has no attributes to match"
			break
		for dslAttribute in dslAttributes:
			if stAttribute == dslAttribute:
				print "  ++ Attribute matched %s" % stAttribute
				matched = True
				break
		if not matched:
			print "  -- No match for %s" % stAttribute

def makeMappers(outputDirectory, st, dsl):
	for stTypeName in st.typeNames():
		matched = False
		for dslTypeName in dsl.typeNames():
			if stTypeName == dslTypeName:
				print "Type matched %s" % (stTypeName)
				stType = st.getType(stTypeName)
				dslType = dsl.getType(dslTypeName)
				makeMapper(st, stType, dsl, dslType)
				matched = True
				break
		if not matched:
			print "No match for %s" % (stTypeName)
#	print "ST %s" % repr(st.typeNames())
#	print "DSL %s" % repr(dsl.typeNames())
	
if __name__ == '__main__':
	try:
		options, args = getopt.getopt(sys.argv[1:], 'do:')
		if len(args) != 2:
			usage()
	except getopt.GetoptError, (msg, opt):
		usage(msg)
	
	outputDirectory = os.getcwd()
	configFile = None
	for option, argument in options:
		if option == "-d":
			# Default logger
			logging.basicConfig(
				level=logging.DEBUG,
			    format='%(asctime)s %(levelname)s %(message)s')
		if option == "-o":
			logging.debug("Output directory: %s" % argument)
			outputDirectory = argument
		if option == "-c":
			configFile = open(argument, "r")

	st = STModel(args[0])
	dsl = DSLModel(args[1])
	
	makeMappers(outputDirectory, st, dsl)
