"""
Object model and helper classes used in the generation of Java classes from
an OME XML (http://www.ome-xml.org) XSD document.
"""

#
#  Copyright (C) 2009 University of Dundee. All rights reserved.
#
#
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or
#  (at your option) any later version.
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#  
#  You should have received a copy of the GNU General Public License along
#  with this program; if not, write to the Free Software Foundation, Inc.,
#  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#


import generateDS.generateDS
import logging
import re
import os

from generateDS.generateDS import *
from xml import sax

from posix import getuid
from pwd import getpwuid

from util import odict

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

# A global mapping from XSD Schema types and Java types that is used to
# inform and override type mappings for OME Model properties which are
# comprised of XML Schema attributes, elements and OME XML reference virtual
# types.
JAVA_TYPE_MAP = None

# A global type mapping from XSD Schema types to Java base classes that is
# used to override places in the model where we do not wish subclassing to
# take place.
JAVA_BASE_TYPE_MAP = None

# Types which have not been recognized as explicit defines (XML Schema
# definitions that warrant a the creation of a first class model object) that
# we wish to be treated otherwise. As part of the code generation process they 
# will also be confirmed to be top-level types.
EXPLICIT_DEFINE_OVERRIDE = ('Dichroic', 'Experimenter', 'FilterSet')

def updateTypeMaps(namespace):
	"""
	Updates the type maps with a new namespace. **Must** be executed at least 
	once, **before** node class file generation.
	"""
	global JAVA_TYPE_MAP
	JAVA_TYPE_MAP = {
		# Base types
		namespace + 'boolean': 'Boolean',
		namespace + 'dateTime': 'String',
		namespace + 'string': 'String',
		namespace + 'integer': 'Integer',
		namespace + 'positiveInteger': 'Integer',
		namespace + 'nonNegativeInteger': 'Integer',
		namespace + 'float': 'Double',
		namespace + 'anyURI': 'String',
		# Hacks
		'PixelType': 'String',
		'NamingConvention': 'String',
		'PercentFraction': 'Double',
		'MIMEtype': 'String',
#		'RedChannel': 'ChannelSpecTypeNode',
#		'GreenChannel': 'ChannelSpecTypeNode',
#		'BlueChannel': 'ChannelSpecTypeNode',
		'AcquiredPixelsRef': 'PixelsNode',
		'Description': 'String',
#		'Leader': 'ExperimenterRefNode',
	}
	
	global JAVA_BASE_TYPE_MAP
	JAVA_BASE_TYPE_MAP = {
		'UniversallyUniqueIdentifier': DEFAULT_BASE_CLASS
	}

# The list of properties not to process.
DO_NOT_PROCESS = [] #["ID"]

# Default root XML Schema namespace
DEFAULT_NAMESPACE = "xsd:"

# The default Java base class for OME XML model objects.
DEFAULT_BASE_CLASS = "Object"

# The default Java package for OME XML model objects.
DEFAULT_PACKAGE = "ome.xml.r2008"

# The default template for enum class processing.
ENUM_TEMPLATE = "templates/Enum.template"

# The default template for enum handler class processing.
ENUM_HANDLER_TEMPLATE = "templates/EnumHandler.template"

# The default template for class processing.
CLASS_TEMPLATE = "templates/Pojo.template"

# The default template for MetadataStore processing.
METADATA_STORE_TEMPLATE = "templates/MetadataStore.template"

# The default template for MetadataRetrieve processing.
METADATA_RETRIEVE_TEMPLATE = "templates/MetadataRetrieve.template"

# The default template for AggregateMetadata processing.
METADATA_AGGREGATE_TEMPLATE = "templates/AggregateMetadata.template"

def resolve_parents(model, element_name):
	"""
	Resolves the parents of an element and returns them as an ordered list.
	"""
	parents = dict()
	try:
		my_parents = model.parents[element_name]
	except KeyError:
		return None
	for parent in my_parents:
		parents[parent] = resolve_parents(model, parent)
	return parents
	
def resolve_paths(paths, level, names):
	"""
	Resolves a set of ASCII art graphics for a given hierarchical tree
	structure of element names.
	"""
	if names is None:
		return
	for name in names.keys():
		path = "+-- %s" % name
		paths.insert(0, path)
		resolve_paths(paths, level + 1, names[name])

class ModelProcessingError(Exception):
	"""
	Raised when there is an error during model processing.
	"""
	pass

class ReferenceDelegate(object):
	"""
	A "virtual" property delegate to be used with "reference" 
	OMEModelProperty instances. This delegate conforms loosely to the same
	interface as a delegate coming from generateDS (ie. an "attribute" or
	an "element").
	"""
	def __init__(self, dataType):
		self.name = dataType + "_BackReference"
		self.dataType = dataType
		# Ensures property code which is looking for elements or attributes
		# which conform to an enumeration can still function.
		self.values = None
	
	def getValues(self):
		return self.values
	
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
	An aggregate type representing either an OME XML Schema element, 
	attribute or our OME XML Schema "Reference" meta element (handled by the
	ReferenceDelegate class). This class equates conceptually to an object
	oriented language instance variable which may be of a singular type or a 
	collection.
	"""

	REF_REGEX = re.compile(r'Ref$|RefNode$')

	LOWER_CASE_REGEX = re.compile(r'[a-z]')

	def __init__(self, delegate, parent, model):
		self.model = model
		self.delegate = delegate
		self.parent = parent
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
		if hasattr(self.delegate, 'choice') \
		   and self.delegate.choice is not None:
			return self.delegate.choice.getMaxOccurs()
		return self.delegate.getMaxOccurs()
	maxOccurs = property(_get_maxOccurs,
		doc="""The maximum number of occurances for this property.""")
		
	def _get_minOccurs(self):
		if self.isAttribute:
			if self.delegate.getUse() == "optional":
				return 0
			return 1
		if hasattr(self.delegate, 'choice') \
		   and self.delegate.choice is not None:
			return self.delegate.choice.getMinOccurs()
		return self.delegate.getMinOccurs()
	minOccurs = property(_get_minOccurs,
		doc="""The minimum number of occurances for this property.""")

	def _get_name(self):
		return self.delegate.getName()
	name = property(_get_name, doc="""The property's name.""")
	
	def _get_javaType(self):
		try:
			# Hand back the type of enumerations
			if self.isEnumeration:
				javaType = self.name
				if len(self.delegate.values) == 0:
					# As we have no directly defined possible values we have
					# no reason to qualify our type explicitly.
					return self.type
				if javaType == "Type":
					# One of the OME XML unspecific "Type" properties which
					# can only be qualified by the parent.
					if self.type.endswith("string"):
						# We've been defined entirely inline, prefix our Java
						# type name with the parent type's name.
						return "%s%s" % (self.parent.name, javaType)
					# There's another type which describes us, use its name
					# as our Java type name.
					return self.type
				return javaType
			# Handle XML Schema types that directly map to Java types
			return JAVA_TYPE_MAP[self.type]
		except KeyError:
			# Hand back the type of references or complex types with the
			# useless OME XML 'Ref' suffix removed.
			if self.isReference or \
			   (not self.isAttribute and self.delegate.isComplex()):
				return self.REF_REGEX.sub('', self.type)
			# Hand back the type of complex types
			if not self.isAttribute and self.delegate.isComplex():
				return self.type
			if not self.isEnumeration:
				# We have a property whose type was defined by a top level 
				# simpleType. 
				getSimpleType = self.model.getTopLevelSimpleType
				simpleTypeName = self.type
				while True:
					simpleType = getSimpleType(simpleTypeName)
					if simpleType is None:
						logging.debug("No simpleType found with name: %s" % \
								simpleTypeName)
						# Handle cases where the simple type is prefixes by
						# a namespace definition. (ex. OME:LSID).
						namespaceless = simpleTypeName.split(':')[-1]
						if namespaceless != simpleTypeName:
							simpleTypeName = namespaceless
							continue
						break
					logging.debug("%s simpleType dump: %s" % \
							(self, simpleType.__dict__))
					# It's possible the simpleType is a union of other
					# simpleTypes so we need to handle that. We assume
					# that all the unioned simpleTypes are of the same
					# base type (ex. "xsd:string" or "xsd:float").
					if simpleType.unionOf:
						union = getSimpleType(simpleType.unionOf[0])
						try:
							return JAVA_TYPE_MAP[union.getBase()]
						except KeyError:
							simpleTypeName = union.getBase()
					"""
					base = simpleType.base

					match = self.BASE_NAMESPACE_REGEX.match(base)
					if match:
						base = match.group(1)
					simpleTypeBase = getSimpleType(base)
					if simpleTypeBase is not None:
						base = simpleTypeBase.getBase()
						return JAVA_TYPE_MAP[base]
					"""
					try:
						return JAVA_TYPE_MAP[simpleType.getBase()]
					except KeyError:
						simpleTypeName = simpleType.getBase()
			logging.debug("%s dump: %s" % (self, self.__dict__))
			logging.debug("%s delegate dump: %s" % (self, self.delegate.__dict__))
			raise ModelProcessingError, \
				"Unable to find %s Java type for %s" % (self.name, self.type)
	javaType = property(_get_javaType, doc="""The property's Java type.""")

	def _get_metadataStoreType(self):
		# FIXME: No more node
		#javaType = self.javaType
		#if javaType[-4:] == "Node":
		#	return "String"
		return javaType
	metadataStoreType = property(_get_metadataStoreType,
			                     doc="""The property's MetadataStore type.""")
	
	def _get_javaArgumentName(self):
		argumentName = self.REF_REGEX.sub('', self.name)
		m = self.LOWER_CASE_REGEX.search(argumentName)
		if not m:
			return argumentName.lower()
		i = m.start()
		return argumentName[:i].lower() + argumentName[i:]
	javaArgumentName = property(_get_javaArgumentName,
		doc="""The property's Java argument name (camelCase).""")
	
	def _get_javaMethodName(self):
		return self.REF_REGEX.sub('', self.name)
	javaMethodName = property(_get_javaMethodName,
		doc="""The property's Java method name.""")

	def _get_javaInstanceVariableName(self):
		if self.maxOccurs > 1:
			return self.javaArgumentName + 'List';
		return self.javaArgumentName
	javaInstanceVariableName = property(_get_javaInstanceVariableName,
		doc="""The property's Java instance variable name.""")

	def _get_isEnumeration(self):
		v = self.delegate.getValues()
		if v is not None and len(v) > 0:
			return True
		return False
	isEnumeration = property(_get_isEnumeration,
		doc="""Whether or not the property is an enumeration.""")
		
	def _get_possibleValues(self):
		return self.delegate.getValues()
	possibleValues = property(_get_possibleValues,
		doc="""If the property is an enumeration, it's possible values.""")
	
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
		
	def fromAttribute(klass, attribute, parent, model):
		"""
		Instantiates a property from an XML Schema attribute.
		"""
		instance = klass(attribute, parent, model)
		instance.isAttribute = True
		return instance
	fromAttribute = classmethod(fromAttribute)
	
	def fromElement(klass, element, parent, model):
		"""
		Instantiates a property from an XML Schema element.
		"""
		return klass(element, parent, model)
	fromElement = classmethod(fromElement)

	def fromReference(klass, reference, parent, model):
		"""
		Instantiates a property from a "virtual" OME XML schema reference.
		"""
		instance = klass(reference, parent, model)
		instance.isReference = True
		return instance
	fromReference = classmethod(fromReference)

class OMEModelObject(object):
	"""
	A single element of an OME data model.
	"""
	
	def __init__(self, element, parent, model):
		self.model = model
		self.element = element
		self.parent = parent
		self.base = element.getBase()
		self.name = element.getName()
		self.type = element.getType()
		self.properties = odict()
		if hasattr(element, 'appinfo') and element.appinfo == 'abstract':
			self.isAbstract = True
		else:
			self.isAbstract = False
	
	def addAttribute(self, attribute):
		"""
		Adds an OME XML Schema attribute to the object's data model.
		"""
		name = attribute.getName()
		self.properties[name] = \
			OMEModelProperty.fromAttribute(attribute, self, self.model)
		
	def addElement(self, element):
		"""
		Adds an OME XML Schema element to the object's data model.
		"""
		name = element.getName()
		self.properties[name] = \
			OMEModelProperty.fromElement(element, self, self.model)

	def _get_javaBase(self):
		base = self.element.getBase()
		if base in JAVA_BASE_TYPE_MAP:
			return JAVA_BASE_TYPE_MAP[base]
		if base is None and self.element.attrs['type'] != self.name:
			base = self.element.attrs['type']
		if base is None:
			return DEFAULT_BASE_CLASS
		return base
	javaBase = property(_get_javaBase, 
		doc="""The model object's Java base class.""")

	def _get_baseObjectProperties(self):
		base = self.model.getObjectByName(self.base)
		if base:
			return base.properties.values()
		return list()
	baseObjectProperties = property(_get_baseObjectProperties,
		doc="""The model object's base object properties.""")
		
	def _get_refNodeName(self):
		if self.base == "Reference":
			return self.properties["ID"].javaType
		return None
	refNodeName = property(_get_refNodeName,
		doc="""The name of this node's reference node; None otherwise.""")
	
	def __str__(self):
		return self.__repr__()
	
	def __repr__(self):
		return '<"%s" OMEModelObject instance at 0x%x>' % (self.name, id(self))
	
class OMEModel(object):
	def __init__(self):
		self.objects = odict()
		self.parents = dict()
		
	def addObject(self, element, obj):
		if self.objects.has_key(element):
			raise ModelProcessingError(
				"Element %s has been processed!" % element)
		self.objects[element] = obj
		
	def getObject(self, element):
		try:
			return self.objects[element]
		except KeyError:
			return None
		
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
			logging.debug("Processing attribute: %s %d/%d" % (key, i + 1, length))
			attribute = attributes[key]
			logging.debug("Dump: %s" % attribute.__dict__)
			name = attribute.getName()
			obj.addAttribute(attribute)

		children = element.getChildren()
		length = len(children)
		for i, child in enumerate(children):
			logging.debug("Processing child: %s %d/%d" % (child, i + 1, length))
			logging.debug("Dump: %s" % child.__dict__)
			name = child.getCleanName()
			obj.addElement(child)

	def processLeaf(self, element, parent):
		"""
		Process an element (a leaf).
		"""
		logging.debug("Processing leaf: (%s) --> (%s)" % (parent, element))
		e = element
		e_name = e.getName()
		e_type = e.getType()
		if parent is not None:
			if e_name not in self.parents:
				self.parents[e_name] = list()
			self.parents[e_name].append(parent.getName())
		if not e.isExplicitDefine() \
		   and (e_name not in EXPLICIT_DEFINE_OVERRIDE or not e.topLevel):
			logging.info("Element %s.%s not an explicit define, skipping." % (parent, e))
			return
		if e.getMixedExtensionError():
			logging.error("Element %s.%s extension chain contains mixed and non-mixed content, skipping." % (parent, e))
			return
		if e_type != e_name and e_name not in EXPLICIT_DEFINE_OVERRIDE:
		    logging.info("Element %s.%s is not a concrete type (%s != %s), skipping." % (parent, e, e_type, e_name))
		    return
		obj = OMEModelObject(e, parent, self)
		self.addObject(e, obj)
		self.processAttributes(e)

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
					if shortName not in references:
						references[shortName] = list()
					references[shortName].append(o.name)
		logging.debug("Model references: %s" % references)

		for o in self.objects.values():
			if o.name in references:
				for ref in references[o.name]:
					delegate = ReferenceDelegate(ref)
					prop = OMEModelProperty.fromReference(delegate, o, self)
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

def parseXmlSchema(filenames, namespace=DEFAULT_NAMESPACE):
	"""
	Entry point for XML Schema parsing into an OME Model.
	"""
	# The following two statements are required to "prime" the generateDS
	# code and ensure we have reasonable namespace support.
	logging.debug("Namespace: %s" % namespace)
	set_type_constants(namespace)
	updateTypeMaps(namespace)
	logging.debug("Java type map: %s" % JAVA_TYPE_MAP)

	parser = sax.make_parser()
	ch = XschemaHandler()
	parser.setContentHandler(ch)
	for filename in filenames:
		parser.parse(filename)

	root = ch.getRoot()
	if root is None:
		raise ModelProcessingError(
			"No model objects found, have you set the correct namespace?")
	root.annotate()
	return OMEModel.process(ch)

def resetGenerateDS():
	"""
	Since the generateDS module contains many globals and package scoped
	variables we need the ability to reset its state if we are to re-use
	it multiple times in the same process.
	"""
	generateDS.generateDS.GenerateProperties = 0
	generateDS.generateDS.UseOldGetterSetter = 0
	generateDS.generateDS.MemberSpecs = None
	generateDS.generateDS.DelayedElements = []
	generateDS.generateDS.DelayedElements_subclass = []
	generateDS.generateDS.AlreadyGenerated = []
	generateDS.generateDS.AlreadyGenerated_subclass = []
	generateDS.generateDS.PostponedExtensions = []
	generateDS.generateDS.ElementsForSubclasses = []
	generateDS.generateDS.ElementDict = {}
	generateDS.generateDS.Force = 0
	generateDS.generateDS.Dirpath = []
	generateDS.generateDS.ExternalEncoding = sys.getdefaultencoding()

	generateDS.generateDS.NamespacesDict = {}
	generateDS.generateDS.Targetnamespace = ""

	generateDS.generateDS.NameTable = {
		'type': 'type_',
		'float': 'float_',
	}
	for kw in keyword.kwlist:
		generateDS.generateDS.NameTable[kw] = '%sxx' % kw

	generateDS.generateDS.SubclassSuffix = 'Sub'
	generateDS.generateDS.RootElement = None
	generateDS.generateDS.AttributeGroups = {}
	generateDS.generateDS.SubstitutionGroups = {}
	#
	# SubstitutionGroups can also include simple types that are
	#   not (defined) elements.  Keep a list of these simple types.
	#   These are simple types defined at top level.
	generateDS.generateDS.SimpleElementDict = {}
	generateDS.generateDS.SimpleTypeDict = {}
	generateDS.generateDS.ValidatorBodiesBasePath = None
	generateDS.generateDS.UserMethodsPath = None
	generateDS.generateDS.UserMethodsModule = None
	generateDS.generateDS.XsdNameSpace = ''
