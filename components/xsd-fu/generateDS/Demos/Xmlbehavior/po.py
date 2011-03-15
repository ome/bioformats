#!/usr/bin/env python

#
# Generated Fri Jul  2 13:32:06 2004 by generateDS.py.
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

class purchase_order:
    subclass = None
    def __init__(self, customer=None, date='', line_item=None, shipper=None):
        self.customer = customer
        self.date = date
        if line_item is None:
            self.line_item = []
        else:
            self.line_item = line_item
        self.shipper = shipper
    def factory(*args_, **kwargs_):
        if purchase_order.subclass:
            return purchase_order.subclass(*args_, **kwargs_)
        else:
            return purchase_order(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getCustomer(self): return self.customer
    def setCustomer(self, customer): self.customer = customer
    def getDate(self): return self.date
    def setDate(self, date): self.date = date
    def getLine_item(self): return self.line_item
    def addLine_item(self, value): self.line_item.append(value)
    def setLine_item(self, index, value): self.line_item[index] = value
    def getShipper(self): return self.shipper
    def setShipper(self, shipper): self.shipper = shipper
    def export(self, outfile, level, name_='purchase-order'):
        showIndent(outfile, level)
        outfile.write('<%s>\n' % name_)
        level += 1
        if self.customer:
            self.customer.export(outfile, level, name_='customer')
        showIndent(outfile, level)
        outfile.write('<date>%s</date>\n' % quote_xml(self.getDate()))
        for line_item in self.line_item:
            line_item.export(outfile, level, name_='line-item')
        if self.shipper:
            self.shipper.export(outfile, level, name_='shipper')
        level -= 1
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportLiteral(self, outfile, level, name_='purchase-order'):
        level += 1
        if self.customer:
            showIndent(outfile, level)
            outfile.write('customer=po:customer(\n')
            self.customer.exportLiteral(outfile, level, name_='customer')
            showIndent(outfile, level)
            outfile.write('),\n')
        showIndent(outfile, level)
        outfile.write('date=%s,\n' % quote_python(self.getDate()))
        showIndent(outfile, level)
        outfile.write('line_item=[\n')
        level += 1
        for line_item in self.line_item:
            showIndent(outfile, level)
            outfile.write('po:line-item(\n')
            line_item.exportLiteral(outfile, level, name_='line_item')
            showIndent(outfile, level)
            outfile.write('),\n')
        level -= 1
        showIndent(outfile, level)
        outfile.write('],\n')
        if self.shipper:
            showIndent(outfile, level)
            outfile.write('shipper=po:shipper(\n')
            self.shipper.exportLiteral(outfile, level, name_='shipper')
            showIndent(outfile, level)
            outfile.write('),\n')
        level -= 1
    def build(self, node_):
        attrs = node_.attributes
        for child in node_.childNodes:
            nodeName_ = child.nodeName.split(':')[-1]
            if child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'customer':
                obj = customer.factory()
                obj.build(child)
                self.setCustomer(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'date':
                date = ''
                for text_ in child.childNodes:
                    date += text_.nodeValue
                self.date = date
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'line-item':
                obj = line_item.factory()
                obj.build(child)
                self.line_item.append(obj)
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'shipper':
                obj = shipper.factory()
                obj.build(child)
                self.setShipper(obj)
# end class purchase_order


class customer:
    subclass = None
    def __init__(self, name='', address=''):
        self.name = name
        self.address = address
    def factory(*args_, **kwargs_):
        if customer.subclass:
            return customer.subclass(*args_, **kwargs_)
        else:
            return customer(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getName(self): return self.name
    def setName(self, name): self.name = name
    def getAddress(self): return self.address
    def setAddress(self, address): self.address = address
    def export(self, outfile, level, name_='customer'):
        showIndent(outfile, level)
        outfile.write('<%s>\n' % name_)
        level += 1
        showIndent(outfile, level)
        outfile.write('<name>%s</name>\n' % quote_xml(self.getName()))
        showIndent(outfile, level)
        outfile.write('<address>%s</address>\n' % quote_xml(self.getAddress()))
        level -= 1
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportLiteral(self, outfile, level, name_='customer'):
        level += 1
        showIndent(outfile, level)
        outfile.write('name=%s,\n' % quote_python(self.getName()))
        showIndent(outfile, level)
        outfile.write('address=%s,\n' % quote_python(self.getAddress()))
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
                nodeName_ == 'address':
                address = ''
                for text_ in child.childNodes:
                    address += text_.nodeValue
                self.address = address
# end class customer


class line_item:
    subclass = None
    def __init__(self, description='', per_unit_ounces=0.0, price=0.0, quantity=-1):
        self.description = description
        self.per_unit_ounces = per_unit_ounces
        self.price = price
        self.quantity = quantity
    def factory(*args_, **kwargs_):
        if line_item.subclass:
            return line_item.subclass(*args_, **kwargs_)
        else:
            return line_item(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getDescription(self): return self.description
    def setDescription(self, description): self.description = description
    def getPer_unit_ounces(self): return self.per_unit_ounces
    def setPer_unit_ounces(self, per_unit_ounces): self.per_unit_ounces = per_unit_ounces
    def getPrice(self): return self.price
    def setPrice(self, price): self.price = price
    def getQuantity(self): return self.quantity
    def setQuantity(self, quantity): self.quantity = quantity
    def export(self, outfile, level, name_='line-item'):
        showIndent(outfile, level)
        outfile.write('<%s>\n' % name_)
        level += 1
        showIndent(outfile, level)
        outfile.write('<description>%s</description>\n' % quote_xml(self.getDescription()))
        showIndent(outfile, level)
        outfile.write('<per-unit-ounces>%f</per-unit-ounces>\n' % self.getPer_unit_ounces())
        showIndent(outfile, level)
        outfile.write('<price>%e</price>\n' % self.getPrice())
        showIndent(outfile, level)
        outfile.write('<quantity>%d</quantity>\n' % self.getQuantity())
        level -= 1
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportLiteral(self, outfile, level, name_='line-item'):
        level += 1
        showIndent(outfile, level)
        outfile.write('description=%s,\n' % quote_python(self.getDescription()))
        showIndent(outfile, level)
        outfile.write('per_unit_ounces=%f,\n' % self.getPer_unit_ounces())
        showIndent(outfile, level)
        outfile.write('price=%e,\n' % self.getPrice())
        showIndent(outfile, level)
        outfile.write('quantity=%d,\n' % self.getQuantity())
        level -= 1
    def build(self, node_):
        attrs = node_.attributes
        for child in node_.childNodes:
            nodeName_ = child.nodeName.split(':')[-1]
            if child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'description':
                description = ''
                for text_ in child.childNodes:
                    description += text_.nodeValue
                self.description = description
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'per-unit-ounces':
                if child.firstChild:
                    sval = child.firstChild.nodeValue
                    try:
                        fval = float(sval)
                    except ValueError:
                        raise ValueError('requires float (or double) -- %s' % child.toxml())
                    self.per_unit_ounces = fval
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'price':
                if child.firstChild:
                    sval = child.firstChild.nodeValue
                    try:
                        fval = float(sval)
                    except ValueError:
                        raise ValueError('requires float (or double) -- %s' % child.toxml())
                    self.price = fval
            elif child.nodeType == Node.ELEMENT_NODE and \
                nodeName_ == 'quantity':
                if child.firstChild:
                    sval = child.firstChild.nodeValue
                    try:
                        ival = int(sval)
                    except ValueError:
                        raise ValueError('requires integer -- %s' % child.toxml())
                    self.quantity = ival
# end class line_item


class shipper:
    subclass = None
    def __init__(self, name='', per_ounce_rate=0.0):
        self.name = name
        self.per_ounce_rate = per_ounce_rate
    def factory(*args_, **kwargs_):
        if shipper.subclass:
            return shipper.subclass(*args_, **kwargs_)
        else:
            return shipper(*args_, **kwargs_)
    factory = staticmethod(factory)
    def getName(self): return self.name
    def setName(self, name): self.name = name
    def getPer_ounce_rate(self): return self.per_ounce_rate
    def setPer_ounce_rate(self, per_ounce_rate): self.per_ounce_rate = per_ounce_rate
    def export(self, outfile, level, name_='shipper'):
        showIndent(outfile, level)
        outfile.write('<%s>\n' % name_)
        level += 1
        showIndent(outfile, level)
        outfile.write('<name>%s</name>\n' % quote_xml(self.getName()))
        showIndent(outfile, level)
        outfile.write('<per-ounce-rate>%f</per-ounce-rate>\n' % self.getPer_ounce_rate())
        level -= 1
        showIndent(outfile, level)
        outfile.write('</%s>\n' % name_)
    def exportLiteral(self, outfile, level, name_='shipper'):
        level += 1
        showIndent(outfile, level)
        outfile.write('name=%s,\n' % quote_python(self.getName()))
        showIndent(outfile, level)
        outfile.write('per_ounce_rate=%f,\n' % self.getPer_ounce_rate())
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
                nodeName_ == 'per-ounce-rate':
                if child.firstChild:
                    sval = child.firstChild.nodeValue
                    try:
                        fval = float(sval)
                    except ValueError:
                        raise ValueError('requires float (or double) -- %s' % child.toxml())
                    self.per_ounce_rate = fval
# end class shipper


from xml.sax import handler, make_parser

class SaxStackElement:
    def __init__(self, name='', obj=None):
        self.name = name
        self.obj = obj
        self.content = ''

#
# SAX handler
#
class SaxPurchase_orderHandler(handler.ContentHandler):
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
        if name == 'purchase-order':
            obj = purchase-order.factory()
            stackObj = SaxStackElement('purchase-order', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'customer':
            obj = po_customer.factory()
            stackObj = SaxStackElement('customer', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'date':
            stackObj = SaxStackElement('date', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'line-item':
            obj = po_line_item.factory()
            stackObj = SaxStackElement('line_item', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'shipper':
            obj = po_shipper.factory()
            stackObj = SaxStackElement('shipper', obj)
            self.stack.append(stackObj)
            done = 1
        elif name == 'name':
            stackObj = SaxStackElement('name', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'address':
            stackObj = SaxStackElement('address', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'description':
            stackObj = SaxStackElement('description', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'per-unit-ounces':
            stackObj = SaxStackElement('per_unit_ounces', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'price':
            stackObj = SaxStackElement('price', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'quantity':
            stackObj = SaxStackElement('quantity', None)
            self.stack.append(stackObj)
            done = 1
        elif name == 'per-ounce-rate':
            stackObj = SaxStackElement('per_ounce_rate', None)
            self.stack.append(stackObj)
            done = 1
        if not done:
            self.reportError('"%s" element not allowed here.' % name)

    def endElement(self, name):
        done = 0
        if name == 'purchase-order':
            if len(self.stack) == 1:
                self.root = self.stack[-1].obj
                self.stack.pop()
                done = 1
        elif name == 'customer':
            if len(self.stack) >= 2:
                self.stack[-2].obj.setCustomer(self.stack[-1].obj)
                self.stack.pop()
                done = 1
        elif name == 'date':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setDate(content)
                self.stack.pop()
                done = 1
        elif name == 'line-item':
            if len(self.stack) >= 2:
                self.stack[-2].obj.addLine_item(self.stack[-1].obj)
                self.stack.pop()
                done = 1
        elif name == 'shipper':
            if len(self.stack) >= 2:
                self.stack[-2].obj.setShipper(self.stack[-1].obj)
                self.stack.pop()
                done = 1
        elif name == 'name':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setName(content)
                self.stack.pop()
                done = 1
        elif name == 'address':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setAddress(content)
                self.stack.pop()
                done = 1
        elif name == 'description':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                self.stack[-2].obj.setDescription(content)
                self.stack.pop()
                done = 1
        elif name == 'per-unit-ounces':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                if content:
                    try:
                        content = float(content)
                    except:
                        self.reportError('"per-unit-ounces" must be float -- content: %s' % content)
                else:
                    content = -1
                self.stack[-2].obj.setPer_unit_ounces(content)
                self.stack.pop()
                done = 1
        elif name == 'price':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                if content:
                    try:
                        content = float(content)
                    except:
                        self.reportError('"price" must be float -- content: %s' % content)
                else:
                    content = -1
                self.stack[-2].obj.setPrice(content)
                self.stack.pop()
                done = 1
        elif name == 'quantity':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                if content:
                    try:
                        content = int(content)
                    except:
                        self.reportError('"quantity" must be integer -- content: %s' % content)
                else:
                    content = -1
                self.stack[-2].obj.setQuantity(content)
                self.stack.pop()
                done = 1
        elif name == 'per-ounce-rate':
            if len(self.stack) >= 2:
                content = self.stack[-1].content
                if content:
                    try:
                        content = float(content)
                    except:
                        self.reportError('"per-ounce-rate" must be float -- content: %s' % content)
                else:
                    content = -1
                self.stack[-2].obj.setPer_ounce_rate(content)
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
    documentHandler = SaxPurchase_orderHandler()
    parser.setDocumentHandler(documentHandler)
    parser.parse('file:%s' % inFileName)
    root = documentHandler.getRoot()
    sys.stdout.write('<?xml version="1.0" ?>\n')
    root.export(sys.stdout, 0)
    return root


def saxParseString(inString):
    parser = make_parser()
    documentHandler = SaxPurchase_orderHandler()
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
    rootObj = purchase_order.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('<?xml version="1.0" ?>\n')
    rootObj.export(sys.stdout, 0)
    return rootObj


def parseString(inString):
    doc = minidom.parseString(inString)
    rootNode = doc.childNodes[0]
    rootObj = purchase_order.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('<?xml version="1.0" ?>\n')
    rootObj.export(sys.stdout, 0)
    return rootObj


def parseLiteral(inFileName):
    doc = minidom.parse(inFileName)
    rootNode = doc.childNodes[0]
    rootObj = purchase_order.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    sys.stdout.write('from po import *\n\n')
    sys.stdout.write('rootObj = purchase_order(\n')
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

