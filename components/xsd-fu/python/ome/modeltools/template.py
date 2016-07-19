# Copyright (C) 2009 - 2016 Open Microscopy Environment. All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice,
#    this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

from ome.modeltools import config

try:
    import mx.DateTime as DateTime

    def now():
        return DateTime.ISO.str(DateTime.now())
except ImportError:
    from datetime import datetime

    def now():
        return datetime.now()


class TemplateInfo(object):
    """
    Basic status information to pass to the template engine.
    """
    def __init__(self, outputDirectory, package):
        self.outputDirectory = outputDirectory
        self.package = package
        self.date = now()
        self.DO_NOT_PROCESS = config.DO_NOT_PROCESS
        self.BACK_REFERENCE_OVERRIDE = config.BACK_REFERENCE_OVERRIDE
        self.BACK_REFERENCE_LINK_OVERRIDE = \
            config.BACK_REFERENCE_LINK_OVERRIDE
        self.BACK_REFERENCE_NAME_OVERRIDE = \
            config.BACK_REFERENCE_NAME_OVERRIDE
        self.ANNOTATION_OVERRIDE = config.ANNOTATION_OVERRIDE
        self.COMPLEX_OVERRIDE = config.COMPLEX_OVERRIDE
        self.REF_REGEX = config.REF_REGEX

    def link_overridden(self, property_name, class_name):
        """Whether or not a back reference link should be overridden."""
        try:
            return class_name in \
                self.BACK_REFERENCE_LINK_OVERRIDE[property_name]
        except KeyError:
            return False

    def backReference_overridden(self, property_name, class_name):
        """Whether or not a back reference link name should be overridden."""
        try:
            name = class_name + "." + self.REF_REGEX.sub('', property_name)
            return self.BACK_REFERENCE_NAME_OVERRIDE[name]
        except KeyError:
            return False
