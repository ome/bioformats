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
