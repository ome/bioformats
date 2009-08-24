#!/usr/bin/env python
# -*- mode: pymode; coding: latin1; -*-

import sys
import re

#
# You must include the following class definition at the top of
#   your method specification file.
#
class MethodSpec(object):
    def __init__(self, name='', source='', class_names='',
            class_names_compiled=None):
        """MethodSpec -- A specification of a method.
        Member variables:
            name -- The method name
            source -- The source code for the method.  Must be
                indented to fit in a class definition.
            class_names -- A regular expression that must match the
                class names in which the method is to be inserted.
            class_names_compiled -- The compiled class names.
                generateDS.py will do this compile for you.
        """
        self.name = name
        self.source = source
        if class_names is None:
            self.class_names = ('.*', )
        else:
            self.class_names = class_names
        if class_names_compiled is None:
            self.class_names_compiled = re.compile(self.class_names)
        else:
            self.class_names_compiled = class_names_compiled
    def get_name(self):
        return self.name
    def set_name(self, name):
        self.name = name
    def get_source(self):
        return self.source
    def set_source(self, source):
        self.source = source
    def get_class_names(self):
        return self.class_names
    def set_class_names(self, class_names):
        self.class_names = class_names
        self.class_names_compiled = re.compile(class_names)
    def get_class_names_compiled(self):
        return self.class_names_compiled
    def set_class_names_compiled(self, class_names_compiled):
        self.class_names_compiled = class_names_compiled
    def match_name(self, class_name):
        """Match against the name of the class currently being generated.
        If this method returns True, the method will be inserted in
          the generated class.
        """
        if self.class_names_compiled.search(class_name):
            return True
        else:
            return False
    def get_interpolated_source(self, values_dict):
        """Get the method source code, interpolating values from values_dict
        into it.  The source returned by this method is inserted into
        the generated class.
        """
        source = self.source % values_dict
        return source
    def show(self):
        print 'specification:'
        print '    name: %s' % (self.name, )
        print self.source
        print '    class_names: %s' % (self.class_names, )
        print '    names pat  : %s' % (self.class_names_compiled.pattern, )


#
# Provide one or more method specification such as the following.
# Notes:
# - Each generated class contains a class variable _member_data_items.
#   This variable contains a list of instances of class _MemberSpec.
#   See the definition of class _MemberSpec near the top of the
#   generated superclass file and also section "User Methods" in
#   the documentation, as well as the examples below.

#
# Replace the following method specifications with your own.

#
# Sample method specification #1
#
method1 = MethodSpec(name='walk_and_update',
    source='''\
    def walk_and_update(self):
        members = %(class_name)s._member_data_items
        for member in members:
            obj1 = getattr(self, member.get_name())
            if member.get_data_type() == 'xs:date':
                newvalue = date_calcs.date_from_string(obj1)
                setattr(self, member.get_name(), newvalue)
            elif member.get_container():
                for child in obj1:
                    if type(child) == types.InstanceType:
                        child.walk_and_update()
            else:
                obj1 = getattr(self, member.get_name())
                if type(obj1) == types.InstanceType:
                    obj1.walk_and_update()
        if %(class_name)s.superclass != None:
          %(class_name)s.superclass.walk_and_update(self)
''',
    # class_names=r'^Employee$|^[a-zA-Z]*Dependent$',
    class_names=r'^.*$',
    )

#
# Sample method specification #2
#
method2 = MethodSpec(name='walk_and_show',
    source='''\
    def walk_and_show(self, depth):
        global counter
        counter += 1
        depth += 1
        print '%%d. class: %(class_name)s  depth: %%d' %% (counter, depth, )
        members = %(class_name)s._member_data_items
        for member in members:
            s1 = member.get_name()
            s2 = member.get_data_type()
            s3 = '%%d' %% member.get_container()
            obj1 = getattr(self, member.get_name())
            if member.get_container():
                s4 = '<container>'
            else:
                if type(obj1) != types.InstanceType:
                    s4 = '%%s' %% obj1
                else:
                    s4 = '<instance>'
            s5 = '%%s%%s%%s  %%s' %% (s1.ljust(16), s2.ljust(16), s3.rjust(4), s4, )
            print '   ', s5
        for member in members:
            if member.get_container():
                for child in getattr(self, member.get_name()):
                    if type(child) == types.InstanceType:
                        child.walk_and_show(depth)
            else:
                obj1 = getattr(self, member.get_name())
                if type(obj1) == types.InstanceType:
                    obj1.walk_and_show(depth)
''',
    # Attach to all classes.
    class_names=r'^.*$',
    )

#
# Sample method specification #3
#
method3 = MethodSpec(name='set_up',
    source='''\
    def set_up(self):
        global types, counter
        import types as types_module
        types = types_module
        counter = 0
''',
    # Attach only to the root class: people.
    class_names=r'^people$',
    )


#
# Sample method specification #4
#
method4 = MethodSpec(name='method4',
    source='''\
    def method2(self, max):
        if self.max > max:
            return False
        else:
            return True
''',
    class_names=r'^Truck$|^Boat$',
    )



#
# Provide a list of your method specifications.
#   This list of specifications must be named METHOD_SPECS.
#
METHOD_SPECS = (
    method1,
    method2,
    method3,
    method4,
    )


def test():
    for spec in METHOD_SPECS:
        spec.show()

def main():
    test()


if __name__ == '__main__':
    main()


