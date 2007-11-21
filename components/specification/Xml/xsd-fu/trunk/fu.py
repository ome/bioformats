"""
Object model and helper classes used in the generation of Java classes from
an OME XML (http://www.ome.xml.org) XSD document.
"""

#  
#  Copyright (c) 2007 Chris Allan
#
#  Redistribution and use in source and binary forms, with or without
#  modification, are permitted provided that the following conditions
#  are met:
#  1. Redistributions of source code must retain the above copyright
#     notice, this list of conditions and the following disclaimer.
#  2. Redistributions in binary form must reproduce the above copyright
#     notice, this list of conditions and the following disclaimer in the
#     documentation and/or other materials provided with the distribution.
#
#  THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
#  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
#  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
#  ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
#  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
#  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
#  OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
#  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
#  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
#  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
#  SUCH DAMAGE.

import logging
import re
import os

from generateDS.generateDS import *
from xml import sax

from posix import getuid
from pwd import getpwuid

try:
	import mx.DateTime as DateTime
	def now():
		return DateTime.ISO.str(DateTime.now())
except ImportError:
	from datetime import datetime
	def now():
		return datetime.now()

# Default logger configuration
#logging.basicConfig(level=logging.DEBUG,
#                    format='%(asctime)s %(levelname)s %(message)s')

# A global mapping from XSD Schema types and Java types
JAVA_TYPE_MAP = {
	# Base types
	'xsd:boolean': 'Boolean',
	'xsd:dateTime': 'String',
	'xsd:string': 'String',
	'xsd:integer': 'Integer',
	'xsd:positiveInteger': 'Integer',
	'xsd:nonNegativeInteger': 'Integer',
	'xsd:float': 'Float',
	'xsd:anyURI': 'String',
	# Hacks
	'MIMEtype': 'String',
	'RedChannel': 'ChannelSpecTypeNode',
	'GreenChannel': 'ChannelSpecTypeNode',
	'BlueChannel': 'ChannelSpecTypeNode',
	'AcquiredPixelsRef': 'PixelsNode',
	'Description': 'String',
}

# The list of properties not to process.
DO_NOT_PROCESS = ["ID"]

# The default Java base class for OME XML model objects.
DEFAULT_BASE_CLASS = "OMEXMLNode"

# The default Java package for OME XML model objects.
DEFAULT_PACKAGE = "org.openmicroscopy.xml2007"

# The default template for class processing.
CLASS_TEMPLATE = "templates/Class.template"

class ModelProcessingError(Exception):
	"""
	Raised when there is an error during model processing.
	"""
	pass

class ReferenceDelegate(object):
	"""
	A "virtual" property delegate to be used with "reference" 
	OMEModelProperty instances. This delegate conforms loosely to the same
	interface as a delegate coming from generateDS.
	"""
	def __init__(self, dataType):
		self.name = dataType + "_BackReference"
		self.dataType = dataType
		# Ensures property code which is looking for elements or attributes
		# which conform to an enumeration can function.
		self.values = None
	
	def getMaxOccurs(self):
		return 9999

	def getMinOccurs(self):
		return 0

	def getType(self):
		return self.dataType

	def getName(self):
		return self.name
	
class OMEModelProperty(object):
	"""
	An aggregate type representing either an OME XML Schema element or 
	attribute. This class equates conceptually to an instance variable which
	may be of a singular type or a collection.
	"""
	
	def __init__(self, delegate, model):
		self.model = model
		self.delegate = delegate
		self.isAttribute = False
		self.isReference = False

	def _get_type(self):
		if self.isAttribute:
			return self.delegate.getData_type()
		return self.delegate.getType()
		
	type = property(_get_type, doc="""The property's XML Schema data type.""")
			
	def _get_maxOccurs(self):
		if self.isAttribute:
			return 1
		return self.delegate.getMaxOccurs()
		
	maxOccurs = property(_get_maxOccurs,
		doc="""The maximum number of occurances for this property.""")
		
	def _get_minOccurs(self):
		if self.isAttribute:
			if self.delegate.getUse() == "optional":
				return 0
			return 1
		return self.delegate.getMinOccurs()
		
	minOccurs = property(_get_minOccurs,
		doc="""The minimum number of occurances for this property.""")

	def _get_name(self):
		return self.delegate.getName()
		
	name = property(_get_name, doc="""The property's name.""")
	
	def _get_javaType(self):
		try:
			# Handle XML Schema types that directly map to Java types
			return JAVA_TYPE_MAP[self.type]
		except KeyError:
			# Hand back the type of references
			if self.isReference:
				return self.type
			# Hand back the type of complex types
			if not self.isAttribute and self.delegate.isComplex():
				return self.type + "Node"
			# Handle OME XML Schema "ID" types
			if self.type[-2:] == "ID":
				return self.type[:-2] + "Node"
			elif not self.delegate.values:
				# We have a property whose type was defined by a top level 
				# simpleType. 
				getSimpleType = self.model.getTopLevelSimpleType
				simpleType = getSimpleType(self.type)
				if simpleType is not None:
					try:
						# It's possible the simpleType is a union of other
						# simpleTypes so we need to handle that. We assume
						# that all the unioned simpleTypes are of the same
						# base type (ex. "xsd:string" or "xsd:float").
						if simpleType.unionOf:
							union = getSimpleType(simpleType.unionOf[0])
							return JAVA_TYPE_MAP[union.getBase()]
						return JAVA_TYPE_MAP[simpleType.getBase()]
					except KeyError:
						pass
			raise ModelProcessingError, \
				"Unable to find Java type for %s" % self.type
		
	javaType = property(_get_javaType, doc="""The property's Java type.""")
	
	def _get_javaArgumentName(self):
		m = re.search ('[a-z]', self.name)
		if not m: return self.name.lower()
		i = m.start()
		return self.name[:i].lower() + self.name[i:]
	
	javaArgumentName = property(_get_javaArgumentName,
		doc="""The property's Java argument name (camelCase).""")
	
	def isComplex(self):
		"""
		Returns whether or not the property has a "complex" content type.
		"""
		if self.isAttribute:
			raise ModelProcessingError, \
				"This property is an attribute and has no content model!"
		# FIXME: This hack is in place because of the incorrect content
		# model in the XML Schema document itself for the "Description"
		# element.
		if self.name == "Description":
			return False
		return self.delegate.isComplex()
		
	def fromAttribute(klass, attribute, model):
		"""
		Instantiates a property from an XML Schema attribute.
		"""
		instance = klass(attribute, model)
		instance.isAttribute = True
		return instance
	fromAttribute = classmethod(fromAttribute)
	
	def fromElement(klass, element, model):
		"""
		Instantiates a property from an XML Schema element.
		"""
		return klass(element, model)
	fromElement = classmethod(fromElement)

	def fromReference(klass, reference, model):
		"""
		Instantiates a property from a "virtual" OME XML schema reference.
		"""
		instance = klass(reference, model)
		instance.isReference = True
		return instance
	fromReference = classmethod(fromReference)

class OMEModelObject(object):
	"""
	A single element of an OME data model.
	"""
	
	def __init__(self, element, model):
		self.model = model
		self.element = element
		self.base = element.getBase()
		self.name = element.getName()
		self.properties = dict()
	
	def addAttribute(self, attribute):
		"""
		Adds an OME XML Schema attribute to the object's data model.
		"""
		name = attribute.getName()
		self.properties[name] = \
			OMEModelProperty.fromAttribute(attribute, self.model)
		
	def addElement(self, element):
		"""
		Adds an OME XML Schema element to the object's data model.
		"""
		name = element.getName()
		self.properties[name] = \
			OMEModelProperty.fromElement(element, self.model)

	def _get_javaBase(self):
		base = self.element.getBase()
		if base is None:
			return DEFAULT_BASE_CLASS
		return base + "Node"

	javaBase = property(_get_javaBase, 
		doc="""The model object's Java base class.""")
	
	def __str__(self):
		return self.__repr__(self)
	
	def __repr__(self):
		return '<"%s" OMEModelObject instance at 0x%x>' % (self.name, id(self))
	
class OMEModel(object):
	def __init__(self):
		self.objects = dict()
		
	def addObject(self, element, obj):
		if self.objects.has_key(element):
			raise ModelProcessingError(
				"Element %s has been processed!" % element)
		self.objects[element] = obj
		
	def getObject(self, element):
		return self.objects[element]
		
	def getObjectByName(self, name):
		for obj in self.objects:
			if obj.getName() == name:
				return self.objects[obj]
				
	def getTopLevelSimpleType(self, name):
		"""
		Returns the simpleType that has a given name from the list of top
		level simple types for this model.
		"""
		for simpleType in self.topLevelSimpleTypes:
			if simpleType.name == name:
				return simpleType
		return None

	def processAttributes(self, element):
		"""
		Process all the attributes for a given element (a leaf).
		"""
		attributes = element.getAttributeDefs()
		obj = self.getObject(element)
		length = len(attributes)
		for i, key in enumerate(attributes):
			logging.debug("Processing attribute: %s %d/%d" % (key, i, length))
			attribute = attributes[key]
			logging.debug("Dump: %s" % attribute.__dict__)
			name = attribute.getName()
			obj.addAttribute(attribute)

		children = element.getChildren()
		length = len(children)
		for i, child in enumerate(children):
			logging.debug("Processing child: %s %d/%d" % (child, i, length))
			logging.debug("Dump: %s" % child.__dict__)
			name = child.getCleanName()
			obj.addElement(child)

	def processLeaf(self, element, parent):
		"""
		Process an element (a leaf).
		"""
		if not element.isExplicitDefine():
			logging.info("Element %s.%s not an explicit define, skipping." % (parent, element))
			return
		if element.getMixedExtensionError():
			logging.error("Element %s.%s extension chain contains mixed and non-mixed content, skipping." % (parent, element))
			return
		obj = OMEModelObject(element, self)
		self.addObject(element, obj)
		self.processAttributes(element)

	def processTree(self, elements, parent=None):
		"""
		Recursively processes a tree of elements.
		"""
		length = len(elements)
		for i, element in enumerate(elements):
			logging.info("Processing element: %s %d/%d" % (element, i + 1, length))
			self.processLeaf(element, parent)
			children = element.getChildren()
			if children:
				self.processTree(children, element)

	def postProcessReferences(self):
		"""
		Examines the list of objects in the model for instances that conform
		to the OME XML Schema referential object naming conventions and
		injects properties into referenced objects to provide back links.
		"""
		references = dict()
		for o in self.objects.values():
			for prop in o.properties.values():
				if prop.type[-3:] == "Ref":
					shortName = prop.type[:-3]
					if prop.type not in references:
						references[shortName] = list()
					references[shortName].append(o.name)

		for o in self.objects.values():
			if o.name in references:
				for ref in references[o.name]:
					delegate = ReferenceDelegate(ref)
					prop = OMEModelProperty.fromReference(delegate, self)
					o.properties[ref] = prop

	def process(klass, contentHandler):
		"""
		Main process entry point. All instantiations of this class should be
		made through this class method unless you really know what you are
		doing.
		"""
		elements = contentHandler.getRoot().getChildren()
		model = klass()
		model.topLevelSimpleTypes = contentHandler.topLevelSimpleTypes
		model.processTree(elements)
		model.postProcessReferences()
		return model
	process = classmethod(process)
	
	def __str__(self):
		a = str()
		for o in model.objects.values():
			if o.base:
				a += "%s extends %s\n" % (o.name, o.base)
			else:
				a += "%s\n" % (o.name)
			attributes = o.flattenedAttributes(model)
			for name, atype in attributes.items():
				a += "   + Attribute: %s Type: %s\n" % (name, atype)
		return a
		
class TemplateInfo(object):
	"""
	Basic status information to pass to the template engine.
	"""
	def __init__(self, outputDirectory, package):
		self.outputDirectory = outputDirectory
		self.package = package
		self.date = now()
		self.user = getpwuid(getuid())[0]
		self.DO_NOT_PROCESS = DO_NOT_PROCESS

def parseXmlSchema(filename):
	"""
	Entry point for XML Schema parsing into an OME Model.
	"""
	# The following two statements are required to "prime" the generateDS
	# code.
	namespace = 'xsd:'
	set_type_constants(namespace)

	parser = sax.make_parser()
	ch = XschemaHandler()
	parser.setContentHandler(ch)
	parser.parse(filename)

	ch.getRoot().annotate()
	return OMEModel.process(ch)
