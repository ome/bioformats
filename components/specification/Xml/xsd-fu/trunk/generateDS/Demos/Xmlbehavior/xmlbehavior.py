#!/usr/bin/env python

#
# Generated Wed Jun 30 10:34:05 2004 by generateDS.py.
#

import sys
import getopt
from xml.dom import minidom
from xml.dom import Node

#
# If you have installed IPython you can uncomment and use the following.
# IPython is available from http://ipython.scipy.org/.
#

## from IPython.Shell import IPShellEmbed
## args = ''
## ipshell = IPShellEmbed(args,
##     banner = 'Dropping into IPython',
##     exit_msg = 'Leaving Interpreter, back to program.')

# Then use the following line where and when you want to drop into the
# IPython shell:
#    ipshell('<some message> -- Entering ipshell.\nHit Ctrl-D to exit')

#
# Support/utility functions.
#

def showIndent(outfile, level):
    for idx in range(level):
        outfile.write('    ')

def quote_xml(inStr):
    s1 = inStr
    s1 = s1.replace('&', '&amp;')
    s1 = s1.replace('<', '&lt;')
    s1 = s1.replace('"', '&quot;')
    return s1

def quote_python(inStr):
    s1 = inStr
    if s1.find("'") == -1:
        if s1.find('\n') == -1:
            return "'%s'" % s1
        else:
            return "'''%s'''" % s1
    else:
        if s1.find('"') != -1:
            s1 = s1.replace('"', '\\"')
        if s1.find('\n') == -1:
            return '"%s"' % s1
        else:
            return '"""%s"""' % s1


#
# Data representation classes.
#

class xml_behavior:
    subclass = None
    def __init__(self, base_impl_url='', behaviors=None):
        self.base_impl_url = base_impl_url
        self.behaviors = behaviors
    def factory(*args_, **kwargs_):
        if xml_behavior.subclass:
            return xml_behavior.subclass(*args_, **kwargs_)
        else:
            return xml_behavior(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getBase_impl_url(self): return self.base_impl_url
    def setBase_impl_url(self, base_impl_url): self.base_impl_url = base_impl_url
    def getBehaviors(self): return self.behaviors
    def setBehaviors(self, behaviors): self.behaviors = behaviors
    def export(self, outfile, level, name_='xml-behavior'):
        showIndent(outfile, level)
        outfile.write('<%s>\n' % name_)
        level += 1
        showIndent(outfile, level)
        outfile.write('<base-impl-url>%s</base-impl-url>\n' % quote_xml(self.getBase_impl_url()))
        if self.behaviors:
            self.behaviors.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportLiteral(self, outfile, level, name_='xml-behavior'):
        level += 1
        showIndent(outfile, level)
        outfile.write('base_impl_url=%s,\n' % quote_python(self.getBase_impl_url()))
        if self.behaviors:
            showIndent(outfile, level)
            outfile.write('behaviors=behaviors(\n')
            self.behaviors.exportLiteral(outfile, level)
            showIndent(outfile, level)
            outfile.write('),\n')
        level -= 1
    def build(self, node_):
        attrs = node_.attributes
        for child in node_.childNodes:
            nodeName_ = child.nodeName.split(':')[-1]
            if child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'base-impl-url':
                base_impl_url = ''
                for text_ in child.childNodes:
                    base_impl_url += text_.nodeValue
                self.base_impl_url = base_impl_url
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'behaviors':
                obj = behaviors.factory()
                obj.build(child)
                self.setBehaviors(obj)
# end class xml_behavior


class behaviors:
    subclass = None
    def __init__(self, behavior=None):
        if behavior is None:
            self.behavior = []
        else:
            self.behavior = behavior
    def factory(*args_, **kwargs_):
        if behaviors.subclass:
            return behaviors.subclass(*args_, **kwargs_)
        else:
            return behaviors(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getBehavior(self): return self.behavior
    def addBehavior(self, value): self.behavior.append(value)
    def setBehavior(self, index, value): self.behavior[index] = value
    def export(self, outfile, level, name_='behaviors'):
        showIndent(outfile, level)
        outfile.write('<%s>\n' % name_)
        level += 1
        for behavior in self.behavior:
            behavior.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportLiteral(self, outfile, level, name_='behaviors'):
        level += 1
        showIndent(outfile, level)
        outfile.write('behavior=[\n')
        level += 1
        for behavior in self.behavior:
            showIndent(outfile, level)
            outfile.write('behavior(\n')
            behavior.exportLiteral(outfile, level)
            showIndent(outfile, level)
            outfile.write('),\n')
        level -= 1
        showIndent(outfile, level)
        outfile.write('],\n')
        level -= 1
    def build(self, node_):
        attrs = node_.attributes
        for child in node_.childNodes:
            nodeName_ = child.nodeName.split(':')[-1]
            if child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'behavior':
                obj = behavior.factory()
                obj.build(child)
                self.behavior.append(obj)
# end class behaviors


class behavior:
    subclass = None
    def __init__(self, klass='', name='', return_type='', args=None, impl_url='', ancillaries=None):
        self.klass = klass
        self.name = name
        self.return_type = return_type
        self.args = args
        self.impl_url = impl_url
        self.ancillaries = ancillaries
    def factory(*args_, **kwargs_):
        if behavior.subclass:
            return behavior.subclass(*args_, **kwargs_)
        else:
            return behavior(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getClass(self): return self.klass
    def setClass(self, klass): self.klass = klass
    def getName(self): return self.name
    def setName(self, name): self.name = name
    def getReturn_type(self): return self.return_type
    def setReturn_type(self, return_type): self.return_type = return_type
    def getArgs(self): return self.args
    def setArgs(self, args): self.args = args
    def getImpl_url(self): return self.impl_url
    def setImpl_url(self, impl_url): self.impl_url = impl_url
    def getAncillaries(self): return self.ancillaries
    def setAncillaries(self, ancillaries): self.ancillaries = ancillaries
    def export(self, outfile, level, name_='behavior'):
        showIndent(outfile, level)
        outfile.write('<%s>\n' % name_)
        level += 1
        showIndent(outfile, level)
        outfile.write('<class>%s</class>\n' % quote_xml(self.getKlass()))
        showIndent(outfile, level)
        outfile.write('<name>%s</name>\n' % quote_xml(self.getName()))
        showIndent(outfile, level)
        outfile.write('<return-type>%s</return-type>\n' % quote_xml(self.getReturn_type()))
        if self.args:
            self.args.export(outfile, level)
        showIndent(outfile, level)
        outfile.write('<impl-url>%s</impl-url>\n' % quote_xml(self.getImpl_url()))
        if self.ancillaries:
            self.ancillaries.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportLiteral(self, outfile, level, name_='behavior'):
        level += 1
        showIndent(outfile, level)
        outfile.write('klass=%s,\n' % quote_python(self.getKlass()))
        showIndent(outfile, level)
        outfile.write('name=%s,\n' % quote_python(self.getName()))
        showIndent(outfile, level)
        outfile.write('return_type=%s,\n' % quote_python(self.getReturn_type()))
        if self.args:
            showIndent(outfile, level)
            outfile.write('args=args(\n')
            self.args.exportLiteral(outfile, level)
            showIndent(outfile, level)
            outfile.write('),\n')
        showIndent(outfile, level)
        outfile.write('impl_url=%s,\n' % quote_python(self.getImpl_url()))
        if self.ancillaries:
            showIndent(outfile, level)
            outfile.write('ancillaries=ancillaries(\n')
            self.ancillaries.exportLiteral(outfile, level)
            showIndent(outfile, level)
            outfile.write('),\n')
        level -= 1
    def build(self, node_):
        attrs = node_.attributes
        for child in node_.childNodes:
            nodeName_ = child.nodeName.split(':')[-1]
            if child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'class':
                klass = ''
                for text_ in child.childNodes:
                    klass += text_.nodeValue
                self.klass = klass
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'name':
                name = ''
                for text_ in child.childNodes:
                    name += text_.nodeValue
                self.name = name
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'return-type':
                return_type = ''
                for text_ in child.childNodes:
                    return_type += text_.nodeValue
                self.return_type = return_type
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'args':
                obj = args.factory()
                obj.build(child)
                self.setArgs(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'impl-url':
                impl_url = ''
                for text_ in child.childNodes:
                    impl_url += text_.nodeValue
                self.impl_url = impl_url
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'ancillaries':
                obj = ancillaries.factory()
                obj.build(child)
                self.setAncillaries(obj)
# end class behavior


class args:
    subclass = None
    def __init__(self, arg=None):
        if arg is None:
            self.arg = []
        else:
            self.arg = arg
    def factory(*args_, **kwargs_):
        if args.subclass:
            return args.subclass(*args_, **kwargs_)
        else:
            return args(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getArg(self): return self.arg
    def addArg(self, value): self.arg.append(value)
    def setArg(self, index, value): self.arg[index] = value
    def export(self, outfile, level, name_='args'):
        showIndent(outfile, level)
        outfile.write('<%s>\n' % name_)
        level += 1
        for arg in self.arg:
            arg.export(outfile, level)
        level -= 1
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportLiteral(self, outfile, level, name_='args'):
        level += 1
        showIndent(outfile, level)
        outfile.write('arg=[\n')
        level += 1
        for arg in self.arg:
            showIndent(outfile, level)
            outfile.write('arg(\n')
            arg.exportLiteral(outfile, level)
            showIndent(outfile, level)
            outfile.write('),\n')
        level -= 1
        showIndent(outfile, level)
        outfile.write('],\n')
        level -= 1
    def build(self, node_):
        attrs = node_.attributes
        for child in node_.childNodes:
            nodeName_ = child.nodeName.split(':')[-1]
            if child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'arg':
                obj = arg.factory()
                obj.build(child)
                self.arg.append(obj)
# end class args


class arg:
    subclass = None
    def __init__(self, name='', data_type=''):
        self.name = name
        self.data_type = data_type
    def factory(*args_, **kwargs_):
        if arg.subclass:
            return arg.subclass(*args_, **kwargs_)
        else:
            return arg(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getName(self): return self.name
    def setName(self, name): self.name = name
    def getData_type(self): return self.data_type
    def setData_type(self, data_type): self.data_type = data_type
    def export(self, outfile, level, name_='arg'):
        showIndent(outfile, level)
        outfile.write('<%s>\n' % name_)
        level += 1
        showIndent(outfile, level)
        outfile.write('<name>%s</name>\n' % quote_xml(self.getName()))
        showIndent(outfile, level)
        outfile.write('<data-type>%s</data-type>\n' % quote_xml(self.getData_type()))
        level -= 1
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportLiteral(self, outfile, level, name_='arg'):
        level += 1
        showIndent(outfile, level)
        outfile.write('name=%s,\n' % quote_python(self.getName()))
        showIndent(outfile, level)
        outfile.write('data_type=%s,\n' % quote_python(self.getData_type()))
        level -= 1
    def build(self, node_):
        attrs = node_.attributes
        for child in node_.childNodes:
            nodeName_ = child.nodeName.split(':')[-1]
            if child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'name':
                name = ''
                for text_ in child.childNodes:
                    name += text_.nodeValue
                self.name = name
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'data-type':
                data_type = ''
                for text_ in child.childNodes:
                    data_type += text_.nodeValue
                self.data_type = data_type
# end class arg


class ancillaries:
    subclass = None
    def __init__(self, ancillary=None):
        if ancillary is None:
            self.ancillary = []
        else:
            self.ancillary = ancillary
    def factory(*args_, **kwargs_):
        if ancillaries.subclass:
            return ancillaries.subclass(*args_, **kwargs_)
        else:
            return ancillaries(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getAncillary(self): return self.ancillary
    def addAncillary(self, value): self.ancillary.append(value)
    def setAncillary(self, index, value): self.ancillary[index] = value
    def export(self, outfile, level, name_='ancillaries'):
        showIndent(outfile, level)
        outfile.write('<%s>\n' % name_)
        level += 1
        for ancillary in self.ancillary:
            ancillary.export(outfile, level, name_='ancillary')
        level -= 1
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportLiteral(self, outfile, level, name_='ancillaries'):
        level += 1
        showIndent(outfile, level)
        outfile.write('ancillary=[\n')
        level += 1
        for ancillary in self.ancillary:
            showIndent(outfile, level)
            outfile.write('arg(\n')
            ancillary.exportLiteral(outfile, level, name_='ancillary')
            showIndent(outfile, level)
            outfile.write('),\n')
        level -= 1
        showIndent(outfile, level)
        outfile.write('],\n')
        level -= 1
    def build(self, node_):
        attrs = node_.attributes
        for child in node_.childNodes:
            nodeName_ = child.nodeName.split(':')[-1]
            if child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'ancillary':
                obj = ancillary.factory()
                obj.build(child)
                self.ancillary.append(obj)
# end class ancillaries


class ancillary:
    subclass = None
    def __init__(self, klass='', role='', return_type='', name='', args=None, impl_url=''):
        self.klass = klass
        self.role = role
        self.return_type = return_type
        self.name = name
        self.args = args
        self.impl_url = impl_url
    def factory(*args_, **kwargs_):
        if ancillary.subclass:
            return ancillary.subclass(*args_, **kwargs_)
        else:
            return ancillary(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getClass(self): return self.klass
    def setClass(self, klass): self.klass = klass
    def getRole(self): return self.role
    def setRole(self, role): self.role = role
    def getReturn_type(self): return self.return_type
    def setReturn_type(self, return_type): self.return_type = return_type
    def getName(self): return self.name
    def setName(self, name): self.name = name
    def getArgs(self): return self.args
    def setArgs(self, args): self.args = args
    def getImpl_url(self): return self.impl_url
    def setImpl_url(self, impl_url): self.impl_url = impl_url
    def export(self, outfile, level, name_='ancillary'):
        showIndent(outfile, level)
        outfile.write('<%s>\n' % name_)
        level += 1
        showIndent(outfile, level)
        outfile.write('<class>%s</class>\n' % quote_xml(self.getKlass()))
        showIndent(outfile, level)
        outfile.write('<role>%s</role>\n' % quote_xml(self.getRole()))
        showIndent(outfile, level)
        outfile.write('<return-type>%s</return-type>\n' % quote_xml(self.getReturn_type()))
        showIndent(outfile, level)
        outfile.write('<name>%s</name>\n' % quote_xml(self.getName()))
        if self.args:
            self.args.export(outfile, level)
        showIndent(outfile, level)
        outfile.write('<impl-url>%s</impl-url>\n' % quote_xml(self.getImpl_url()))
        level -= 1
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportLiteral(self, outfile, level, name_='ancillary'):
        level += 1
        showIndent(outfile, level)
        outfile.write('klass=%s,\n' % quote_python(self.getKlass()))
        showIndent(outfile, level)
        outfile.write('role=%s,\n' % quote_python(self.getRole()))
        showIndent(outfile, level)
        outfile.write('return_type=%s,\n' % quote_python(self.getReturn_type()))
        showIndent(outfile, level)
        outfile.write('name=%s,\n' % quote_python(self.getName()))
        if self.args:
            showIndent(outfile, level)
            outfile.write('args=args(\n')
            self.args.exportLiteral(outfile, level)
            showIndent(outfile, level)
            outfile.write('),\n')
        showIndent(outfile, level)
        outfile.write('impl_url=%s,\n' % quote_python(self.getImpl_url()))
        level -= 1
    def build(self, node_):
        attrs = node_.attributes
        for child in node_.childNodes:
            nodeName_ = child.nodeName.split(':')[-1]
            if child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'class':
                klass = ''
                for text_ in child.childNodes:
                    klass += text_.nodeValue
                self.klass = klass
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'role':
                role = ''
                for text_ in child.childNodes:
                    role += text_.nodeValue
                self.role = role
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'return-type':
                return_type = ''
                for text_ in child.childNodes:
                    return_type += text_.nodeValue
                self.return_type = return_type
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'name':
                name = ''
                for text_ in child.childNodes:
                    name += text_.nodeValue
                self.name = name
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'args':
                obj = args.factory()
                obj.build(child)
                self.setArgs(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'impl-url':
                impl_url = ''
                for text_ in child.childNodes:
                    impl_url += text_.nodeValue
                self.impl_url = impl_url
# end class ancillary


from xml.sax import handler, make_parser

class SaxStackElement:
    def __init__(self, name='', obj=None):
        self.name = name
        self.obj = obj
        self.content = ''

#
# SAX handler
#
class SaxXml_behaviorHandler(handler.ContentHandler):
    def __init__(self):
        self.stack = []
        self.root = None

    def getRoot(self):
        return self.root

    def setDocumentLocator(self, locator):
        self.locator = locator
    
    def showError(self, msg):
        print '*** (showError):', msg
        sys.exit(-1)

    def startElement(self, name, attrs):
        done = 0
        if name == 'xml-behavior':
            obj = xml-behavior.factory()
            stackObj = SaxStackElement('xml-behavior', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'base-impl-url':
            stackObj = SaxStackElement('base_impl_url', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'behaviors':
            obj = behaviors.factory()
            stackObj = SaxStackElement('behaviors', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'behavior':
            obj = behavior.factory()
            stackObj = SaxStackElement('behavior', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'class':
            stackObj = SaxStackElement('klass', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'name':
            stackObj = SaxStackElement('name', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'return-type':
            stackObj = SaxStackElement('return_type', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'args':
            obj = args.factory()
            stackObj = SaxStackElement('args', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'impl-url':
            stackObj = SaxStackElement('impl_url', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'ancillaries':
            obj = ancillaries.factory()
            stackObj = SaxStackElement('ancillaries', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'arg':
            obj = arg.factory()
            stackObj = SaxStackElement('arg', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'data-type':
            stackObj = SaxStackElement('data_type', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'ancillary':
            obj = arg.factory()
            stackObj = SaxStackElement('ancillary', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'role':
            stackObj = SaxStackElement('role', None)
            self.stack.append(stackObj)
            done = 1
        if not done:
            self.reportError('"%s" element not allowed here.' % name)

    def endElement(self, name):
        done = 0
        if name == 'xml-behavior':
            if len(self.stack) == 1:
                self.root = self.stack[-1].obj
                self.stack.pop()
                done = 1
        elif name == 'base-impl-url':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setBase_impl_url(content)
                self.stack.pop()
                done = 1
        elif name == 'behaviors':
            if len(self.stack) >= 2:
                self.stack[-2].obj.setBehaviors(self.stack[-1].obj)
                self.stack.pop()
                done = 1
        elif name == 'behavior':
            if len(self.stack) >= 2:
                self.stack[-2].obj.addBehavior(self.stack[-1].obj)
                self.stack.pop()
                done = 1
        elif name == 'class':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setClass(content)
                self.stack.pop()
                done = 1
        elif name == 'name':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setName(content)
                self.stack.pop()
                done = 1
        elif name == 'return-type':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setReturn_type(content)
                self.stack.pop()
                done = 1
        elif name == 'args':
            if len(self.stack) >= 2:
                self.stack[-2].obj.setArgs(self.stack[-1].obj)
                self.stack.pop()
                done = 1
        elif name == 'impl-url':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setImpl_url(content)
                self.stack.pop()
                done = 1
        elif name == 'ancillaries':
            if len(self.stack) >= 2:
                self.stack[-2].obj.setAncillaries(self.stack[-1].obj)
                self.stack.pop()
                done = 1
        elif name == 'arg':
            if len(self.stack) >= 2:
                self.stack[-2].obj.addArg(self.stack[-1].obj)
                self.stack.pop()
                done = 1
        elif name == 'data-type':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setData_type(content)
                self.stack.pop()
                done = 1
        elif name == 'ancillary':
            if len(self.stack) >= 2:
                self.stack[-2].obj.addAncillary(self.stack[-1].obj)
                self.stack.pop()
                done = 1
        elif name == 'role':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setRole(content)
                self.stack.pop()
                done = 1
        if not done:
            self.reportError('"%s" element not allowed here.' % name)

    def characters(self, chrs, start, end):
        if len(self.stack) > 0:
            self.stack[-1].content += chrs[start:end]

    def reportError(self, mesg):
        locator = self.locator
        sys.stderr.write('Doc: %s  Line: %d  Column: %d\n' % \
            (locator.getSystemId(), locator.getLineNumber(), 
            locator.getColumnNumber() + 1))
        sys.stderr.write(mesg)
        sys.stderr.write('\n')
        sys.exit(-1)
        #raise RuntimeError

USAGE_TEXT = """
Usage: python <Parser>.py [ -s ] <in_xml_file>
Options:
    -s        Use the SAX parser, not the minidom parser.
"""

def usage():
    print USAGE_TEXT
    sys.exit(-1)


def saxParse(inFileName):
    parser = make_parser()
    documentHandler = SaxXml_behaviorHandler()
    parser.setDocumentHandler(documentHandler)
    parser.parse('file:%s' % inFileName)
    root = documentHandler.getRoot()
    sys.stdout.write('<?xml version="1.0" ?>\n')
    root.export(sys.stdout, 0)
    return root


def saxParseString(inString):
    parser = make_parser()
    documentHandler = SaxXml_behaviorHandler()
    parser.setDocumentHandler(documentHandler)
    parser.feed(inString)
    parser.close()
    rootObj = documentHandler.getRoot()
    #sys.stdout.write('<?xml version="1.0" ?>\n')
    #rootObj.export(sys.stdout, 0)
    return rootObj


def parse(inFileName):
    doc = minidom.parse(inFileName)
    rootNode = doc.childNodes[0]
    rootObj = xml_behavior.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('<?xml version="1.0" ?>\n')
    rootObj.export(sys.stdout, 0)
    return rootObj


def parseString(inString):
    doc = minidom.parseString(inString)
    rootNode = doc.childNodes[0]
    rootObj = xml_behavior.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('<?xml version="1.0" ?>\n')
    rootObj.export(sys.stdout, 0)
    return rootObj


def parseLiteral(inFileName):
    doc = minidom.parse(inFileName)
    rootNode = doc.childNodes[0]
    rootObj = xml_behavior.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('from xmlbehavior import *\n\n')
    sys.stdout.write('rootObj = xml_behavior(\n')
    rootObj.exportLiteral(sys.stdout, 0)
    sys.stdout.write(')\n')
    return rootObj


def main():
    args = sys.argv[1:]
    if len(args) == 2 and args[0] == '-s':
        saxParse(args[1])
    elif len(args) == 1:
        parseLiteral(args[0])
    else:
        usage()


if __name__ == '__main__':
    main()
    #import pdb
    #pdb.run('main()')

