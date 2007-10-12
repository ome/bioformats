#!/usr/bin/env python

import sys
import getopt
from xml.dom import minidom
from xml.dom import Node
#import yaml

#
# If you have installed IPython you can uncomment and use the following.
# IPython is available from http://www-hep.colorado.edu/~fperez/ipython.
#
#from IPython.Shell import IPythonShellEmbed
#IPShell = IPythonShellEmbed('-nosep',
#    banner = 'Entering interpreter.  Ctrl-D to exit.',
#    exit_msg = 'Leaving Interpreter.')

# Use the following line where and when you want to drop into the
# IPython shell:
#    IPShell(vars(), '<a msg>')

#
# Support/utility functions.
#

def showIndent(outfile, level):
    for idx in range(level):
        outfile.write('    ')


#
# Data representation classes.
#

class top:
    subclass = None
    def __init__(self, attributelist=None, include=None, id='', addr=''):
        self.attributelist = attributelist
        if include is None:
            self.include = []
        else:
            self.include = include
        self.id = id
        self.addr = addr
    def factory(*args):
        if top.subclass:
            return apply(top.subclass, args)
        else:
            return apply(top, args)
    factory = staticmethod(factory)
    def getAttributelist(self): return self.attributelist
    def setAttributelist(self, attributelist): self.attributelist = attributelist
    def getInclude(self): return self.include
    def addInclude(self, include): self.include.append(include)
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<top id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        if self.attributelist:
            self.attributelist.export(outfile, level)
        for include in self.include:
            include.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</top>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'attributelist':
                obj = attributelist.factory()
                obj.build(child)
                self.setAttributelist(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'include':
                obj = include.factory()
                obj.build(child)
                self.include.append(obj)
# end class top


class attributelist:
    subclass = None
    def __init__(self, attribute=None, kwargs=None, parmlist=None, id='', addr=''):
        if attribute is None:
            self.attribute = []
        else:
            self.attribute = attribute
        if kwargs is None:
            self.kwargs = []
        else:
            self.kwargs = kwargs
        self.parmlist = parmlist
        self.id = id
        self.addr = addr
    def factory(*args):
        if attributelist.subclass:
            return apply(attributelist.subclass, args)
        else:
            return apply(attributelist, args)
    factory = staticmethod(factory)
    def getAttribute(self): return self.attribute
    def addAttribute(self, attribute): self.attribute.append(attribute)
    def getKwargs(self): return self.kwargs
    def addKwargs(self, kwargs): self.kwargs.append(kwargs)
    def getParmlist(self): return self.parmlist
    def setParmlist(self, parmlist): self.parmlist = parmlist
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<attributelist id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        for attribute in self.attribute:
            attribute.export(outfile, level)
        for kwargs in self.kwargs:
            kwargs.export(outfile, level)
        if self.parmlist:
            self.parmlist.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</attributelist>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'attribute':
                obj = attribute.factory()
                obj.build(child)
                self.attribute.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'kwargs':
                obj = kwargs.factory()
                obj.build(child)
                self.kwargs.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'parmlist':
                obj = parmlist.factory()
                obj.build(child)
                self.setParmlist(obj)
# end class attributelist


class attribute:
    subclass = None
    def __init__(self, name='', value='', id='', addr=''):
        self.name = name
        self.value = value
        self.id = id
        self.addr = addr
    def factory(*args):
        if attribute.subclass:
            return apply(attribute.subclass, args)
        else:
            return apply(attribute, args)
    factory = staticmethod(factory)
    def getName(self): return self.name
    def setName(self, name): self.name = name
    def getValue(self): return self.value
    def setValue(self, value): self.value = value
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<attribute name="%s" value="%s" id="%s" addr="%s">\n' % (self.name, self.value, self.id, self.addr, ))
        level += 1
        level -= 1
        showIndent(outfile, level)
        outfile.write('</attribute>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('name'):
            self.name = attrs.get('name').value
        if attrs.get('value'):
            self.value = attrs.get('value').value
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            pass
# end class attribute


class include:
    subclass = None
    def __init__(self, attributelist=None, include=None, typemap=None, insert=None, module=None, cdecl=None, klass=None, emport=None, enum=None, id='', addr=''):
        self.attributelist = attributelist
        if include is None:
            self.include = []
        else:
            self.include = include
        if typemap is None:
            self.typemap = []
        else:
            self.typemap = typemap
        if insert is None:
            self.insert = []
        else:
            self.insert = insert
        if module is None:
            self.module = []
        else:
            self.module = module
        if cdecl is None:
            self.cdecl = []
        else:
            self.cdecl = cdecl
        if klass is None:
            self.klass = []
        else:
            self.klass = klass
        if emport is None:
            self.emport = []
        else:
            self.emport = emport
        if enum is None:
            self.enum = []
        else:
            self.enum = enum
        self.id = id
        self.addr = addr
    def factory(*args):
        if include.subclass:
            return apply(include.subclass, args)
        else:
            return apply(include, args)
    factory = staticmethod(factory)
    def getAttributelist(self): return self.attributelist
    def setAttributelist(self, attributelist): self.attributelist = attributelist
    def getInclude(self): return self.include
    def addInclude(self, include): self.include.append(include)
    def getTypemap(self): return self.typemap
    def addTypemap(self, typemap): self.typemap.append(typemap)
    def getInsert(self): return self.insert
    def addInsert(self, insert): self.insert.append(insert)
    def getModule(self): return self.module
    def addModule(self, module): self.module.append(module)
    def getCdecl(self): return self.cdecl
    def addCdecl(self, cdecl): self.cdecl.append(cdecl)
    def getClass(self): return self.klass
    def addClass(self, klass): self.klass.append(klass)
    def getImport(self): return self.emport
    def addImport(self, emport): self.emport.append(emport)
    def getEnum(self): return self.enum
    def addEnum(self, enum): self.enum.append(enum)
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<include id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        if self.attributelist:
            self.attributelist.export(outfile, level)
        for include in self.include:
            include.export(outfile, level)
        for typemap in self.typemap:
            typemap.export(outfile, level)
        for insert in self.insert:
            insert.export(outfile, level)
        for module in self.module:
            module.export(outfile, level)
        for cdecl in self.cdecl:
            cdecl.export(outfile, level)
        for klass in self.klass:
            klass.export(outfile, level)
        for emport in self.emport:
            emport.export(outfile, level)
        for enum in self.enum:
            enum.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</include>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'attributelist':
                obj = attributelist.factory()
                obj.build(child)
                self.setAttributelist(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'include':
                obj = include.factory()
                obj.build(child)
                self.include.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'typemap':
                obj = typemap.factory()
                obj.build(child)
                self.typemap.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'insert':
                obj = insert.factory()
                obj.build(child)
                self.insert.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'module':
                obj = module.factory()
                obj.build(child)
                self.module.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'cdecl':
                obj = cdecl.factory()
                obj.build(child)
                self.cdecl.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'class':
                obj = klass.factory()
                obj.build(child)
                self.klass.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'import':
                obj = emport.factory()
                obj.build(child)
                self.emport.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'enum':
                obj = enum.factory()
                obj.build(child)
                self.enum.append(obj)
# end class include


class emport:
    subclass = None
    def __init__(self, attributelist=None, include=None, typemap=None, insert=None, module=None, cdecl=None, klass=None, emport=None, enum=None, id='', addr=''):
        self.attributelist = attributelist
        if include is None:
            self.include = []
        else:
            self.include = include
        if typemap is None:
            self.typemap = []
        else:
            self.typemap = typemap
        if insert is None:
            self.insert = []
        else:
            self.insert = insert
        if module is None:
            self.module = []
        else:
            self.module = module
        if cdecl is None:
            self.cdecl = []
        else:
            self.cdecl = cdecl
        if klass is None:
            self.klass = []
        else:
            self.klass = klass
        if emport is None:
            self.emport = []
        else:
            self.emport = emport
        if enum is None:
            self.enum = []
        else:
            self.enum = enum
        self.id = id
        self.addr = addr
    def factory(*args):
        if emport.subclass:
            return apply(emport.subclass, args)
        else:
            return apply(emport, args)
    factory = staticmethod(factory)
    def getAttributelist(self): return self.attributelist
    def setAttributelist(self, attributelist): self.attributelist = attributelist
    def getInclude(self): return self.include
    def addInclude(self, include): self.include.append(include)
    def getTypemap(self): return self.typemap
    def addTypemap(self, typemap): self.typemap.append(typemap)
    def getInsert(self): return self.insert
    def addInsert(self, insert): self.insert.append(insert)
    def getModule(self): return self.module
    def addModule(self, module): self.module.append(module)
    def getCdecl(self): return self.cdecl
    def addCdecl(self, cdecl): self.cdecl.append(cdecl)
    def getClass(self): return self.klass
    def addClass(self, klass): self.klass.append(klass)
    def getImport(self): return self.emport
    def addImport(self, emport): self.emport.append(emport)
    def getEnum(self): return self.enum
    def addEnum(self, enum): self.enum.append(enum)
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<import id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        if self.attributelist:
            self.attributelist.export(outfile, level)
        for include in self.include:
            include.export(outfile, level)
        for typemap in self.typemap:
            typemap.export(outfile, level)
        for insert in self.insert:
            insert.export(outfile, level)
        for module in self.module:
            module.export(outfile, level)
        for cdecl in self.cdecl:
            cdecl.export(outfile, level)
        for klass in self.klass:
            klass.export(outfile, level)
        for emport in self.emport:
            emport.export(outfile, level)
        for enum in self.enum:
            enum.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</import>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'attributelist':
                obj = attributelist.factory()
                obj.build(child)
                self.setAttributelist(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'include':
                obj = include.factory()
                obj.build(child)
                self.include.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'typemap':
                obj = typemap.factory()
                obj.build(child)
                self.typemap.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'insert':
                obj = insert.factory()
                obj.build(child)
                self.insert.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'module':
                obj = module.factory()
                obj.build(child)
                self.module.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'cdecl':
                obj = cdecl.factory()
                obj.build(child)
                self.cdecl.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'class':
                obj = klass.factory()
                obj.build(child)
                self.klass.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'import':
                obj = emport.factory()
                obj.build(child)
                self.emport.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'enum':
                obj = enum.factory()
                obj.build(child)
                self.enum.append(obj)
# end class emport


class enum:
    subclass = None
    def __init__(self, attributelist=None, enumitem=None, id='', addr=''):
        self.attributelist = attributelist
        if enumitem is None:
            self.enumitem = []
        else:
            self.enumitem = enumitem
        self.id = id
        self.addr = addr
    def factory(*args):
        if enum.subclass:
            return apply(enum.subclass, args)
        else:
            return apply(enum, args)
    factory = staticmethod(factory)
    def getAttributelist(self): return self.attributelist
    def setAttributelist(self, attributelist): self.attributelist = attributelist
    def getEnumitem(self): return self.enumitem
    def addEnumitem(self, enumitem): self.enumitem.append(enumitem)
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<enum id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        if self.attributelist:
            self.attributelist.export(outfile, level)
        for enumitem in self.enumitem:
            enumitem.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</enum>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'attributelist':
                obj = attributelist.factory()
                obj.build(child)
                self.setAttributelist(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'enumitem':
                obj = enumitem.factory()
                obj.build(child)
                self.enumitem.append(obj)
# end class enum


class enumitem:
    subclass = None
    def __init__(self, attributelist=None, id='', addr=''):
        self.attributelist = attributelist
        self.id = id
        self.addr = addr
    def factory(*args):
        if enumitem.subclass:
            return apply(enumitem.subclass, args)
        else:
            return apply(enumitem, args)
    factory = staticmethod(factory)
    def getAttributelist(self): return self.attributelist
    def setAttributelist(self, attributelist): self.attributelist = attributelist
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<enumitem id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        if self.attributelist:
            self.attributelist.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</enumitem>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'attributelist':
                obj = attributelist.factory()
                obj.build(child)
                self.setAttributelist(obj)
# end class enumitem


class kwargs:
    subclass = None
    def __init__(self, attributelist=None, id='', addr=''):
        self.attributelist = attributelist
        self.id = id
        self.addr = addr
    def factory(*args):
        if kwargs.subclass:
            return apply(kwargs.subclass, args)
        else:
            return apply(kwargs, args)
    factory = staticmethod(factory)
    def getAttributelist(self): return self.attributelist
    def setAttributelist(self, attributelist): self.attributelist = attributelist
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<kwargs id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        if self.attributelist:
            self.attributelist.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</kwargs>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'attributelist':
                obj = attributelist.factory()
                obj.build(child)
                self.setAttributelist(obj)
# end class kwargs


class typemap:
    subclass = None
    def __init__(self, attributelist=None, typemapitem=None, id='', addr=''):
        self.attributelist = attributelist
        if typemapitem is None:
            self.typemapitem = []
        else:
            self.typemapitem = typemapitem
        self.id = id
        self.addr = addr
    def factory(*args):
        if typemap.subclass:
            return apply(typemap.subclass, args)
        else:
            return apply(typemap, args)
    factory = staticmethod(factory)
    def getAttributelist(self): return self.attributelist
    def setAttributelist(self, attributelist): self.attributelist = attributelist
    def getTypemapitem(self): return self.typemapitem
    def addTypemapitem(self, typemapitem): self.typemapitem.append(typemapitem)
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<typemap id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        if self.attributelist:
            self.attributelist.export(outfile, level)
        for typemapitem in self.typemapitem:
            typemapitem.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</typemap>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'attributelist':
                obj = attributelist.factory()
                obj.build(child)
                self.setAttributelist(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'typemapitem':
                obj = typemapitem.factory()
                obj.build(child)
                self.typemapitem.append(obj)
# end class typemap


class typemapitem:
    subclass = None
    def __init__(self, attributelist=None, id='', addr=''):
        self.attributelist = attributelist
        self.id = id
        self.addr = addr
    def factory(*args):
        if typemapitem.subclass:
            return apply(typemapitem.subclass, args)
        else:
            return apply(typemapitem, args)
    factory = staticmethod(factory)
    def getAttributelist(self): return self.attributelist
    def setAttributelist(self, attributelist): self.attributelist = attributelist
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<typemapitem id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        if self.attributelist:
            self.attributelist.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</typemapitem>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'attributelist':
                obj = attributelist.factory()
                obj.build(child)
                self.setAttributelist(obj)
# end class typemapitem


class klass:
    subclass = None
    def __init__(self, attributelist=None, cdecl=None, access=None, constructor=None, destructor=None, id='', addr=''):
        self.attributelist = attributelist
        if cdecl is None:
            self.cdecl = []
        else:
            self.cdecl = cdecl
        self.access = access
        if constructor is None:
            self.constructor = []
        else:
            self.constructor = constructor
        self.destructor = destructor
        self.id = id
        self.addr = addr
    def factory(*args):
        if klass.subclass:
            return apply(klass.subclass, args)
        else:
            return apply(klass, args)
    factory = staticmethod(factory)
    def getAttributelist(self): return self.attributelist
    def setAttributelist(self, attributelist): self.attributelist = attributelist
    def getCdecl(self): return self.cdecl
    def addCdecl(self, cdecl): self.cdecl.append(cdecl)
    def getAccess(self): return self.access
    def setAccess(self, access): self.access = access
    def getConstructor(self): return self.constructor
    def addConstructor(self, constructor): self.constructor.append(constructor)
    def getDestructor(self): return self.destructor
    def setDestructor(self, destructor): self.destructor = destructor
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<class id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        if self.attributelist:
            self.attributelist.export(outfile, level)
        for cdecl in self.cdecl:
            cdecl.export(outfile, level)
        if self.access:
            self.access.export(outfile, level)
        for constructor in self.constructor:
            constructor.export(outfile, level)
        if self.destructor:
            self.destructor.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</class>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'attributelist':
                obj = attributelist.factory()
                obj.build(child)
                self.setAttributelist(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'cdecl':
                obj = cdecl.factory()
                obj.build(child)
                self.cdecl.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'access':
                obj = access.factory()
                obj.build(child)
                self.setAccess(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'constructor':
                obj = constructor.factory()
                obj.build(child)
                self.constructor.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'destructor':
                obj = destructor.factory()
                obj.build(child)
                self.setDestructor(obj)
# end class klass


class cdecl:
    subclass = None
    def __init__(self, attributelist=None, id='', addr=''):
        self.attributelist = attributelist
        self.id = id
        self.addr = addr
    def factory(*args):
        if cdecl.subclass:
            return apply(cdecl.subclass, args)
        else:
            return apply(cdecl, args)
    factory = staticmethod(factory)
    def getAttributelist(self): return self.attributelist
    def setAttributelist(self, attributelist): self.attributelist = attributelist
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<cdecl id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        if self.attributelist:
            self.attributelist.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</cdecl>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'attributelist':
                obj = attributelist.factory()
                obj.build(child)
                self.setAttributelist(obj)
# end class cdecl


class access:
    subclass = None
    def __init__(self, attributelist=None, id='', addr=''):
        self.attributelist = attributelist
        self.id = id
        self.addr = addr
    def factory(*args):
        if access.subclass:
            return apply(access.subclass, args)
        else:
            return apply(access, args)
    factory = staticmethod(factory)
    def getAttributelist(self): return self.attributelist
    def setAttributelist(self, attributelist): self.attributelist = attributelist
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<access id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        if self.attributelist:
            self.attributelist.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</access>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'attributelist':
                obj = attributelist.factory()
                obj.build(child)
                self.setAttributelist(obj)
# end class access


class parmlist:
    subclass = None
    def __init__(self, parm=None, id='', addr=''):
        if parm is None:
            self.parm = []
        else:
            self.parm = parm
        self.id = id
        self.addr = addr
    def factory(*args):
        if parmlist.subclass:
            return apply(parmlist.subclass, args)
        else:
            return apply(parmlist, args)
    factory = staticmethod(factory)
    def getParm(self): return self.parm
    def addParm(self, parm): self.parm.append(parm)
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<parmlist id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        for parm in self.parm:
            parm.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</parmlist>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'parm':
                obj = parm.factory()
                obj.build(child)
                self.parm.append(obj)
# end class parmlist


class parm:
    subclass = None
    def __init__(self, attributelist=None, id='', addr=''):
        self.attributelist = attributelist
        self.id = id
        self.addr = addr
    def factory(*args):
        if parm.subclass:
            return apply(parm.subclass, args)
        else:
            return apply(parm, args)
    factory = staticmethod(factory)
    def getAttributelist(self): return self.attributelist
    def setAttributelist(self, attributelist): self.attributelist = attributelist
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<parm id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        if self.attributelist:
            self.attributelist.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</parm>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'attributelist':
                obj = attributelist.factory()
                obj.build(child)
                self.setAttributelist(obj)
# end class parm


class constructor:
    subclass = None
    def __init__(self, attributelist=None, id='', addr=''):
        self.attributelist = attributelist
        self.id = id
        self.addr = addr
    def factory(*args):
        if constructor.subclass:
            return apply(constructor.subclass, args)
        else:
            return apply(constructor, args)
    factory = staticmethod(factory)
    def getAttributelist(self): return self.attributelist
    def setAttributelist(self, attributelist): self.attributelist = attributelist
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<constructor id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        if self.attributelist:
            self.attributelist.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</constructor>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'attributelist':
                obj = attributelist.factory()
                obj.build(child)
                self.setAttributelist(obj)
# end class constructor


class destructor:
    subclass = None
    def __init__(self, attributelist=None, id='', addr=''):
        self.attributelist = attributelist
        self.id = id
        self.addr = addr
    def factory(*args):
        if destructor.subclass:
            return apply(destructor.subclass, args)
        else:
            return apply(destructor, args)
    factory = staticmethod(factory)
    def getAttributelist(self): return self.attributelist
    def setAttributelist(self, attributelist): self.attributelist = attributelist
    def getId(self): return self.id
    def setId(self, id): self.id = id
    def getAddr(self): return self.addr
    def setAddr(self, addr): self.addr = addr
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<destructor id="%s" addr="%s">\n' % (self.id, self.addr, ))
        level += 1
        if self.attributelist:
            self.attributelist.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</destructor>\n')
    def build(self, node_):
        attrs = node_.attributes
        if attrs.get('id'):
            self.id = attrs.get('id').value
        if attrs.get('addr'):
            self.addr = attrs.get('addr').value
        for child in node_.childNodes:
            if child.nodeType == Node.ELEMENT_NODE and \
                child.nodeName == 'attributelist':
                obj = attributelist.factory()
                obj.build(child)
                self.setAttributelist(obj)
# end class destructor


class module:
    subclass = None
    def __init__(self):
        pass
    def factory(*args):
        if module.subclass:
            return apply(module.subclass, args)
        else:
            return apply(module, args)
    factory = staticmethod(factory)
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<module>\n')
        level += 1
        level -= 1
        showIndent(outfile, level)
        outfile.write('</module>\n')
    def build(self, node_):
        attrs = node_.attributes
        for child in node_.childNodes:
            pass
# end class module


class insert:
    subclass = None
    def __init__(self):
        pass
    def factory(*args):
        if insert.subclass:
            return apply(insert.subclass, args)
        else:
            return apply(insert, args)
    factory = staticmethod(factory)
    def export(self, outfile, level):
        showIndent(outfile, level)
        outfile.write('<insert>\n')
        level += 1
        level -= 1
        showIndent(outfile, level)
        outfile.write('</insert>\n')
    def build(self, node_):
        attrs = node_.attributes
        for child in node_.childNodes:
            pass
# end class insert


USAGE_TEXT = """
Usage: python <Parser>.py <in_xml_file>
"""

def usage():
    print USAGE_TEXT
    sys.exit(-1)


def parse(inFileName):
    doc = minidom.parse(inFileName)
    rootNode = doc.childNodes[0]
    rootObj = top.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('?xml version="1.0" ?>')
    rootObj.export(sys.stdout, 0)
    #yamlObj = rootObj.exportYaml()
    #yaml.dumpToFile(sys.stdout, yamlObj)
    return rootObj


def main():
    args = sys.argv[1:]
    if len(args) != 1:
        usage()
    parse(args[0])


if __name__ == '__main__':
    main()
    #import pdb
    #pdb.run('main()')

